package slimeknights.tconstruct.fluids;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraft.block.entity.BlockEntity;

/**
 * TODO: replacement for forges fluid util
 */
public class FluidUtil {
    public static IFluidHandler getFluidHandler(BlockEntity te) {
      return new IFluidHandler() {
        @Override
        public int getTanks() {
          return 1;
        }

        @Override
        public FluidVolume getFluidInTank(int tank) {
          return FluidVolume.create(FluidKeys.EMPTY, 0);
        }

        @Override
        public int getTankCapacity(int tank) {
          return 100000;
        }

        @Override
        public boolean isFluidValid(int tank, FluidVolume stack) {
          return true;
        }

        @Override
        public int fill(FluidVolume resource, Simulation action) {
          throw new RuntimeException("CRAB!"); // FIXME: PORT
        }

        @Override
        public FluidVolume drain(FluidVolume resource, Simulation action) {
          throw new RuntimeException("CRAB!"); // FIXME: PORT
        }

        @Override
        public FluidVolume drain(int maxDrain, Simulation action) {
          throw new RuntimeException("CRAB!"); // FIXME: PORT
        }
      };
    }
}
