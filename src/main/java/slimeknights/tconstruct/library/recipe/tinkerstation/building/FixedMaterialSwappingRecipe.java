package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.array.ArrayLoadable;
import slimeknights.mantle.data.loadable.array.IntArrayLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.List;

/** Recipe for swapping a single material on a tool given a specific input ingredient. */
public class FixedMaterialSwappingRecipe extends MaterialSwappingRecipe {
  public static final RecordLoadable<FixedMaterialSwappingRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(), TOOLS_FIELD, STACK_SIZE_FIELD,
    SizedIngredient.LOADABLE.requiredField("ingredient", r -> r.ingredient),
    MaterialVariantId.LOADABLE.requiredField("material", r -> r.material),
    new IntArrayLoadable(IntLoadable.FROM_ZERO, ArrayLoadable.COMPACT, 10).requiredField("index", r -> r.indices),
    IntLoadable.FROM_ZERO.defaultField("repair_value", 0, false, r -> r.repairValue),
    FixedMaterialSwappingRecipe::new);

  /** Ingredient matching the input item */
  private final SizedIngredient ingredient;
  /** Material to set on the tool */
  private final MaterialVariantId material;
  /** Options of indexes to set the material */
  private final int[] indices;
  /** Amount this swapping repairs the tool */
  private final int repairValue;

  protected FixedMaterialSwappingRecipe(ResourceLocation id, Ingredient tools, int maxStackSize, SizedIngredient ingredient, MaterialVariantId material, int[] indices, int repairValue) {
    super(id, tools, maxStackSize);
    this.ingredient = ingredient;
    this.material = material;
    this.indices = indices;
    this.repairValue = repairValue;
  }

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
    // find the ingredient and nothing else
    boolean found = false;
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack input = inv.getInput(i);
      if (!input.isEmpty()) {
        // must match, but don't want multiple matches
        if (found || !ingredient.test(input)) {
          return false;
        }
        found = true;
      }
    }
    return found;
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
      if (!stack.isEmpty()) {
        // we already know the item is valid, no need to check again - we just wanted its index
        // though if the index is not in our indices list, use the first one
        int index = indices[0];
        for (int j = 1; j < indices.length; j++) {
          if (indices[j] == i) {
            index = i;
            break;
          }
        }

        // ensure this material is valid for the tool. If its not its really the recipes fault, but better a valid tool
        if (MaterialRegistry.getInstance().getMaterialStats(material.getId(), materials.get(index)).isEmpty()) {
          return INVALID_MATERIAL;
        }

        return swapMaterial(inv, material, index, repairValue);
      }
    }

    // no item found, should never happen
    return RecipeResult.pass();
  }

  @Override
  public void updateInputs(LazyToolStack result, IMutableTinkerStationContainer inv, boolean isServer) {
    // want to shrink the input by more
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty()) {
        inv.shrinkInput(i, ingredient.getAmountNeeded());
        break;
      }
    }
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.fixedMaterialSwapping.get();
  }
}
