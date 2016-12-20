package slimeknights.tconstruct.library.materials;


import com.google.common.collect.Lists;

import net.minecraft.util.text.TextFormatting;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomFontColor;
import slimeknights.tconstruct.library.utils.HarvestLevels;

public class HeadMaterialStats extends AbstractMaterialStats {

  @Deprecated
  public final static String TYPE = MaterialTypes.HEAD;

  public final static String LOC_Durability   = "stat.head.durability.name";
  public final static String LOC_MiningSpeed  = "stat.head.miningspeed.name";
  public final static String LOC_Attack       = "stat.head.attack.name";
  public final static String LOC_HarvestLevel = "stat.head.harvestlevel.name";

  public final static String LOC_DurabilityDesc    = "stat.head.durability.desc";
  public final static String LOC_MiningSpeedDesc   = "stat.head.miningspeed.desc";
  public final static String LOC_AttackDesc        = "stat.head.attack.desc";
  public final static String LOC_HarvestLevelDesc  = "stat.head.harvestlevel.desc";

  public final static String COLOR_Durability = CustomFontColor.valueToColorCode(1f);
  public final static String COLOR_Attack     = CustomFontColor.encodeColor(215, 100, 100);
  public final static String COLOR_Speed      = CustomFontColor.encodeColor(120, 160, 205);

  public final int durability; // usually between 1 and 1000
  public final int harvestLevel; // see HarvestLevels class
  public final float attack; // usually between 0 and 10 (in 1/2 hearts, so divide by 2 for damage in hearts)
  public final float miningspeed; // usually between 1 and 10

  public HeadMaterialStats(int durability, float miningspeed, float attack, int harvestLevel) {
    super(MaterialTypes.HEAD);
    this.durability = durability;
    this.miningspeed = miningspeed;
    this.attack = attack;
    this.harvestLevel = harvestLevel;
  }

  @Override
  public List<String> getLocalizedInfo() {
    List<String> info = Lists.newArrayList();

    info.add(formatDurability(durability));
    info.add(formatHarvestLevel(harvestLevel));
    info.add(formatMiningSpeed(miningspeed));
    info.add(formatAttack(attack));

    return info;
  }

  public static String formatDurability(int durability) {
    return formatNumber(LOC_Durability, COLOR_Durability, durability);
  }

  public static String formatDurability(int durability, int ref) {
    return String.format("%s: %s",
                         Util.translate(LOC_Durability),
                         CustomFontColor.formatPartialAmount(durability, ref))
           + TextFormatting.RESET;
  }

  public static String formatHarvestLevel(int level) {
    return String.format("%s: %s", Util.translate(LOC_HarvestLevel), HarvestLevels.getHarvestLevelName(level)) + TextFormatting.RESET;
  }

  public static String formatMiningSpeed(float speed) {
    return formatNumber(LOC_MiningSpeed, COLOR_Speed, speed);
  }

  public static String formatAttack(float attack) {
    return formatNumber(LOC_Attack, COLOR_Attack, attack);
  }

  @Override
  public List<String> getLocalizedDesc() {
    List<String> info = Lists.newArrayList();

    info.add(Util.translate(LOC_DurabilityDesc));
    info.add(Util.translate(LOC_HarvestLevelDesc));
    info.add(Util.translate(LOC_MiningSpeedDesc));
    info.add(Util.translate(LOC_AttackDesc));

    return info;
  }
}
