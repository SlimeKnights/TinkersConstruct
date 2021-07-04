package slimeknights.tconstruct.common;

import lombok.Getter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.TConstruct;

@Mod.EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
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

  @Getter
  private final SoundEvent sound;

  Sounds(String name) {
    ResourceLocation registryName = TConstruct.getResource(name);
    sound = new SoundEvent(registryName).setRegistryName(registryName);
  }

  @SubscribeEvent
  public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
    for (Sounds sound : values()) {
      event.getRegistry().register(sound.getSound());
    }
  }
}
