package slimeknights.tconstruct.tables.recipe.part;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.tconstruct.tables.TinkerTables;

public class PartRecipe implements IRecipe<IInventory> {
  protected final ResourceLocation id;
  protected final String group;
  protected final ResourceLocation pattern;
  protected final int cost;
  protected final ItemStack output;

  public PartRecipe(ResourceLocation id, String group, ResourceLocation pattern, int cost, ItemStack output) {
    this.id = id;
    this.group = group;
    this.pattern = pattern;
    this.cost = cost;
    this.output = output;
  }

  @Override
  public IRecipeType<?> getType() {
    return TinkerTables.partRecipeType;
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
    return TinkerTables.partRecipeSerializer.get();
  }

  /**
   * Used to check if a recipe matches current crafting inventory
   */
  @Override
  public boolean matches(IInventory inv, World worldIn) {
    return inv.getStackInSlot(1).getItem() == TinkerTables.pattern.get();
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
    return this.output;
  }

  /**
   * Returns an Item that is the result of this recipe
   */
  @Override
  public ItemStack getCraftingResult(IInventory inv) {
    return this.output.copy();
  }

  public int getCost() {
    return this.cost;
  }

  public ResourceLocation getPattern() {
    return this.pattern;
  }

  public ItemStack getCraftingResult() {
    return this.output.copy();
  }
}
