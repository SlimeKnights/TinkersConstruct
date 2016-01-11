package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Maps;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.Map;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.tools.TinkerMaterials;

public class HarvestLevels {

  public static final int STONE = 0;
  public static final int IRON = 1;
  public static final int DIAMOND = 2;
  public static final int OBSIDIAN = 3;
  public static final int COBALT = 4;

  private HarvestLevels() {
  } // non-instantiable

  public static final Map<Integer, String> harvestLevelNames = Maps.newHashMap();

  public static String getHarvestLevelName(int num) {
    return harvestLevelNames.containsKey(num) ? harvestLevelNames.get(num) : String.valueOf(num);
  }

  public static void init() {
    harvestLevelNames.put(STONE, TinkerMaterials.stone.getTextColor() + Util.translate("ui.mininglevel.stone"));
    harvestLevelNames.put(IRON, TinkerMaterials.iron.getTextColor() + Util.translate("ui.mininglevel.iron"));
    harvestLevelNames.put(DIAMOND, EnumChatFormatting.AQUA + Util.translate("ui.mininglevel.diamond"));
    harvestLevelNames.put(OBSIDIAN, TinkerMaterials.obsidian.getTextColor() + Util.translate("ui.mininglevel.obsidian"));
    harvestLevelNames.put(COBALT, TinkerMaterials.cobalt.getTextColor() + Util.translate("ui.mininglevel.cobalt"));
  }

  // initialization
  static {
    String base = "gui.mining";
    int i = 0;
    while(StatCollector.canTranslate(String.format("%s%d", base, i + 1))) {
      harvestLevelNames.put(i, StatCollector.translateToLocal(String.format("%s%d", base, i + 1)));
      i++;
    }
  }
}
