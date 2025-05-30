package slimeknights.tconstruct.tools.modifiers.ability.sling;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.events.teleport.SlingModifierTeleportEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Teleport through blocks in the look direction */
public class WarpingModifier extends SlingModifier {
  @Override
  public void beforeReleaseUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    Level level = entity.level();
    if (!level.isClientSide && entity instanceof ServerPlayer player) {
      float f = getForce(tool, modifier, entity, timeLeft, false) * 6;
      if (f > 0) {
        Vec3 look = player.getLookAngle();
        float inaccuracy = ModifierUtil.getInaccuracy(tool, player) * 0.0075f;
        RandomSource random = player.getRandom();
        double offX = (look.x + random.nextGaussian() * inaccuracy) * f;
        double offY = (look.y + random.nextGaussian() * inaccuracy) * f + 1; // add extra to help with bad collisions
        double offZ = (look.z + random.nextGaussian() * inaccuracy) * f;

        // find teleport target
        BlockPos furthestPos = null;
        while (Math.abs(offX) > .5 || Math.abs(offY) > .5 || Math.abs(offZ) > .5) { // while not too close to player
          BlockPos posAttempt = BlockPos.containing(player.getX() + offX, player.getY() + offY, player.getZ() + offZ);
          // if we do not have a position yet, see if this one is valid
          if (furthestPos == null) {
            if (level.getWorldBorder().isWithinBounds(posAttempt) && !level.getBlockState(posAttempt).isSuffocating(level, posAttempt)) {
              furthestPos = posAttempt;
            }
          } else {
            // if we already have a position, clear if the new one is unbreakable
            if (level.getBlockState(posAttempt).getDestroySpeed(level, posAttempt) == -1) {
              furthestPos = null;
            }
          }

          // update for next iteration
          offX -= (Math.abs(offX) > .25 ? (offX >= 0 ? 1 : -1) * .25 : 0);
          offY -= (Math.abs(offY) > .25 ? (offY >= 0 ? 1 : -1) * .25 : 0);
          offZ -= (Math.abs(offZ) > .25 ? (offZ >= 0 ? 1 : -1) * .25 : 0);
        }

        // get furthest teleportable block
        if (furthestPos != null) {
          SlingModifierTeleportEvent event = new SlingModifierTeleportEvent(player, furthestPos.getX() + 0.5f, furthestPos.getY(), furthestPos.getZ() + 0.5f, tool, modifier);
          MinecraftForge.EVENT_BUS.post(event);
          if (!event.isCanceled()) {
            player.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());

            // particle effect from EnderPearlEntity
            for (int i = 0; i < 32; ++i) {
              level.addParticle(ParticleTypes.PORTAL, player.getX(), player.getY() + level.random.nextDouble() * 2.0D, player.getZ(), level.random.nextGaussian(), 0.0D, level.random.nextGaussian());
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.SLIME_SLING_TELEPORT.getSound(), player.getSoundSource(), 1f, 1f);
            player.causeFoodExhaustion(0.2F);
            player.getCooldowns().addCooldown(tool.getItem(), 3);
            ToolDamageUtil.damageAnimated(tool, 1, entity);
            return;
          }
        }
      }
      if (isActive(tool, modifier, activeModifier)) {
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), Sounds.SLIME_SLING_TELEPORT.getSound(), entity.getSoundSource(), 1, 0.5f);
      }
    }
  }
}
