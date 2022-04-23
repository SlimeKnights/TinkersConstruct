package slimeknights.tconstruct.tools.recipe;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierSalvage;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ModifierRemovalRecipe implements ITinkerStationRecipe {
  private static final ValidatedResult NO_MODIFIERS = ValidatedResult.failure(TConstruct.makeTranslationKey("recipe", "remove_modifier.no_modifiers"));

  @Getter
  private final ResourceLocation id;
  private final Ingredient ingredient;
  private final ItemStack container;

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    if (!inv.getTinkerableStack().is(TinkerTags.Items.MODIFIABLE)) {
      return false;
    }
    return IncrementalModifierRecipe.containsOnlyIngredient(inv, ingredient);
  }

  /** Gets the modifier entry being removed */
  @Nullable
  private ModifierEntry getModifierToRemove(ITinkerStationContainer inv, List<ModifierEntry> modifiers) {
    // sums all filled slots for the removal index, should be able to reach any index, but requires 1 wet sponge for every 5
    int removeIndex = -1;
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty() && ingredient.test(stack)) {
        removeIndex += (i + 1) * stack.getCount();
      }
    }
    // shouldn't be possible, but better than a crash just in case
    if (removeIndex == -1) {
      return null;
    }
    // we start at the most recent modifier, moving backwards
    if (removeIndex >= modifiers.size()) {
      removeIndex = 0;
    } else {
      removeIndex = modifiers.size() - removeIndex - 1;
    }
    return modifiers.get(removeIndex);
  }

  @Override
  public ValidatedResult getValidatedResult(ITinkerStationContainer inv) {
    ItemStack toolStack = inv.getTinkerableStack();
    ToolStack tool = ToolStack.from(toolStack);
    List<ModifierEntry> modifiers = tool.getUpgrades().getModifiers();
    if (modifiers.isEmpty()) {
      return NO_MODIFIERS;
    }

    // find the modifier to remove
    ModifierEntry toRemove = getModifierToRemove(inv, modifiers);
    if (toRemove == null) {
      return ValidatedResult.PASS;
    }

    // salvage
    tool = tool.copy();
    ModifierId id = toRemove.getId();
    ModifierSalvage salvage = ModifierRecipeLookup.getSalvage(toolStack, tool, id, toRemove.getLevel());

    // restore the slots
    if (salvage != null) {
      salvage.updateTool(tool);
    }

    // first remove hook, primarily for removing raw NBT which is highly discouraged using
    int newLevel = tool.getModifierLevel(id) - 1;
    Modifier modifier = toRemove.getModifier();
    if (newLevel <= 0) {
      modifier.beforeRemoved(tool, tool.getRestrictedNBT());
    }

    // remove the actual modifier
    tool.removeModifier(id, 1);

    // second remove hook, useful for removing modifier specific state data
    if (newLevel <= 0) {
      modifier.onRemoved(tool);
    }

    // ensure the tool is still valid
    ValidatedResult validated = tool.validate();
    if (validated.hasError()) {
      return validated;
    }
    // if this was the last level, validate the tool is still valid without it
    if (newLevel <= 0) {
      validated = modifier.validate(tool, 0);
      if (validated.hasError()) {
        return validated;
      }
    }

    // check the modifier requirements
    ItemStack resultStack = tool.createStack(Math.min(toolStack.getCount(), shrinkToolSlotBy())); // creating a stack to make it as accurate as possible, though the old stack should be sufficient
    validated = ModifierRecipeLookup.checkRequirements(resultStack, tool);
    if (validated.hasError()) {
      return validated;
    }
    
    // successfully removed
    return ValidatedResult.success(resultStack);
  }

  @Override
  public int shrinkToolSlotBy() {
    return 64;
  }

  @Override
  public void updateInputs(ItemStack result, IMutableTinkerStationContainer inv, boolean isServer) {
    // remove the input item, done second as we need its location for salvage
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty() && ingredient.test(stack)) {
        inv.shrinkInput(i, 1, container.copy());
        break;
      }
    }
  }

  /** @deprecated Use {@link #getValidatedResult(ITinkerStationContainer)} */
  @Deprecated
  @Override
  public ItemStack getResultItem() {
    return ItemStack.EMPTY;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.removeModifierSerializer.get();
  }

  public static class Serializer extends LoggingRecipeSerializer<ModifierRemovalRecipe> {

    @Override
    public ModifierRemovalRecipe fromJson(ResourceLocation id, JsonObject json) {
      Ingredient ingredient = Ingredient.fromJson(JsonHelper.getElement(json, "ingredient"));
      ItemStack container = ItemStack.EMPTY;
      if (json.has("container")) {
        container = IncrementalModifierRecipe.deseralizeResultItem(json, "container");
      }
      return new ModifierRemovalRecipe(id, ingredient, container);
    }

    @Nullable
    @Override
    public ModifierRemovalRecipe fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      Ingredient ingredient = Ingredient.fromNetwork(buffer);
      ItemStack container = buffer.readItem();
      return new ModifierRemovalRecipe(id, ingredient, container);
    }

    @Override
    public void toNetworkSafe(FriendlyByteBuf buffer, ModifierRemovalRecipe recipe) {
      recipe.ingredient.toNetwork(buffer);
      buffer.writeItem(recipe.container);
    }
  }

  @RequiredArgsConstructor(staticName = "removal")
  public static class Builder extends AbstractRecipeBuilder<Builder> {
    private final Ingredient ingredient;
    private final ItemStack container;

    @Override
    public void save(Consumer<FinishedRecipe> consumer) {
      save(consumer, Objects.requireNonNull(container.getItem().getRegistryName()));
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
      if (ingredient == Ingredient.EMPTY) {
        throw new IllegalStateException("Empty ingredient not allowed");
      }
      ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
      consumer.accept(new Finished(id, advancementId));
    }

    private class Finished extends AbstractFinishedRecipe {
      public Finished(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
        super(ID, advancementID);
      }

      @Override
      public void serializeRecipeData(JsonObject json) {
        json.add("ingredient", ingredient.toJson());
        if (!container.isEmpty()) {
          json.add("container", IncrementalModifierRecipeBuilder.serializeResult(container));
        }
      }

      @Override
      public RecipeSerializer<?> getType() {
        return TinkerModifiers.removeModifierSerializer.get();
      }
    }
  }
}
