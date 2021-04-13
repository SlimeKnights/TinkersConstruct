package slimeknights.tconstruct.library.recipe.casting.container;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

/**
 * Casting recipe that takes an arbitrary fluid for a given amount and fills a container
 */
@RequiredArgsConstructor
public abstract class ContainerFillingRecipe implements ICastingRecipe {
  @Getter
  protected final RecipeType<?> type;
  @Getter
  protected final Identifier id;
  @Getter
  protected final String group;
  @Getter
  protected final int fluidAmount;
  @Getter
  protected final Item container;

  @Override
  public int getFluidAmount(ICastingInventory inv) {
    return this.fluidAmount;
  }

  @Override
  public boolean isConsumed() {
    return true;
  }

  @Override
  public boolean switchSlots() {
    return false;
  }

  @Override
  public int getCoolingTime(ICastingInventory inv) {
    return 5;
  }

  @Override
  public boolean matches(ICastingInventory inv, World worldIn) {
    return inv.getStack().getItem() == this.container.asItem();
  }

  @Override
  @Deprecated
  public ItemStack getOutput() {
    return new ItemStack(this.container);
  }

//  @Override
//  public ItemStack getCraftingResult(ICastingInventory inv) {
//    ItemStack output = new ItemStack(container);
//    return FluidUtil.getFluidHandler(output).map(handler -> {
//      handler.fill(new FluidVolume(inv.getFluid(), this.fluidAmount), Simulation.EXECUTE);
//      return handler.getContainer();
//    }).orElse(ItemStack.EMPTY);
//  }

  /** Basin implementation */
  public static class Basin extends ContainerFillingRecipe {
    public Basin(Identifier idIn, String groupIn, int fluidAmount, Item containerIn) {
      super(RecipeTypes.CASTING_BASIN, idIn, groupIn, fluidAmount, containerIn);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinFillingRecipeSerializer;
    }
  }

  /** Table implementation */
  public static class Table extends ContainerFillingRecipe {

    public Table(Identifier idIn, String groupIn, int fluidAmount, Item containerIn) {
      super(RecipeTypes.CASTING_TABLE, idIn, groupIn, fluidAmount, containerIn);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.tableFillingRecipeSerializer;
    }
  }
}
