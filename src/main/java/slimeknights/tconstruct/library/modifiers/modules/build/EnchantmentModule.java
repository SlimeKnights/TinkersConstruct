package slimeknights.tconstruct.library.modifiers.modules.build;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.HarvestEnchantmentsModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.JsonUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Modules that add enchantments to a tool. */
public interface EnchantmentModule extends ModifierModule {

  /** Gets the enchantment for this module */
  Enchantment enchantment();

  /** Gets the level for this module */
  int level();

  /** Gets the block predicate, will be {@link BlockPredicate#ANY} for {@link Constant} */
  default IJsonPredicate<BlockState> block() {
    return BlockPredicate.ANY;
  }

  /** Gets the holder predicate, will be {@link LivingEntityPredicate#ANY} for {@link Constant} */
  default IJsonPredicate<LivingEntity> holder() {
    return LivingEntityPredicate.ANY;
  }

  /** Gets the modifier conditions */
  ModifierModuleCondition condition();

  /** Simple helper to get the level for the enchantment since its reused a bunch */
  default int getEnchantmentLevel(IToolStackView tool, ModifierEntry modifier) {
    return Mth.floor(modifier.getEffectiveLevel(tool)) * this.level();
  }

  /**
   * Creates a builder for a constant enchantment
   */
  static Builder builder(Enchantment enchantment) {
    return new Builder(enchantment);
  }

  /**
   * Shared builder instance
   */
  @SuppressWarnings("unused") // API
  @Setter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  class Builder extends ModifierModuleCondition.Builder<Builder> {
    private final Enchantment enchantment;
    private int level = 1;
    private IJsonPredicate<BlockState> block = BlockPredicate.ANY;
    private IJsonPredicate<LivingEntity> holder = LivingEntityPredicate.ANY;

    /** Builds a module for a constant enchantment */
    public Constant constant() {
      if (block != BlockPredicate.ANY || holder != LivingEntityPredicate.ANY) {
        throw new IllegalStateException("Cannot build a constant enchantment module with block or holder conditions");
      }
      return new Constant(enchantment, level, condition);
    }

    /**
     * Creates a new main hand harvest module
     * @param key  Key to use for checking conditions, needs to be unique. Recommend suffixing the modifier ID (using the modifier ID will conflict with incremental)
     * @return  Module instance
     */
    public MainHandHarvest mainHandHarvest(ResourceLocation key) {
      return new MainHandHarvest(enchantment, level, condition, key, block, holder);
    }

    /**
     * Creates a new armor harvest module
     * @param slots  Slots to allow this to run
     * @return  Module instance
     */
    public ArmorHarvest armorHarvest(EquipmentSlot... slots) {
      if (slots.length == 0) {
        throw new IllegalArgumentException("Must have at least 1 slot");
      }
      // immutable set preserves insertion order
      Set<EquipmentSlot> set = ImmutableSet.copyOf(slots);
      if (set.contains(EquipmentSlot.MAINHAND)) {
        throw new IllegalArgumentException("Cannot create armor harvest for the main hand slot");
      }
      return new ArmorHarvest(enchantment, level, condition, set, block, holder);
    }

    /** Creates a new armor harvest module with the default slots */
    public ArmorHarvest armorHarvest() {
      return armorHarvest(HarvestEnchantmentsModifierHook.APPLICABLE_SLOTS);
    }
  }

  /**
   * Loader shared logic for enchantment modules
   */
  @RequiredArgsConstructor
  abstract class Loader<T extends EnchantmentModule> implements IGenericLoader<T> {
    private final boolean harvest;

    /** Creates a module instance for JSON */
    protected abstract T deserialize(Enchantment enchantment, int level, ModifierModuleCondition conditions, IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder, JsonObject json);
    /** Creates a module instance for the buffer */
    protected abstract T fromNetwork(Enchantment enchantment, int level, ModifierModuleCondition conditions, IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder, FriendlyByteBuf buffer);

    @Override
    public T deserialize(JsonObject json) {
      Enchantment enchantment = JsonHelper.getAsEntry(ForgeRegistries.ENCHANTMENTS, json, "name");
      int level = JsonUtils.getIntMin(json, "level", 1);
      ModifierModuleCondition condition = ModifierModuleCondition.deserializeFrom(json);
      if (harvest) {
        return deserialize(enchantment, level, condition, BlockPredicate.LOADER.getAndDeserialize(json, "block"), LivingEntityPredicate.LOADER.getAndDeserialize(json, "holder"), json);
      } else {
        return deserialize(enchantment, level, condition, BlockPredicate.ANY, LivingEntityPredicate.ANY, json);
      }
    }

