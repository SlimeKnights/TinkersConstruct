package slimeknights.tconstruct.library.tools.stat;

import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.ErrorFactory;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.stat.impl.IntegerToolStat;
import slimeknights.tconstruct.library.tools.stat.impl.PercentToolStat;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static slimeknights.mantle.data.predicate.item.ItemPredicate.or;
import static slimeknights.mantle.data.predicate.item.ItemPredicate.tag;

/**
 * Class handling all tool stats.
 *
 * Custom stat types need to be initialized before item registration for most uses, and need to be registered before worldload. Safe to statically register as done for TConstruct stat types
 */
public class ToolStats {
  /** Loader for general tool stats */
  public static final StringLoadable<IToolStat<?>> LOADER = ToolStatId.PARSER.comapFlatMap((id, error) -> {
    IToolStat<?> stat = ToolStats.getToolStat(id);
    if (stat != null) {
      return stat;
    }
    throw error.create("Unknown stat type " + id);
  }, IToolStat::getName);
  /** Loader that filters to only numeric tool stats */
  public static final StringLoadable<INumericToolStat<?>> NUMERIC_LOADER = LOADER.comapFlatMap((stat, error) -> {
    if (stat instanceof INumericToolStat<?> numeric) {
      return numeric;
    }
    throw error.create("Invalid tool stat " + stat.getName() + ", must be a numeric stat");
  }, stat -> stat);


  /** Map of ID to stat */
  private static final Map<ToolStatId,IToolStat<?>> ALL_STATS = new HashMap<>();
  /** Set of stats supporting {@link slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook} */
  private static final Set<INumericToolStat<?>> CONDITIONAL = new HashSet<>();

  /** Logs an error message for unsupported stats */
  private static void logUnsupportedConditional(INumericToolStat<?> stat) {
    // TODO 1.21: make this error instead of log
    ResourceLocation id = stat.getName();
    if (TConstruct.MOD_ID.equals(id.getNamespace())) {
      TConstruct.LOG.error("Tool stat {} does not support conditional stats but is used in a conditional stat loader.", id);
    } else {
      TConstruct.LOG.warn("Tool stat {} is not marked as supporting conditional stats but is used in a conditional stat loader. This may be a bug with the mod adding the stat having not marked it using the new API.", id);
    }
  }

  /** Loader that filters to only numeric tool stats */
  public static final StringLoadable<INumericToolStat<?>> CONDITIONAL_LOADER = LOADER.xmap((stat, error) -> {
    if (stat instanceof INumericToolStat<?> numeric) {
      if (error == ErrorFactory.JSON_SYNTAX_ERROR && !CONDITIONAL.contains(numeric)) {
        logUnsupportedConditional(numeric);
      }
      return numeric;
    }
    throw error.create("Invalid tool stat " + stat.getName() + ", must be a numeric stat");
  }, (stat, error) -> {
    if (error == ErrorFactory.RUNTIME && !CONDITIONAL.contains(stat)) {
      throw error.create("Tool stat " + stat.getName() + " does not support conditional stats but is used in a conditional stat loader.");
    }
    return stat;
  });

  /** Tools durability, determines how long it lasts */
  public static final FloatToolStat DURABILITY = register(new IntegerToolStat(name("durability"), 0xFF47CC47, 1, 1, Integer.MAX_VALUE, TinkerTags.Items.DURABILITY));
  /** Movement speed percentage when using this item, applicable to charging tools, pulling back bows, and shield blocking among other actions */
  public static final FloatToolStat USE_ITEM_SPEED = register(new FloatToolStat(name("use_item_speed"), 0xFF78A0CD, 0.2f, 0, 1, TinkerTags.Items.HELD));

  // melee
  /** Tools attack damage for melee */
  public static final FloatToolStat ATTACK_DAMAGE = register(new FloatToolStat(name("attack_damage"), 0xFFD76464, 0, 0, 2048f, TinkerTags.Items.MELEE));
  /** Equivalent to the vanilla attack speed, which is effectively number of attacks per second. 4 is equal to any standard item */
  public static final FloatToolStat ATTACK_SPEED = register(new FloatToolStat(name("attack_speed"), 0xFF8547CC, 1, 0, 1024f, TinkerTags.Items.MELEE_WEAPON));

  // harvest
  /** How fast the tool breaks blocks */
  public static final FloatToolStat MINING_SPEED = register(new FloatToolStat(name("mining_speed"), 0xFF78A0CD, 1, 0.1f, 2048f, TinkerTags.Items.HARVEST));
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
  /** Maximum angle of blocking in degrees. 180 is vanilla (90 degrees either direction). */
  public static final FloatToolStat BLOCK_ANGLE = registerConditional(new FloatToolStat(name("block_angle"), 0xFF78A0CD, 120, 0, 180, TinkerTags.Items.HELD));

