package slimeknights.tconstruct.library.recipe.partbuilder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Recipe to make a tool part from a material item in the part builder
 */
@RequiredArgsConstructor
public class PartRecipe implements IPartBuilderRecipe, IMultiRecipe<ItemPartRecipe> {
  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final Pattern pattern;
  /** Recipe material cost */
  @Getter
  protected final int cost;
  /** Recipe result, used to fetch a material */
  protected final IMaterialItem output;
  /** Count for the recipe output */
  protected final int outputCount;

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerTables.partRecipeSerializer.get();
  }

  @Override
  public boolean partialMatch(IPartBuilderInventory inv) {
    // first, must have a pattern
    if (inv.getPatternStack().getItem() != TinkerTables.pattern.get()) {
      return false;
    }
    // if there is a material item, it must have a valid material and be craftable
    if (!inv.getStack().isEmpty()) {
      MaterialRecipe materialRecipe = inv.getMaterial();
      if (materialRecipe == null) {
        return false;
      }
      IMaterial material = materialRecipe.getMaterial();
      return material.isCraftable() && output.canUseMaterial(material);
    }
    // no material item? return match in case we get one later
    return true;
  }

  /**
   * Checks if the recipe is valid for the given input. Assumes {@link #partialMatch(IPartBuilderInventory)} is true
   * @param inv    Inventory instance
   * @param world  World instance
   * @return  True if this recipe matches
   */
  @Override
  public boolean matches(IPartBuilderInventory inv, World world) {
    // must have a material
    MaterialRecipe materialRecipe = inv.getMaterial();
    if (materialRecipe != null) {
      // material must be craftable, usable in the item, and have a cost we can afford
      IMaterial material = materialRecipe.getMaterial();
      return material.isCraftable() && output.canUseMaterial(material)
             && inv.getStack().getCount() >= materialRecipe.getItemsUsed(cost);
    }
    return false;
  }

  /** @deprecated use {@link #getRecipeOutput(IMaterial)} */
  @Deprecated
  @Override
  public ItemStack getRecipeOutput() {
    return new ItemStack(output);
  }

  /**
   * Gets the output of the recipe for display
   * @param material  Material to use
   * @return  Output of the recipe
   */
  @SuppressWarnings("WeakerAccess")
  public ItemStack getRecipeOutput(IMaterial material) {
    ItemStack stack = output.withMaterial(material);
    stack.setCount(outputCount);
    return stack;
  }

  @Override
  public ItemStack getCraftingResult(IPartBuilderInventory inv) {
    IMaterial material = IMaterial.UNKNOWN;
    MaterialRecipe materialRecipe = inv.getMaterial();
    if (materialRecipe != null) {
      material = materialRecipe.getMaterial();
    }
    return this.getRecipeOutput(material);
  }

  /** Cache of recipes for display in JEI */
  @Nullable
  private List<ItemPartRecipe> multiRecipes;

  @Override
  public List<ItemPartRecipe> getRecipes() {
    if (multiRecipes == null) {
      multiRecipes = MaterialRegistry
        .getMaterials().stream()
        .filter(mat -> mat.isCraftable() && output.canUseMaterial(mat))
        .map(mat -> new ItemPartRecipe(id, mat.getIdentifier(), pattern, getCost(), ItemOutput.fromStack(output.withMaterial(mat))))
        .collect(Collectors.toList());
    }
    return multiRecipes;
  }
}
