package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayToolModification;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/** Recipe swapping a tool material using another tool as input */
// dynamic notes: show the tool as a sacrifice only if it has no modifiers. Otherwise stick to modifications to the shown tool
  // no reason to not copy the tool for display
public class ToolMaterialSwappingRecipe extends MaterialSwappingRecipe implements IMultiRecipe<IDisplayToolModification> {
  protected static final RecipeResult<LazyToolStack> NO_MODIFIERS = RecipeResult.failure(TConstruct.makeTranslationKey("recipe", "part_swapping.no_modifiers"));
  public static final RecordLoadable<ToolMaterialSwappingRecipe> LOADER = RecordLoadable.create(ContextKey.ID.requiredField(), TOOLS_FIELD, STACK_SIZE_FIELD, EXTRA_REQUIREMENTS_FIELD, ToolMaterialSwappingRecipe::new);

  /** @apiNote Internal usage. To create see {@link slimeknights.tconstruct.tables.recipe.TinkerStationPartSwappingBuilder} */
  @Internal
  public ToolMaterialSwappingRecipe(ResourceLocation id, Ingredient tools, int maxStackSize, List<SizedIngredient> extraRequirements) {
    super(id, tools, maxStackSize, extraRequirements);
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    ItemStack tinkerable = inv.getTinkerableStack();
    if (tinkerable.isEmpty() || !tools.test(tinkerable)) {
      return false;
    }
    // get the list of materials, ensuring its valid
    List<MaterialStatsId> materials = ToolMaterialHook.stats(IModifiable.getToolDefinition(tinkerable.getItem()));
    if (materials.isEmpty()) {
      return false;
    }

    // find the tool we are using to replace materials
    BitSet used = ModifierRecipe.makeBitset(inv);
    boolean found = false;
    // don't bother checking higher than the max material index
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack input = inv.getInput(i);
      if (!input.isEmpty() && input.getItem() == tinkerable.getItem()) {
        found = true;
        used.set(i);
        break;
      }
    }
    // if we found the tool and all extra requirements, we match
    return found && ModifierRecipe.checkMatch(inv, extraRequirements, used);
  }

  @Override
  public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
    // copy the tool NBT to ensure the original tool is intact
    IToolStackView original = inv.getTinkerable();
    List<MaterialStatsId> materials = ToolMaterialHook.stats(original.getDefinition());
    // prevent part swapping on large tools in small tables
    if (materials.size() > inv.getInputCount()) {
      return TOO_MANY_PARTS;
    }

    // find the index to swap
    // actual part swap logic
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty() && stack.getItem() == original.getItem()) {
        IToolStackView sacrifice = ToolStack.from(stack);
        // ensure the sacrifice does not have any modifiers
        if (!sacrifice.getUpgrades().isEmpty()) {
          return NO_MODIFIERS;
        }
        int index = i;
        if (index >= materials.size()) {
          index = 0;
        }

        // swap the material
        boolean mayRepair = MaterialRegistry.getInstance().canRepair(materials.get(index));
        RecipeResult<LazyToolStack> result = swapMaterial(inv, sacrifice.getMaterial(index).getVariant(), index, 0, mayRepair);
        // if it's a repairable part, repair the tool based on the remaining durability of the sacrifice, plus a 5% bonus
        if (mayRepair && result.isSuccess()) {
          ToolStack tool = result.getResult().getTool();
          if (tool.getDamage() > 0) {
            ToolDamageUtil.repair(tool, sacrifice.getCurrentDurability() + tool.getStats().getInt(ToolStats.DURABILITY) / 20);
          }
        }
        return result;
      }
    }

    // no item found, should never happen
    return RecipeResult.pass();
  }

  @Override
  protected boolean shrinkPart(IMutableTinkerStationContainer inv, int index, ItemStack stack) {
    if (stack.getItem() == inv.getTinkerableStack().getItem()) {
      inv.shrinkInput(index, 1);
      return true;
    }
    return false;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.toolMaterialSwapping.get();
  }


  /* JEI */
  private List<IDisplayToolModification> multiRecipes;

  @Override
  public List<IDisplayToolModification> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      IMaterialRegistry registry = MaterialRegistry.getInstance();
      Collection<IMaterial> materials = registry.getVisibleMaterials();
      multiRecipes = Arrays.stream(tools.getItems()).flatMap(stack -> {
        ToolStack tool = ToolStack.from(stack);
        List<MaterialStatsId> stats = ToolMaterialHook.stats(tool.getDefinition());
        if (stats.size() > MAX_SLOTS) {
          return Stream.empty();
        }
        List<MaterialVariant> renderMaterials = IntStream.range(0, stats.size()).mapToObj(i -> MaterialVariant.of(ToolBuildHandler.getRenderMaterial(i))).toList();
        ToolStack displayTool = tool.copy();
        displayTool.setMaterials(MaterialNBT.of(renderMaterials.toArray(MaterialVariant[]::new)));
        return IntStream.range(0, stats.size()).<IDisplayToolModification>mapToObj(i -> {
          MaterialStatsId stat = stats.get(i);
          List<IMaterial> filtered = materials.stream().filter(mat -> registry.getMaterialStats(mat.getIdentifier(), stat).isPresent()).toList();
          ToolStack copy = tool.copy();
          setMaterials(copy, i, renderMaterials.get(i));
          return new DisplayRecipe(i,
            // one part per material
            filtered.stream().map(mat -> {
              ToolStack displayCopy = displayTool.copy();
              displayCopy.replaceMaterial(i, MaterialVariant.of(mat));
              return displayCopy.createStack();
            }).toList(),
            // single tool with the material to swap left blank
            List.of(copy.createStack().copy()),
            // one output per material
            filtered.stream().map(mat -> {
              copy.replaceMaterial(i, MaterialVariant.of(mat));
              return copy.createStack().copy();
            }).toList(),
            // material list
            filtered.stream().map(MaterialVariant::of).toList(), stat, displayTool
          );
        });
      }).toList();
    }
    return multiRecipes;
  }

  private class DisplayRecipe extends MaterialSwappingRecipe.LinkedDisplayRecipe {
    private static final Component TITLE = TConstruct.makeTranslation("recipe", "tool_material_swapping");
    private static final Component TOOLTIP = TConstruct.makeTranslation("recipe", "tool_material_swapping.tooltip");

    private final MaterialStatsId statType;
    private final ToolStack displayTool;
    public DisplayRecipe(int index, List<ItemStack> input, List<ItemStack> toolWithoutModifier, List<ItemStack> toolWithModifier, List<MaterialVariant> materials, MaterialStatsId statType, ToolStack displayTool) {
      super(index, input, toolWithoutModifier, toolWithModifier, materials);
      this.statType = statType;
      this.displayTool = displayTool;
    }

    @Override
    public Component getTitle() {
      return TITLE;
    }

    @Override
    public Component getTooltip() {
      return TOOLTIP;
    }


    /* Dynamic focus */

    @Override
    public List<ItemStack> getDisplayItems(int slot, ItemStack focus, boolean focusOutput) {
      if (slot == index && !focus.isEmpty() && (focusOutput || isTool(focus))) {
        MaterialVariantId material = MaterialIdNBT.getMaterial(focus, index);
        // if focusing on the output, display a sacrifice that would create this tool
        if (focusOutput) {
          if (statType.canUseMaterial(material.getId())) {
            ToolStack tool = displayTool.copy();
            tool.replaceMaterial(index, material);
            return List.of(tool.createStack());
          }
        } else if (ModifierUtil.hasUpgrades(focus)) {
          // we can only use the focus as the sacrifice if it has no upgrades. if it has upgrades, just filter to materials that don't match current material
          List<ItemStack> tools = indicesWithout(material).mapToObj(input::get).toList();
          if (!tools.isEmpty()) {
            return tools;
          }
        } else {
          // if the tool is a valid sacrifice, use it as the input anywhere that matches the material
          ItemStack copy = focus.copyWithCount(1);
          return IntStream.range(0, this.materials.size()).mapToObj(i -> this.materials.get(i).sameVariant(material) ? copy : input.get(i)).toList();
        }
      }
      return getDisplayItems(slot);
    }

    @Override
    public List<ItemStack> getToolWithoutModifier(ItemStack focus, boolean focusOutput) {
      // skip inputs that are not the tool
      if (!focus.isEmpty() && (focusOutput || isTool(focus))) {
        MaterialIdNBT materials = MaterialIdNBT.from(focus);
        // for inputs, display the focus itself provided we have at least 1 material that is not the current material
        if (!focusOutput) {
          MaterialVariantId material = materials.getMaterial(index);
          // if the focus has upgrades, it cannot be a sacrifice, so just use it as the input every time
          if (ModifierUtil.hasUpgrades(focus)) {
            if (indicesWithout(material).findAny().isPresent()) {
              return focusInput(focus);
            }
          } else {
            // if the focus lacks upgrades, it will be the sacrifice whenever the material matches and the input otherwise
            ItemStack copy = focus.copyWithCount(1);
            ItemStack withoutModifier = toolWithoutModifier.get(0);
            return this.materials.stream().map(variant -> variant.sameVariant(material) ? withoutModifier : copy).toList();
          }
        }
        // otherwise, display a generic render tool with all other materials copied
        return createDisplayStack(materials, focus);
      }
      return toolWithoutModifier;
    }

    @Override
    public List<ItemStack> getToolWithModifier(ItemStack focus, boolean focusOutput) {
      if (!focus.isEmpty()) {
        if (focusOutput) {
          return getOutputFocusWithModifier(statType, focus);
        } else if (isTool(focus)) {
          // if focusing on an input tool, output is the input with the new material
          ToolStack tool = ToolStack.copyFrom(focus);
          MaterialVariantId material = tool.getMaterial(index).getVariant();
          if (tool.getUpgrades().isEmpty()) {
            // tool lacking upgrades means it's a valid sacrifice. show a generic output with its material at the matching spot and a copy with new material otherwise
            return IntStream.range(0, this.materials.size()).mapToObj(i -> {
              MaterialVariant newMaterial = this.materials.get(i);
              if (newMaterial.sameVariant(material)) {
                return toolWithModifier.get(i);
              } else {
                return replaceMaterial(tool, newMaterial, focus);
              }
            }).toList();
          } else {
            return getInputFocusWithModifier(tool, material, focus);
          }
        }
      }
      return toolWithModifier;
    }
  }
}