  // ranged
  /** Number of times per second a tool can be used */
  public static final FloatToolStat DRAW_SPEED = registerConditional(new FloatToolStat(name("draw_speed"), 0xFF8547CC, 1, 0, 1024f, TinkerTags.Items.RANGED));
  /** Starting velocity of the projectile launched from a ranged weapon */
  public static final FloatToolStat VELOCITY = registerConditional(new FloatToolStat(name("velocity"), 0xFF78A0CD, 1, 0, 1024f, or(tag(TinkerTags.Items.RANGED), tag(TinkerTags.Items.AMMO))));
  /** Starting velocity of the projectile launched from a ranged weapon */
  public static final FloatToolStat ACCURACY = registerConditional(new FloatToolStat(name("accuracy"), 0xFF8547CC, 0.75f, 0.1f, 1f, or(tag(TinkerTags.Items.RANGED), tag(TinkerTags.Items.AMMO))));
  /** Base damage of the projectile, boosted by enchantments such as power. Assumes the arrow itself does 2 damage, so we boost on top of that */
  // TODO 1.21: rename to projectile power?
  public static final FloatToolStat PROJECTILE_DAMAGE = registerConditional(new FloatToolStat(name("projectile_damage"), 0xFFD76464, 2f, 0f, 1024f, or(tag(TinkerTags.Items.LAUNCHERS), tag(TinkerTags.Items.AMMO))));
  /** Projectile movement speed reduction while underwater */
  public static final FloatToolStat WATER_INERTIA = registerConditional(new PercentToolStat(name("water_inertia"), 0xFF5A82F3, 0.6f, 0.01f, 0.99f));

  // fishing
  /** Luck bonus applied to fishing rods */ // TODO 1.21: change field type to IntToolStat
  public static final FloatToolStat SEA_LUCK = registerConditional(new IntegerToolStat(name("sea_luck"), 0xFF345EC3, 0, 0, 1024f, TinkerTags.Items.FISHING_RODS));
  /** Floored value will reduce fishing time by 5 seconds */
  public static final FloatToolStat LURE = registerConditional(new IntegerToolStat(name("lure"), 0xFFCBCC18, 0, 0, 5, TinkerTags.Items.FISHING_RODS));

  /**
   * Gets the tool stat for the given name
   * @param name  Name
   * @return  Tool stat
   */
  @Nullable
  public static IToolStat<?> getToolStat(ToolStatId name) {
    return ALL_STATS.get(name);
  }

  /** @deprecated use {@link #LOADER} with {@link StringLoadable#parseString(String, String)} */
  @Deprecated(forRemoval = true)
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

  /** @deprecated use {@link #NUMERIC_LOADER} with {@link StringLoadable#parseString(String, String)} */
  @Deprecated(forRemoval = true)
  public static INumericToolStat<?> numericFromJson(String key) {
    if (fromJson(key) instanceof INumericToolStat<?> stat) {
      return stat;
    }
    throw new JsonSyntaxException("Invalid tool stat " + key + ", must be a numeric stat");
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


  /* Conditional */

  /** Marks a tool stat as supporting {@link slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook} */
  @SuppressWarnings("unused") // API
  public static void markConditional(INumericToolStat<?>... toolStat) {
    Collections.addAll(CONDITIONAL, toolStat);
  }

  /** Combination of {@link #register(IToolStat)} and {@link #markConditional(INumericToolStat[])} */
  public static <T extends INumericToolStat<?>> T registerConditional(T toolStat) {
    CONDITIONAL.add(toolStat);
    return register(toolStat);
  }

  /**
   * Checks if the given stat supports the condition stat hook.
   * Note that some stats support conditional stats via a dedicated hook instead of using the generic one.
   */
  public static boolean supportsConditional(INumericToolStat<?> toolStat) {
    return CONDITIONAL.contains(toolStat);
  }


  /* Deprecated */

  /** @deprecated use {@link #LOADER} with {@link slimeknights.mantle.data.loadable.Loadable#decode(FriendlyByteBuf)} */
  @Deprecated(forRemoval = true)
  public static IToolStat<?> fromNetwork(FriendlyByteBuf buffer) {
    return LOADER.decode(buffer);
  }

  /** @deprecated use {@link #NUMERIC_LOADER} with {@link slimeknights.mantle.data.loadable.Loadable#decode(FriendlyByteBuf)} */
  @Deprecated(forRemoval = true)
  public static INumericToolStat<?> numericFromNetwork(FriendlyByteBuf buffer) {
    return NUMERIC_LOADER.decode(buffer);
  }
}
