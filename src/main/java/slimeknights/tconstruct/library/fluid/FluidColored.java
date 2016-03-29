package slimeknights.tconstruct.library.fluid;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import slimeknights.tconstruct.library.Util;

public class FluidColored extends Fluid {

  public static ResourceLocation ICON_LiquidStill = Util.getResource("blocks/fluids/liquid");
  public static ResourceLocation ICON_LiquidFlowing = Util.getResource("blocks/fluids/liquid_flow");
  public static ResourceLocation ICON_MilkStill = Util.getResource("blocks/fluids/milk");
  public static ResourceLocation ICON_MilkFlowing = Util.getResource("blocks/fluids/milk_flow");
  public static ResourceLocation ICON_StoneStill = Util.getResource("blocks/fluids/liquid_stone");
  public static ResourceLocation ICON_StoneFlowing = Util.getResource("blocks/fluids/liquid_stone_flow");

  public final int color;

  public FluidColored(String fluidName, int color) {
    this(fluidName, color, ICON_LiquidStill, ICON_LiquidFlowing);
  }

  public FluidColored(String fluidName, int color, ResourceLocation still, ResourceLocation flowing) {
    super(fluidName, still, flowing);

    // make opaque if no alpha is set
    if(((color >> 24) & 0xFF) == 0) {
      color |= 0xFF << 24;
    }
    this.color = color;
  }


  @Override
  public int getColor() {
    return color;
  }

  @Override
  public String getLocalizedName(FluidStack stack) {
    String s = this.getUnlocalizedName();
    return s == null ? "" : I18n.translateToLocal(s + ".name");
  }
}
