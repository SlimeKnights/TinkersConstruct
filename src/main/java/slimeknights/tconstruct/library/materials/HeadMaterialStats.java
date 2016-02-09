package slimeknights.tconstruct.library.materials;


import com.google.common.collect.Lists;

import net.minecraft.util.EnumChatFormatting;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomFontColor;
import slimeknights.tconstruct.library.utils.HarvestLevels;

public class HeadMaterialStats extends AbstractMaterialStats {

  public final static String TYPE = "head";

  public final static String LOC_Durability   = "stat.tool.durability.name";
  public final static String LOC_MiningSpeed  = "stat.tool.miningspeed.name";
  public final static String LOC_Attack       = "stat.tool.attack.name";
  public final static String LOC_HarvestLevel = "stat.tool.harvestlevel.name";

  public final static String LOC_DurabilityDesc    = "stat.tool.durability.desc";
  public final static String LOC_MiningSpeedDesc   = "stat.tool.miningspeed.desc";
  public final static String LOC_AttackDesc        = "stat.tool.attack.desc";
  public final static String LOC_HarvestLevelDesc  = "stat.tool.harvestlevel.desc";

  public final int durability; // usually between 1 and 1000
  public final int harvestLevel; // see HarvestLevels class
  public final float attack; // usually between 0 and 10 (in 1/2 hearts, so divide by 2 for damage in hearts)
  public final float miningspeed; // usually between 1 and 10

  public HeadMaterialStats(int durability, float miningspeed, float attack, int harvestLevel) {
    super(TYPE);
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
    return formatDurability(durability, 1000, false);
  }

  public static String formatDurability(int durability, int ref, boolean showRef) {
    String refStr = "";
    if(showRef) {
      refStr = EnumChatFormatting.GRAY.toString() + "/" + CustomFontColor.valueToColorCode(1f) + df.format(ref);
    }
    return String.format("%s: %s%s%s",
                         Util.translate(LOC_Durability),
                         CustomFontColor.valueToColorCode((float)durability / (float)ref),
                         df.format(durability),
                         refStr)
           + EnumChatFormatting.RESET;
  }

  public static String formatHarvestLevel(int level) {
    return String.format("%s: %s", Util.translate(LOC_HarvestLevel), HarvestLevels.getHarvestLevelName(level)) + EnumChatFormatting.RESET;
  }

  public static String formatMiningSpeed(float speed) {
    return String.format("%s: %s%s", Util.translate(LOC_MiningSpeed), CustomFontColor
        .valueToColorCode(speed / 10f), df.format(speed)) + EnumChatFormatting.RESET;
  }

  public static String formatAttack(float attack) {
    return String.format("%s: %s%s", Util.translate(LOC_Attack), CustomFontColor
        .valueToColorCode(attack / 10f), df.format(attack)) + EnumChatFormatting.RESET;
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
