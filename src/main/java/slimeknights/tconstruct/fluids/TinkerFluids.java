package slimeknights.tconstruct.fluids;

import net.minecraft.block.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.registration.FluidDeferredRegister;
import slimeknights.tconstruct.library.registration.object.FluidObject;

/**
 * Contains all fluids used throughout the mod
 */
public class TinkerFluids extends TinkerModule {
  private static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(TConstruct.modID);

  public static final FluidObject<ForgeFlowingFluid> blue_slime = FLUIDS.register("blue_slime", FluidAttributes.builder(FluidIcons.FLUID_STILL, FluidIcons.FLUID_FLOWING).color(0xef67f0f5).density(1500).viscosity(1500).temperature(310), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER);
  public static final FluidObject<ForgeFlowingFluid> purple_slime = FLUIDS.register("purple_slime", FluidAttributes.builder(FluidIcons.FLUID_STILL, FluidIcons.FLUID_FLOWING).color(0xefd236ff).density(1600).viscosity(1600).temperature(370), SlimeFluid.Source::new, SlimeFluid.Flowing::new,  Material.WATER);

  static final Logger log = Util.getLogger("tinker_fluids");

  public TinkerFluids() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    FLUIDS.register(modEventBus);
  }

  public static int applyAlphaIfNotPresent(int color) {
    if (((color >> 24) & 0xFF) == 0) {
      color |= 0xFF << 24;
    }

    return color;
  }
}
