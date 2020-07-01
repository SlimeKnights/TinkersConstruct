package slimeknights.tconstruct.library.recipe.material;

import net.minecraft.inventory.IInventory;
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
import slimeknights.tconstruct.tables.TinkerTables;

public class MaterialRecipe implements IRecipe<IInventory> {
  protected final ResourceLocation id;
  protected final String group;
  protected final Ingredient ingredient;
  protected final MaterialId materialId;
  protected final int value;
  protected final int needed;

  public MaterialRecipe(ResourceLocation id, String group, Ingredient ingredient, int value, int needed, MaterialId materialId) {
    this.id = id;
    this.group = group;
    this.ingredient = ingredient;
    this.materialId = materialId;
    this.value = value;
    this.needed = needed;
  }

  @Override
  public IRecipeType<?> getType() {
    return RecipeTypes.MATERIAL;
  }

  @Override
  public ResourceLocation getId() {
    return this.id;
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(TinkerTables.partBuilder);
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerTables.materialRecipeSerializer.get();
  }

  /**
   * Used to check if a recipe matches current crafting inventory
   */
  @Override
  public boolean matches(IInventory inv, World worldIn) {
    return this.ingredient.test(inv.getStackInSlot(0));
  }

  /**
   * Recipes with equal group are combined into one button in the recipe book
   */
  @Override
  public String getGroup() {
    return this.group;
  }

  /**
   * Used to determine if this recipe can fit in a grid of the given width/height
   */
  @Override
  public boolean canFit(int width, int height) {
    return true;
  }

  /**
   * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
   * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
   */
  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  /**
   * Returns an Item that is the result of this recipe
   */
  @Override
  public ItemStack getCraftingResult(IInventory inv) {
    return ItemStack.EMPTY;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    NonNullList<Ingredient> ingredients = NonNullList.create();
    ingredients.add(this.ingredient);
    return ingredients;
  }

  /**
   * Returns the material id for this recipe
   *
   * @return the material id
   */
  public MaterialId getMaterialId() {
    return this.materialId;
  }

  public IMaterial getMaterial() {
    return MaterialRegistry.getInstance().getMaterial(this.materialId);
  }

  public int getValue() {
    return this.value;
  }

  public int getNeeded() {
    return this.needed;
  }
}
