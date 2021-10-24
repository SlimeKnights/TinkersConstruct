package slimeknights.tconstruct.common;

import lombok.Getter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.TConstruct;

import java.util.Locale;

/** All sounds registered by Tinkers, should be used instead of vanilla events when subtitles need to be distinguished */
@Mod.EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum Sounds {
  // blocks
  SAW("little_saw"),
  ITEM_FRAME_CLICK,
  CASTING_COOLS,

  // tools
  SLIME_SLING,
  SLIME_SLING_TELEPORT("slime_sling.teleport"),
  THROWBALL_THROW("throw.throwball"),
  SHURIKEN_THROW("throw.shuriken"),

  // modifiers
  NECROTIC_HEAL,
  ENDERPORTING,

  // entity
  SLIME_TELEPORT,
  SLIMY_BOUNCE("slimy_bounce"),

  // equip sounds
  EQUIP_SLIME("equip.slime"),

  // unused
  FRYING_PAN_BOING("frypan_hit"),
  TOY_SQUEAK,
  CROSSBOW_RELOAD,
  STONE_HIT,
  WOOD_HIT,
  CHARGED,
  DISCHARGE;

  @Getter
  private final SoundEvent sound;

  Sounds(String name) {
    ResourceLocation registryName = TConstruct.getResource(name);
    sound = new SoundEvent(registryName).setRegistryName(registryName);
  }

  Sounds() {
    String name = name().toLowerCase(Locale.US);
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
