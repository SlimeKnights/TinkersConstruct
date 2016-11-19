package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableList;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomFontColor;

public class BowMaterialStats extends AbstractMaterialStats {

  public final static String LOC_Drawspeed = "stat.bow.drawspeed.name";
  public final static String LOC_Range = "stat.bow.range.name";
  public final static String LOC_Damage = "stat.bow.damage.name";

  public final static String LOC_DrawspeedDesc = "stat.bow.drawspeed.desc";
  public final static String LOC_RangeDesc = "stat.bow.range.desc";
  public final static String LOC_DamageDesc = "stat.bow.damage.desc";

  public final static String COLOR_Drawspeed = CustomFontColor.encodeColor(128, 128, 128);
  public final static String COLOR_Range = CustomFontColor.encodeColor(140, 175, 175);
  public final static String COLOR_Damage = CustomFontColor.encodeColor(155, 80, 65);

  public final float drawspeed;
  public final float range;
  /**
   * Ok, here is where things get complicated.
   * Think about the bonus damage as the extra damage the arrow has because the force he was shot with was so great
   * Usually this is higher, the higher the range. But it can't scale directly because that leads to problems with how it interacts.
   * Think of the bonus damage as a flat damage-reward for using materials that are slower, but flexible, like metals.
   */
  public final float bonusDamage;

  public BowMaterialStats(float drawspeed, float range, float bonusDamage) {
    super(MaterialTypes.BOW);
    this.drawspeed = drawspeed;
    this.range = range;
    this.bonusDamage = bonusDamage;
  }

  public static String formatDrawspeed(float drawspeed) {
    return formatNumber(LOC_Drawspeed, COLOR_Drawspeed, drawspeed);
  }

  public static String formatRange(float range) {
    return formatNumber(LOC_Range, COLOR_Range, range);
  }

  public static String formatDamage(float damage) {
    return formatNumber(LOC_Damage, COLOR_Damage, damage);
  }

  @Override
  public List<String> getLocalizedInfo() {
    return ImmutableList.of(
        // "normalized" drawspeed
        formatDrawspeed(1f/drawspeed),
        formatRange(range),
        formatDamage(bonusDamage)
    );
  }

  @Override
  public List<String> getLocalizedDesc() {
    return ImmutableList.of(
        Util.translate(LOC_DrawspeedDesc),
        Util.translate(LOC_RangeDesc),
        Util.translate(LOC_DamageDesc)
    );
  }
}
