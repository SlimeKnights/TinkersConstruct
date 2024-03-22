package slimeknights.tconstruct.library.modifiers.modules.build;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.HarvestEnchantmentsModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.IntLevelModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition.ConditionalModifierModule;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** Modules that add enchantments to a tool. */
public interface EnchantmentModule extends ModifierModule, IntLevelModule, ConditionalModifierModule {
  /* Common fields */
  LoadableField<Enchantment,EnchantmentModule> ENCHANTMENT = Loadables.ENCHANTMENT.field("name", EnchantmentModule::enchantment);
  LoadableField<IJsonPredicate<BlockState>,EnchantmentModule> BLOCK = BlockPredicate.LOADER.field("block", EnchantmentModule::block);
  LoadableField<IJsonPredicate<LivingEntity>,EnchantmentModule> HOLDER = LivingEntityPredicate.LOADER.field("holder", EnchantmentModule::holder);

  /** Gets the enchantment for this module */
  Enchantment enchantment();

  /** Gets the block predicate, will be {@link BlockPredicate#ANY} for {@link Constant} */
  default IJsonPredicate<BlockState> block() {
    return BlockPredicate.ANY;
  }

  /** Gets the holder predicate, will be {@link LivingEntityPredicate#ANY} for {@link Constant} */
  default IJsonPredicate<LivingEntity> holder() {
    return LivingEntityPredicate.ANY;
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

  /** Implementation of a simple constant enchantment for the current tool */
  record Constant(Enchantment enchantment, int level, ModifierModuleCondition condition) implements EnchantmentModule, EnchantmentModifierHook {
    private static final List<ModifierHook<?>> DEFAULT_HOOKS = ModifierModule.<Constant>defaultHooks(TinkerHooks.ENCHANTMENTS);
    public static final RecordLoadable<Constant> LOADER = RecordLoadable.create(ENCHANTMENT, IntLevelModule.FIELD, ModifierModuleCondition.FIELD, Constant::new);

    public Constant(Enchantment enchantment, int level) {
      this(enchantment, level, ModifierModuleCondition.ANY);
    }

    @Override
    public int updateEnchantmentLevel(IToolStackView tool, ModifierEntry modifier, Enchantment enchantment, int level) {
      if (enchantment == this.enchantment() && condition().matches(tool, modifier)) {
        level += getLevel(tool, modifier);
      }
      return level;
    }

    @Override
    public void updateEnchantments(IToolStackView tool, ModifierEntry modifier, Map<Enchantment,Integer> map) {
      if (condition().matches(tool, modifier)) {
        EnchantmentModifierHook.addEnchantment(map, this.enchantment(), getLevel(tool, modifier));
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
    public static final RecordLoadable<MainHandHarvest> LOADER = RecordLoadable.create(ENCHANTMENT, IntLevelModule.FIELD, ModifierModuleCondition.FIELD, Loadables.RESOURCE_LOCATION.field("condition_flag", MainHandHarvest::conditionFlag), BLOCK, HOLDER, MainHandHarvest::new);

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
        level += getLevel(tool, modifier);
      }
      return level;
    }

    @Override
    public void updateEnchantments(IToolStackView tool, ModifierEntry modifier, Map<Enchantment,Integer> map) {
      if (tool.getPersistentData().getBoolean(conditionFlag)) {
        EnchantmentModifierHook.addEnchantment(map, this.enchantment(), getLevel(tool, modifier));
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
    public static final RecordLoadable<ArmorHarvest> LOADER = RecordLoadable.create(ENCHANTMENT, IntLevelModule.FIELD, ModifierModuleCondition.FIELD, TinkerLoadables.EQUIPMENT_SLOT_SET.field("slots", ArmorHarvest::slots), BLOCK, HOLDER, ArmorHarvest::new);

    @Override
    public void updateHarvestEnchantments(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, EquipmentContext equipment, EquipmentSlot slot, Map<Enchantment,Integer> map) {
      if (slots.contains(slot) && condition.matches(tool, modifier) && block.matches(context.getState()) && holder.matches(context.getLiving())) {
        EnchantmentModifierHook.addEnchantment(map, enchantment, getLevel(tool, modifier));
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
