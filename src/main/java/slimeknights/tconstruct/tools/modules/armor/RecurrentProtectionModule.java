package slimeknights.tconstruct.tools.modules.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerEffect;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.SlotInChargeModule.SlotInCharge;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.stats.ToolType;

import javax.annotation.Nullable;
import java.util.List;

/** Module for boosting protection after taking damage */
public record RecurrentProtectionModule(LevelingValue percent, LevelingInt duration) implements ModifierModule, ModifyDamageModifierHook, TooltipModifierHook {
  private static final Component PROTECTION = TConstruct.makeTranslation("modifier", "recurrent_protection.resistance");
  private static final TinkerDataKey<SlotInCharge> SLOT_KEY = TConstruct.createKey("momentum");
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<RecurrentProtectionModule>defaultHooks(ModifierHooks.MODIFY_HURT, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<RecurrentProtectionModule> LOADER = RecordLoadable.create(
    LevelingValue.LOADABLE.requiredField("percent", RecurrentProtectionModule::percent),
    LevelingInt.LOADABLE.requiredField("duration", RecurrentProtectionModule::duration),
    RecurrentProtectionModule::new);

  @Override
  public RecordLoadable<RecurrentProtectionModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void addModules(Builder builder) {
    builder.addModule(new SlotInChargeModule(SLOT_KEY));
  }

  @Override
  public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
      int level = SlotInChargeModule.getLevel(context.getTinkerData(), SLOT_KEY, slotType);
      if (level > 0) {
        // step 1: reduce damage based on the current effect level
        MobEffect effect = TinkerModifiers.momentumEffect.get(ToolType.ARMOR);
        LivingEntity entity = context.getEntity();
        amount -= TinkerEffect.getLevel(entity, effect);

        // step 2: apply momentum based on damage taken
        int reduction = (int)(percent.compute(level) * amount);
        if (reduction > 0) {
          entity.addEffect(new MobEffectInstance(effect, duration.compute(level), reduction - 1, false, false, true));
        }
      }
    }
    return amount;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    // if not holding shift or no player, display the percent amount
    if (player == null || tooltipKey != TooltipKey.SHIFT) {
      TooltipModifierHook.addPercentBoost(modifier.getModifier(), PROTECTION, this.percent.compute(modifier.getLevel()), tooltip);
    } else {
      // if we have a player, use the current effect level for reduction display
      int level = TinkerEffect.getLevel(player, TinkerModifiers.momentumEffect.get(ToolType.ARMOR));
      if (level > 0) {
        TooltipModifierHook.addFlatBoost(modifier.getModifier(), PROTECTION, this.percent.compute(modifier.getLevel()), tooltip);
      }
    }
  }
}
