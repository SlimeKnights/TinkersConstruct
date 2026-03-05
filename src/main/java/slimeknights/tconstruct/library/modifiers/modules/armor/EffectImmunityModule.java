package slimeknights.tconstruct.library.modifiers.modules.armor;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import net.minecraft.world.effect.MobEffect;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.function.Supplier;

/**
 * Module for armor modifiers that makes the wearer immune to a mob effect
 */
public record EffectImmunityModule(MobEffect effect, LevelingInt maxLevel, ModifierCondition<IToolStackView> condition) implements ModifierModule, EquipmentChangeModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MobDisguiseModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  public static final ComputableDataKey<Multiset<MobEffect>> EFFECT_IMMUNITY = TConstruct.createKey("effect_immunity", HashMultiset::create);
  private static final LevelingInt ANY_LEVEL = LevelingInt.flat(255);
  public static final RecordLoadable<EffectImmunityModule> LOADER = RecordLoadable.create(
    Loadables.MOB_EFFECT.requiredField("effect", EffectImmunityModule::effect),
    LevelingInt.LOADABLE.defaultField("max_level", ANY_LEVEL, false, EffectImmunityModule::maxLevel),
    ModifierCondition.TOOL_FIELD,
    EffectImmunityModule::new);

  /** @deprecated use {@link #EffectImmunityModule(MobEffect, LevelingInt, ModifierCondition)} */
  @Deprecated(forRemoval = true)
  public EffectImmunityModule(MobEffect effect, ModifierCondition<IToolStackView> condition) {
    this(effect, ANY_LEVEL, condition);
  }

  public EffectImmunityModule(MobEffect effect, LevelingInt maxLevel) {
    this(effect, maxLevel, ModifierCondition.ANY_TOOL);
  }

  public EffectImmunityModule(MobEffect effect) {
    this(effect, ANY_LEVEL);
  }

  public EffectImmunityModule(Supplier<? extends MobEffect> effect, LevelingInt maxLevel) {
    this(effect.get(), maxLevel);
  }

  public EffectImmunityModule(Supplier<? extends MobEffect> effect) {
    this(effect.get());
  }

  @Override
  public RecordLoadable<EffectImmunityModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (!tool.isBroken() && ArmorLevelModule.validSlot(tool, context.getChangedSlot(), TinkerTags.Items.HELD_ARMOR) && condition.matches(tool, modifier)) {
      TinkerDataCapability.Holder data = context.getDataHolder();
      if (data != null) {
        data.computeIfAbsent(EFFECT_IMMUNITY).add(effect, maxLevel.compute(modifier));
      }
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (!tool.isBroken() && ArmorLevelModule.validSlot(tool, context.getChangedSlot(), TinkerTags.Items.HELD_ARMOR) && condition.matches(tool, modifier)) {
      TinkerDataCapability.Holder data = context.getDataHolder();
      if (data != null) {
        Multiset<MobEffect> effects = data.get(EFFECT_IMMUNITY);
        if (effects != null) {
          effects.remove(effect, maxLevel.compute(modifier));
        }
      }
    }
  }
}
