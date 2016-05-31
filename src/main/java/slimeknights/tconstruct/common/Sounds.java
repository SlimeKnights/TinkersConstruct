package slimeknights.tconstruct.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import slimeknights.tconstruct.library.Util;

public abstract class Sounds {
  private Sounds() {}

  public static final SoundEvent saw = sound("little_saw");
  //public static final SoundEvent anvil_use = "random.anvil_use";
  //public static final SoundEvent nom = "random.eat";
  //public static final SoundEvent slime_big = "mob.slime.big";
  //public static final SoundEvent slime_small = "mob.slime.small";

  public static final SoundEvent frypan_boing = sound("frypan_hit");
  public static final SoundEvent toy_squeak = sound("toy_squeak");
  public static final SoundEvent slimesling = sound("slimesling");
  public static final SoundEvent shocking_charged = sound("charged");
  public static final SoundEvent shocking_discharge = sound("discharge");

  private static SoundEvent sound(String name) {
    ResourceLocation location = Util.getResource(name);
    SoundEvent event = new SoundEvent(location);
    SoundEvent.REGISTRY.register(-1, location, event);
    return event;
  }

  public static void playSoundForAll(Entity entity, SoundEvent sound, float volume, float pitch) {
    entity.worldObj.playSound(null, entity.getPosition(), sound, entity.getSoundCategory(), volume, pitch);
  }

  public static void PlaySoundForPlayer(Entity entity, SoundEvent sound, float volume, float pitch) {
    if(entity instanceof EntityPlayerMP) {
      ((EntityPlayerMP) entity).connection.sendPacket(new SPacketSoundEffect(sound, entity.getSoundCategory(), entity.posX, entity.posY, entity.posZ, volume, pitch));
    }
  }
}
