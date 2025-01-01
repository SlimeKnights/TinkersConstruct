package slimeknights.tconstruct.tools.modules.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modifiers.traits.melee.ConductingModifier;

import javax.annotation.Nullable;
import java.util.List;

/** Module for boosting damage while on fire, based on the fire amount. TODO: consider merging into protection module via a formula builder */
public record FlameBarrierModule(LevelingValue amount) implements ModifierModule, ProtectionModifierHook, TooltipModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<FlameBarrierModule>defaultHooks(ModifierHooks.PROTECTION, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<FlameBarrierModule> LOADER = RecordLoadable.create(LevelingValue.LOADABLE.directField(FlameBarrierModule::amount), FlameBarrierModule::new);

  @Override
  public RecordLoadable<FlameBarrierModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    if (!source.is(DamageTypeTags.IS_FIRE) && DamageSourcePredicate.CAN_PROTECT.matches(source)) {
      float amount = this.amount.compute(modifier.getEffectiveLevel());
      if (amount != 0) {
        modifierValue += ConductingModifier.bonusScale(context.getEntity()) * amount;
      }
    }
    return modifierValue;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    float bonus = amount.compute(modifier.getEffectiveLevel());
    // client only knows if the player is on fire or not, not the amount of fire, so just show full if on fire
    if (bonus > 0 && (player == null || tooltipKey != TooltipKey.SHIFT || player.getRemainingFireTicks() > 0)) {
      ProtectionModule.addResistanceTooltip(tool, modifier.getModifier(), bonus, player, tooltip);
    }
  }
}
