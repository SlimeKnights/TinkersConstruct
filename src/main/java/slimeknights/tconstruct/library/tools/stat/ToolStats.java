package slimeknights.tconstruct.library.tools.stat;

import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;

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
  public static final FloatToolStat DURABILITY = register(new FloatToolStat(name("durability"), 0xFF47CC47, 1, 1, Integer.MAX_VALUE, TinkerTags.Items.DURABILITY));

  // melee
  /** Tools attack damage for melee */
  public static final FloatToolStat ATTACK_DAMAGE = register(new FloatToolStat(name("attack_damage"), 0xFFD76464, 0, 0, 2048f, TinkerTags.Items.MELEE_OR_UNARMED));
  /** Equivalent to the vanilla attack speed, which is effectively number of attacks per second. 4 is equal to any standard item */
  public static final FloatToolStat ATTACK_SPEED = register(new FloatToolStat(name("attack_speed"), 0xFF8547CC, 1, 0, 1024f, TinkerTags.Items.MELEE));

  // harvest
  /** How fast the tool breaks blocks */
  public static final FloatToolStat MINING_SPEED = register(new FloatToolStat(name("mining_speed"), 0xFF78A0CD, 1, 0, 2048f, TinkerTags.Items.HARVEST));
  /** Mining level for breaking blocks */
  public static final ToolTierStat HARVEST_TIER = register(new ToolTierStat(name("harvest_tier")));

  // armor
  /** Main armor value */
  public static final FloatToolStat ARMOR = register(new FloatToolStat(name("armor"), 0xFF8547CC, 0, 0, 30, TinkerTags.Items.ARMOR));
  /** Hidden toughness value */
  public static final FloatToolStat ARMOR_TOUGHNESS = register(new FloatToolStat(name("armor_toughness"), 0xFF8547CC, 0, 0, 20, TinkerTags.Items.ARMOR));
  /** Knockback resistance percentage */
  public static final FloatToolStat KNOCKBACK_RESISTANCE = register(new FloatToolStat(name("knockback_resistance"), 0xFF8547CC, 0, 0, 1, TinkerTags.Items.ARMOR));

  // shield
  /** Maximum damage blocked by the shield. If more than this number is dealt, the damage is reduced by this number */
  public static final FloatToolStat BLOCK_AMOUNT = register(new FloatToolStat(name("block_amount"), 0xFF78A0CD, 5, 0, 2048, TinkerTags.Items.HELD));
  /** Maximum angle of blocking in degrees. 90 is vanilla (90 degrees either direction). */
  public static final FloatToolStat BLOCK_ANGLE = register(new FloatToolStat(name("block_angle"), 0xFF78A0CD, 60, 0, 90, TinkerTags.Items.HELD));

  // ranged
  /** Number of times per second a tool can be used */
  public static final FloatToolStat DRAW_SPEED = register(new FloatToolStat(name("draw_speed"), 0xFF8547CC, 1, 0, 1024f, TinkerTags.Items.RANGED));
  /** Starting velocity of the projectile launched from a ranged weapon */
  public static final FloatToolStat VELOCITY = register(new FloatToolStat(name("velocity"), 0xFF78A0CD, 1, 0, 1024f, TinkerTags.Items.RANGED));
  /** Starting velocity of the projectile launched from a ranged weapon */
  public static final FloatToolStat ACCURACY = register(new FloatToolStat(name("accuracy"), 0xFF8547CC, 0.75f, 0.1f, 1f, TinkerTags.Items.RANGED));
  /** Base damage of the projectile, boosted by enchantments such as power. Assumes the arrow itself does 2 damage, so we boost on top of that */
  public static final FloatToolStat PROJECTILE_DAMAGE = register(new FloatToolStat(name("projectile_damage"), 0xFFD76464, 2f, 0f, 1024f, TinkerTags.Items.RANGED));


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
   * Parses a stat from JSON, throwing if invalid
   * @throws JsonSyntaxException if invalid
   */
  public static IToolStat<?> fromJson(String key) {
    ResourceLocation location = ResourceLocation.tryParse(key);
    if (location != null) {
      IToolStat<?> stat = ToolStats.getToolStat(new ToolStatId(location));
      if (stat != null) {
        return stat;
      }
    }
    throw new JsonSyntaxException("Unknown stat type " + key);
  }

  /**
   * Parses a numeric stat from JSON, throwing if invalid
   * @throws JsonSyntaxException if invalid
   */
  public static INumericToolStat<?> numericFromJson(String key) {
    if (fromJson(key) instanceof INumericToolStat<?> stat) {
      return stat;
    }
    throw new JsonSyntaxException("Invalid tool stat " + key + ", must be a numeric stat");
  }

  /** Reads a stat from the network */
  public static IToolStat<?> fromNetwork(FriendlyByteBuf buffer) {
    ToolStatId id = new ToolStatId(buffer.readUtf(Short.MAX_VALUE));
    IToolStat<?> stat = ToolStats.getToolStat(id);
    if (stat == null) {
      throw new DecoderException("Invalid stat type name " + id);
    }
    return stat;
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
