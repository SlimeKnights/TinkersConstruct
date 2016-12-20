package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Maps;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

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
    harvestLevelNames.put(DIAMOND, TextFormatting.AQUA + Util.translate("ui.mininglevel.diamond"));
    harvestLevelNames.put(OBSIDIAN, TinkerMaterials.obsidian.getTextColor() + Util.translate("ui.mininglevel.obsidian"));
    harvestLevelNames.put(COBALT, TinkerMaterials.cobalt.getTextColor() + Util.translate("ui.mininglevel.cobalt"));

    // custom names via resource pack.. deprecated
    String base = "gui.mining";
    int i = 0;
    while(I18n.canTranslate(String.format("%s%d", base, i))) {
      harvestLevelNames.put(i, I18n.translateToLocal(String.format("%s%d", base, i)));
      i++;
    }

    // and new
    base = "ui.mininglevel.";
    i = 0;
    while(I18n.canTranslate(String.format("%s%d", base, i))) {
      harvestLevelNames.put(i, I18n.translateToLocal(String.format("%s%d", base, i)));
      i++;
    }
  }
}
