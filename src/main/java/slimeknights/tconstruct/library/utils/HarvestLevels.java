package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Maps;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.tools.data.MaterialIds;

import java.util.Map;

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
    harvestLevelNames.put(STONE, MaterialRegistry.getMaterial(MaterialIds.stone).getEncodedTextColor() + new TranslationTextComponent("ui.mining_level.stone").getFormattedText());
    harvestLevelNames.put(IRON, MaterialRegistry.getMaterial(MaterialIds.iron).getEncodedTextColor() + new TranslationTextComponent("ui.mining_level.iron").getFormattedText());
    harvestLevelNames.put(DIAMOND, TextFormatting.AQUA + new TranslationTextComponent("ui.mining_level.diamond").getFormattedText());
    harvestLevelNames.put(OBSIDIAN, MaterialRegistry.getMaterial(MaterialIds.obsidian).getEncodedTextColor() + new TranslationTextComponent("ui.mining_level.obsidian").getFormattedText());
    harvestLevelNames.put(COBALT, MaterialRegistry.getMaterial(MaterialIds.cobalt).getEncodedTextColor() + new TranslationTextComponent("ui.mining_level.cobalt").getFormattedText());

    // custom names via resource pack..
    String base = "ui.mining_level.";
    int i = 0;
    while (Util.canTranslate(String.format("%s%d", base, i))) {
      harvestLevelNames.put(i, new TranslationTextComponent(String.format("%s%d", base, i)).getFormattedText());
      i++;
    }
  }

  static {
    init();
  }
}
