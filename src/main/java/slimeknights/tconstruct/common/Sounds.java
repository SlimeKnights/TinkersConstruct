package slimeknights.tconstruct.common;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import slimeknights.tconstruct.TConstruct;

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

    sound = new SoundEvent(loc);

    Registry.register(Registry.SOUND_EVENT, new Identifier(TConstruct.modID, name), sound);
  }

  public SoundEvent getSound() {
    return sound;
  }
}
