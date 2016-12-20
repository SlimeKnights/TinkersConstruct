package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableList;

import java.util.List;

import slimeknights.tconstruct.library.Util;

public class ExtraMaterialStats extends AbstractMaterialStats {

  @Deprecated
  public final static String TYPE = MaterialTypes.EXTRA;

  public final static String LOC_Durability = "stat.extra.durability.name";
  public final static String LOC_DurabilityDesc = "stat.extra.durability.desc";
  public final static String COLOR_Durability = HeadMaterialStats.COLOR_Durability;

  public final int extraDurability; // usually between 0 and 500

  public ExtraMaterialStats(int extraDurability) {
    super(MaterialTypes.EXTRA);
    this.extraDurability = extraDurability;
  }

  @Override
  public List<String> getLocalizedInfo() {
    return ImmutableList.of(formatDurability(extraDurability));
  }

  @Override
  public List<String> getLocalizedDesc() {
    return ImmutableList.of(Util.translate(LOC_DurabilityDesc));
  }

  public static String formatDurability(int durability) {
    return formatNumber(LOC_Durability, COLOR_Durability, durability);
  }

}
