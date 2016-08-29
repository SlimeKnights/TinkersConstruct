package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableList;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomFontColor;

public class BowMaterialStats extends AbstractMaterialStats {

  public final static String LOC_Drawspeed = "stat.bow.drawspeed.name";
  public final static String LOC_Range = "stat.bow.range.name";

  public final static String LOC_DrawspeedDesc = "stat.bow.drawspeed.desc";
  public final static String LOC_RangeDesc = "stat.bow.range.desc";

  public final static String COLOR_Drawspeed = CustomFontColor.encodeColor(225, 225, 225);
  public final static String COLOR_Range = CustomFontColor.encodeColor(150, 205, 205);

  public final float drawspeed;
  public final float range;

  public BowMaterialStats(float drawspeed, float range) {
    super(MaterialTypes.BOW);
    this.drawspeed = drawspeed;
    this.range = range;
  }

  public static String formatDrawspeed(float drawspeed) {
    return formatNumber(LOC_Drawspeed, COLOR_Drawspeed, drawspeed);
  }

  public static String formatRange(float range) {
    return formatNumber(LOC_Range, COLOR_Range, range);
  }

  @Override
  public List<String> getLocalizedInfo() {
    return ImmutableList.of(
        formatDrawspeed(drawspeed),
        formatRange(range)
    );
  }

  @Override
  public List<String> getLocalizedDesc() {
    return ImmutableList.of(
        Util.translate(LOC_DrawspeedDesc),
        Util.translate(LOC_RangeDesc)
    );
  }
}
