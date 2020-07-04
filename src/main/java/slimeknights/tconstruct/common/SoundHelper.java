package slimeknights.tconstruct.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.SoundEvent;
import slimeknights.tconstruct.library.network.TinkerNetwork;

public class SoundHelper {

  public static void playSoundForAll(Entity entity, SoundEvent sound, float volume, float pitch) {
    entity.getEntityWorld().playSound(null, entity.getPosition(), sound, entity.getSoundCategory(), volume, pitch);
  }

  public static void playSoundForPlayer(Entity entity, SoundEvent sound, float volume, float pitch) {
    if (entity instanceof ServerPlayerEntity) {
      TinkerNetwork.getInstance().sendVanillaPacket(entity, new SPlaySoundEffectPacket(sound, entity.getSoundCategory(), entity.getPosX(), entity.getPosY(), entity.getPosZ(), volume, pitch));
    }
  }
}
