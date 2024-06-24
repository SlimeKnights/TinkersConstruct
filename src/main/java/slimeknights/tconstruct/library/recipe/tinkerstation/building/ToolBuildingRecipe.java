package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.util.LogicHelper;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolPartsHook;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.layout.LayoutSlot;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * This recipe is used for crafting a set of parts into a tool
 */

@AllArgsConstructor
public class ToolBuildingRecipe implements ITinkerStationRecipe {
  public static final RecordLoadable<ToolBuildingRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    LoadableRecipeSerializer.RECIPE_GROUP,
    TinkerLoadables.MODIFIABLE_ITEM.requiredField("result", r -> r.output),
    IntLoadable.FROM_ONE.defaultField("result_count", 1, true, r -> r.outputCount),
    Loadables.RESOURCE_LOCATION.nullableField("slot_layout",  r -> r.layoutSlot),
    IngredientLoadable.DISALLOW_EMPTY.list(0).defaultField("extra_requirements", List.of(), r -> r.ingredients),
    ToolBuildingRecipe::new);

  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final IModifiable output;
  protected final int outputCount;
  @Nullable
  protected ResourceLocation layoutSlot;
  protected final List<Ingredient> ingredients;

  /** @deprecated Use {@link #ToolBuildingRecipe(ResourceLocation, String, IModifiable, int, ResourceLocation, List)} */
  @Deprecated
  public ToolBuildingRecipe(ResourceLocation id, String group, IModifiable output, int outputCount, List<Ingredient> ingredients) {
    this(id, group, output, outputCount, null, ingredients);
  }

  /**
   * Station slot layout ids and tool definition ids are not always the same
   * layoutSlot specified in the tool recipe JSON is potentially null,
   * so we should fallback to the tool definition id
   * @return StationSlotLayout id
   */
  public ResourceLocation getLayoutSlotId() {
    return Objects.requireNonNullElse(layoutSlot, getOutput().getToolDefinition().getId());
  }

  /** Gets the layout slots so we know where go position item slots for guis */
  public List<LayoutSlot> getLayoutSlots() {
    return StationSlotLayoutLoader.getInstance().get(getLayoutSlotId()).getInputSlots();
  }

  /** Gets the tool parts for this tool */
  public List<IToolPart> getToolParts() {
    return ToolPartsHook.parts(getOutput().getToolDefinition());
  }

  /**
   * Gets all tool parts as and all its variants for JEI input lookups.
   */
  public List<List<ItemStack>> getAllToolParts() {
    return getToolParts().stream()
      .map(part -> MaterialRegistry.getInstance().getVisibleMaterials().stream()
        .filter(part::canUseMaterial)
        .map(mat -> part.withMaterial(mat.getIdentifier()))
        .toList())
      .toList();
  }

  /** Gets the additional recipe requirements beyond the tool parts */
  public List<Ingredient> getExtraRequirements() {
    return ingredients;
  }

  /** Helper to determine if an anvil is required */
  public boolean requiresAnvil() {
    return getToolParts().size() + getExtraRequirements().size() >= 4;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.toolBuildingRecipeSerializer.get();
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level worldIn) {
    if (!inv.getTinkerableStack().isEmpty()) {
      return false;
    }
    List<IToolPart> parts = getToolParts();
    int requiredInputs = parts.size() + ingredients.size();
    int maxInputs = inv.getInputCount();
    // disallow if we have no inputs, or if we have too few slots
    if (requiredInputs == 0 || requiredInputs > maxInputs) {
      return false;
    }
    // each part must match the given slot
    int i;
    int partSize = parts.size();
    for (i = 0; i < partSize; i++) {
      if (parts.get(i).asItem() != inv.getInput(i).getItem()) {
        return false;
      }
    }
    // remaining slots must match extra requirements
    for (; i < maxInputs; i++) {
      Ingredient ingredient = LogicHelper.getOrDefault(ingredients, i - partSize, Ingredient.EMPTY);
      if (!ingredient.test(inv.getInput(i))) {
        return false;
      }
    }

    return true;
  }

  @Override
  public ItemStack assemble(ITinkerStationContainer inv) {
    // first n slots contain parts
    List<MaterialVariant> materials = IntStream.range(0, ToolPartsHook.parts(output.getToolDefinition()).size())
                                               .mapToObj(i -> MaterialVariant.of(IMaterialItem.getMaterialFromStack(inv.getInput(i))))
                                               .toList();
    return ToolStack.createTool(output.asItem(), output.getToolDefinition(), new MaterialNBT(materials)).createStack(outputCount);
  }

  /** @deprecated Use {@link #assemble(ITinkerStationContainer)} */
  @Deprecated
  @Override
  public ItemStack getResultItem() {
    return new ItemStack(this.output);
  }
}
