package slimeknights.tconstruct.library.materials;


import com.google.common.collect.Lists;

import java.text.DecimalFormat;
import java.util.List;

import codechicken.lib.math.MathHelper;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.utils.HarvestLevels;

public class ToolMaterialStats extends AbstractMaterialStats {

  public final static String TYPE = "tool";

  public final static String LOC_Durability    = "stat.durability.name";
  public final static String LOC_Quality       = "stat.quality.name";
  public final static String LOC_MiningSpeed   = "stat.miningspeed.name";
  public final static String LOC_HarvestLevel  = "stat.harvestlevel.name";
  public final static String LOC_Attack        = "stat.attack.name";
  public final static String LOC_Durability2    = "stat.durability.desc";
  public final static String LOC_Quality2       = "stat.quality.desc";
  public final static String LOC_MiningSpeed2   = "stat.miningspeed.desc";
  public final static String LOC_HarvestLevel2  = "stat.harvestlevel.desc";
  public final static String LOC_Attack2        = "stat.attack.desc";

  private static final DecimalFormat df = new DecimalFormat("#,###,###.##");
  private static final DecimalFormat dfPercent = new DecimalFormat("#%");

  public final int durability; // usually between 1 and 2000
  public final float quality; // how good the material is for secondary parts. 0.0 - 1.0
  public final float attack; // usually between 0 and 5 HEARTS
  public final float miningspeed; // usually between 1 and 10
  public final int harvestLevel; // see Harvestlevelname stuff

  public ToolMaterialStats(int harvestLevel, int durability, float quality,
                           float miningspeed, float attack) {
    super(TYPE);
    this.durability = durability;
    this.quality = (float) MathHelper.clip(quality, 0f, 1f);
    this.attack = attack;
    this.miningspeed = miningspeed;
    this.harvestLevel = harvestLevel;
  }

  @Override
  public List<String> getLocalizedInfo() {
    List<String> info = Lists.newArrayList();

    info.add(formatDurability(durability));
    info.add(formatHarvestLevel(harvestLevel));
    info.add(formatMiningSpeed(miningspeed));
    info.add(formatAttack(attack));
    info.add(formatQuality(quality));

    return info;
  }

  public static String formatDurability(int durability) {
    return String.format("%s: %s", Util.translate(LOC_Durability), df.format(durability));
  }

  public static String formatHarvestLevel(int level) {
    return String.format("%s: %s", Util.translate(LOC_HarvestLevel), HarvestLevels.getHarvestLevelName(level));
  }

  public static String formatMiningSpeed(float speed) {
    return String.format("%s: %s", Util.translate(LOC_MiningSpeed), df.format(speed));
  }

  public static String formatAttack(float attack) {
    return String.format("%s: %s", Util.translate(LOC_Attack), df.format(attack));
  }

  public static String formatQuality(float quality) {
    return String.format("%s: %s", Util.translate(LOC_Quality), dfPercent.format(quality));
  }

  @Override
  public List<String> getLocalizedDesc() {
    List<String> info = Lists.newArrayList();

    info.add(Util.translate(LOC_Durability2));
    info.add(Util.translate(LOC_HarvestLevel2));
    info.add(Util.translate(LOC_MiningSpeed2));
    info.add(Util.translate(LOC_Attack2));
    info.add(Util.translate(LOC_Quality2));

    return info;
  }
}
