package slimeknights.tconstruct.tools.modules.armor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.OverslimeModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/** Module converting overslime into bonus protection. */
public record OvershieldModule(float protection, int consumed) implements ModifierModule, ProtectionModifierHook, TooltipModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<FlameBarrierModule>defaultHooks(ModifierHooks.PROTECTION, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<OvershieldModule> LOADER = RecordLoadable.create(
    FloatLoadable.FROM_ZERO.requiredField("protection", OvershieldModule::protection),
    IntLoadable.FROM_ONE.requiredField("consumed", OvershieldModule::consumed),
    OvershieldModule::new);

  @Override
  public RecordLoadable<OvershieldModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    if (DamageSourcePredicate.CAN_PROTECT.matches(source)) {
      int overslime = OverslimeModule.INSTANCE.getAmount(tool);
      if (overslime > 0) {
        int consumed = Math.min(overslime, this.consumed);
        // scale the modifier value based on the consumed overslime
        modifierValue += protection * consumed / consumed;
        // remove overslime from the tool
        OverslimeModule.INSTANCE.removeAmount(tool, consumed);
      }
    }
    return modifierValue;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    float protection = this.protection;
    if (tooltipKey == TooltipKey.SHIFT) {
      int overslime = OverslimeModule.INSTANCE.getAmount(tool);
      if (overslime == 0) {
        return;
      }
      protection *= Math.min(overslime, consumed) / (float) consumed;
    }
    ProtectionModule.addResistanceTooltip(tool, modifier.getModifier(), protection, player, tooltip);
  }
}
