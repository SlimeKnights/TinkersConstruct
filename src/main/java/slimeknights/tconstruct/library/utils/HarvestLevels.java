package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
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
  private static final Map<Integer, Component> harvestLevelNames = Maps.newHashMap();

  /** Makes a translation key for the given name */
  private static MutableComponent makeLevelKey(String levelName) {
    return new TranslatableComponent(TConstruct.makeTranslationKey("stat", "mining_level." + levelName));
  }

  /**
   * Loads the list of names from the lang file
   */
  private static void loadNames() {
    if (namesLoaded) return;
    namesLoaded = true;

    // default names: vanilla levels
    harvestLevelNames.put(WOOD, makeLevelKey("wood").withStyle(style -> style.withColor(TextColor.fromRgb(0x8e661b))));
    harvestLevelNames.put(STONE, makeLevelKey("stone").withStyle(style -> style.withColor(TextColor.fromRgb(0x999999))));
    harvestLevelNames.put(IRON, makeLevelKey("iron").withStyle(style -> style.withColor(TextColor.fromRgb(0xcacaca))));
    harvestLevelNames.put(DIAMOND, makeLevelKey("diamond").withStyle(ChatFormatting.AQUA));
    harvestLevelNames.put(NETHERITE, makeLevelKey("netherite").withStyle(ChatFormatting.DARK_GRAY));

    // load custom names, may override vanilla replacing with uncolored
    String base = TConstruct.makeTranslationKey("stat", "mining_level.");
    for (int i = 0; Util.canTranslate(base + i); i++) {
      harvestLevelNames.put(i, new TranslatableComponent(base + i));
    }
  }

  /**
   * Gets the harvest level name for the given level number
   * @param num  Level number
   * @return     Level name
   */
  public static Component getHarvestLevelName(int num) {
    loadNames();
    return harvestLevelNames.computeIfAbsent(num, n -> new TextComponent(Integer.toString(num)));
  }

  @Override
  public void onReloadSafe(ResourceManager resourceManager) {
    harvestLevelNames.clear();
    namesLoaded = false;
  }
}
