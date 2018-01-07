package slimeknights.tconstruct.library.fluid;

import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;

import slimeknights.tconstruct.library.Util;

public class FluidMolten extends FluidColored {

  public static ResourceLocation ICON_MetalStill = Util.getResource("blocks/fluids/molten_metal");
  public static ResourceLocation ICON_MetalFlowing = Util.getResource("blocks/fluids/molten_metal_flow");

  public FluidMolten(String fluidName, int color) {
    this(fluidName, color, ICON_MetalStill, ICON_MetalFlowing);
  }

  public FluidMolten(String fluidName, int color, ResourceLocation still, ResourceLocation flow) {
    super(fluidName, color, still, flow);

    this.setDensity(2000); // thicker than a bowl of oatmeal
    this.setViscosity(10000); // sloooow moving
    this.setTemperature(1000); // not exactly lava, but still hot. Should depend on the material
    this.setLuminosity(10); // glowy by default!

    // rare by default
    setRarity(EnumRarity.UNCOMMON);
  }
}
