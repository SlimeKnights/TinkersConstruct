package slimeknights.tconstruct.library.modifiers.modules.build;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
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
import slimeknights.mantle.util.LogicHelper;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.HarvestEnchantmentsModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.IntLevelModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** Modules that add enchantments to a tool. */
public interface EnchantmentModule extends ModifierModule, IntLevelModule, ConditionalModule<IToolStackView> {
  /* Common fields */
  LoadableField<Enchantment,EnchantmentModule> ENCHANTMENT = Loadables.ENCHANTMENT.requiredField("name", EnchantmentModule::enchantment);
  LoadableField<IJsonPredicate<BlockState>,EnchantmentModule> BLOCK = BlockPredicate.LOADER.defaultField("block", EnchantmentModule::block);
  LoadableField<IJsonPredicate<LivingEntity>,EnchantmentModule> HOLDER = LivingEntityPredicate.LOADER.defaultField("holder", EnchantmentModule::holder);

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
  class Builder extends ModuleBuilder.Stack<Builder> {
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

    /** Builds a module for a constant enchantment which ignores its protection value */
    public Protection protection() {
      if (block != BlockPredicate.ANY || holder != LivingEntityPredicate.ANY) {
        throw new IllegalStateException("Cannot build a constant enchantment module with block or holder conditions");
      }
      return new Protection(enchantment, level, condition);
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
  @Accessors(fluent = true)
  @Getter
  @RequiredArgsConstructor
  class Constant implements EnchantmentModule, EnchantmentModifierHook {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<Constant>defaultHooks(ModifierHooks.ENCHANTMENTS);
    public static final RecordLoadable<Constant> LOADER = RecordLoadable.create(ENCHANTMENT, IntLevelModule.FIELD, ModifierCondition.TOOL_FIELD, Constant::new);
    private final Enchantment enchantment;
    private final int level;
    private final ModifierCondition<IToolStackView> condition;

    public Constant(Enchantment enchantment, int level) {
      this(enchantment, level, ModifierCondition.ANY_TOOL);
    }

    @Override
    public int updateEnchantmentLevel(IToolStackView tool, ModifierEntry modifier, Enchantment enchantment, int level) {
      if (enchantment == this.enchantment() && condition().matches(tool, modifier)) {
        level += getLevel(modifier);
      }
      return level;
    }

    @Override
    public void updateEnchantments(IToolStackView tool, ModifierEntry modifier, Map<Enchantment,Integer> map) {
      if (condition().matches(tool, modifier)) {
        EnchantmentModifierHook.addEnchantment(map, this.enchantment(), getLevel(modifier));
      }
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<Constant> getLoader() {
      return LOADER;
    }
  }

  /** Constant enchantment which cancels out the protection value */
  class Protection extends Constant implements ProtectionModifierHook {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<Protection>defaultHooks(ModifierHooks.ENCHANTMENTS, ModifierHooks.PROTECTION);
    public static final RecordLoadable<Constant> LOADER = RecordLoadable.create(ENCHANTMENT, IntLevelModule.FIELD, ModifierCondition.TOOL_FIELD, Protection::new);
    public Protection(Enchantment enchantment, int level, ModifierCondition<IToolStackView> condition) {
      super(enchantment, level, condition);
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<Constant> getLoader() {
      return LOADER;
    }

    @Override
    public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
      if (condition().matches(tool, modifier)) {
        int subtractLevel = getLevel(modifier);
        Enchantment enchantment = enchantment();
        if (subtractLevel > 0 && LogicHelper.isInList(enchantment.slots, slotType) && !source.isBypassEnchantments()) {
          modifierValue -= enchantment.getDamageProtection(subtractLevel, source);
        }
      }
      return modifierValue;
    }
  }

  /** Enchantment module that can condition on the block mined or the entity mining. */
  record MainHandHarvest(Enchantment enchantment, int level, ModifierCondition<IToolStackView> condition, ResourceLocation conditionFlag, IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder) implements EnchantmentModule, EnchantmentModifierHook, BlockHarvestModifierHook {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MainHandHarvest>defaultHooks(ModifierHooks.ENCHANTMENTS, ModifierHooks.BLOCK_HARVEST);
    public static final RecordLoadable<MainHandHarvest> LOADER = RecordLoadable.create(ENCHANTMENT, IntLevelModule.FIELD, ModifierCondition.TOOL_FIELD, Loadables.RESOURCE_LOCATION.requiredField("condition_flag", MainHandHarvest::conditionFlag), BLOCK, HOLDER, MainHandHarvest::new);

    @Override
    public void startHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
      if (condition.matches(tool, modifier) && block.matches(context.getState()) && holder.matches(context.getLiving())) {
        tool.getPersistentData().putBoolean(conditionFlag, true);
      }
      BlockHarvestModifierHook.super.startHarvest(tool, modifier, context);
    }

    @Override
    public void finishHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, int harvested) {
      tool.getPersistentData().remove(conditionFlag);
    }

    @Override
    public int updateEnchantmentLevel(IToolStackView tool, ModifierEntry modifier, Enchantment enchantment, int level) {
      if (enchantment == this.enchantment() && tool.getPersistentData().getBoolean(conditionFlag)) {
        level += getLevel(modifier);
      }
      return level;
    }

    @Override
    public void updateEnchantments(IToolStackView tool, ModifierEntry modifier, Map<Enchantment,Integer> map) {
      if (tool.getPersistentData().getBoolean(conditionFlag)) {
        EnchantmentModifierHook.addEnchantment(map, this.enchantment(), getLevel(modifier));
      }
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<MainHandHarvest> getLoader() {
      return LOADER;
    }
  }

  /** Enchantment module that can condition on the block mined or the entity mining on armor. Requires the harvesting be done with a tinker tool. */
  record ArmorHarvest(Enchantment enchantment, int level, ModifierCondition<IToolStackView> condition, Set<EquipmentSlot> slots, IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder) implements EnchantmentModule, HarvestEnchantmentsModifierHook {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ArmorHarvest>defaultHooks(ModifierHooks.HARVEST_ENCHANTMENTS);
    public static final RecordLoadable<ArmorHarvest> LOADER = RecordLoadable.create(ENCHANTMENT, IntLevelModule.FIELD, ModifierCondition.TOOL_FIELD, TinkerLoadables.EQUIPMENT_SLOT_SET.requiredField("slots", ArmorHarvest::slots), BLOCK, HOLDER, ArmorHarvest::new);

    @Override
    public void updateHarvestEnchantments(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, EquipmentContext equipment, EquipmentSlot slot, Map<Enchantment,Integer> map) {
      if (slots.contains(slot) && condition.matches(tool, modifier) && block.matches(context.getState()) && holder.matches(context.getLiving())) {
        EnchantmentModifierHook.addEnchantment(map, enchantment, getLevel(modifier));
      }
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public RecordLoadable<ArmorHarvest> getLoader() {
      return LOADER;
    }
  }
}
