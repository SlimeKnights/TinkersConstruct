package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.util.LogicHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolPartsHook;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.layout.LayoutSlot;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This recipe is used for crafting a set of parts into a tool
 */

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ToolBuildingRecipe implements ITinkerStationRecipe {
  // placement of recipes in JEI
  public static final int X_OFFSET = -6;
  public static final int Y_OFFSET = -15;
  public static final int SLOT_SIZE = 18;
  /** Error for when the result ends up at stack size 0 due to weird tool traits */
  protected static final RecipeResult<LazyToolStack> NO_COUNT = RecipeResult.failure(TConstruct.makeTranslationKey("recipe", "tool_build.no_count"));
  // loadable fields
  protected static final LoadableField<IModifiable,ToolBuildingRecipe> RESULT_FIELD = TinkerLoadables.MODIFIABLE_ITEM.requiredField("result", r -> r.output);
  protected static final LoadableField<ResourceLocation,ToolBuildingRecipe> LAYOUT_FIELD = Loadables.RESOURCE_LOCATION.nullableField("slot_layout",  r -> r.layoutSlot);
  /** Loader instance */
  public static final RecordLoadable<ToolBuildingRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(), LoadableRecipeSerializer.RECIPE_GROUP, RESULT_FIELD,
    IntLoadable.FROM_ONE.defaultField("result_count", 1, true, r -> r.outputCount),
    LAYOUT_FIELD,
    IngredientLoadable.DISALLOW_EMPTY.list(0).defaultField("extra_requirements", List.of(), r -> r.ingredients),
    TinkerLoadables.TOOL_PART_ITEM.list(0).nullableField("parts_override", r -> r.parts),
    MaterialVariantId.LOADABLE.list(0).defaultField("extra_materials", List.of(), false, r -> r.materials),
    ToolBuildingRecipe::new);

  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  /** Tool result */
  @Getter
  protected final IModifiable output;
  /** Size of the result */
  protected final int outputCount;
  /** Layout for slots in JEI */
  @Nullable
  protected final ResourceLocation layoutSlot;
  /** List of input ingredients required in addition to the parts */
  protected final List<Ingredient> ingredients;
  /** If nonnull, uses these parts to craft the tool. If null, parts are pulled from the tool definition */
  @Nullable
  protected final List<IToolPart> parts;
  /** List of materials to apply after the parts */
  protected final List<MaterialVariantId> materials;
  // JEI cache
  protected List<LayoutSlot> layoutSlots;
  protected List<List<ItemStack>> allToolParts;
  protected List<ItemStack> displayOutput;

  @Deprecated(forRemoval = true)
  public ToolBuildingRecipe(ResourceLocation id, String group, IModifiable output, int outputCount, @Nullable ResourceLocation layoutSlot, List<Ingredient> ingredients) {
    this(id, group, output, outputCount, layoutSlot, ingredients, null, List.of());
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.toolBuildingRecipeSerializer.get();
  }

  /** Gets the tool parts for this tool */
  public List<IToolPart> getToolParts() {
    if (parts != null) {
      return parts;
    }
    return ToolPartsHook.parts(output.getToolDefinition());
  }

  /** Gets the additional recipe requirements beyond the tool parts */
  public List<Ingredient> getExtraRequirements() {
    return ingredients;
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level worldIn) {
    if (!inv.getTinkerableStack().isEmpty()) {
      return false;
    }
    List<IToolPart> parts = getToolParts();
    int partSize = parts.size();
    int requiredInputs = partSize + ingredients.size();
    int maxInputs = inv.getInputCount();
    // disallow if we have no inputs, or if we have too few slots
    if (requiredInputs == 0 || requiredInputs > maxInputs) {
      return false;
    }

    // if we are crafting the tool using a single non-part input, allow matching it in any slot
    if (requiredInputs == 1 && partSize == 0) {
      Ingredient ingredient = ingredients.get(0);
      boolean found = false;
      for (int i = 0; i < maxInputs; i++) {
        ItemStack stack = inv.getInput(i);
        if (!stack.isEmpty()) {
          // if we already found our input, or this stack doesn't match, recipe failed
          if (found || !ingredient.test(stack)) {
            return false;
          }
          found = true;
        }
      }
      return found;
    }

    // each part must match the given slot
    int i;
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
  public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
    int materialCount = ToolMaterialHook.stats(output.getToolDefinition()).size();
    // fill in materials
    List<MaterialVariant> materials = new ArrayList<>(materialCount);
    int parts = getToolParts().size();
    if (materialCount > 0) {
      int max = Math.min(parts, materialCount);
      // first n slots contain parts
      for (int i = 0; i < max; i++) {
        materials.add(MaterialVariant.of(IMaterialItem.getMaterialFromStack(inv.getInput(i))));
      }
      // add any material overrides after the parts, if we still have space
      max = Math.min(materialCount - parts, this.materials.size());
      for (int i = 0; i < max; i++) {
        materials.add(MaterialVariant.of(this.materials.get(i)));
      }
    }
    // create tool
    ToolStack tool = ToolStack.createTool(output.asItem(), output.getToolDefinition(), new MaterialNBT(materials));
    int count = outputCount;
    // if we have any parts set, run the count hook
    // no point running it if all materials are set through override/no materials, just set the recipe count in that case
    // note there is an edge case when you have a fixed material that adjusts count plus parts, not really a good solution for that case
    if (parts > 0) {
      // apply tool craft hook for remaining traits
      float newCount = count;
      for (ModifierEntry entry : tool.getModifiers()) {
        newCount = entry.getHook(ModifierHooks.CRAFT_COUNT).modifyCraftCount(tool, entry, newCount);
        if (newCount <= 0) {
          return NO_COUNT;
        }
      }
      count = (int) newCount;
    }

    // validate the tool, lets people have traits reject each other or do weird slot shenanigans
    Component error = tool.tryValidate();
    if (error != null) {
      return RecipeResult.failure(error);
    }
    return LazyToolStack.success(tool, Math.min(output.asItem().getMaxStackSize(), count));
  }


  /* JEI */

  /** Helper to determine if an anvil is required */
  public boolean requiresAnvil() {
    return getToolParts().size() + getExtraRequirements().size() >= 4;
  }

  /**
   * Gets the ID of the station slot layout for displaying this recipe.
   * Typically matches the output definition ID, but some tool recipes share a single layout.
   */
  public ResourceLocation getLayoutSlotId() {
    return Objects.requireNonNullElse(layoutSlot, output.getToolDefinition().getId());
  }

  /**
   * Gets all tool parts as and all its variants for JEI input lookups.
   */
  public List<List<ItemStack>> getAllToolParts() {
    if (allToolParts == null) {
      allToolParts = getToolParts().stream()
        .map(part -> MaterialRecipeCache.getAllVariants().stream()
          .filter(mat -> part.canUseMaterial(mat.getId()))
          .map(part::withMaterial)
          .toList())
        .toList();
    }
    return allToolParts;
  }

  /** Gets the layout slots so we know where go position item slots for guis */
  public List<LayoutSlot> getLayoutSlots() {
    if (layoutSlots == null) {
      layoutSlots = StationSlotLayoutLoader.getInstance().get(getLayoutSlotId()).getInputSlots();
      if (layoutSlots.isEmpty()) {
        // fallback to tinker station or anvil
        layoutSlots = StationSlotLayoutLoader.getInstance().get(TConstruct.getResource(requiresAnvil() ? "tinkers_anvil" : "tinker_station")).getInputSlots();
      }
      int missingSlots = getAllToolParts().size() + getExtraRequirements().size() - layoutSlots.size();
      // check layout slots if its too small
      if (missingSlots > 0) {
        TConstruct.LOG.error(String.format("Tool part count is greater than layout slot count for %s!", getId()));
        layoutSlots = new ArrayList<>(layoutSlots);
        for (int additionalSlot = 0; additionalSlot < missingSlots; additionalSlot++) {
          layoutSlots.add(new LayoutSlot(null, null, additionalSlot * SLOT_SIZE - X_OFFSET, -Y_OFFSET, null));
        }
      }
    }
    return layoutSlots;
  }

  /** Gets the result to display */
  public List<ItemStack> getDisplayOutput() {
    if (displayOutput == null) {
      // apply extra materials
      ItemStack result = null;
      if (!this.materials.isEmpty()) {
        // first, determine if we need them; our parts list applies first
        // if not, saves effort using the default render material
        int offset = getToolParts().size();
        int materialCount = ToolMaterialHook.stats(output.getToolDefinition()).size();
        if (offset < materialCount) {
          List<MaterialVariantId> list = new ArrayList<>(materialCount);
          // fill in all provided parts with render materials
          for (int i = 0; i < offset; i++) {
            list.add(ToolBuildHandler.getRenderMaterial(i));
          }
          // finally, if the original size was too small, append to the end
          int max = Math.min(materialCount - offset, materials.size());
          for (int i = 0; i < max; i++) {
            list.add(materials.get(i));
          }
          // if we have only real materials, make a proper tool
          if (offset == 0) {
            result = ToolBuildHandler.buildItemFromMaterials(output, new MaterialNBT(list.stream().map(MaterialVariant::of).toList()));
            result.setCount(outputCount);
          } else {
            // not a full list? mark it for display with just the materials on the end
            result = new MaterialIdNBT(list).updateStack(new ItemStack(output, outputCount));
            result.getOrCreateTag().putBoolean(TooltipUtil.KEY_DISPLAY, true);
          }
        }
      }
      // if the materials override did not make a tool successfully, make one now
      if (result == null) {
        result = output instanceof IModifiableDisplay modifiable ? modifiable.getRenderTool() : output.asItem().getDefaultInstance();
        // apply output count
        if (outputCount > 1) {
          result = result.copyWithCount(outputCount);
        }
      }
      displayOutput = List.of(result);
    }
    return displayOutput;
  }


  /* Unused */

  @Deprecated
  @Override
  public ItemStack getResultItem(RegistryAccess access) {
    return new ItemStack(this.output);
  }

  @Deprecated
  @Override
  public ItemStack assemble(ITinkerStationContainer inv, RegistryAccess access) {
    return getValidatedResult(inv, access).getResult().getStack();
  }
}
