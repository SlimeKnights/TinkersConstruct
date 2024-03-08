package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.data.ModifierMaxLevel;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

public class ShulkingModifier extends AbstractProtectionModifier<ModifierMaxLevel> implements ProtectionModifierHook, TooltipModifierHook {
  private static final TinkerDataKey<ModifierMaxLevel> KEY = TConstruct.createKey("shulking");
  public ShulkingModifier() {
    super(KEY);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LivingHurtEvent.class, ShulkingModifier::onAttack);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.PROTECTION, TinkerHooks.TOOLTIP);
  }

  @Override
  protected ModifierMaxLevel createData() {
    return new ModifierMaxLevel();
  }

  @Override
  public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    if (context.getEntity().isCrouching() && !source.isBypassMagic() && !source.isBypassInvul()) {
      modifierValue += modifier.getEffectiveLevel(tool) * 2.5f;
    }
    return modifierValue;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    ProtectionModule.addResistanceTooltip(tool, this, modifier.getEffectiveLevel(tool) * 2.5f, player, tooltip);
  }

  private static void onAttack(LivingHurtEvent event) {
    // if the attacker is crouching, deal less damage
    Entity attacker = event.getSource().getEntity();
    if (attacker != null && attacker.isCrouching()) {
      attacker.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        ModifierMaxLevel max = data.get(KEY);
        if (max != null) {
          event.setAmount(event.getAmount() * (1 - (max.getMax() * 0.1f)));
        }
      });
    }
  }
}
