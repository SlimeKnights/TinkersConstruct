package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Maps;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.TierSortingRegistry;
import slimeknights.tconstruct.TConstruct;

import java.util.List;
import java.util.Map;

/**
 * Harvest level display names
 */
public class HarvestTiers {
  private HarvestTiers() {}

  private static boolean namesLoaded = false;
  private static final Map<Tier, Component> harvestLevelNames = Maps.newHashMap();

  /** Makes a translation key for the given name */
  private static MutableComponent makeLevelKey(Tier tier) {
    return new TranslatableComponent(TConstruct.makeTranslationKey("stat", Util.makeTranslationKey("harvest_tier", TierSortingRegistry.getName(tier))));
  }

  /**
   * Loads the list of names from the lang file
   */
  private static void loadNames() {
    if (namesLoaded) return;
    namesLoaded = true;

    // default names: vanilla levels (have colors)
    harvestLevelNames.put(Tiers.WOOD, makeLevelKey(Tiers.WOOD).withStyle(style -> style.withColor(TextColor.fromRgb(0x8e661b))));
    harvestLevelNames.put(Tiers.STONE, makeLevelKey(Tiers.STONE).withStyle(style -> style.withColor(TextColor.fromRgb(0x999999))));
    harvestLevelNames.put(Tiers.IRON, makeLevelKey(Tiers.IRON).withStyle(style -> style.withColor(TextColor.fromRgb(0xcacaca))));
    harvestLevelNames.put(Tiers.DIAMOND, makeLevelKey(Tiers.DIAMOND).withStyle(ChatFormatting.AQUA));
    harvestLevelNames.put(Tiers.NETHERITE, makeLevelKey(Tiers.NETHERITE).withStyle(ChatFormatting.DARK_GRAY));
    harvestLevelNames.put(Tiers.GOLD, makeLevelKey(Tiers.GOLD).withStyle(ChatFormatting.GOLD));
  }

  /**
   * Gets the harvest level name for the given level number
   * @param tier  Tier
   * @return  Level name
   */
  public static Component getName(Tier tier) {
    loadNames();
    return harvestLevelNames.computeIfAbsent(tier, n ->  makeLevelKey(tier));
  }

  /** Gets the larger of two tiers */
  public static Tier max(Tier a, Tier b) {
    List<Tier> sorted = TierSortingRegistry.getSortedTiers();
    // note indexOf returns -1 if the tier is missing, so the larger of an unsorted tier and a sorted one is the sorted one
    if (sorted.indexOf(b) > sorted.indexOf(a)) {
      return b;
    }
    return a;
  }

  /** Gets the smaller of two tiers */
  public static Tier min(Tier a, Tier b) {
    List<Tier> sorted = TierSortingRegistry.getSortedTiers();
    // note indexOf returns -1 if the tier is missing, so the smaller of an unsorted tier and a sorted one is the unsorted one
    if (sorted.indexOf(b) < sorted.indexOf(a)) {
      return b;
    }
    return a;
  }

  /** Gets the smallest tier in the sorting registry */
  public static Tier minTier() {
    List<Tier> sortedTiers = TierSortingRegistry.getSortedTiers();
    if (sortedTiers.isEmpty()) {
      TConstruct.LOG.error("No sorted tiers exist, this should not happen");
      return Tiers.WOOD;
    }
    return sortedTiers.get(0);
  }
}
