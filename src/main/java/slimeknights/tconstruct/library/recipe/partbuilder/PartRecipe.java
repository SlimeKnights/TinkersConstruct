package slimeknights.tconstruct.library.recipe.partbuilder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.json.field.MergingField;
import slimeknights.tconstruct.library.json.field.MergingField.MissingMode;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.material.IMaterialValue;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Recipe to make a tool part from a material item in the part builder
 */
@RequiredArgsConstructor
public class PartRecipe implements IPartBuilderRecipe, IMultiRecipe<ItemPartRecipe> {
  public static final RecordLoadable<PartRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    LoadableRecipeSerializer.RECIPE_GROUP,
    Pattern.PARSER.requiredField("pattern", PartRecipe::getPattern),
    IngredientLoadable.DISALLOW_EMPTY.defaultField("pattern_item", DEFAULT_PATTERNS, r -> r.patternItem),
    IntLoadable.FROM_ONE.requiredField("cost", PartRecipe::getCost),
    new MergingField<>(TinkerLoadables.MATERIAL_ITEM.requiredField("item", r -> r.output), "result", MissingMode.DISALLOWED),
    new MergingField<>(IntLoadable.FROM_ONE.defaultField("count", 1, r -> r.outputCount), "result", MissingMode.CREATE),
    PartRecipe::new);

  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final Pattern pattern;
  protected final Ingredient patternItem;
  /** Recipe material cost */
  @Getter
  protected final int cost;
  /** Recipe result, used to fetch a material */
  protected final IMaterialItem output;
  /** Count for the recipe output */
  protected final int outputCount;

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.partRecipeSerializer.get();
  }

  @Override
  public boolean partialMatch(IPartBuilderContainer inv) {
    // first, must have a pattern
    if (!patternItem.test(inv.getPatternStack())) {
      return false;
    }
    // if there is a material item, it must have a valid material and be craftable
    ItemStack stack = inv.getStack();
    if (!stack.isEmpty()) {
      // no sense allowing if there is no change
      if (stack.getItem() == output) {
        return false;
      }
      IMaterialValue materialRecipe = inv.getMaterial();
      if (materialRecipe == null) {
        return false;
      }
      MaterialVariant material = materialRecipe.getMaterial();
      return material.get().isCraftable() && output.canUseMaterial(material.getId());
    }
    // no material item? return match in case we get one later
    return true;
  }

  /**
   * Checks if the recipe is valid for the given input. Assumes {@link #partialMatch(IPartBuilderContainer)} is true
   * @param inv    Inventory instance
   * @param world  World instance
   * @return  True if this recipe matches
   */
  @Override
  public boolean matches(IPartBuilderContainer inv, Level world) {
    // must have a material
    IMaterialValue materialRecipe = inv.getMaterial();
    if (materialRecipe != null) {
      // material must be craftable, usable in the item, and have a cost we can afford
      MaterialVariant material = materialRecipe.getMaterial();
      return material.get().isCraftable() && output.canUseMaterial(material.getId())
             && inv.getStack().getCount() >= materialRecipe.getItemsUsed(cost);
    }
    return false;
  }

  /** @deprecated use {@link #getRecipeOutput(MaterialVariantId)} */
  @Deprecated
  @Override
  public ItemStack getResultItem(RegistryAccess access) {
    return new ItemStack(output);
  }

  /**
   * Gets the output of the recipe for display
   * @param material  Material to use
   * @return  Output of the recipe
   */
  @SuppressWarnings("WeakerAccess")
  public ItemStack getRecipeOutput(MaterialVariantId material) {
    ItemStack stack = output.withMaterial(material);
    stack.setCount(outputCount);
    return stack;
  }

  @Override
  public ItemStack assemble(IPartBuilderContainer inv, RegistryAccess access) {
    MaterialVariant material = MaterialVariant.UNKNOWN;
    IMaterialValue materialRecipe = inv.getMaterial();
    if (materialRecipe != null) {
      material = materialRecipe.getMaterial();
    }
    return this.getRecipeOutput(material.getVariant());
  }

  /** Cache of recipes for display in JEI */
  @Nullable
  private List<ItemPartRecipe> multiRecipes;

  @Override
  public List<ItemPartRecipe> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      // TODO: recipe per variant instead of per material?
      multiRecipes = MaterialRegistry
        .getMaterials().stream()
        .filter(mat -> mat.isCraftable() && output.canUseMaterial(mat))
        .map(mat -> {
          MaterialId materialId = mat.getIdentifier();
          return new ItemPartRecipe(materialId, mat.getIdentifier(), pattern, patternItem, getCost(), ItemOutput.fromStack(output.withMaterial(materialId)));
        })
        .collect(Collectors.toList());
    }
    return multiRecipes;
  }
}
