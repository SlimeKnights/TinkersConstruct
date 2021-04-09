package slimeknights.tconstruct.fluids;

import alexiil.mc.lib.attributes.fluid.volume.FluidTemperature;
import net.minecraft.block.Material;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidAttributes;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.registration.object.MantleFluid;
import slimeknights.mantle.util.FluidProperties;
import slimeknights.tconstruct.TConstruct;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.fluids.fluids.SlimeFluid;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.SlimeType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Contains all fluids used throughout the mod
 */
public final class TinkerFluids extends TinkerModule {
  static final Logger log = Util.getLogger("tinker_fluids");

  public TinkerFluids() {
    ForgeMod.enableMilkFluid();
  }
  // basic
  public static final FluidObject<MantleFluid> blood = Registry.register(Registry.FLUID, new Identifier(TConstruct.modID, "blood"), coolBuilder().setRenderColor(0xff540000).density(1200).viscosity(1200).temperature(336), Material.WATER, 0);

  // slime -  note second name parameter is forge tag name
  public static final FluidObject<MantleFluid> earthSlime = FLUIDS.register("earth_slime", "slime", coolBuilder().setRenderColor(0xef76be6d).density(1400).viscosity(1400).temperature(350), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER, 0);
  public static final FluidObject<MantleFluid> skySlime = FLUIDS.register("sky_slime", coolBuilder().setRenderColor(0xef67f0f5).density(1500).viscosity(1500).temperature(310), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER, 0);
  public static final FluidObject<MantleFluid> enderSlime = FLUIDS.register("ender_slime", coolBuilder().setRenderColor(0xefd236ff).density(1600).viscosity(1600).temperature(370), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER, 0);
  public static final FluidObject<MantleFluid> magmaCream  = FLUIDS.register("magma_cream", "magma", FluidAttributes
    .builder(FluidIcons.MAGMA_CREAM_STILL, FluidIcons.MAGMA_CREAM_FLOWING).sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY).density(1900).viscosity(1900).temperature(600), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER, 4);
  public static final Map<SlimeType, FluidObject<MantleFluid>> slime;
  static {
    slime = new EnumMap<>(SlimeType.class);
    slime.put(SlimeType.EARTH, earthSlime);
    slime.put(SlimeType.SKY, skySlime);
    slime.put(SlimeType.ENDER, enderSlime);
    slime.put(SlimeType.BLOOD, blood);
  }

  // molten
  public static final FluidObject<MantleFluid> searedStone    = FLUIDS.register("seared_stone",     stoneBuilder().setRenderColor(0xff777777).temperature( 900), Material.LAVA,  7);
  public static final FluidObject<MantleFluid> moltenClay     = FLUIDS.register("molten_clay",      stoneBuilder().setRenderColor(0xffb75a40).temperature( 750), Material.LAVA,  3);
  public static final FluidObject<MantleFluid> moltenGlass    = FLUIDS.register("molten_glass",    moltenBuilder().setRenderColor(0xffc0f5fe).temperature(1050), Material.LAVA, 10);
  public static final FluidObject<MantleFluid> liquidSoul     = FLUIDS.register("liquid_soul",       coolBuilder().setRenderColor(0xffb78e77).temperature( 700), Material.LAVA,  2);
  public static final FluidObject<MantleFluid> moltenObsidian = FLUIDS.register("molten_obsidian",  stoneBuilder().setRenderColor(0xff2c0d59).temperature(1300), Material.LAVA, 11);
  public static final FluidObject<MantleFluid> moltenEmerald  = FLUIDS.register("molten_emerald",  moltenBuilder().setRenderColor(0xff41f384).temperature(1234), Material.LAVA,  4);
  public static final FluidObject<MantleFluid> moltenEnder    = FLUIDS.register("molten_ender",     stoneBuilder().setRenderColor(0xff105e51).temperature( 777), Material.LAVA,  7);
  public static final FluidObject<MantleFluid> moltenBlaze    = FLUIDS.register("molten_blaze",    hotBuilder(FluidIcons.BLAZE_STILL, FluidIcons.BLAZE_FLOWING).temperature(1800).density(3500), Material.LAVA, 14);

  // ores
  public static final FluidObject<MantleFluid> moltenIron   = FLUIDS.register("molten_iron",   moltenBuilder().setRenderColor(0xffa81212).setTemperature(1100), Material.LAVA, 12);
  public static final FluidObject<MantleFluid> moltenGold   = FLUIDS.register("molten_gold",   moltenBuilder().setRenderColor(0xfff6d609).temperature(1000), Material.LAVA, 13);
  public static final FluidObject<MantleFluid> moltenCopper = FLUIDS.register("molten_copper", moltenBuilder().setRenderColor(0xfffba165).temperature( 800), Material.LAVA, 11);
  public static final FluidObject<MantleFluid> moltenCobalt = FLUIDS.register("molten_cobalt", moltenBuilder().setRenderColor(0xff2376dd).temperature(1250), Material.LAVA, 10);
  public static final FluidObject<MantleFluid> moltenDebris = FLUIDS.register("molten_debris", moltenBuilder().setRenderColor(0xff5d342c).temperature(1475), Material.LAVA,  9);
  // alloys
  public static final FluidObject<MantleFluid> moltenSlimesteel    = FLUIDS.register("molten_slimesteel",     moltenBuilder().setRenderColor(0xffb3e0dc).temperature(1200), Material.LAVA, 10);
  public static final FluidObject<MantleFluid> moltenTinkersBronze = FLUIDS.register("molten_tinkers_bronze", moltenBuilder().setRenderColor(0xfff9cf72).temperature(1000), Material.LAVA, 12);
  public static final FluidObject<MantleFluid> moltenRoseGold      = FLUIDS.register("molten_rose_gold",      moltenBuilder().setRenderColor(0xfff7cdbb).temperature( 850), Material.LAVA, 12);
  public static final FluidObject<MantleFluid> moltenPigIron       = FLUIDS.register("molten_pig_iron",       moltenBuilder().setRenderColor(0xfff0a8a4).temperature(1111), Material.LAVA, 10);

  public static final FluidObject<MantleFluid> moltenManyullyn   = FLUIDS.register("molten_manyullyn",    moltenBuilder().setRenderColor(0xff9261cc).temperature(1500), Material.LAVA,  9);
  public static final FluidObject<MantleFluid> moltenHepatizon   = FLUIDS.register("molten_hepatizon",    moltenBuilder().setRenderColor(0xff60496b).temperature(1700), Material.LAVA,  5);
  public static final FluidObject<MantleFluid> moltenQueensSlime = FLUIDS.register("molten_queens_slime", moltenBuilder().setRenderColor(0xff236c45).temperature(1450), Material.LAVA,  7);
  public static final FluidObject<MantleFluid> moltenSoulsteel   = FLUIDS.register("molten_soulsteel",    moltenBuilder().setRenderColor(0xc46a5244).temperature(1500), Material.LAVA,  9);
  public static final FluidObject<MantleFluid> moltenNetherite   = FLUIDS.register("molten_netherite",    moltenBuilder().setRenderColor(0xff3c3232).temperature(1550), Material.LAVA, 11);
  public static final FluidObject<MantleFluid> moltenKnightslime = FLUIDS.register("molten_knightslime",  moltenBuilder().setRenderColor(0xfff18ff0).temperature(1425), Material.LAVA,  9);

  // compat ores
  public static final FluidObject<MantleFluid> moltenTin      = FLUIDS.register("molten_tin",      moltenBuilder().setRenderColor(0xffc1cddc).temperature( 525), Material.LAVA, 12);
  public static final FluidObject<MantleFluid> moltenAluminum = FLUIDS.register("molten_aluminum", moltenBuilder().setRenderColor(0xffefe0d5).temperature( 725), Material.LAVA, 12);
  public static final FluidObject<MantleFluid> moltenLead     = FLUIDS.register("molten_lead",     moltenBuilder().setRenderColor(0xff4d4968).temperature( 630), Material.LAVA, 12);
  public static final FluidObject<MantleFluid> moltenSilver   = FLUIDS.register("molten_silver",   moltenBuilder().setRenderColor(0xffd1ecf6).temperature(1090), Material.LAVA, 12);
  public static final FluidObject<MantleFluid> moltenNickel   = FLUIDS.register("molten_nickel",   moltenBuilder().setRenderColor(0xffc8d683).temperature(1250), Material.LAVA, 12);
  public static final FluidObject<MantleFluid> moltenZinc     = FLUIDS.register("molten_zinc",     moltenBuilder().setRenderColor(0xffd3efe8).temperature( 720), Material.LAVA, 12);
  public static final FluidObject<MantleFluid> moltenPlatinum = FLUIDS.register("molten_platinum", moltenBuilder().setRenderColor(0xffb5aea4).temperature(1270), Material.LAVA, 12);
  public static final FluidObject<MantleFluid> moltenTungsten = FLUIDS.register("molten_tungsten", moltenBuilder().setRenderColor(0xffd1c08b).temperature(1250), Material.LAVA, 12);
  public static final FluidObject<MantleFluid> moltenOsmium   = FLUIDS.register("molten_osmium",   moltenBuilder().setRenderColor(0xffbed3cd).temperature(1275), Material.LAVA, 12);
  public static final FluidObject<MantleFluid> moltenUranium  = FLUIDS.register("molten_uranium",  moltenBuilder().setRenderColor(0xff7f9374).temperature(1130), Material.LAVA, 12);

  // compat alloys
  public static final FluidObject<MantleFluid> moltenBronze     = FLUIDS.register("molten_bronze",     moltenBuilder().setRenderColor(0xffcea179).temperature(1000), Material.LAVA, 12);
  public static final FluidObject<MantleFluid> moltenBrass      = FLUIDS.register("molten_brass",      moltenBuilder().setRenderColor(0xffede38b).temperature( 905), Material.LAVA, 12);
  public static final FluidObject<MantleFluid> moltenElectrum   = FLUIDS.register("molten_electrum",   moltenBuilder().setRenderColor(0xffe8db49).temperature(1060), Material.LAVA, 12);
  public static final FluidObject<MantleFluid> moltenInvar      = FLUIDS.register("molten_invar",      moltenBuilder().setRenderColor(0xffbab29b).temperature(1200), Material.LAVA, 12);
  public static final FluidObject<MantleFluid> moltenConstantan = FLUIDS.register("molten_constantan", moltenBuilder().setRenderColor(0xffff9e7f).temperature(1220), Material.LAVA, 12);
  public static final FluidObject<MantleFluid> moltenPewter     = FLUIDS.register("molten_pewter",     moltenBuilder().setRenderColor(0xffa09b6b).temperature( 700), Material.LAVA, 10);
  public static final FluidObject<MantleFluid> moltenSteel      = FLUIDS.register("molten_steel",      moltenBuilder().setRenderColor(0xffa7a7a7).temperature(1510), Material.LAVA, 12);


  /** Creates a builder for a cool fluid */
  private static FluidKey.FluidKeyBuilder coolBuilder() {
    return FluidAttributes.builder(FluidIcons.LIQUID_STILL, FluidIcons.LIQUID_FLOWING).sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY);
  }

  /** Creates a builder for a hot fluid */
  private static FluidKey.FluidKeyBuilder hotBuilder(Identifier stillTexture, Identifier flowingTexture) {
    return FluidAttributes.builder(stillTexture, flowingTexture).density(2000).viscosity(10000).temperature(1000).sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
  }

  /** Creates a builder for a molten fluid */
  private static FluidKey.FluidKeyBuilder stoneBuilder() {
    return hotBuilder(FluidIcons.STONE_STILL, FluidIcons.STONE_FLOWING);
  }

  /** Creates a builder for a molten fluid */
  private static FluidKey.FluidKeyBuilder moltenBuilder() {
    return hotBuilder(FluidIcons.MOLTEN_STILL, FluidIcons.MOLTEN_FLOWING);
  }
}
