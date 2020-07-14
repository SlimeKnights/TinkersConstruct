package slimeknights.tconstruct.library.recipe.casting;

import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

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
  @Getter
  protected final boolean consumed = true;
  protected final boolean switchSlots = false;
  @Getter
  protected final int coolingTime = 5;

  public ContainerFillingRecipe(IRecipeType<?> typeIn, ResourceLocation idIn, String groupIn, int fluidAmount, Item containerIn) {
    this.type = typeIn;
    this.id = idIn;
    this.group = groupIn;
    this.fluidAmount = fluidAmount;
    this.container = containerIn;
  }

  @Override
  public int getFluidAmount(ICastingInventory inv) {
    return this.fluidAmount;
  }

  @Override
  public boolean switchSlots() {
    return switchSlots;
  }

  @Override
  public int getCoolingTime(ICastingInventory inv) {
    return getCoolingTime();
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
    IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(output).orElse(null);
    fluidHandler.fill(new FluidStack(inv.getFluid(), this.fluidAmount), IFluidHandler.FluidAction.EXECUTE);
    return fluidHandler.getContainer();
  }

  public static class Basin extends ContainerFillingRecipe {
    public Basin(ResourceLocation idIn, String groupIn, int fluidAmount, Item containerIn) {
      super(RecipeTypes.CASTING_BASIN, idIn, groupIn, fluidAmount, containerIn);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinFillingRecipeSerializer.get();
    }
  }

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
