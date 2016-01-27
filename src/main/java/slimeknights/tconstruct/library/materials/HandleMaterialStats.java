package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.EnumChatFormatting;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomFontColor;

public class HandleMaterialStats extends AbstractMaterialStats {

  public final static String TYPE = "handle";

  public final static String LOC_Handle       = "stat.handle.name";
//  public final static String LOC_Durability   = ToolMaterialStats.LOC_Durability; //"stat.durability.name";

  public final static String LOC_HandleDesc       = "stat.handle.desc";

  public final float handleQuality; // how good the material is for handles. 0.0 - 1.0
  public final int durability; // usually between -500 and 500

  public HandleMaterialStats(float handleQuality, int durability) {
    super(TYPE);
    this.durability = durability;
    this.handleQuality = handleQuality;
  }

  @Override
  public List<String> getLocalizedInfo() {
    return ImmutableList.of(formatHandle(handleQuality),
                            HeadMaterialStats.formatDurability(durability));
  }

  @Override
  public List<String> getLocalizedDesc() {
    return ImmutableList.of(Util.translate(LOC_HandleDesc),
                            Util.translate(HeadMaterialStats.LOC_DurabilityDesc));
  }


  public static String formatHandle(float quality) {
    return String.format("%s: %s%s", Util.translate(LOC_Handle), CustomFontColor
        .valueToColorCode(quality), dfPercent.format(quality)) + EnumChatFormatting.RESET;
  }
}
