package slimeknights.tconstruct.common.registration;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.FluidAttributes.Builder;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.ForgeFlowingFluid.Properties;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.object.FluidObject;

import java.util.function.Function;

public class FluidDeferredRegisterExtension extends FluidDeferredRegister {
  public FluidDeferredRegisterExtension(String modID) {
    super(modID);
  }

  @Override
  public <F extends ForgeFlowingFluid> FluidObject<F> register(String name, String tagName, Builder builder, Function<Properties, ? extends F> still, Function<Properties, ? extends F> flowing, Material material, int lightLevel) {
    return super.register(name, tagName, builder.luminosity(lightLevel), still, flowing, material, lightLevel);
  }
}
