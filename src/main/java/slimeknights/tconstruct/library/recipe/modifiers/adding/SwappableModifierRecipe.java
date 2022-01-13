package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Standard recipe to add a modifier
 */
public class SwappableModifierRecipe extends ModifierRecipe {
  /** Value of the modifier being swapped, distinguishing this recipe from others for the same modifier */
  private final String value;
  public SwappableModifierRecipe(ResourceLocation id, List<SizedIngredient> inputs, Ingredient toolRequirement, ModifierMatch requirements, String requirementsError, Modifier result, String value, @Nullable SlotCount slots) {
    super(id, inputs, toolRequirement, requirements, requirementsError, new ModifierEntry(result, 1), 1, slots);
    this.value = value;
  }

  /**
   * Gets the recipe result, or an object containing an error message if the recipe matches but cannot be applied.
   * @return Validated result
   */
  @Override
  public ValidatedResult getValidatedResult(ITinkerStationInventory inv) {
    ToolStack tool = ToolStack.from(inv.getTinkerableStack());

    // if the tool has the modifier already, can skip most requirements
    Modifier modifier = result.getModifier();

    ValidatedResult commonError;
    boolean needsModifier;
    if (tool.getUpgrades().getLevel(modifier) == 0) {
      needsModifier = true;
      commonError = validatePrerequisites(tool);
    } else {
      needsModifier = false;
      commonError = validateRequirements(tool);
    }
    if (commonError.hasError()) {
      return commonError;
    }

    // consume slots
    tool = tool.copy();
    ModDataNBT persistentData = tool.getPersistentData();
    if (needsModifier) {
      SlotCount slots = getSlots();
      if (slots != null) {
        persistentData.addSlots(slots.getType(), -slots.getCount());
      }
    }

    // set the new value to the modifier
    persistentData.putString(modifier.getId(), value);

    // add modifier if needed
    if (needsModifier) {
      tool.addModifier(result.getModifier(), 1);
    } else {
      tool.rebuildStats();
    }

    // ensure no modifier problems
    ValidatedResult toolValidation = tool.validate();
    if (toolValidation.hasError()) {
      return toolValidation;
    }

    return ValidatedResult.success(tool.createStack());
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerModifiers.swappableModifierSerializer.get();
  }


  /* Display */

  @Override
  public List<ItemStack> getToolWithModifier() {
    if (toolWithModifier == null) {
      ResourceLocation id = result.getModifier().getId();
      toolWithModifier = getToolInputs().stream().map(stack -> IDisplayModifierRecipe.withModifiers(stack, requirements, result, data -> data.putString(id, value))).collect(Collectors.toList());
    }
    return toolWithModifier;
  }

  public static class Serializer extends AbstractModifierRecipe.Serializer<SwappableModifierRecipe> {
    @Override
    protected ModifierEntry readResult(JsonObject json) {
      JsonObject result = JSONUtils.getJsonObject(json, "result");
      return new ModifierEntry(ModifierEntry.deserializeModifier(result, "name"), 1);
    }

    @Override
    public SwappableModifierRecipe read(ResourceLocation id, JsonObject json, Ingredient toolRequirement, ModifierMatch requirements, String requirementsError, ModifierEntry result, int maxLevel, int upgradeSlots, int abilitySlots) {
      throw new UnsupportedOperationException();
    }

    @Override
    public SwappableModifierRecipe read(ResourceLocation id, PacketBuffer buffer, Ingredient toolRequirement, ModifierMatch requirements, String requirementsError, ModifierEntry result, int maxLevel, int upgradeSlots, int abilitySlots) {
      throw new UnsupportedOperationException();
    }

    @Override
    public SwappableModifierRecipe read(ResourceLocation id, JsonObject json, Ingredient toolRequirement, ModifierMatch requirements,
																				String requirementsError, ModifierEntry result, int maxLevel, @Nullable SlotCount slots) {
      List<SizedIngredient> ingredients = JsonHelper.parseList(json, "inputs", SizedIngredient::deserialize);
      String value = JSONUtils.getString(JSONUtils.getJsonObject(json, "result"), "value");
      return new SwappableModifierRecipe(id, ingredients, toolRequirement, requirements, requirementsError, result.getModifier(), value, slots);
    }

    @Override
    public SwappableModifierRecipe read(ResourceLocation id, PacketBuffer buffer, Ingredient toolRequirement, ModifierMatch requirements,
                                        String requirementsError, ModifierEntry result, int maxLevel, @Nullable SlotCount slots) {
      int size = buffer.readVarInt();
      ImmutableList.Builder<SizedIngredient> builder = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        builder.add(SizedIngredient.read(buffer));
      }
      String value = buffer.readString();
      return new SwappableModifierRecipe(id, builder.build(), toolRequirement, requirements, requirementsError, result.getModifier(), value, slots);
    }

    @Override
    protected void writeSafe(PacketBuffer buffer, SwappableModifierRecipe recipe) {
      super.writeSafe(buffer, recipe);
      buffer.writeVarInt(recipe.inputs.size());
      for (SizedIngredient ingredient : recipe.inputs) {
        ingredient.write(buffer);
      }
      buffer.writeString(recipe.value);
    }
  }
}
