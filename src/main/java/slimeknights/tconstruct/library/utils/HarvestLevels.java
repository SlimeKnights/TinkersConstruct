package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Maps;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.tools.data.MaterialIds;

import java.util.Map;

public class HarvestLevels {

  public static final int WOOD = 0;
  public static final int STONE = 1;
  public static final int IRON = 2;
  public static final int DIAMOND = 3;
  public static final int NETHERITE = 4;
  public static final int MANYULLYN = 5;

  private HarvestLevels() {
  } // non-instantiable

  public static final Map<Integer, ITextComponent> harvestLevelNames = Maps.newHashMap();

  public static ITextComponent getHarvestLevelName(int num) {
    return harvestLevelNames.containsKey(num) ? harvestLevelNames.get(num) : new StringTextComponent(String.valueOf(num));
  }

  public static void init() {
    harvestLevelNames.put(WOOD, new TranslationTextComponent("ui.mining_level.wood").modifyStyle(style -> style.setColor(MaterialRegistry.getMaterial(MaterialIds.wood).getColor())));
    harvestLevelNames.put(STONE, new TranslationTextComponent("ui.mining_level.stone").modifyStyle(style -> style.setColor(MaterialRegistry.getMaterial(MaterialIds.stone).getColor())));
    harvestLevelNames.put(IRON, new TranslationTextComponent("ui.mining_level.iron").modifyStyle(style -> style.setColor(MaterialRegistry.getMaterial(MaterialIds.iron).getColor())));
    harvestLevelNames.put(DIAMOND, new TranslationTextComponent("ui.mining_level.diamond").mergeStyle(TextFormatting.AQUA));
    harvestLevelNames.put(NETHERITE, new TranslationTextComponent("ui.mining_level.netherite").mergeStyle(TextFormatting.DARK_GRAY));
    harvestLevelNames.put(MANYULLYN, new TranslationTextComponent("ui.mining_level.manyullyn").modifyStyle(style -> style.setColor(MaterialRegistry.getMaterial(MaterialIds.manyullyn).getColor())));

    // custom names via resource pack..
    String base = "ui.mining_level.";
    int i = 0;
    while (Util.canTranslate(String.format("%s%d", base, i))) {
      harvestLevelNames.put(i, new TranslationTextComponent(String.format("%s%d", base, i)));
      i++;
    }
  }

  static {
    init();
  }
}
