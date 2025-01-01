package slimeknights.tconstruct.library.tools.definition.module.weapon;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.List;

/** Deals damage in a circle around the primary target */
public record CircleWeaponAttack(float diameter) implements MeleeHitToolHook, ToolModule {
  public static final RecordLoadable<CircleWeaponAttack> LOADER = RecordLoadable.create(FloatLoadable.ANY.defaultField("diameter", 0f, true, CircleWeaponAttack::diameter), CircleWeaponAttack::new);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<CircleWeaponAttack>defaultHooks(ToolHooks.MELEE_HIT);

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<CircleWeaponAttack> getLoader() {
    return LOADER;
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ToolAttackContext context, float damage) {
    // only need fully charged for scythe sweep, easier than sword sweep
    if (context.isFullyCharged()) {
      // basically sword sweep logic, just deals full damage to all entities
      double range = diameter + tool.getModifierLevel(TinkerModifiers.expanded.getId());
      // allow having no range until modified with range
      if (range > 0) {
        double rangeSq = range * range;
        LivingEntity attacker = context.getAttacker();
        Entity target = context.getTarget();
        Level level = attacker.level();
        for (LivingEntity aoeTarget : level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(range, 0.25D, range))) {
          if (aoeTarget != attacker && aoeTarget != target && !attacker.isAlliedTo(aoeTarget)
              && !(aoeTarget instanceof ArmorStand stand && stand.isMarker()) && target.distanceToSqr(aoeTarget) < rangeSq) {
            float angle = attacker.getYRot() * ((float)Math.PI / 180F);
            aoeTarget.knockback(0.4F, Mth.sin(angle), -Mth.cos(angle));
            // TODO: do we want to bring back the behavior where circle returns success if any AOE target is hit?
            ToolAttackUtil.extraEntityAttack(tool, attacker, context.getHand(), aoeTarget);
          }
        }

        level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, attacker.getSoundSource(), 1.0F, 1.0F);
        if (attacker instanceof Player player) {
          player.sweepAttack();
        }
      }
    }
  }
}
