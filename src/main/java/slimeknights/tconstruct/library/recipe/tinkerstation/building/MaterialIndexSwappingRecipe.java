package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.array.ArrayLoadable;
import slimeknights.mantle.data.loadable.array.IntArrayLoadable;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;

import java.util.BitSet;
import java.util.List;

/** Recipe that swaps materials on a tool based on a list of indices and dynamic detection of a material. */
public abstract class MaterialIndexSwappingRecipe extends MaterialSwappingRecipe {
  protected static final LoadableField<int[], MaterialIndexSwappingRecipe> INDICES_FIELD = new IntArrayLoadable(IntLoadable.FROM_ZERO, ArrayLoadable.COMPACT, 10).requiredField("index", r -> r.indices);

  /** Options of indexes to set the material */
  protected final int[] indices;

  protected MaterialIndexSwappingRecipe(ResourceLocation id, Ingredient tools, int maxStackSize, int[] indices, List<SizedIngredient> extraRequirements) {
    super(id, tools, maxStackSize, extraRequirements);
    this.indices = indices;
  }

  /** Checks if the given item stack is a material providing stack */
  protected abstract boolean isMaterial(ItemStack stack);

  /** Gets the material from the given stack */
  protected abstract MaterialVariantId getMaterial(ItemStack stack);

  /** Gets the amount to repair from this part, assuming its a repairable stat type. */
  protected abstract int getRepairValue();

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    ItemStack tinkerable = inv.getTinkerableStack();
    if (tinkerable.isEmpty() || !tools.test(tinkerable)) {
      return false;
    }
    // must be a valid material index
    List<MaterialStatsId> materials = ToolMaterialHook.stats(IModifiable.getToolDefinition(tinkerable.getItem()));
    if (indices[0] >= materials.size()) {
      return false;
    }
    // find the part and mark it as used
    BitSet used = ModifierRecipe.makeBitset(inv);
    boolean found = false;
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack input = inv.getInput(i);
      if (!input.isEmpty() && isMaterial(input)) {
        found = true;
        used.set(i);
        break;
      }
    }
    // if we found the part and all extra requirements, we match
    return found && ModifierRecipe.checkMatch(inv, extraRequirements, used);
  }

  @Override
  public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
    // copy the tool NBT to ensure the original tool is intact
    List<MaterialStatsId> materials = ToolMaterialHook.stats(inv.getTinkerable().getDefinition());

    // prevent part swapping on large tools in small tables
    if (materials.size() > inv.getInputCount()) {
      return TOO_MANY_PARTS;
    }

    // find the index to swap
    // actual part swap logic
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty() && isMaterial(stack)) {
        // ensure the part is valid
        MaterialVariantId partVariant = getMaterial(stack);
        if (partVariant.equals(MaterialId.UNKNOWN)) {
          return RecipeResult.pass();
        }

        // we already know the item is valid, no need to check again - we just wanted its index
        // though if the index is not in our indices list, use the first one
        int index = indices[0];
        if (i < materials.size()) {
          for (int j = 1; j < indices.length; j++) {
            if (indices[j] == i) {
              index = i;
              break;
            }
          }
        }

        // ensure this material is valid for the tool
        // may not be if the part you choose is not the tool part for this tool
        if (MaterialRegistry.getInstance().getMaterialStats(partVariant.getId(), materials.get(index)).isEmpty()) {
          return INVALID_MATERIAL;
        }

        return swapMaterial(inv, partVariant, index, getRepairValue());
      }
    }

    // no item found, should never happen
    return RecipeResult.pass();
  }

  @Override
  protected boolean shrinkPart(IMutableTinkerStationContainer inv, int index, ItemStack stack) {
    if (isMaterial(stack)) {
      inv.shrinkInput(index, 1);
      return true;
    }
    return false;
  }
}
