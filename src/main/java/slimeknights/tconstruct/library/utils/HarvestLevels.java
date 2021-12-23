package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.data.ISafeManagerReloadListener;

import java.util.Map;

/**
 * Harvest level display names
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HarvestLevels implements ISafeManagerReloadListener {
  /** Instance for resource reloading */
  public static final HarvestLevels INSTANCE = new HarvestLevels();

  public static final int WOOD = 0;
  public static final int STONE = 1;
  public static final int IRON = 2;
  public static final int DIAMOND = 3;
  public static final int NETHERITE = 4;

  private static boolean namesLoaded = false;
  private static final Map<Integer, ITextComponent> harvestLevelNames = Maps.newHashMap();

  /** Makes a translation key for the given name */
  private static IFormattableTextComponent makeLevelKey(String levelName) {
    return new TranslationTextComponent(TConstruct.makeTranslationKey("stat", "mining_level." + levelName));
  }

  /**
   * Loads the list of names from the lang file
   */
  private static void loadNames() {
    if (namesLoaded) return;
    namesLoaded = true;

    // default names: vanilla levels
    harvestLevelNames.put(WOOD, makeLevelKey("wood").modifyStyle(style -> style.setColor(Color.fromInt(0x8e661b))));
    harvestLevelNames.put(STONE, makeLevelKey("stone").modifyStyle(style -> style.setColor(Color.fromInt(0x999999))));
    harvestLevelNames.put(IRON, makeLevelKey("iron").modifyStyle(style -> style.setColor(Color.fromInt(0xcacaca))));
    harvestLevelNames.put(DIAMOND, makeLevelKey("diamond").mergeStyle(TextFormatting.AQUA));
    harvestLevelNames.put(NETHERITE, makeLevelKey("netherite").mergeStyle(TextFormatting.DARK_GRAY));

    // load custom names, may override vanilla replacing with uncolored
    String base = TConstruct.makeTranslationKey("stat", "mining_level.");
    for (int i = 0; Util.canTranslate(base + i); i++) {
      harvestLevelNames.put(i, new TranslationTextComponent(base + i));
    }
  }

  /**
   * Gets the harvest level name for the given level number
   * @param num  Level number
   * @return     Level name
   */
  public static ITextComponent getHarvestLevelName(int num) {
    loadNames();
    return harvestLevelNames.computeIfAbsent(num, n -> new StringTextComponent(Integer.toString(num)));
  }

  @Override
  public void onReloadSafe(IResourceManager resourceManager) {
    harvestLevelNames.clear();
    namesLoaded = false;
  }
}
