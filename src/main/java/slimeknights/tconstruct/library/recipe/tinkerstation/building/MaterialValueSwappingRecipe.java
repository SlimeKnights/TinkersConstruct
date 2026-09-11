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
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayToolModification;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
      ItemStack[] tools = this.tools.getItems();

      // filter materials list to just the ones we need for this recipe
      List<MaterialVariantId> materials = MaterialRecipeCache.getAllVariants().stream().filter(material::matches).toList();

      // inputs are the same regardless of tool or slot, so compute once
      List<ItemStack> inputs = new ArrayList<>();
      // each tool will have its own materials, but to save memory we just duplicate the stack multiple times
      record MaterialCount(MaterialVariant variant, int count) {}
      List<MaterialCount> materialCounts = new ArrayList<>();
      for (MaterialVariantId material : materials) {
        List<ItemStack> newStacks = MaterialRecipeCache.getRecipes(material).stream()
          .flatMap(recipe -> Arrays.stream(recipe.getIngredient().getItems())
            .map(stack -> stack.copyWithCount(recipe.getItemsUsed(cost)))).toList();
        inputs.addAll(newStacks);
        materialCounts.add(new MaterialCount(MaterialVariant.of(material), newStacks.size()));
      }
      List<ItemStack> finalInputs = List.copyOf(inputs);
      // final recipes: 1 per tool and 1 per part in the tool
      // for most usages, this will be just 1 recipe
      multiRecipes = Arrays.stream(tools).<IDisplayToolModification>flatMap(tool ->
        Arrays.stream(indices).filter(VALID_SLOT).mapToObj(i ->
          new DisplayRecipe(
            i, finalInputs, List.of(withMaterial(tool.copy(), i, MaterialVariant.of(ToolBuildHandler.getRenderMaterial(i)))),
            materialCounts.stream().flatMap(count -> {
              ItemStack withMaterial = withMaterial(tool.copy(), i, count.variant);
              return IntStream.range(0, count.count).mapToObj(j -> withMaterial);
            }).toList()
          )
        )
      ).toList();
    }
    return multiRecipes;
  }

  /** Recipe combining linked with material. */
  private class DisplayRecipe extends LinkedDisplayRecipe {
    public DisplayRecipe(int index, List<ItemStack> input, List<ItemStack> toolWithoutModifier, List<ItemStack> toolWithModifier) {
      super(index, input, toolWithoutModifier, toolWithModifier);
    }

    @Override
    public Component getTitle() {
      return MaterialDisplayRecipe.TITLE;
    }

    @Override
    public Component getTooltip() {
      return MaterialDisplayRecipe.TOOLTIP;
    }
  }
}
