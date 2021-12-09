package slimeknights.tconstruct.library.tools.stat;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.utils.HarvestLevels;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Class handling all tool stats.
 *
 * Custom stat types need to be initialized before item registration for most uses, and need to be registered before worldload. Safe to statically register as done for TConstruct stat types
 */
public class ToolStats {
  /** Map of ID to stat */
  private static final Map<ToolStatId,IToolStat<?>> ALL_STATS = new HashMap<>();

  /** Tools durability, determines how long it lasts */
  public static final FloatToolStat DURABILITY = register(new FloatToolStat(name("durability"), 0xFF47CC47, 1, 1, Integer.MAX_VALUE));

  // melee
  /** Tools attack damage for melee */
  public static final FloatToolStat ATTACK_DAMAGE = register(new FloatToolStat(name("attack_damage"), 0xFFD76464, 0, 0, 2048f));
  /** Equivalent to the vanilla attack speed. 4 is equal to any standard item */
  public static final FloatToolStat ATTACK_SPEED = register(new FloatToolStat(name("attack_speed"), 0xFF8547CC, 1, 0, 1024f));

  // harvest
  /** How fast the tool breaks blocks */
  public static final FloatToolStat MINING_SPEED = register(new FloatToolStat(name("mining_speed"), 0xFF78A0CD, 1, 0, 2048f));
  /** Mining level for breaking blocks */
  public static final TierToolStat HARVEST_LEVEL = register(new TierToolStat(name("harvest_level"), HarvestLevels.WOOD, HarvestLevels::getHarvestLevelName));
  /** Multiplier for mining speed on non-dirt blocks for the mattock */
  public static final FloatToolStat SECONDARY_MINING = register(new FloatToolStat(name("secondary_mining"), 0xFF78A0CD, 1, 0, 10f));

  // armor
  /** Main armor value */
  public static final FloatToolStat ARMOR = register(new FloatToolStat(name("armor"), 0xFF8547CC, 0, 0, 30));
  /** Hidden toughness value */
  public static final FloatToolStat ARMOR_TOUGHNESS = register(new FloatToolStat(name("armor_toughness"), 0xFF8547CC, 0, 0, 20));
  /** Knockback resistance percentage */
  public static final FloatToolStat KNOCKBACK_RESISTANCE = register(new FloatToolStat(name("knockback_resistance"), 0xFF8547CC, 0, 0, 1));
  

  /**
   * Gets the tool stat for the given name
   * @param name  Name
   * @return  Tool stat
   */
  @Nullable
  public static IToolStat<?> getToolStat(ToolStatId name) {
    return ALL_STATS.get(name);
  }

  /**
   * Registers a new tool stat
   * @param toolStat  Stat to register
   * @param <T>  Stat type
   * @return  Registerd stat
   * @throws IllegalArgumentException If duplicate tool stats are registered
   */
  public static <T extends IToolStat<?>> T register(T toolStat) {
    if (ALL_STATS.containsKey(toolStat.getName())) {
      throw new IllegalArgumentException("Attempt to register duplicate tool stat " + toolStat.getName());
    }
    ALL_STATS.put(toolStat.getName(), toolStat);
    return toolStat;
  }

  /** Gets a collection of all stat keys */
  public static Collection<IToolStat<?>> getAllStats() {
    return ALL_STATS.values();
  }

  /** Creates a resource location for a Tinkers stat */
  private static ToolStatId name(String name) {
    return new ToolStatId(TConstruct.MOD_ID, name);
  }
}
