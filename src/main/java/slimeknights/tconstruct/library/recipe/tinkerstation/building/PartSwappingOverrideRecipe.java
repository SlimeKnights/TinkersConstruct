package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
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
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayToolModification;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.stream.IntStream;

/** Recipe for swapping a single material on a tool given a specific tool part. Notably allows swapping a part into a tool on an index other than the first. */
public class PartSwappingOverrideRecipe extends MaterialSwappingRecipe implements IMultiRecipe<IDisplayToolModification> {
  public static final RecordLoadable<PartSwappingOverrideRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(), TOOLS_FIELD, STACK_SIZE_FIELD,
    TinkerLoadables.TOOL_PART_ITEM.requiredField("part", r -> r.part),
    new IntArrayLoadable(IntLoadable.FROM_ZERO, ArrayLoadable.COMPACT, 10).requiredField("index", r -> r.indices),
    EXTRA_REQUIREMENTS_FIELD,
    PartSwappingOverrideRecipe::new);

  /** Part to match allowing the swap */
  private final IToolPart part;
  /** Options of indexes to set the material */
  private final int[] indices;

  protected PartSwappingOverrideRecipe(ResourceLocation id, Ingredient tools, int maxStackSize, IToolPart part, int[] indices, List<SizedIngredient> extraRequirements) {
    super(id, tools, maxStackSize, extraRequirements);
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
    // find the part and mark it as used
    BitSet used = ModifierRecipe.makeBitset(inv);
    boolean found = false;
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack input = inv.getInput(i);
      if (!input.isEmpty() && input.getItem() == part) {
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
      if (!stack.isEmpty() && stack.getItem() == part) {
        // ensure the part is valid
        MaterialVariantId partVariant = part.getMaterial(stack);
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

        return swapMaterial(inv, partVariant, index, MaterialCastingLookup.getItemCost(part));
      }
    }

    // no item found, should never happen
    return RecipeResult.pass();
  }

  @Override
  protected boolean shrinkPart(IMutableTinkerStationContainer inv, int index, ItemStack stack) {
    if (stack.getItem() == part) {
      inv.shrinkInput(index, 1);
      return true;
    }
    return false;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.fixedMaterialSwapping.get();
  }


  /* JEI */
  private List<IDisplayToolModification> multiRecipes;

  @Override
  public List<IDisplayToolModification> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      ItemStack[] tools = this.tools.getItems();
      // need 1 recipe per material, then one per index
      IMaterialRegistry registry = MaterialRegistry.getInstance();
      MaterialStatsId statType = part.getStatType();
      // create tools for each index - this is the same regardless of material
      List<List<ItemStack>> toolsWithoutMaterial = Arrays.stream(indices)
        .mapToObj(i -> Arrays.stream(tools)
          .map(stack -> withMaterial(stack.copy(), MaterialVariant.of(ToolBuildHandler.getRenderMaterial(i)), i))
          .toList())
        .toList();

      // create a recipe per material matching the part
      multiRecipes = registry.getVisibleMaterials().stream()
        .filter(material -> registry.getMaterialStats(material.getIdentifier(), statType).isPresent())
        .flatMap(material -> {
          MaterialId id = material.getIdentifier();
          Component title = MaterialTooltipCache.getDisplayName(id);
          MaterialVariant variant = MaterialVariant.of(material);
          // create a recipe per index you can swap
          return IntStream.range(0, indices.length).<IDisplayToolModification>mapToObj(i -> {
            // for each index, use first as the material on input, desired material on output
            int index = indices[i];
            List<ItemStack> withMaterial = Arrays.stream(tools).map(stack -> withMaterial(stack.copy(), variant, index)).toList();
            return new DisplayRecipe(title, index, List.of(part.withMaterialForDisplay(id)), toolsWithoutMaterial.get(i), withMaterial);
          });
        }).toList();
    }
    return multiRecipes;
  }
}
