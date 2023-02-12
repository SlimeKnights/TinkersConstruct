package slimeknights.tconstruct.plugin.jsonthings;

import dev.gigaherz.jsonthings.things.IFlexItem;
import dev.gigaherz.jsonthings.things.serializers.FlexItemType;
import dev.gigaherz.jsonthings.things.serializers.IItemSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.definition.IToolStatProvider;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.ToolStatProviders;
import slimeknights.tconstruct.plugin.jsonthings.item.DummyArmorMaterial;
import slimeknights.tconstruct.plugin.jsonthings.item.FlexBasicArmorItem;
import slimeknights.tconstruct.plugin.jsonthings.item.FlexFlatEmbellishedArmor;
import slimeknights.tconstruct.plugin.jsonthings.item.FlexLayeredEmbellishedArmor;
import slimeknights.tconstruct.plugin.jsonthings.item.FlexModifiableBowItem;
import slimeknights.tconstruct.plugin.jsonthings.item.FlexModifiableCrossbowItem;
import slimeknights.tconstruct.plugin.jsonthings.item.FlexModifiableItem;
import slimeknights.tconstruct.plugin.jsonthings.item.FlexModifiableStaffItem;
import slimeknights.tconstruct.plugin.jsonthings.item.FlexRepairKitItem;
import slimeknights.tconstruct.plugin.jsonthings.item.FlexToolPartItem;
import slimeknights.tconstruct.tools.item.ArmorSlotType;

import java.util.ArrayList;
import java.util.List;

/** Collection of custom item types added by Tinkers */
@SuppressWarnings("unused")
public class FlexItemTypes {
  /** Standard tools that need standard properties */
  static final List<Item> TOOL_ITEMS = new ArrayList<>();
  /** All bow items that need their predicate registered */
  static final List<Item> BOW_ITEMS = new ArrayList<>();
  /** All crossbow items that need their predicate registered */
  static final List<Item> CROSSBOW_ITEMS = new ArrayList<>();

  /** Adds a thing to a list so we can fetch the instances later */
  private static <T> T add(List<? super T> list, T item) {
    list.add(item);
    return item;
  }

