package slimeknights.tconstruct.library.recipe.casting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

/**
 * Casting recipe that takes an arbitrary fluid for a given amount and fills a container
 */
@RequiredArgsConstructor
public abstract class ContainerFillingRecipe implements ICastingRecipe {
  @Getter
  protected final IRecipeType<?> type;
  @Getter
  protected final ResourceLocation id;
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

  /** @deprecated use {@link ICastingRecipe#getCraftingResult(ICastingInventory)}
   */
  @Override
  @Deprecated
  public ItemStack getRecipeOutput() {
    return new ItemStack(this.container);
  }

  @Override
  public ItemStack getCraftingResult(ICastingInventory inv) {
    ItemStack output = new ItemStack(container);
    return FluidUtil.getFluidHandler(output).map(handler -> {
      handler.fill(new FluidStack(inv.getFluid(), this.fluidAmount), FluidAction.EXECUTE);
      return handler.getContainer();
    }).orElse(ItemStack.EMPTY);
  }

  /** Basin implementation */
  public static class Basin extends ContainerFillingRecipe {
    public Basin(ResourceLocation idIn, String groupIn, int fluidAmount, Item containerIn) {
      super(RecipeTypes.CASTING_BASIN, idIn, groupIn, fluidAmount, containerIn);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinFillingRecipeSerializer.get();
    }
  }

  /** Table implementation */
  public static class Table extends ContainerFillingRecipe {

    public Table(ResourceLocation idIn, String groupIn, int fluidAmount, Item containerIn) {
      super(RecipeTypes.CASTING_TABLE, idIn, groupIn, fluidAmount, containerIn);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.tableFillingRecipeSerializer.get();
    }
  }
}
