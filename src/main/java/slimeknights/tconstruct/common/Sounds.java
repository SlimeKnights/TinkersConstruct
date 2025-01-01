package slimeknights.tconstruct.common;

import lombok.Getter;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registration.GeodeItemObject.BudSize;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/** All sounds registered by Tinkers, should be used instead of vanilla events when subtitles need to be distinguished */
@Mod.EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum Sounds {
  // blocks
  SAW("little_saw"),
  ITEM_FRAME_CLICK,
  CASTING_COOLS,
  CASTING_CLICKS,

  // earth crystals
  EARTH_CRYSTAL_CHIME("block.earth_crystal.chime"),
  SKY_CRYSTAL_CHIME("block.sky_crystal.chime"),
  ICHOR_CRYSTAL_CHIME("block.ichor_crystal.chime"),
  ENDER_CRYSTAL_CHIME("block.ender_crystal.chime"),

  // tools
  SLIME_SLING,
  SLIME_SLING_TELEPORT("slime_sling.teleport"),
  THROWBALL_THROW("throw.throwball"),
  SHURIKEN_THROW("throw.shuriken"),
  LONGBOW_CHARGE("longbow.charge"),
  CRYSTALSHOT,
  BONK,

  // modifiers
  NECROTIC_HEAL,
  ENDERPORTING,
  EXTRA_JUMP,

  // entity
  SLIME_TELEPORT,
  SLIMY_BOUNCE("slimy_bounce"),

  // equip sounds
  EQUIP_SLIME("equip.slime"),
  EQUIP_TRAVELERS("equip.travelers"),
  EQUIP_PLATE("equip.plate"),

  // unused
  TOY_SQUEAK,
  CROSSBOW_RELOAD,
  STONE_HIT,
  WOOD_HIT,
  CHARGED,
  DISCHARGE;

  @Getter
  private final SoundEvent sound;

  public static final SoundType EARTH_CRYSTAL = makeCrystalSound(0.75f);
  public static final Map<BudSize,SoundType> EARTH_CRYSTAL_CLUSTER = makeClusterSounds(0.75f);
  public static final SoundType SKY_CRYSTAL = makeCrystalSound(1.2f);
  public static final Map<BudSize,SoundType> SKY_CRYSTAL_CLUSTER = makeClusterSounds(1.2f);
  public static final SoundType ICHOR_CRYSTAL = makeCrystalSound(0.35f);
  public static final Map<BudSize,SoundType> ICHOR_CRYSTAL_CLUSTER = makeClusterSounds(0.35f);
  public static final SoundType ENDER_CRYSTAL = makeCrystalSound(1.45f);
  public static final Map<BudSize,SoundType> ENDER_CRYSTAL_CLUSTER = makeClusterSounds(1.45f);

  /** Creates a new event */
  private static SoundEvent createEvent(String name) {
    return SoundEvent.createVariableRangeEvent(TConstruct.getResource(name));
  }

  Sounds(String name) {
    sound = createEvent(name);
  }

  Sounds() {
    sound = createEvent(name().toLowerCase(Locale.US));
  }

  @SubscribeEvent
  public static void registerSounds(RegisterEvent event) {
    if (event.getRegistryKey() == Registries.SOUND_EVENT) {
      for (Sounds sound : values()) {
        ForgeRegistries.SOUND_EVENTS.register(sound.sound.getLocation(), sound.getSound());
      }
    }
  }

  /** Makes sound type for crystals */
  @SuppressWarnings("deprecation")  // sound events really aren't complex enough to need suppliers
  private static SoundType makeCrystalSound(float pitch) {
    return new SoundType(1.0f, pitch, SoundEvents.AMETHYST_BLOCK_BREAK, SoundEvents.AMETHYST_BLOCK_STEP, SoundEvents.AMETHYST_BLOCK_PLACE, SoundEvents.AMETHYST_BLOCK_HIT, SoundEvents.AMETHYST_BLOCK_FALL);
  }

  /** Makes sound type for clusters */
  @SuppressWarnings("deprecation")  // sound events really aren't complex enough to need suppliers
  private static Map<BudSize,SoundType> makeClusterSounds(float pitch) {
    Map<BudSize,SoundType> map = new EnumMap<>(BudSize.class);
    map.put(BudSize.CLUSTER, new SoundType(1.0f, pitch, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundEvents.AMETHYST_CLUSTER_STEP, SoundEvents.AMETHYST_CLUSTER_PLACE, SoundEvents.AMETHYST_CLUSTER_HIT, SoundEvents.AMETHYST_CLUSTER_FALL));
    map.put(BudSize.SMALL,   new SoundType(1.0f, pitch, SoundEvents.SMALL_AMETHYST_BUD_BREAK, SoundEvents.AMETHYST_CLUSTER_STEP, SoundEvents.SMALL_AMETHYST_BUD_PLACE, SoundEvents.AMETHYST_CLUSTER_HIT, SoundEvents.AMETHYST_CLUSTER_FALL));
    map.put(BudSize.MEDIUM,  new SoundType(1.0f, pitch, SoundEvents.MEDIUM_AMETHYST_BUD_BREAK, SoundEvents.AMETHYST_CLUSTER_STEP, SoundEvents.MEDIUM_AMETHYST_BUD_PLACE, SoundEvents.AMETHYST_CLUSTER_HIT, SoundEvents.AMETHYST_CLUSTER_FALL));
    map.put(BudSize.LARGE,   new SoundType(1.0f, pitch, SoundEvents.LARGE_AMETHYST_BUD_BREAK, SoundEvents.AMETHYST_CLUSTER_STEP, SoundEvents.LARGE_AMETHYST_BUD_PLACE, SoundEvents.AMETHYST_CLUSTER_HIT, SoundEvents.AMETHYST_CLUSTER_FALL));
    return map;
  }
}
