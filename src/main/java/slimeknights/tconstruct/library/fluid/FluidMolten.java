package slimeknights.tconstruct.library.fluid;

import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import slimeknights.tconstruct.library.Util;

public class FluidMolten extends Fluid {

  public static ResourceLocation ICON_MetalStill = Util.getResource("blocks/fluids/molten_metal");
  public static ResourceLocation ICON_MetalFlowing = Util.getResource("blocks/fluids/molten_metal_flow");
  public static ResourceLocation ICON_LiquidStill = Util.getResource("blocks/fluids/liquid");
  public static ResourceLocation ICON_LiquidFlowing = Util.getResource("blocks/fluids/liquid_flow");

  public final int color;

  public FluidMolten(String fluidName, int color, ResourceLocation still, ResourceLocation flow) {
    super(fluidName, still, flow);
    // make opaque if no alpha is set
    if(((color >> 24) & 0xFF) == 0) {
      color |= 0xFF << 24;
    }
    this.color = color;

    this.setDensity(2000); // thicker than a bowl of oatmeal
    this.setViscosity(10000); // sloooow moving
    this.setTemperature(1000); // not exactly lava, but still hot. Should depend on the material

    // rare by default
    setRarity(EnumRarity.RARE);
  }

  @Override
  public String getLocalizedName(FluidStack stack) {
    String s = this.getUnlocalizedName();
    return s == null ? "" : StatCollector.translateToLocal(s + ".name");
  }

  @Override
  public int getColor() {
    return color;
  }

  public static FluidMolten metal(String name, int color) {
    return new FluidMolten(name, color, ICON_MetalStill, ICON_MetalFlowing);
  }

  public static FluidMolten liquid(String name, int color) {
    return new FluidMolten(name, color, ICON_LiquidStill, ICON_LiquidFlowing);
  }
}
