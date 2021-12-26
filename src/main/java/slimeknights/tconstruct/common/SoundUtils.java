package slimeknights.tconstruct.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.SoundEvent;
import slimeknights.tconstruct.common.network.TinkerNetwork;

public class SoundUtils {

  /**
   * Plays a sound for all entity's around a given entity's position
   *
   * @param entity the entity to play the sound from
   * @param sound the sound event play
   * @param volume the volume of the sound
   * @param pitch the pitch of the sound
   */
  public static void playSoundForAll(Entity entity, SoundEvent sound, float volume, float pitch) {
    entity.getCommandSenderWorld().playSound(null, entity.blockPosition(), sound, entity.getSoundSource(), volume, pitch);
  }

  /**
   * Plays a sound to the selected player only
   *
   * @param entity the entity to play at
   * @param sound the sound to play
   * @param volume the volume of the sound
   * @param pitch the pitch of the sound
   */
  public static void playSoundForPlayer(Entity entity, SoundEvent sound, float volume, float pitch) {
    if (entity instanceof ServerPlayerEntity) {
      TinkerNetwork.getInstance().sendVanillaPacket(entity,
        new SPlaySoundEffectPacket(sound, entity.getSoundSource(), entity.getX(), entity.getY(),
          entity.getZ(), volume, pitch));
    }
  }
}
