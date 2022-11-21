package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class EnderferenceModifier extends Modifier {
  public EnderferenceModifier() {
    MinecraftForge.EVENT_BUS.addListener(EnderferenceModifier::onTeleport);
  }

  private static void onTeleport(EntityTeleportEvent event) {
    if (event.getEntity() instanceof LivingEntity living && living.hasEffect(TinkerModifiers.enderferenceEffect.get())) {
      event.setCanceled(true);
    }
  }

  @Override
  public float beforeEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    LivingEntity entity = context.getLivingTarget();
    if (entity != null) {
      // hack: do not want them teleporting from this hit
      TinkerModifiers.enderferenceEffect.get().apply(entity, 1, 0, true);
    }
    return knockback;
  }

  @Override
  public void failedEntityHit(IToolStackView tool, int level, ToolAttackContext context) {
    LivingEntity entity = context.getLivingTarget();
    if (entity != null) {
      entity.removeEffect(TinkerModifiers.enderferenceEffect.get());
    }
  }

  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    LivingEntity entity = context.getLivingTarget();
    if (entity != null) {
      // 5 seconds of interference per level, affect all entities as players may teleport too
      entity.addEffect(new MobEffectInstance(TinkerModifiers.enderferenceEffect.get(), level * 100, 0, false, true, true));
    }
    return 0;
  }
}
