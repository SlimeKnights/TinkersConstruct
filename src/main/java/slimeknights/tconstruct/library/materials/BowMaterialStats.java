package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableList;

import java.util.List;

import slimeknights.tconstruct.library.client.CustomFontColor;

public class BowMaterialStats extends AbstractMaterialStats {

  public final static String LOC_Drawspeed = "stat.bow.drawspeed.name";
  public final static String LOC_Range = "stat.bow.range.name";

  public final static String LOC_DrawspeedDesc = "stat.bow.drawspeed.desc";
  public final static String LOC_RangeDesc = "stat.bow.range.desc";

  public final static String COLOR_Drawspeed = CustomFontColor.encodeColor(205, 205, 205);
  public final static String COLOR_Range = CustomFontColor.encodeColor(150, 205, 205);

  public final float drawspeed;
  public final float range;

  public BowMaterialStats(float drawspeed, float range) {
    super(MaterialTypes.BOW);
    this.drawspeed = drawspeed;
    this.range = range;
  }

  @Override
  public List<String> getLocalizedInfo() {
    return ImmutableList.of("");
  }

  @Override
  public List<String> getLocalizedDesc() {
    return ImmutableList.of("");
  }
}
