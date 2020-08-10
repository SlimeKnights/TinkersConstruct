package slimeknights.tconstruct.library.recipe.casting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

import java.util.List;

/**
 * Casting recipe that takes a fluid and optional cast and outputs an item
 */
@AllArgsConstructor
public abstract class ItemCastingRecipe implements ICastingRecipe {
  @Getter
  protected final IRecipeType<?> type;
  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  /** 'cast' item for recipe (doesn't have to be an actual 'cast') */
  @Getter
  protected final Ingredient cast;
  @Getter // For JEI
  protected final FluidIngredient fluid;
  protected final ItemStack result;
  @Getter
  protected final int coolingTime;
  @Getter
  protected final boolean consumed;
  @Getter @Accessors(fluent = true)
  protected final boolean switchSlots;

  @Override
  public boolean matches(ICastingInventory inv, World worldIn) {
    return this.getCast().test(inv.getStack()) && this.fluid.test(inv.getFluid());
  }

  @Override
  public int getFluidAmount(ICastingInventory inv) {
    return this.fluid.getAmount(inv.getFluid());
  }

  @Override
  public ItemStack getRecipeOutput() {
    return this.result;
  }

  @Override
  public int getCoolingTime(ICastingInventory inv) {
    return this.coolingTime;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.from(Ingredient.EMPTY, this.cast);
  }

  /**
   * Gets a list of valid fluid inputs for this recipe, for display in JEI
   * @return  List of fluids
   */
  public List<FluidStack> getFluids() {
    return this.fluid.getFluids();
  }

  /** Subclass for basin recipes */
  public static class Basin extends ItemCastingRecipe {
    public Basin(ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, ItemStack result, int coolingTime, boolean consumed, boolean switchSlots) {
      super(RecipeTypes.CASTING_BASIN, id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinRecipeSerializer.get();
    }
  }

  /** Subclass for table recipes */
  public static class Table extends ItemCastingRecipe {
    public Table(ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, ItemStack result, int coolingTime, boolean consumed, boolean switchSlots) {
      super(RecipeTypes.CASTING_TABLE, id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.tableRecipeSerializer.get();
    }
  }
}
