package slimeknights.tconstruct.library.fluid;

import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import slimeknights.tconstruct.library.Util;

public class FluidMoltenMetal extends Fluid {

  public static ResourceLocation ICON_Still = Util.getResource("blocks/fluids/molten_metal");
  public static ResourceLocation ICON_Flowing = Util.getResource("blocks/fluids/molten_metal_flow");

  public final int color;

  public FluidMoltenMetal(String fluidName, int color) {
    super(fluidName, ICON_Still, ICON_Flowing);
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
}
