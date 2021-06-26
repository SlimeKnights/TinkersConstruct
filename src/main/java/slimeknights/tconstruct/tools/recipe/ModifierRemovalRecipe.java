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
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IncrementalModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierRecipeLookup;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ModifierRemovalRecipe implements ITinkerStationRecipe {
  private static final ValidatedResult NO_MODIFIERS = ValidatedResult.failure(Util.makeTranslationKey("recipe", "remove_modifier.no_modifiers"));

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

  @Override
  public ValidatedResult getValidatedResult(ITinkerStationInventory inv) {
    ToolStack tool = ToolStack.from(inv.getTinkerableStack());
    List<ModifierEntry> modifiers = tool.getUpgrades().getModifiers();
    if (modifiers.isEmpty()) {
      return NO_MODIFIERS;
    }
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
      return ValidatedResult.PASS;
    }
    // we start at the most recent modifier, moving backwards
    if (removeIndex >= modifiers.size()) {
      removeIndex = 0;
    } else {
      removeIndex = modifiers.size() - removeIndex - 1;
    }
    // restore slots
    tool = tool.copy();
    Modifier toRemove = modifiers.get(removeIndex).getModifier();
    int slots = ModifierRecipeLookup.getUpgradeSlots(toRemove);
    // -1 means no upgrade recipe defined, so try abilities
    if (slots > -1) {
      tool.getPersistentData().addUpgrades(slots);
    } else {
      slots = ModifierRecipeLookup.getAbilitySlots(toRemove);
      if (slots > 0) {
        tool.getPersistentData().addAbilities(slots);
      }
    }
    // remove the actual modifier
    tool.removeModifier(toRemove, 1);
    // if the modifier is incremental, clear progress on level
    // means you don't pick up in the middle, or hinder previous levels
    if (ModifierRecipeLookup.getNeededPerLevel(toRemove) > 0) {
      tool.getPersistentData().remove(toRemove.getId());
    }

    // ensure the tool is still valid
    ValidatedResult validated = tool.validate();
    if (validated.hasError()) {
      return validated;
    }
    // if this was the last level, validate the tool is still valid without it
    if (tool.getModifierLevel(toRemove) == 0) {
      validated = toRemove.validate(tool, 0);
      if (validated.hasError()) {
        return validated;
      }
    }

    // successfully removed
    return ValidatedResult.success(tool.createStack());
  }

  @Override
  public void updateInputs(ItemStack result, IMutableTinkerStationInventory inv) {
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty() && ingredient.test(stack)) {
        inv.shrinkInput(i, 1, container);
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