    @Override
    public void serialize(T object, JsonObject json) {
      object.condition().serializeInto(json);
      json.addProperty("name", Objects.requireNonNull(Registry.ENCHANTMENT.getKey(object.enchantment())).toString());
      json.addProperty("level", object.level());
      if (harvest) {
        json.add("block", BlockPredicate.LOADER.serialize(object.block()));
        json.add("holder", LivingEntityPredicate.LOADER.serialize(object.holder()));
      }
    }

    @Override
    public T fromNetwork(FriendlyByteBuf buffer) {
      Enchantment enchantment = buffer.readRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS);
      int level = buffer.readVarInt();
      ModifierModuleCondition condition = ModifierModuleCondition.fromNetwork(buffer);
      if (harvest) {
        return fromNetwork(enchantment, level, condition, BlockPredicate.LOADER.fromNetwork(buffer), LivingEntityPredicate.LOADER.fromNetwork(buffer), buffer);
      } else {
        return fromNetwork(enchantment, level, condition, BlockPredicate.ANY, LivingEntityPredicate.ANY, buffer);
      }
    }

    @Override
    public void toNetwork(T object, FriendlyByteBuf buffer) {
      buffer.writeRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS, object.enchantment());
      buffer.writeVarInt(object.level());
      object.condition().toNetwork(buffer);
      if (harvest) {
        BlockPredicate.LOADER.toNetwork(object.block(), buffer);
        LivingEntityPredicate.LOADER.toNetwork(object.holder(), buffer);
      }
    }
  }

  /** Implementation of a simple constant enchantment for the current tool */
  record Constant(Enchantment enchantment, int level, ModifierModuleCondition condition) implements EnchantmentModule, EnchantmentModifierHook {
    private static final List<ModifierHook<?>> DEFAULT_HOOKS = ModifierModule.<Constant>defaultHooks(TinkerHooks.ENCHANTMENTS);
    public static final IGenericLoader<Constant> LOADER = new EnchantmentModule.Loader<>(false) {
      @Override
      protected Constant deserialize(Enchantment enchantment, int level, ModifierModuleCondition conditions, IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder, JsonObject json) {
        return new Constant(enchantment, level, conditions);
      }

      @Override
      protected Constant fromNetwork(Enchantment enchantment, int level, ModifierModuleCondition conditions, IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder, FriendlyByteBuf buffer) {
        return new Constant(enchantment, level, conditions);
      }
    };

    public Constant(Enchantment enchantment, int level) {
      this(enchantment, level, ModifierModuleCondition.ANY);
    }

    @Override
    public int updateEnchantmentLevel(IToolStackView tool, ModifierEntry modifier, Enchantment enchantment, int level) {
      if (enchantment == this.enchantment() && condition().matches(tool, modifier)) {
        level += getEnchantmentLevel(tool, modifier);
      }
      return level;
    }

    @Override
    public void updateEnchantments(IToolStackView tool, ModifierEntry modifier, Map<Enchantment,Integer> map) {
      if (condition().matches(tool, modifier)) {
        EnchantmentModifierHook.addEnchantment(map, this.enchantment(), getEnchantmentLevel(tool, modifier));
      }
    }

    @Override
    public List<ModifierHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public IGenericLoader<? extends ModifierModule> getLoader() {
      return LOADER;
    }
  }

  /** Enchantment module that can condition on the block mined or the entity mining. */
  record MainHandHarvest(Enchantment enchantment, int level, ModifierModuleCondition condition, ResourceLocation conditionFlag, IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder) implements EnchantmentModule, EnchantmentModifierHook, BlockHarvestModifierHook {
    private static final List<ModifierHook<?>> DEFAULT_HOOKS = ModifierModule.<MainHandHarvest>defaultHooks(TinkerHooks.ENCHANTMENTS, TinkerHooks.BLOCK_HARVEST);
    public static final IGenericLoader<MainHandHarvest> LOADER = new EnchantmentModule.Loader<>(true) {
      @Override
      protected MainHandHarvest deserialize(Enchantment enchantment, int level, ModifierModuleCondition conditions, IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder, JsonObject json) {
        return new MainHandHarvest(enchantment, level, conditions, JsonHelper.getResourceLocation(json, "condition_flag"), block, holder);
      }

      @Override
      public void serialize(MainHandHarvest object, JsonObject json) {
        super.serialize(object, json);
        json.addProperty("condition_flag", object.conditionFlag.toString());
      }

      @Override
      protected MainHandHarvest fromNetwork(Enchantment enchantment, int level, ModifierModuleCondition conditions, IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder, FriendlyByteBuf buffer) {
        return new MainHandHarvest(enchantment, level, conditions, buffer.readResourceLocation(), block, holder);
      }

      @Override
      public void toNetwork(MainHandHarvest object, FriendlyByteBuf buffer) {
        super.toNetwork(object, buffer);
        buffer.writeResourceLocation(object.conditionFlag);
      }
    };

    @Override
    public void startHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
      if (condition.matches(tool, modifier) && block.matches(context.getState()) && holder.matches(context.getLiving())) {
        tool.getPersistentData().putBoolean(conditionFlag, true);
      }
      BlockHarvestModifierHook.super.startHarvest(tool, modifier, context);
    }

    @Override
    public void finishHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, boolean didHarvest) {
      tool.getPersistentData().remove(conditionFlag);
    }

    @Override
    public int updateEnchantmentLevel(IToolStackView tool, ModifierEntry modifier, Enchantment enchantment, int level) {
      if (enchantment == this.enchantment() && tool.getPersistentData().getBoolean(conditionFlag)) {
        level += getEnchantmentLevel(tool, modifier);
      }
      return level;
    }

    @Override
    public void updateEnchantments(IToolStackView tool, ModifierEntry modifier, Map<Enchantment,Integer> map) {
      if (tool.getPersistentData().getBoolean(conditionFlag)) {
        EnchantmentModifierHook.addEnchantment(map, this.enchantment(), getEnchantmentLevel(tool, modifier));
      }
    }

    @Override
    public List<ModifierHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public IGenericLoader<? extends ModifierModule> getLoader() {
      return LOADER;
    }
  }

  /** Enchantment module that can condition on the block mined or the entity mining on armor. Requires the harvesting be done with a tinker tool. */
  record ArmorHarvest(Enchantment enchantment, int level, ModifierModuleCondition condition, Set<EquipmentSlot> slots, IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder) implements EnchantmentModule, HarvestEnchantmentsModifierHook {
    private static final List<ModifierHook<?>> DEFAULT_HOOKS = ModifierModule.<ArmorHarvest>defaultHooks(TinkerHooks.HARVEST_ENCHANTMENTS);
    public static final IGenericLoader<ArmorHarvest> LOADER = new EnchantmentModule.Loader<>(true) {
      @Override
      protected ArmorHarvest deserialize(Enchantment enchantment, int level, ModifierModuleCondition conditions, IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder, JsonObject json) {
        return new ArmorHarvest(enchantment, level, conditions, Set.copyOf(JsonHelper.parseList(json, "slots", (element, key) -> JsonHelper.convertToEnum(element, key, EquipmentSlot.class))), block, holder);
      }

      @Override
      public void serialize(ArmorHarvest object, JsonObject json) {
        super.serialize(object, json);
        JsonArray slots = new JsonArray();
        for (EquipmentSlot slot : object.slots) {
          slots.add(slot.getName());
        }
        json.add("slots", slots);
      }

      @Override
      protected ArmorHarvest fromNetwork(Enchantment enchantment, int level, ModifierModuleCondition conditions, IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder, FriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        ImmutableSet.Builder<EquipmentSlot> builder = ImmutableSet.builder();
        for (int i = 0; i < size; i++) {
          builder.add(buffer.readEnum(EquipmentSlot.class));
        }
        return new ArmorHarvest(enchantment, level, conditions, builder.build(), block, holder);
      }

      @Override
      public void toNetwork(ArmorHarvest object, FriendlyByteBuf buffer) {
        super.toNetwork(object, buffer);
        buffer.writeVarInt(object.slots.size());
        for (EquipmentSlot slot : object.slots) {
          buffer.writeEnum(slot);
        }
      }
    };

    @Override
    public void updateHarvestEnchantments(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, EquipmentContext equipment, EquipmentSlot slot, Map<Enchantment,Integer> map) {
      if (slots.contains(slot) && condition.matches(tool, modifier) && block.matches(context.getState()) && holder.matches(context.getLiving())) {
        EnchantmentModifierHook.addEnchantment(map, enchantment, getEnchantmentLevel(tool, modifier));
      }
    }

    @Override
    public List<ModifierHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public IGenericLoader<? extends ModifierModule> getLoader() {
      return LOADER;
    }
  }
}
