package slimeknights.tconstruct.library.materials.stats;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.Util;

import java.util.List;

/**
 * A simple material class without stats.
 * This class is meant to be extended with custom stats added to it for your use.
 */
public class BaseMaterialStats implements IMaterialStats {

  // needs to be resourceLocation to be easily deserializable
  private final ResourceLocation id;

  public BaseMaterialStats(MaterialStatsId identifier) {
    this.id = identifier;
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return new MaterialStatsId(id);
  }

  @Override
  public String getLocalizedName() {
    // todo
    return null;
  }

  @Override
  public List<String> getLocalizedInfo() {
    // todo
    return null;
  }

  @Override
  public List<String> getLocalizedDesc() {
    // todo
    return null;
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
