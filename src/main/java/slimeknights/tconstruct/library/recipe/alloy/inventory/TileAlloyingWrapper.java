package slimeknights.tconstruct.library.recipe.alloy.inventory;

import lombok.Getter;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import slimeknights.tconstruct.smeltery.tileentity.AlloyTankTileEntity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TileAlloyingWrapper implements IAlloyInventory {
  private final AlloyTankTileEntity tile;
  private Fluid fluid;
  @Getter
  private Map<Direction,WeakReference<IFluidTank>> tanks;

  public TileAlloyingWrapper(AlloyTankTileEntity tile, Fluid fluid) {
    this.tile = tile;
    this.fluid = fluid;
    this.tanks = Collections.emptyMap();
  }

  /**
   * Checks if this reference is still valid
   * @return  False if any stored tank is removed
   */
  public boolean isValid() {
    for (Map.Entry<Direction,WeakReference<IFluidTank>> tank : tanks.entrySet()) {
      if (tank.getValue().get() == null) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Fluid getFluid() {
    return fluid;
  }

  @Override
  public List<FluidStack> getFluidStacks() {
    List<FluidStack> fluidStacks = new ArrayList<>();
    for (Map.Entry<Direction,WeakReference<IFluidTank>> entry : tanks.entrySet()) {
      IFluidTank tank = entry.getValue().get();
      if (tank != null && tank.getFluid() != FluidStack.EMPTY) {
        fluidStacks.add(tank.getFluid());
      }
    }
    return fluidStacks;
  }

  public void addTank(Direction direction, IFluidTank tank) {
    tanks.put(direction, new WeakReference<>(tank));
  }
}
