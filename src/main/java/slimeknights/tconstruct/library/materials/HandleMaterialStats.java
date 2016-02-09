package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.EnumChatFormatting;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomFontColor;

public class HandleMaterialStats extends AbstractMaterialStats {

  public final static String TYPE = "handle";

  public final static String LOC_Multiplier = "stat.handle.multiplier.name";
  public final static String LOC_Durability = "stat.handle.durability.name";

  public final static String LOC_MultiplierDesc = "stat.handle.multiplier.desc";
  public final static String LOC_DurabilityDesc = "stat.handle.durability.desc";

  public final float multiplier; // how good the material is for handles. 0.0 - 1.0
  public final int durability; // usually between -500 and 500

  public HandleMaterialStats(float multiplier, int durability) {
    super(TYPE);
    this.durability = durability;
    this.multiplier = multiplier;
  }

  @Override
  public List<String> getLocalizedInfo() {
    return ImmutableList.of(formatHandle(multiplier),
                            formatDurability(durability));
  }

  @Override
  public List<String> getLocalizedDesc() {
    return ImmutableList.of(Util.translate(LOC_MultiplierDesc),
                            Util.translate(LOC_DurabilityDesc));
  }


  public static String formatHandle(float quality) {
    return String.format("%s: %s%s", Util.translate(LOC_Multiplier), CustomFontColor
        .valueToColorCode(quality), dfPercent.format(quality)) + EnumChatFormatting.RESET;
  }

  public static String formatDurability(int durability) {
    return String.format("%s: %s",
                         Util.translate(LOC_Durability),
                         df.format(durability))
           + EnumChatFormatting.RESET;
  }
}
