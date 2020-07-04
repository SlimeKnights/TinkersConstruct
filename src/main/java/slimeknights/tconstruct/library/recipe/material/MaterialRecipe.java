package slimeknights.tconstruct.library.recipe.material;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.inventory.ISingleItemInventory;
import slimeknights.tconstruct.tables.TinkerTables;

@AllArgsConstructor
public class MaterialRecipe implements IRecipe<ISingleItemInventory> {
  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  protected final Ingredient ingredient;
  /** Amount of material this recipe returns */
  @Getter
  protected final int value;
  /** Amount of input items needed to craft this material */
  @Getter
  protected final int needed;
  /** Material returned by this recipe */
  @Getter
  // TODO: why store the ID and not the material?
  protected final MaterialId materialId;

  /* Basic */

  @Override
  public IRecipeType<?> getType() {
    return RecipeTypes.MATERIAL;
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(TinkerTables.partBuilder);
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerTables.materialRecipeSerializer.get();
  }

  /* Material methods */

  @Override
  public boolean matches(ISingleItemInventory inv, World worldIn) {
    return this.ingredient.test(inv.getStack());
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.from(ingredient);
  }

  /**
   * Returns a material instance for this recipe
   * @return  Material for the recipe
   */
  public IMaterial getMaterial() {
    return MaterialRegistry.getInstance().getMaterial(this.materialId);
  }


  /**
   * Gets the amount of material present in the inventory as a float for display
   * @param inv  Inventory reference
   * @return  Number of material present as a float
   */
  public float getMaterialValue(ISingleItemInventory inv) {
    return inv.getStack().getCount() * this.value / (float)this.needed;
  }

  /**
   * Gets the number of items in order to craft a material with the given cost
   * @param itemCost  Cost of the item being crafted
   * @return  Number of the input to consume
   */
  public int getItemsUsed(int itemCost) {
    int needed = itemCost * this.needed;
    int cost = needed / this.value;
    if (needed % this.value != 0) {
      cost++;
    }
    return cost;
  }

  /**
   * Gets the number of leftover material from crafting a part with this material
   * @param itemCost  Cost of the item being crafted
   * @return  Number of input to consume
   */
  public int getRemainder(int itemCost) {
    return itemCost * this.needed % this.value;
  }

  /*
   * Required methods
   */
  @Override
  public boolean canFit(int width, int height) {
    return true;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack getCraftingResult(ISingleItemInventory inv) {
    return ItemStack.EMPTY;
  }
}
