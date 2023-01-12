package slimeknights.tconstruct.plugin.jsonthings;

import dev.gigaherz.jsonthings.things.IFlexItem;
import dev.gigaherz.jsonthings.things.serializers.IItemSerializer;
import dev.gigaherz.jsonthings.things.serializers.ItemType;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.definition.IToolStatProvider;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.ToolStatProviders;
import slimeknights.tconstruct.plugin.jsonthings.item.FlexModifiableBowItem;
import slimeknights.tconstruct.plugin.jsonthings.item.FlexModifiableCrossbowItem;
import slimeknights.tconstruct.plugin.jsonthings.item.FlexModifiableItem;
import slimeknights.tconstruct.plugin.jsonthings.item.FlexToolPartItem;

import java.util.ArrayList;
import java.util.List;

/** Collection of custom item types added by Tinkers */
public class FlexItemTypes {
  /** All bow items that need their predicate registered */
  static final List<Item> BOW_ITEMS = new ArrayList<>();
  /** All crossbow items that need their predicate registered */
  static final List<Item> CROSSBOW_ITEMS = new ArrayList<>();

  /** Adds a thing to a list so we can fetch the instances later */
  private static <T> T add(List<? super T> list, T item) {
    list.add(item);
    return item;
  }

  /** Register a modifiable tool instance for melee/harvest tools */
  public static final ItemType<FlexToolPartItem> TOOL_PART = register("tool_part", data -> {
    MaterialStatsId statType = new MaterialStatsId(JsonHelper.getResourceLocation(data, "stat_type"));
    return (props, builder) -> new FlexToolPartItem(props, statType);
  });

  /** Register a modifiable tool instance for melee/harvest tools */
  public static final ItemType<FlexModifiableItem> MODIFIABLE_TOOL = register("tool", data -> {
    IToolStatProvider statProvider = ToolStatProviders.REGISTRY.deserialize(data, "stat_provider");
    boolean breakBlocksInCreative = GsonHelper.getAsBoolean(data, "break_blocks_in_creative", true);
    return (props, builder) -> new FlexModifiableItem(props, ToolDefinition.builder(builder.getRegistryName()).setStatsProvider(statProvider).build(), breakBlocksInCreative);
  });

  /** Register a modifiable tool instance for bow like items (release on finish) */
  public static final ItemType<FlexModifiableBowItem> BOW = register("bow", data -> {
    IToolStatProvider statProvider = data.has("stat_provider") ? ToolStatProviders.REGISTRY.deserialize(data, "stat_provider") : ToolStatProviders.RANGED;
    return (props, builder) -> add(BOW_ITEMS, new FlexModifiableBowItem(props, ToolDefinition.builder(builder.getRegistryName()).setStatsProvider(statProvider).build()));
  });

  /** Register a modifiable tool instance for crossbow like items (load on finish) */
  public static final ItemType<FlexModifiableCrossbowItem> CROSSBOW = register("crossbow", data -> {
    IToolStatProvider statProvider = data.has("stat_provider") ? ToolStatProviders.REGISTRY.deserialize(data, "stat_provider") : ToolStatProviders.RANGED;
    boolean allowFireworks = GsonHelper.getAsBoolean(data, "allow_fireworks");
    return (props, builder) -> add(allowFireworks ? CROSSBOW_ITEMS : BOW_ITEMS,
                                   new FlexModifiableCrossbowItem(props, ToolDefinition.builder(builder.getRegistryName()).setStatsProvider(statProvider).build(), allowFireworks));
  });

  /** Initializes the item types */
  public static void init() {}

  /** Local helper to register our stuff */
  private static <T extends Item & IFlexItem> ItemType<T> register(String name, IItemSerializer<T> factory) {
    return ItemType.register(TConstruct.resourceString(name), factory);
  }
}
