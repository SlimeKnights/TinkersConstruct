package slimeknights.tconstruct.library.materials;

import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.PartType;

public abstract class AbstractMaterialStats implements IMaterialStats {

  protected final PartType partType;

  public AbstractMaterialStats(PartType partType) {
    this.partType = partType;
  }

  @Override
  public PartType getIdentifier() {
    return partType;
  }

  @Override
  public String getLocalizedName() {
    return Util.translate("stat.%s.name", partType);
  }

  public static String formatNumber(String loc, String color, int number) {
    return formatNumber(loc, color, (float) number);
  }

  public static String formatNumber(String loc, String color, float number) {
    return String.format("%s: %s%s%s",
      Util.translate(loc),
      color,
      Util.df.format(number),
      TextFormatting.RESET);
  }

  public static String formatNumberPercent(String loc, String color, float number) {
    return String.format("%s: %s%s%s",
      Util.translate(loc),
      color,
      Util.dfPercent.format(number),
      TextFormatting.RESET);
  }
}
