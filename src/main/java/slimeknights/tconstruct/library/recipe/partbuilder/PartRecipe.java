package slimeknights.tconstruct.library.recipe.partbuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.Optional;

@AllArgsConstructor
public class PartRecipe implements IRecipe<IPartBuilderInventory> {
  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final ResourceLocation pattern;
  /** Recipe material cost */
  @Getter
  protected final int cost;
  /** Recipe result, used to fetch a material */
  protected final IMaterialItem output;
  /** Count for the recipe output */
  protected final int outputCount;

  @Override
  public IRecipeType<?> getType() {
    return RecipeTypes.PART_BUILDER;
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(TinkerTables.partBuilder);
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerTables.partRecipeSerializer.get();
  }

  /**
   * Checks if the recipe supports the given pattern, used to filter out the button list
   * @param inv  Inventory instance
   * @return  True if the recipe matches the given pattern
   */
  public boolean matchesPattern(IPartBuilderInventory inv) {
    return inv.getPatternStack().getItem() == TinkerTables.pattern.get();
  }

  /**
   * Checks if the recipe is valid for the given input. Assumes {@link #matchesPattern(IPartBuilderInventory)} is true
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

  /**
   * Gets the number of material items consumed by this recipe
   * @param inv  Crafting inventory
   * @return  Number of items consumed
   */
  public int getItemsUsed(IPartBuilderInventory inv) {
    return Optional.ofNullable(inv.getMaterial())
                   .map(mat -> mat.getItemsUsed(cost))
                   .orElse(1);
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
  public ItemStack getRecipeOutput(IMaterial material) {
    ItemStack stack = output.getItemstackWithMaterial(material);
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

  /* Required methods */
  @Override
  public boolean canFit(int width, int height) {
    return true;
  }
}
