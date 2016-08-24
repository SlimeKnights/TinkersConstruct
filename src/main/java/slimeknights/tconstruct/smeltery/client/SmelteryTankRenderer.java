package slimeknights.tconstruct.smeltery.client;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.smeltery.ISmelteryTankHandler;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;

public class SmelteryTankRenderer<T extends TileEntity & ISmelteryTankHandler> extends TileEntitySpecialRenderer<T> {

  public void renderFluids(SmelteryTank tank, @Nonnull BlockPos pos, @Nonnull BlockPos tankMinPos, @Nonnull BlockPos tankMaxPos, double x, double y, double z) {
    if(tank == null) {
      return;
    }

    List<FluidStack> fluids = tank.getFluids();

    // calculate x/z parameters. they'll be the same for all liquids
    double x1 = tankMinPos.getX() - pos.getX();
    double y1 = tankMinPos.getY() - pos.getY();
    double z1 = tankMinPos.getZ() - pos.getZ();

    double x2 = tankMaxPos.getX() - pos.getX();
    double z2 = tankMaxPos.getZ() - pos.getZ();

    // empty smeltery :(
    if(!fluids.isEmpty()) {

      BlockPos minPos = new BlockPos(x1, y1, z1);
      BlockPos maxPos = new BlockPos(x2, y1, z2);

      // calc heights, we use mB capacities and then convert it over to blockheights during rendering
      int yd = 1 + Math.max(0, tankMaxPos.getY() - tankMinPos.getY());
      // one block height = 1000 mb
      int[] heights = calcLiquidHeights(fluids, tank.getCapacity(), yd * 1000 - (int) (RenderUtil.FLUID_OFFSET * 2000d), 100);

      double curY = RenderUtil.FLUID_OFFSET;
      // rendering time
      for(int i = 0; i < fluids.size(); i++) {
        double h = (double) heights[i] / 1000d;
        // minpos as start instead of smeltery.pos because we want to use the lighting inside the smeltery
        RenderUtil.renderStackedFluidCuboid(fluids.get(i), x, y, z, tankMinPos, minPos, maxPos, curY, curY + h);
        curY += h;
      }
    }
  }

  /**
   * calculate the rendering heights for all the liquids
   *
   * @param liquids  The liquids
   * @param capacity Max capacity of smeltery, to calculate how much height one liquid takes up
   * @param height   Maximum height, basically represents how much height full capacity is
   * @param min      Minimum amount of height for a fluid. A fluid can never have less than this value height returned
   * @return Array with heights corresponding to input-list liquids
   */
  public static int[] calcLiquidHeights(List<FluidStack> liquids, int capacity, int height, int min) {
    int fluidHeights[] = new int[liquids.size()];

    if(liquids.size() > 0) {

      for(int i = 0; i < liquids.size(); i++) {
        FluidStack liquid = liquids.get(i);

        float h = (float) liquid.amount / (float) capacity;
        fluidHeights[i] = Math.max(min, (int) Math.ceil(h * (float) height));
      }

      // check if we have enough height to render everything
      int sum = 0;
      do {
        sum = 0;
        int biggest = -1;
        int m = 0;
        for(int i = 0; i < fluidHeights.length; i++) {
          sum += fluidHeights[i];
          if(liquids.get(i).amount > biggest) {
            biggest = liquids.get(i).amount;
            m = i;
          }
        }

        // we can't get a result without going negative
        if(fluidHeights[m] == 0) {
          break;
        }

        // remove a pixel from the biggest one
        if(sum > height) {
          fluidHeights[m]--;
        }
      } while(sum > height);
    }

    return fluidHeights;
  }
}
