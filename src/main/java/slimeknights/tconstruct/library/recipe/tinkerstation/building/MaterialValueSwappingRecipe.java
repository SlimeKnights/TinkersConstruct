package slimeknights.tconstruct.library.recipe.tinkerstation.building;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayToolModification;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

/** Recipe for swapping materials on a tool based on material items. For best results, there needs to not be a repairable part that supports the same materials. */
public class MaterialValueSwappingRecipe extends MaterialIndexSwappingRecipe implements IMultiRecipe<IDisplayToolModification> {
  public static final RecordLoadable<MaterialValueSwappingRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(), TOOLS_FIELD, STACK_SIZE_FIELD,
    MaterialPredicate.LOADER.requiredField("material", r -> r.material),
    IntLoadable.FROM_ONE.requiredField("cost", r -> r.cost),
    INDICES_FIELD, EXTRA_REQUIREMENTS_FIELD,
    MaterialValueSwappingRecipe::new);

  /** Predicate matching materials to allow */
  private final IJsonPredicate<MaterialVariantId> material;
  /** Amount of material needed to swap the part */
  private final int cost;

  protected MaterialValueSwappingRecipe(ResourceLocation id, Ingredient tools, int maxStackSize, IJsonPredicate<MaterialVariantId> material, int cost, int[] indices, List<SizedIngredient> extraRequirements) {
    super(id, tools, maxStackSize, indices, extraRequirements);
    this.material = material;
    this.cost = cost;
  }

  @Override
  protected boolean isMaterial(ItemStack stack) {
    MaterialRecipe recipe = MaterialRecipeCache.findRecipe(stack);
    return recipe != MaterialRecipe.EMPTY && this.material.matches(recipe.getMaterial().getVariant()) && stack.getCount() >= recipe.getItemsUsed(cost);
  }

  @Override
  protected MaterialVariantId getMaterial(ItemStack stack) {
    return MaterialRecipeCache.findRecipe(stack).getMaterial().getVariant();
  }

  @Override
  protected int getRepairValue() {
    return cost;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.materialValueSwapping.get();
  }

  @Override
  protected boolean shrinkPart(IMutableTinkerStationContainer inv, int index, ItemStack stack) {
    MaterialRecipe recipe = MaterialRecipeCache.findRecipe(stack);
    // need to ensure the recipe is a valid material, for the sake of extra requirements
    if (recipe != MaterialRecipe.EMPTY && this.material.matches(recipe.getMaterial().getVariant())) {
      int used = recipe.getItemsUsed(cost);
      inv.shrinkInput(index, used);
      ItemStack leftover = recipe.getLeftover(cost);
      if (!leftover.isEmpty()) {
        inv.giveItem(leftover);
      }
    }
    return false;
  }


  /* JEI */
  private List<IDisplayToolModification> multiRecipes;

  @Override
  public List<IDisplayToolModification> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      // since each tool may have its own valid materials, we may need to filter this list again
      // however, we can avoid duplicating the stacks more than once since the cost is constant, along with apply the common filter
      record MaterialStacks(MaterialVariant variant, List<ItemStack> stacks) {}
      List<MaterialStacks> materialStacks = new ArrayList<>();
      for (MaterialVariantId material : MaterialRecipeCache.getAllVariants()) {
        if (this.material.matches(material)) {
          List<ItemStack> newStacks = MaterialRecipeCache.getRecipes(material).stream()
            .flatMap(recipe -> Arrays.stream(recipe.getIngredient().getItems())
              .map(stack -> stack.copyWithCount(recipe.getItemsUsed(cost)))).toList();
          materialStacks.add(new MaterialStacks(MaterialVariant.of(material), newStacks));
        }
      }
      // final recipes: 1 per tool and 1 per part in the tool
      // for most usages, this will be just 1 recipe
      multiRecipes = Arrays.stream(tools.getItems()).<IDisplayToolModification>flatMap(tool -> {
        List<MaterialStatsId> statTypes = ToolMaterialHook.stats(IModifiable.getToolDefinition(tool.getItem()));
        return Arrays.stream(indices).filter(VALID_SLOT).filter(i -> i < statTypes.size()).mapToObj(i -> {
          MaterialStatsId statType = statTypes.get(i);
          // filter the materials for this tool
          List<MaterialStacks> filtered = materialStacks.stream().filter(count -> statType.canUseMaterial(count.variant.getId())).toList();
          return new DisplayRecipe(
            i,
            filtered.stream().flatMap(count -> count.stacks.stream()).toList(),
            List.of(withMaterial(tool.copy(), i, MaterialVariant.of(ToolBuildHandler.getRenderMaterial(i)))),
            materialStacks.stream().flatMap(count -> {
              ItemStack withMaterial = withMaterial(tool.copy(), i, count.variant);
              return IntStream.range(0, count.stacks.size()).mapToObj(j -> withMaterial);
            }).toList(),
            filtered.stream().flatMap(count -> {
              MaterialVariant variant = count.variant;
              return IntStream.range(0, count.stacks.size()).mapToObj(j -> variant);
            }).toList()
          );
        });
      }).toList();
    }
    return multiRecipes;
  }

  /** Recipe combining linked with material. */
  private class DisplayRecipe extends LinkedDisplayRecipe {
    private final List<MaterialVariant> materials;
    public DisplayRecipe(int index, List<ItemStack> input, List<ItemStack> toolWithoutModifier, List<ItemStack> toolWithModifier, List<MaterialVariant> materials) {
      super(index, input, toolWithoutModifier, toolWithModifier);
      this.materials = materials;
    }

    @Override
    public Component getTitle() {
      return MATERIAL_TITLE;
    }

    @Override
    public Component getTooltip() {
      return MATERIAL_TOOLTIP;
    }


    /* Dynamic focus */

    @Override
    public List<ItemStack> getDisplayItems(int slot, ItemStack focus, boolean focusOutput) {
      if (slot == index && !focus.isEmpty() && (focusOutput || isTool(focus))) {
        MaterialVariantId material = MaterialIdNBT.from(focus).getMaterial(index);
        IntStream indices;
        if (focusOutput) {
          // if focusing on the output, filter to just materials that produce the output
          indices = IntStream.range(0, materials.size()).filter(i -> material.matchesVariant(materials.get(i)));
        } else {
          // if focusing on the input, and focus is a tool, display all parts that are not the original material
          indices = IntStream.range(0, materials.size()).filter(i -> !materials.get(i).sameVariant(material));
        }
        // if nothing matches the filter, just return the original list
        List<ItemStack> filtered = indices.mapToObj(input::get).toList();
        if (!filtered.isEmpty()) return filtered;
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
          if (this.materials.stream().anyMatch(newMaterial -> !newMaterial.sameVariant(material))) {
            return List.of(focus.copyWithCount(getMaxToolSize(focus)));
          }
        }
        // otherwise, display a generic render tool with all other materials copied
        return List.of(createDisplayStack(materials.replaceMaterial(index, ToolBuildHandler.getRenderMaterial(0)), focus.getItem()));
      }
      return toolWithoutModifier;
    }

    @Override
    public List<ItemStack> getToolWithModifier(ItemStack focus, boolean focusOutput) {
      if (!focus.isEmpty()) {
        if (focusOutput) {
          // find all matching materials to display. This may include material variants hence mapping instead of a single item
          MaterialIdNBT materials = MaterialIdNBT.from(focus);
          MaterialVariantId material = materials.getMaterial(index);
          Function<MaterialVariant,ItemStack> applyMaterial = newMaterial -> createDisplayStack(materials.replaceMaterial(index, newMaterial.getVariant()), focus.getItem());
          List<ItemStack> results = this.materials.stream().filter(material::matchesVariant).map(applyMaterial).toList();
          if (!results.isEmpty()) {
            return results;
          } else {
            // on the chance the result stack isn't usable, duplicate the rest of the materials as an animation over parts
            return this.materials.stream().map(applyMaterial).toList();
          }
        } else if (isTool(focus)) {
          // if focusing on an input tool, output is the input with the new material. need to filter our list of options to just new ones
          ToolStack tool = ToolStack.copyFrom(focus);
          MaterialVariantId material = tool.getMaterial(index).getVariant();
          List<ItemStack> results = materials.stream().filter(newMaterial -> !newMaterial.sameVariant(material)).map(newMaterial -> {
            tool.replaceMaterial(index, newMaterial);
            return tool.updateStack(focus.copyWithCount(maxStackSize(tool)), true);
          }).toList();
          if (!results.isEmpty()) {
            return results;
          } else {
            // create a new tool with the same materials for each material option
            return this.materials.stream().map(newMaterial -> createDisplayStack(tool, tool.getMaterials().replaceMaterial(index, newMaterial))).toList();
          }
        }
      }
      return toolWithModifier;
    }
  }
}
