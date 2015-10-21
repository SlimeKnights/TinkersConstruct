package slimeknights.tconstruct.library.materials;


import com.google.common.collect.Lists;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

import java.text.DecimalFormat;
import java.util.List;

import slimeknights.tconstruct.library.client.CustomFontRenderer;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.utils.HarvestLevels;

public class ToolMaterialStats extends AbstractMaterialStats {

  public final static String TYPE = "tool";

  public final static String LOC_Durability   = "stat.durability.name";
  public final static String LOC_MiningSpeed  = "stat.miningspeed.name";
  public final static String LOC_Attack        = "stat.attack.name";
  public final static String LOC_Handle       = "stat.handle.name";
  public final static String LOC_Extra        = "stat.extra.name";
  public final static String LOC_HarvestLevel = "stat.harvestlevel.name";

  public final static String LOC_DurabilityDesc    = "stat.durability.desc";
  public final static String LOC_MiningSpeedDesc   = "stat.miningspeed.desc";
  public final static String LOC_AttackDesc        = "stat.attack.desc";
  public final static String LOC_HandleDesc       = "stat.handle.desc";
  public final static String LOC_ExtraDesc       = "stat.extra.desc";
  public final static String LOC_HarvestLevelDesc  = "stat.harvestlevel.desc";

  private static final DecimalFormat df = new DecimalFormat("#,###,###.##");
  private static final DecimalFormat dfPercent = new DecimalFormat("#%");

  public final int durability; // usually between 1 and 2000
  public final int harvestLevel; // see HarvestLevels class
  public final float attack; // usually between 0 and 10 (in 1/2 hearts, so divide by 2 for damage in hearts)
  public final float miningspeed; // usually between 1 and 10
  public final float handleQuality; // how good the material is for handles. 0.0 - 1.0
  public final float extraQuality; // how good the material is for secondary parts. 0.0 - 1.0

  public ToolMaterialStats(int durability, float miningspeed, float attack, float handleQuality, float extraQuality, int harvestLevel) {
    super(TYPE);
    this.durability = durability;
    this.miningspeed = miningspeed;
    this.attack = attack;
    this.handleQuality = MathHelper.clamp_float(handleQuality, 0f, 1f);
    this.extraQuality = MathHelper.clamp_float(extraQuality, 0f, 1f);
    this.harvestLevel = harvestLevel;
  }

  @Override
  public List<String> getLocalizedInfo() {
    List<String> info = Lists.newArrayList();

    info.add(formatDurability(durability));
    info.add(formatHarvestLevel(harvestLevel));
    info.add(formatMiningSpeed(miningspeed));
    info.add(formatAttack(attack));
    info.add(formatHandle(handleQuality));
    info.add(formatExtra(extraQuality));

    return info;
  }

  public static String formatDurability(int durability) {
    return formatDurability(durability, 1000);
  }

  public static String formatDurability(int durability, int ref) {
    return String.format("%s: %s%s", Util.translate(LOC_Durability), CustomFontRenderer.valueToColorCode((float)durability/(float)ref), df.format(durability)) + EnumChatFormatting.RESET;
  }

  public static String formatHarvestLevel(int level) {
    return String.format("%s: %s", Util.translate(LOC_HarvestLevel), HarvestLevels.getHarvestLevelName(level)) + EnumChatFormatting.RESET;
  }

  public static String formatMiningSpeed(float speed) {
    return String.format("%s: %s%s", Util.translate(LOC_MiningSpeed), CustomFontRenderer.valueToColorCode(speed/10f), df.format(speed)) + EnumChatFormatting.RESET;
  }

  public static String formatAttack(float attack) {
    return String.format("%s: %s%s", Util.translate(LOC_Attack), CustomFontRenderer.valueToColorCode(attack/10f), df.format(attack)) + EnumChatFormatting.RESET;
  }

  public static String formatHandle(float quality) {
    return String.format("%s: %s%s", Util.translate(LOC_Handle), CustomFontRenderer.valueToColorCode(quality), dfPercent.format(quality)) + EnumChatFormatting.RESET;
  }

  public static String formatExtra(float quality) {
    return String.format("%s: %s%s", Util.translate(LOC_Extra), CustomFontRenderer.valueToColorCode(quality), dfPercent.format(quality));
  }

  @Override
  public List<String> getLocalizedDesc() {
    List<String> info = Lists.newArrayList();

    info.add(Util.translate(LOC_DurabilityDesc));
    info.add(Util.translate(LOC_HarvestLevelDesc));
    info.add(Util.translate(LOC_MiningSpeedDesc));
    info.add(Util.translate(LOC_AttackDesc));
    info.add(Util.translate(LOC_HandleDesc));
    info.add(Util.translate(LOC_ExtraDesc));

    return info;
  }
}
