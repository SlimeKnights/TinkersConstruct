package slimeknights.tconstruct.library.materials;


import com.google.common.collect.Lists;

import java.util.List;

import slimeknights.tconstruct.library.Util;

public class ToolMaterialStats extends AbstractMaterialStats {

  public final static String TYPE = "tool";

  public final static String LOC_Durability    = "stat.durability.name";
  public final static String LOC_HandleQuality = "stat.quality.name";
  public final static String LOC_MiningSpeed   = "stat.miningspeed.name";
  public final static String LOC_HarvestLevel  = "stat.harvestlevel.name";
  public final static String LOC_Attack        = "stat.attack.name";
  public final static String LOC_Durability2    = "stat.durability.desc";
  public final static String LOC_HandleQuality2 = "stat.quality.desc";
  public final static String LOC_MiningSpeed2   = "stat.miningspeed.desc";
  public final static String LOC_HarvestLevel2  = "stat.harvestlevel.desc";
  public final static String LOC_Attack2        = "stat.attack.desc";

  public final int durability;
  public final float durabilityModifier;
  public final float attack;
  public final float miningspeed;
  public final int harvestLevel;

  public ToolMaterialStats(int harvestLevel, int durability, float durabilityModifier,
                           float miningspeed, float attack) {
    super(TYPE);
    this.durability = durability;
    this.durabilityModifier = durabilityModifier;
    this.attack = attack;
    this.miningspeed = miningspeed;
    this.harvestLevel = harvestLevel;
  }

  @Override
  public List<String> getLocalizedInfo() {
    List<String> info = Lists.newArrayList();

    info.add(String.format("%s: %d", Util.translate(LOC_Durability), durability));
    info.add(String.format("%s: %d", Util.translate(LOC_HarvestLevel), harvestLevel));
    info.add(String.format("%s: %.2f", Util.translate(LOC_MiningSpeed), miningspeed));
    info.add(String.format("%s: %.2f", Util.translate(LOC_Attack), attack));
    info.add(String.format("%s: %.2f", Util.translate(LOC_HandleQuality), durabilityModifier));

    return info;
  }

  @Override
  public List<String> getLocalizedDesc() {
    List<String> info = Lists.newArrayList();

    info.add(Util.translate(LOC_Durability2));
    info.add(Util.translate(LOC_HarvestLevel2));
    info.add(Util.translate(LOC_MiningSpeed2));
    info.add(Util.translate(LOC_Attack2));
    info.add(Util.translate(LOC_HandleQuality2));

    return info;
  }
}
