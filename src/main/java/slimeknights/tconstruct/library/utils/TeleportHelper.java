package slimeknights.tconstruct.library.utils;

import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import slimeknights.tconstruct.common.Sounds;

import java.util.Set;

public class TeleportHelper {
  private static final Set<RelativeMovement> PACKET_FLAGS = ImmutableSet.of(RelativeMovement.X, RelativeMovement.Y, RelativeMovement.Z);

  /** Randomly teleports an entity, mostly copied from chorus fruit */
  @CanIgnoreReturnValue
  public static boolean randomNearbyTeleport(LivingEntity living, ITeleportEventFactory factory) {
    return randomNearbyTeleport(living, factory, 16, 16);
  }

  /** Randomly teleports an entity, mostly copied from chorus fruit */
  @CanIgnoreReturnValue
  public static boolean randomNearbyTeleport(LivingEntity living, ITeleportEventFactory factory, int diameter, int chances) {
    Level level = living.level();
    if (level.isClientSide) {
      return true;
    }
    double posX = living.getX();
    double posY = living.getY();
    double posZ = living.getZ();

    RandomSource random = living.getRandom();
    float minHeight = level.getMinBuildHeight();
    float maxHeight = (level instanceof ServerLevel server ? (level.getMinBuildHeight() + server.getLogicalHeight()) : level.getMaxBuildHeight()) - 1;
    for(int i = 0; i < chances; ++i) {
      double x = posX + (random.nextDouble() - 0.5D) * diameter;
      double y = Mth.clamp(posY + (double)(random.nextInt(diameter) - 8), minHeight, maxHeight);
      double z = posZ + (random.nextDouble() - 0.5D) * diameter;
      if (living.isPassenger()) {
        living.stopRiding();
      }

      level.gameEvent(GameEvent.TELEPORT, living.position(), GameEvent.Context.of(living));
      EntityTeleportEvent event = factory.create(living, x, y, z);
      MinecraftForge.EVENT_BUS.post(event);
      if (!event.isCanceled() && living.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
        SoundEvent soundevent = Sounds.SLIME_TELEPORT.getSound();
        // sound where we left
        level.playSound(null, posX, posY, posZ, soundevent, living.getSoundSource(), 1.0F, 1.0F);
        // sound at destination
        living.playSound(soundevent, 1.0F, 1.0F);
        return true;
      }
    }
    return false;
  }

  /** Spawns particles around the entity for teleporting */
  private static void spawnParticles(Entity entity) {
    Level level = entity.level();
    if (level instanceof ServerLevel serverWorld) {
      for (int i = 0; i < 32; ++i) {
        serverWorld.sendParticles(ParticleTypes.PORTAL, entity.getX(), entity.getY() + level.random.nextDouble() * 2.0D, entity.getZ(), 1, level.random.nextGaussian(), 0.0D, level.random.nextGaussian(), 0);
      }
    }
  }

  /** Fires the teleport event, then teleports the player if it works */
  public static boolean tryTeleport(EntityTeleportEvent event) {
    MinecraftForge.EVENT_BUS.post(event);
    if (!event.isCanceled()) {
      // spawn particles at old location
      Entity entity = event.getEntity();
      spawnParticles(entity);

      // this logic only runs serverside, so need to use the server controller logic to move the player
      if (entity instanceof ServerPlayer playerMP) {
        playerMP.connection.teleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), playerMP.getYRot(), playerMP.getXRot(), PACKET_FLAGS);
      } else {
        entity.setPos(event.getTargetX(), event.getTargetY(), event.getTargetZ());
      }
      // particles at new location
      spawnParticles(entity);
      entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), Sounds.ENDERPORTING.getSound(), entity.getSoundSource(), 1f, 1f);
      return true;
    }
    return false;
  }

  /** Predicate to test if the entity can teleport, typically just fires a cancelable event */
  @FunctionalInterface
  public interface ITeleportEventFactory {
    EntityTeleportEvent create(LivingEntity entity, double x, double y, double z);
  }
}
