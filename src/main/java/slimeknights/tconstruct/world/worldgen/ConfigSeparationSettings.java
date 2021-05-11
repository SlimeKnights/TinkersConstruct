package slimeknights.tconstruct.world.worldgen;

import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

/** Separation settings that support setting from the config */
public class ConfigSeparationSettings extends StructureSeparationSettings {
  private final IntValue config;
  public ConfigSeparationSettings(IntValue config, int separation, int salt) {
    super(separation * 2, separation, salt);
    this.config = config;
  }

  @Override
  public int func_236668_a_() {
    return this.config.get();
  }
}
