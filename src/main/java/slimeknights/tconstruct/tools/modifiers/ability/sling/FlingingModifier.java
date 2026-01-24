package slimeknights.tconstruct.tools.modifiers.ability.sling;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingAngleModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingForceModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingLaunchModifierHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.SlimeBounceHandler;
import slimeknights.tconstruct.tools.TinkerToolActions;

/** Add velocity opposite of the targeted block */
public class FlingingModifier extends SlingModifier {
  @Override
  public void beforeReleaseUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    Level level = entity.level();
    if (entity.onGround() && entity instanceof Player player) {
      // check if player was targeting a block
      BlockHitResult mop = ModifiableItem.blockRayTrace(level, player, ClipContext.Fluid.NONE);
      if (mop.getType() == HitResult.Type.BLOCK) {
        // must be sufficiently charged, not have too much knockback resistance, and not have any modifier zeroing its force
        float charge = getCharge(tool, modifier, timeLeft);
        if (charge > 0) {
          float multiplier = charge * 4;
          float force = SlingForceModifierHook.modifySlingForce(tool, entity, entity, modifier, getPower(tool, player) * multiplier, multiplier);
          if (force > 0) {
            Vec3 vec = player.getLookAngle().normalize();
            float inaccuracy = ModifierUtil.getInaccuracy(tool, player) * 0.0075f;
            RandomSource random = player.getRandom();

            // we fling the inverted player look vector
            Vec3 angle = SlingAngleModifierHook.modifySlingAngle(tool, entity, entity, modifier, force, multiplier, new Vec3(
              -(vec.x + random.nextGaussian() * inaccuracy),
              -(vec.y + random.nextGaussian() * inaccuracy) / 3f,
              -(vec.z + random.nextGaussian() * inaccuracy)
            ));
            player.push(angle.x * force, angle.y * force, angle.z * force);
            SlimeBounceHandler.addBounceHandler(player);

            // modifier callbacks
            SlingLaunchModifierHook.afterSlingLaunch(tool, entity, entity, modifier, force, multiplier, angle);

            if (!level.isClientSide) {
              level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.SLIME_SLING.getSound(), player.getSoundSource(), 1, 1);
              player.causeFoodExhaustion(0.2F);
              player.getCooldowns().addCooldown(tool.getItem(), 3);
              ToolDamageUtil.damageAnimated(tool, 1, entity);
            }
            // apply drill attack if the modifier is present
            if (ModifierUtil.canPerformAction(tool, TinkerToolActions.DRILL_ATTACK)) {
              player.startAutoSpinAttack(20);
            }
            return;
          }
        }
      }
    }
    // play failure sound
    if (isActive(tool, modifier, activeModifier)) {
      level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), Sounds.SLIME_SLING.getSound(), entity.getSoundSource(), 1, 0.5f);
    }
  }
}
