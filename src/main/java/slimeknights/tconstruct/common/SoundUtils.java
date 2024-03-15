package slimeknights.tconstruct.common;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;

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
}
