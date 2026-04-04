package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.library.events.teleport.EnderdodgingTeleportEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TeleportHelper;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.tools.modules.armor.TeleportDodgeModule;

/** @deprecated use {@link TeleportDodgeModule} */
@Deprecated
public class EnderdodgingModifier extends NoLevelsModifier implements OnAttackedModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(TeleportDodgeModule.builder().damageSource(DamageSourcePredicate.IS_INDIRECT).flat(15 * 20));
    hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // teleport randomly from other damage
    LivingEntity self = context.getEntity();
    if (!self.hasEffect(TinkerEffects.enderference.get()) && source.getEntity() instanceof LivingEntity && RANDOM.nextInt(10) == 0) {
      if (TeleportHelper.randomNearbyTeleport(context.getEntity(), (e, x, y, z) -> new EnderdodgingTeleportEvent(e, x, y, z, modifier))) {
        TinkerEffects.enderference.get().apply(self, 15 * 20, 1, true);
      }
    }
  }
}
