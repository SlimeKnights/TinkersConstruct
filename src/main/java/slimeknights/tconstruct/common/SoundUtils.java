package slimeknights.tconstruct.common;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import slimeknights.tconstruct.library.network.TinkerNetwork;

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
    entity.getEntityWorld().playSound(null, entity.getBlockPos(), sound, entity.getSoundCategory(), volume, pitch);
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
        new PlaySoundS2CPacket(sound, entity.getSoundCategory(), entity.getX(), entity.getY(),
          entity.getZ(), volume, pitch));
    }
  }
}
