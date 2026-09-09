package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayToolModification;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/** Recipe for swapping a single material on a tool given a specific tool part. Notably allows swapping a part into a tool on an index other than the first. */
public class PartSwappingOverrideRecipe extends MaterialIndexSwappingRecipe implements IMultiRecipe<IDisplayToolModification> {
  public static final RecordLoadable<PartSwappingOverrideRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(), TOOLS_FIELD, STACK_SIZE_FIELD,
    TinkerLoadables.TOOL_PART_ITEM.requiredField("part", r -> r.part),
    INDICES_FIELD, EXTRA_REQUIREMENTS_FIELD,
    PartSwappingOverrideRecipe::new);

  /** Part to match allowing the swap */
  private final IToolPart part;

  protected PartSwappingOverrideRecipe(ResourceLocation id, Ingredient tools, int maxStackSize, IToolPart part, int[] indices, List<SizedIngredient> extraRequirements) {
    super(id, tools, maxStackSize, indices, extraRequirements);
    this.part = part;
  }

  @Override
  protected boolean isMaterial(ItemStack stack) {
    return stack.getItem() == part;
  }

  @Override
  protected MaterialVariantId getMaterial(ItemStack stack) {
    return part.getMaterial(stack);
  }

  @Override
  protected int getRepairValue() {
    return MaterialCastingLookup.getItemCost(part);
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
      IMaterialRegistry registry = MaterialRegistry.getInstance();
      MaterialStatsId statType = part.getStatType();
      // since we know the part ahead of time, only need to filter materials once
      List<IMaterial> materials = registry.getVisibleMaterials().stream().filter(mat -> registry.getMaterialStats(mat.getIdentifier(), statType).isPresent()).toList();
      // create a recipe per tool, then per index, matching standard part swapping
      multiRecipes = Arrays.stream(this.tools.getItems()).flatMap(stack -> {
        ToolStack tool = ToolStack.from(stack);
        // JEI only shows up to 5 slots
        if (ToolMaterialHook.stats(tool.getDefinition()).size() > MAX_SLOTS) {
          return Stream.empty();
        }
        return Arrays.stream(indices).filter(VALID_SLOT).<IDisplayToolModification>mapToObj(i -> new LinkedDisplayRecipe(i,
          // one part per material
          materials.stream().map(mat -> part.withMaterialForDisplay(mat.getIdentifier())).toList(),
          // single tool with the material to swap left blank
          List.of(withMaterial(tool.copy(), i, MaterialVariant.of(ToolBuildHandler.getRenderMaterial(i)))),
          // one output per material
          materials.stream().map(mat -> withMaterial(tool.copy(), i, MaterialVariant.of(mat))).toList()
        ));
      }).toList();
    }
    return multiRecipes;
  }
}
