package slimeknights.tconstruct.tools.recipe;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IncrementalModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.salvage.AbstractModifierSalvage;
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
  public boolean matches(ITinkerStationInventory inv, World world) {
    if (!TinkerTags.Items.MODIFIABLE.contains(inv.getTinkerableStack().getItem())) {
      return false;
    }
    return IncrementalModifierRecipe.containsOnlyIngredient(inv, ingredient);
  }

  /** Gets the modifier entry being removed */
  @Nullable
  private ModifierEntry getModifierToRemove(ITinkerStationInventory inv, List<ModifierEntry> modifiers) {
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
  public ValidatedResult getValidatedResult(ITinkerStationInventory inv) {
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
    Modifier modifier = toRemove.getModifier();
    AbstractModifierSalvage salvage = ModifierRecipeLookup.getSalvage(toolStack, tool, modifier, toRemove.getLevel());

    // restore the slots
    if (salvage != null) {
      salvage.updateTool(tool);
    }

    // remove the actual modifier
    tool.removeModifier(modifier, 1);

    // allow the modifier to clean up NBT if its the last level
    int newLevel = tool.getModifierLevel(modifier); // want to check both upgrades and traits
    if (newLevel == 0) {
      modifier.onRemoved(tool);
    }

    // ensure the tool is still valid
    ValidatedResult validated = tool.validate();
    if (validated.hasError()) {
      return validated;
    }
    // if this was the last level, validate the tool is still valid without it
    if (newLevel == 0) {
      validated = modifier.validate(tool, 0);
      if (validated.hasError()) {
        return validated;
      }
    }

    // check the modifier requirements
    ItemStack resultStack = tool.createStack(); // creating a stack to make it as accurate as possible, though the old stack should be sufficient
    validated = ModifierRecipeLookup.checkRequirements(resultStack, tool);
    if (validated.hasError()) {
      return validated;
    }
    
    // successfully removed
    return ValidatedResult.success(resultStack);
  }

  @Override
  public void updateInputs(ItemStack result, IMutableTinkerStationInventory inv) {
    // return salvage items for modifier, using the original tool as that still has the modifier
    ItemStack toolStack = inv.getTinkerableStack();
    ToolStack tool = ToolStack.from(toolStack);
    ModifierEntry toRemove = getModifierToRemove(inv, tool.getUpgrades().getModifiers());
    if (toRemove != null) {
      AbstractModifierSalvage salvage = ModifierRecipeLookup.getSalvage(toolStack, tool, toRemove.getModifier(), toRemove.getLevel());
      if (salvage != null) {
        salvage.acceptItems(tool, inv::giveItem, TConstruct.RANDOM);
      }
    }

    // remove the input item, done second as we need its location for salvage
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty() && ingredient.test(stack)) {
        inv.shrinkInput(i, 1, container.copy());
        break;
      }
    }
  }

  /** @deprecated Use {@link #getValidatedResult(ITinkerStationInventory)} */
  @Deprecated
  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerModifiers.removeModifierSerializer.get();
  }

  public static class Serializer extends RecipeSerializer<ModifierRemovalRecipe> {

    @Override
    public ModifierRemovalRecipe read(ResourceLocation id, JsonObject json) {
      Ingredient ingredient = Ingredient.deserialize(JsonHelper.getElement(json, "ingredient"));
      ItemStack container = ItemStack.EMPTY;
      if (json.has("container")) {
        container = IncrementalModifierRecipe.deseralizeResultItem(json, "container");
      }
      return new ModifierRemovalRecipe(id, ingredient, container);
    }

    @Nullable
    @Override
    public ModifierRemovalRecipe read(ResourceLocation id, PacketBuffer buffer) {
      Ingredient ingredient = Ingredient.read(buffer);
      ItemStack container = buffer.readItemStack();
      return new ModifierRemovalRecipe(id, ingredient, container);
    }

    @Override
    public void write(PacketBuffer buffer, ModifierRemovalRecipe recipe) {
      recipe.ingredient.write(buffer);
      buffer.writeItemStack(recipe.container);
    }
  }

  @RequiredArgsConstructor(staticName = "removal")
  public static class Builder extends AbstractRecipeBuilder<Builder> {
    private final Ingredient ingredient;
    private final ItemStack container;

    @Override
    public void build(Consumer<IFinishedRecipe> consumer) {
      build(consumer, Objects.requireNonNull(container.getItem().getRegistryName()));
    }

    @Override
    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
      if (ingredient == Ingredient.EMPTY) {
        throw new IllegalStateException("Empty ingredient not allowed");
      }
      ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
      consumer.accept(new FinishedRecipe(id, advancementId));
    }

    private class FinishedRecipe extends AbstractFinishedRecipe {
      public FinishedRecipe(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
        super(ID, advancementID);
      }

      @Override
      public void serialize(JsonObject json) {
        json.add("ingredient", ingredient.serialize());
        if (!container.isEmpty()) {
          json.add("container", IncrementalModifierRecipeBuilder.serializeResult(container));
        }
      }

      @Override
      public IRecipeSerializer<?> getSerializer() {
        return TinkerModifiers.removeModifierSerializer.get();
      }
    }
  }
}
