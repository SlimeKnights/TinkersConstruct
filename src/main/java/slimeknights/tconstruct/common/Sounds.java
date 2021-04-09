package slimeknights.tconstruct.common;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.TConstruct;

//@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum Sounds {
  SAW("little_saw"),
  FRYING_PAN_BOING("frypan_hit"),
  TOY_SQUEAK("toy_squeak"),
  SLIME_SLING("slime_sling"),
  CHARGED("charged"),
  DISCHARGE("discharge"),
  STONE_HIT("stone_hit"),
  WOOD_HIT("wood_hit"),
  CROSSBOW_RELOAD("crossbow_reload");

  private SoundEvent sound;

  Sounds(String name) {
    Identifier loc = new Identifier(TConstruct.modID, name);

    sound = new SoundEvent(loc).setRegistryName(name);
  }

  public SoundEvent getSound() {
    return sound;
  }

  @SubscribeEvent
  public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
    for (Sounds sound : values()) {
      event.getRegistry().register(sound.getSound());
    }
  }
}
