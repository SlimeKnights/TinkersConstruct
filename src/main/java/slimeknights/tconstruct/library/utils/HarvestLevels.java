package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.tools.data.MaterialIds;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HarvestLevels {
  public static final int WOOD = 0;
  public static final int STONE = 1;
  public static final int IRON = 2;
  public static final int DIAMOND = 3;
  public static final int NETHERITE = 4;

  private static final Map<Integer, ITextComponent> harvestLevelNames = Maps.newHashMap();

  /**
   * Gets the harvest level name for the given level number
   * @param num  Level number
   * @return     Level name
   */
  public static ITextComponent getHarvestLevelName(int num) {
    return harvestLevelNames.containsKey(num) ? harvestLevelNames.get(num) : new StringTextComponent(String.valueOf(num));
  }

  /** Makes a translation key for the given name */
  private static IFormattableTextComponent makeLevelKey(String levelName) {
    return new TranslationTextComponent(Util.makeTranslationKey("stat", "mining_level." + levelName));
  }

  static {
    harvestLevelNames.put(WOOD, makeLevelKey("wood").modifyStyle(style -> style.setColor(MaterialRegistry.getMaterial(MaterialIds.wood).getColor())));
    harvestLevelNames.put(STONE, makeLevelKey("stone").modifyStyle(style -> style.setColor(MaterialRegistry.getMaterial(MaterialIds.stone).getColor())));
    harvestLevelNames.put(IRON, makeLevelKey("iron").modifyStyle(style -> style.setColor(MaterialRegistry.getMaterial(MaterialIds.iron).getColor())));
    harvestLevelNames.put(DIAMOND, makeLevelKey("diamond").mergeStyle(TextFormatting.AQUA));
    harvestLevelNames.put(NETHERITE, makeLevelKey("netherite").mergeStyle(TextFormatting.DARK_GRAY));
  }

  /**
   * Loads custom harvest level names from resource pack, requires languages to be loaded
   */
  public static void loadCustomNames() {
    String base = Util.makeTranslationKey("stat", "mining_level.");
    for (int i = 0; Util.canTranslate(base + i); i++) {
      harvestLevelNames.put(i, new TranslationTextComponent(base + i));
    }
  }
}