  /** Initializes the item types */
  public static void init() {
    /* Register a tool part to create new tools */
    register("tool_part", data -> {
      MaterialStatsId statType = new MaterialStatsId(JsonHelper.getResourceLocation(data, "stat_type"));
      return (props, builder) -> new FlexToolPartItem(props, statType);
    });

    /* Register an item that can be used to repair tools */
    register("repair_kit", data -> {
      float repairAmount = GsonHelper.getAsFloat(data, "repair_amount");
      return (props, builder) -> new FlexRepairKitItem(props, repairAmount);
    });

    /* Register a modifiable tool instance for melee/harvest tools */
    register("tool", data -> {
      IToolStatProvider statProvider = ToolStatProviders.REGISTRY.deserialize(data, "stat_provider");
      boolean breakBlocksInCreative = GsonHelper.getAsBoolean(data, "break_blocks_in_creative", true);
      return (props, builder) -> add(TOOL_ITEMS, new FlexModifiableItem(props, ToolDefinition.builder(builder.getRegistryName()).setStatsProvider(statProvider).build(), breakBlocksInCreative));
    });

    /* Register a modifiable tool instance for melee/harvest tools */
    register("staff", data -> {
      IToolStatProvider statProvider = ToolStatProviders.REGISTRY.deserialize(data, "stat_provider");
      boolean breakBlocksInCreative = GsonHelper.getAsBoolean(data, "break_blocks_in_creative", true);
      return (props, builder) -> add(TOOL_ITEMS, new FlexModifiableStaffItem(props, ToolDefinition.builder(builder.getRegistryName()).setStatsProvider(statProvider).build(), breakBlocksInCreative));
    });

    /* Register a modifiable tool instance for bow like items (release on finish) */
    register("bow", data -> {
      IToolStatProvider statProvider = data.has("stat_provider") ? ToolStatProviders.REGISTRY.deserialize(data, "stat_provider") : ToolStatProviders.RANGED;
      return (props, builder) -> add(BOW_ITEMS, new FlexModifiableBowItem(props, ToolDefinition.builder(builder.getRegistryName()).setStatsProvider(statProvider).build()));
    });

    /* Register a modifiable tool instance for crossbow like items (load on finish) */
    register("crossbow", data -> {
      IToolStatProvider statProvider = data.has("stat_provider") ? ToolStatProviders.REGISTRY.deserialize(data, "stat_provider") : ToolStatProviders.RANGED;
      boolean allowFireworks = GsonHelper.getAsBoolean(data, "allow_fireworks");
      return (props, builder) -> add(CROSSBOW_ITEMS, new FlexModifiableCrossbowItem(props, ToolDefinition.builder(builder.getRegistryName()).setStatsProvider(statProvider).build(), allowFireworks));
    });

    /* Register a modifiable tool instance for crossbow like items (load on finish) */
    register("basic_armor", data -> {
      ResourceLocation name = JsonHelper.getResourceLocation(data, "texture_name");
      boolean dyeable = GsonHelper.getAsBoolean(data, "dyeable", false);
      boolean hasGolden = GsonHelper.getAsBoolean(data, "has_golden", true);
      ArmorSlotType slot = JsonHelper.getAsEnum(data, "slot", ArmorSlotType.class);
      SoundEvent equipSound = JsonHelper.getAsEntry(ForgeRegistries.SOUND_EVENTS, data, "equip_sound");
      IToolStatProvider statProvider = data.has("stat_provider") ? ToolStatProviders.REGISTRY.deserialize(data, "stat_provider") : ToolStatProviders.NO_PARTS;
      return (props, builder) -> new FlexBasicArmorItem(new DummyArmorMaterial(name, equipSound), slot.getEquipmentSlot(), props, ToolDefinition.builder(builder.getRegistryName()).setStatsProvider(statProvider).build(), name, dyeable, hasGolden);
    });

    /* Register a modifiable armor part that supports embellishments */
    register("layered_embellished_armor", data -> {
      ResourceLocation name = JsonHelper.getResourceLocation(data, "texture_name");
      ArmorSlotType slot = JsonHelper.getAsEnum(data, "slot", ArmorSlotType.class);
      SoundEvent equipSound = JsonHelper.getAsEntry(ForgeRegistries.SOUND_EVENTS, data, "equip_sound");
      IToolStatProvider statProvider = data.has("stat_provider") ? ToolStatProviders.REGISTRY.deserialize(data, "stat_provider") : ToolStatProviders.NO_PARTS;
      return (props, builder) -> new FlexLayeredEmbellishedArmor(new DummyArmorMaterial(name, equipSound), slot.getEquipmentSlot(), props, ToolDefinition.builder(builder.getRegistryName()).setStatsProvider(statProvider).build(), name);
    });

    /* Register a modifiable tool instance for crossbow like items (load on finish) */
    register("flat_embellished_armor", data -> {
      ResourceLocation name = JsonHelper.getResourceLocation(data, "texture_name");
      MaterialId defaultMaterial = new MaterialId(JsonHelper.getResourceLocation(data, "default_material"));
      boolean dyeable = GsonHelper.getAsBoolean(data, "dyeable", false);
      ArmorSlotType slot = JsonHelper.getAsEnum(data, "slot", ArmorSlotType.class);
      SoundEvent equipSound = JsonHelper.getAsEntry(ForgeRegistries.SOUND_EVENTS, data, "equip_sound");
      IToolStatProvider statProvider = data.has("stat_provider") ? ToolStatProviders.REGISTRY.deserialize(data, "stat_provider") : ToolStatProviders.NO_PARTS;
      return (props, builder) -> new FlexFlatEmbellishedArmor(new DummyArmorMaterial(name, equipSound), slot.getEquipmentSlot(), props, ToolDefinition.builder(builder.getRegistryName()).setStatsProvider(statProvider).build(), name, defaultMaterial, dyeable);
    });
  }

  /** Local helper to register our stuff */
  private static <T extends Item & IFlexItem> void register(String name, IItemSerializer<T> factory) {
    FlexItemType.register(TConstruct.resourceString(name), factory);
  }
}
