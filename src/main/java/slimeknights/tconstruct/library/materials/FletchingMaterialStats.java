package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableList;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomFontColor;

public class FletchingMaterialStats extends AbstractMaterialStats {

  public final static String LOC_Accuracy = "stat.fletching.accuracy.name";
  public final static String LOC_Multiplier = "stat.fletching.modifier.name";

  public final static String LOC_AccuracyDesc = "stat.fletching.accuracy.desc";
  public final static String LOC_MultiplierDesc = "stat.fletching.modifier.desc";

  public final static String COLOR_Accuracy = CustomFontColor.encodeColor(205, 170, 205);
  public final static String COLOR_Modifier = HandleMaterialStats.COLOR_Modifier;

  public final float modifier;
  public final float accuracy;

  public FletchingMaterialStats(float accuracy, float modifier) {
    super(MaterialTypes.FLETCHING);
    this.accuracy = accuracy;
    this.modifier = modifier;
  }

  @Override
  public List<String> getLocalizedInfo() {
    return ImmutableList.of(formatModifier(modifier),
                            formatAccuracy(accuracy));
  }

  @Override
  public List<String> getLocalizedDesc() {
    return ImmutableList.of(Util.translate(LOC_MultiplierDesc),
                            Util.translate(LOC_AccuracyDesc));
  }


  public static String formatModifier(float quality) {
    return formatNumber(LOC_Multiplier, COLOR_Modifier, quality);
  }

  public static String formatAccuracy(float accuraccy) {
    return formatNumberPercent(LOC_Accuracy, COLOR_Accuracy, accuraccy);
  }
}
