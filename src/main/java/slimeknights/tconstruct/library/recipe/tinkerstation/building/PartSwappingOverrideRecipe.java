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
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.List;

/** Recipe for swapping a single material on a tool given a specific tool part. Notably allows swapping a part into a tool on an index other than the first. */
public class PartSwappingOverrideRecipe extends MaterialSwappingRecipe {
  public static final RecordLoadable<PartSwappingOverrideRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(), TOOLS_FIELD, STACK_SIZE_FIELD,
    TinkerLoadables.TOOL_PART_ITEM.requiredField("part", r -> r.part),
    new IntArrayLoadable(IntLoadable.FROM_ZERO, ArrayLoadable.COMPACT, 10).requiredField("index", r -> r.indices),
    PartSwappingOverrideRecipe::new);

  /** Part to match allowing the swap */
  private final IToolPart part;
  /** Options of indexes to set the material */
  private final int[] indices;

  protected PartSwappingOverrideRecipe(ResourceLocation id, Ingredient tools, int maxStackSize, IToolPart part, int[] indices) {
    super(id, tools, maxStackSize);
    this.part = part;
    this.indices = indices;
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
        if (found || input.getItem() != part) {
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
        // ensure the part is valid
        MaterialVariantId partVariant = part.getMaterial(stack);
        if (partVariant.equals(IMaterial.UNKNOWN_ID)) {
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

        return swapMaterial(inv, partVariant, index, MaterialCastingLookup.getItemCost(part));
      }
    }

    // no item found, should never happen
    return RecipeResult.pass();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.fixedMaterialSwapping.get();
  }
}
