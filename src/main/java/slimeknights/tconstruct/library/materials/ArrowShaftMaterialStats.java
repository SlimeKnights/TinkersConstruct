package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableList;

import java.util.List;

import slimeknights.tconstruct.library.Util;

public class ArrowShaftMaterialStats extends AbstractMaterialStats {

  public final static String LOC_Multiplier = "stat.shaft.modifier.name";
  public final static String LOC_Ammo = "stat.shaft.ammo.name";

  public final static String LOC_MultiplierDesc = "stat.shaft.modifier.desc";
  public final static String LOC_AmmoDesc = "stat.shaft.ammo.desc";

  public final static String COLOR_Ammo = HeadMaterialStats.COLOR_Durability;
  public final static String COLOR_Modifier = HandleMaterialStats.COLOR_Modifier;

  public final float modifier;
  public final int bonusAmmo;

  public ArrowShaftMaterialStats(float modifier, int bonusAmmo) {
    super(MaterialTypes.SHAFT);
    this.bonusAmmo = bonusAmmo;
    this.modifier = modifier;
  }

  @Override
  public List<String> getLocalizedInfo() {
    return ImmutableList.of(formatModifier(modifier),
                            formatAmmo(bonusAmmo));
  }

  @Override
  public List<String> getLocalizedDesc() {
    return ImmutableList.of(Util.translate(LOC_MultiplierDesc),
                            Util.translate(LOC_AmmoDesc));
  }


  public static String formatModifier(float quality) {
    return formatNumber(LOC_Multiplier, COLOR_Modifier, quality);
  }

  public static String formatAmmo(int durability) {
    return formatNumber(LOC_Ammo, COLOR_Ammo, durability);
  }
}
