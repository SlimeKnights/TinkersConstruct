package slimeknights.tconstruct.library.materials;

import net.minecraft.util.text.TextFormatting;

import slimeknights.tconstruct.library.Util;

public abstract class AbstractMaterialStats implements IMaterialStats {

  protected final String materialType;

  public AbstractMaterialStats(String materialType) {
    this.materialType = materialType;
  }

  @Override
  public String getIdentifier() {
    return materialType;
  }

  @Override
  public String getLocalizedName() {
    return Util.translate("stat.%s.name", materialType);
  }

  public static String formatNumber(String loc, String color, int number) {
    return formatNumber(loc, color, (float) number);
  }

  public static String formatNumber(String loc, String color, float number) {
    return String.format("%s: %s%s",
                         Util.translate(loc),
                         color,
                         Util.df.format(number))
           + TextFormatting.RESET;
  }

  public static String formatNumberPercent(String loc, String color, float number) {
    return String.format("%s: %s%s",
                         Util.translate(loc),
                         color,
                         Util.dfPercent.format(number))
           + TextFormatting.RESET;
  }
}
