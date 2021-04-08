package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.ISafeManagerReloadListener;

import java.util.Map;

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
  private static final Map<Integer, Text> harvestLevelNames = Maps.newHashMap();

  /** Makes a translation key for the given name */
  private static MutableText makeLevelKey(String levelName) {
    return new TranslatableText(Util.makeTranslationKey("stat", "mining_level." + levelName));
  }

  /**
   * Loads the list of names from the lang file
   */
  private static void loadNames() {
    if (namesLoaded) return;
    namesLoaded = true;

    // default names: vanilla levels
    harvestLevelNames.put(WOOD, makeLevelKey("wood").styled(style -> style.withColor(TextColor.fromRgb(0x8e661b))));
    harvestLevelNames.put(STONE, makeLevelKey("stone").styled(style -> style.withColor(TextColor.fromRgb(0x999999))));
    harvestLevelNames.put(IRON, makeLevelKey("iron").styled(style -> style.withColor(TextColor.fromRgb(0xcacaca))));
    harvestLevelNames.put(DIAMOND, makeLevelKey("diamond").formatted(Formatting.AQUA));
    harvestLevelNames.put(NETHERITE, makeLevelKey("netherite").formatted(Formatting.DARK_GRAY));

    // load custom names, may override vanilla replacing with uncolored
    String base = Util.makeTranslationKey("stat", "mining_level.");
    for (int i = 0; Util.canTranslate(base + i); i++) {
      harvestLevelNames.put(i, new TranslatableText(base + i));
    }
  }

  /**
   * Gets the harvest level name for the given level number
   * @param num  Level number
   * @return     Level name
   */
  public static Text getHarvestLevelName(int num) {
    loadNames();
    return harvestLevelNames.computeIfAbsent(num, n -> new LiteralText(Integer.toString(num)));
  }

  @Override
  public void onReloadSafe(ResourceManager resourceManager) {
    harvestLevelNames.clear();
    namesLoaded = false;
  }
}
