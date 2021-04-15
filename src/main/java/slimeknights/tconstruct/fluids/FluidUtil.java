package slimeknights.tconstruct.fluids;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
        public FluidAmount getTankCapacity(int tank) {
          return FluidAmount.of(100000, 1000);
        }

        @Override
        public boolean isFluidValid(int tank, FluidVolume stack) {
          return true;
        }

        @Override
        public FluidVolume fill(FluidVolume resource, Simulation action) {
          throw new RuntimeException("CRAB!"); // FIXME: PORT
        }

        @Override
        public FluidVolume drain(FluidVolume resource, Simulation action) {
          throw new RuntimeException("CRAB!"); // FIXME: PORT
        }

        @Override
        public FluidVolume drain(FluidAmount maxDrain, Simulation action) {
          throw new RuntimeException("CRAB!"); // FIXME: PORT
        }
      };
    }

  public static FluidVolume fromJson(JsonObject json) throws JsonSyntaxException {
    final FluidKey fluid = FluidKeys.get(Registry.FLUID.get(Identifier.tryParse(json.get("fluid").getAsString())));
    return fluid.withAmount(fluid == FluidKeys.EMPTY ? FluidAmount.ZERO : FluidAmount.of(json.get("amount").getAsInt(), 1000));
  }
}
