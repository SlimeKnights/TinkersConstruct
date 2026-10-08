package slimeknights.tconstruct.common.data.advancement;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ToolActions;
import slimeknights.tconstruct.TConstruct;

/** IDs used in {@link slimeknights.tconstruct.common.data.AdvancementsProvider} and {@link FunctionProvider} */
public class AdvancementIds {
  private AdvancementIds() {}

  // basic advancements
  /** Granted by crafting a crafting table, now also crafting stations */
  public static final ResourceLocation STORY_ROOT = id("story/root");
  /** Granted by crafting a stone pickaxe, now also stone harvest tier pickaxes */
  public static final ResourceLocation STONE_PICK = id("story/upgrade_tools");
  /** Granted by crafting an iron pickaxe, now also iron harvest tier pickaxes */
  public static final ResourceLocation IRON_PICK = id("story/iron_tools");
  /** Granted by crafting a netherite hoe, now also nethetite + tilling */
  public static final ResourceLocation NETHERITE_HOE = id("husbandry/obtain_netherite_hoe");
  /** Granted by crafting a netherite hoe, now also nethetite + tilling */
  public static final ResourceLocation WALK_ON_POWDER_SNOW = id("adventure/walk_on_powder_snow_with_leather_boots");
  // armor advancements - has 4 piece criteria
  /** Granted by crafting full set of iron armor, now also iron plating armor */
  public static final ResourceLocation OBTAIN_ARMOR = id("story/obtain_armor");
  /** Granted by crafting full set of diamond armor, now also diamond modifier */
  public static final ResourceLocation SHINY_GEAR = id("story/shiny_gear");
  /** Granted by crafting full set of netherite armor, now also netherite modifier */
  public static final ResourceLocation NETHERITE_ARMOR = id("nether/netherite_armor");

  // modifier volatile flags
  /** Apply to anything with plating to grant it iron armor achievements. */
  public static final ResourceLocation IRON_ARMOR = TConstruct.getResource("iron_armor");
  /** Apply to anything with plating to grant it diamond armor achievements. */
  public static final ResourceLocation DIAMOND_ARMOR = TConstruct.getResource("diamond_armor");
  /** Apply to anything with plating to grant it netherite armor achievements, and to anything with {@link ToolActions#HOE_TILL} to grant netherite hoe. */
  public static final ResourceLocation NETHERITE = TConstruct.getResource("netherite");

  @SuppressWarnings("removal")
  private static ResourceLocation id(String key) {
    return new ResourceLocation("minecraft", key);
  }

  /** Creates a function ID from the given advancement */
  public static ResourceLocation function(ResourceLocation advancement) {
    return TConstruct.getResource("grant_advancement/" + advancement.getPath().replace('/', '_'));
  }

  /** Creates a function ID from the given advancement */
  public static ResourceLocation function(ResourceLocation advancement, String criteria) {
    return function(advancement).withSuffix('/' + criteria);
  }
}
