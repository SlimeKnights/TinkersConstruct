package slimeknights.tconstruct.tools.modifiers.ability.sling;

import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingAngleModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingForceModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.sling.SlingLaunchModifierHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.SlimeBounceHandler;
import slimeknights.tconstruct.tools.TinkerToolActions;

/** Add velocity in the direction you face */
public class SpringingModifier extends SlingModifier {

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (!tool.isBroken() && source == InteractionSource.RIGHT_CLICK) {
      GeneralInteractionModifierHook.startUsingWithDrawtime(tool, modifier.getId(), player, hand, 1f);
    }
    return InteractionResult.SUCCESS;
  }

  @Override
  public void beforeReleaseUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    Level level = entity.level();
    if (entity instanceof Player player && !player.isFallFlying()) {
      player.causeFoodExhaustion(0.2F);

      // must be sufficiently charged, not have too much knockback resistance, and not have any modifier zeroing its force
      float charge = getCharge(tool, modifier, timeLeft);
      if (charge > 0) {
        float multiplier = charge * 1.05f;
        float force = SlingForceModifierHook.modifySlingForce(tool, entity, entity, modifier, getPower(tool, player) * multiplier, multiplier);
        if (force > 0) {
          Vec3 look = player.getLookAngle().add(0, Math.max(0, 0.5 - force * 0.1), 0).normalize();

          // fling in the direction the player looks
          RandomSource random = player.getRandom();
          float inaccuracy = ModifierUtil.getInaccuracy(tool, player) * 0.0075f;
          Vec3 angle = SlingAngleModifierHook.modifySlingAngle(tool, entity, entity, modifier, force, multiplier, new Vec3(
            (look.x + random.nextGaussian() * inaccuracy),
            (look.y + random.nextGaussian() * inaccuracy) / 2f,
            (look.z + random.nextGaussian() * inaccuracy)
          ));
          player.push(force * angle.x, force * angle.y, force * angle.z);

          // if on the ground, get off the ground so jumping is not required before springing
          if (player.onGround()) {
            player.move(MoverType.SELF, new Vec3(0, 1.3f, 0));
          }

          // after sling callback
          SlingLaunchModifierHook.afterSlingLaunch(tool, entity, entity, modifier, force, multiplier, angle);

          SlimeBounceHandler.addBounceHandler(player);
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
    // play failure sound
    if (isActive(tool, modifier, activeModifier)) {
      level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), Sounds.SLIME_SLING.getSound(), entity.getSoundSource(), 1, 0.5f);
    }
  }
}
