package slimeknights.tconstruct.fluids;

import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.registration.object.FluidObject;
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
  public static final FluidObject<ForgeFlowingFluid> blood = FLUIDS.register("blood", coolBuilder().color(0xff540000).density(1200).viscosity(1200).temperature(336), Material.WATER, 0);

  // slime
  public static final FluidObject<ForgeFlowingFluid> earthSlime = FLUIDS.register("earth_slime", coolBuilder().color(0xef76be6d).density(1400).viscosity(1400).temperature(350), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER, 0);
  public static final FluidObject<ForgeFlowingFluid> skySlime = FLUIDS.register("sky_slime", coolBuilder().color(0xef67f0f5).density(1500).viscosity(1500).temperature(310), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER, 0);
  public static final FluidObject<ForgeFlowingFluid> enderSlime = FLUIDS.register("ender_slime", coolBuilder().color(0xefd236ff).density(1600).viscosity(1600).temperature(370), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER, 0);
  public static final FluidObject<ForgeFlowingFluid> magmaCream  = FLUIDS.register("magma_cream", FluidAttributes
    .builder(FluidIcons.MAGMA_CREAM_STILL, FluidIcons.MAGMA_CREAM_FLOWING).sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY).density(1900).viscosity(1900).temperature(600), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER, 4);
  public static final Map<SlimeType, FluidObject<ForgeFlowingFluid>> slime;
  static {
    slime = new EnumMap<>(SlimeType.class);
    slime.put(SlimeType.EARTH, earthSlime);
    slime.put(SlimeType.SKY, skySlime);
    slime.put(SlimeType.ENDER, enderSlime);
    slime.put(SlimeType.BLOOD, blood);
  }

  // molten
  public static final FluidObject<ForgeFlowingFluid> searedStone    = FLUIDS.register("seared_stone",     stoneBuilder().color(0xff777777).temperature( 900), Material.LAVA,  7);
  public static final FluidObject<ForgeFlowingFluid> moltenClay     = FLUIDS.register("molten_clay",      stoneBuilder().color(0xffb75a40).temperature( 750), Material.LAVA,  3);
  public static final FluidObject<ForgeFlowingFluid> moltenGlass    = FLUIDS.register("molten_glass",    moltenBuilder().color(0xffc0f5fe).temperature(1050), Material.LAVA, 10);
  public static final FluidObject<ForgeFlowingFluid> liquidSoul     = FLUIDS.register("liquid_soul",       coolBuilder().color(0xffb78e77).temperature( 700), Material.LAVA,  2);
  public static final FluidObject<ForgeFlowingFluid> moltenObsidian = FLUIDS.register("molten_obsidian",  stoneBuilder().color(0xff2c0d59).temperature(1300), Material.LAVA, 11);
  public static final FluidObject<ForgeFlowingFluid> moltenEmerald  = FLUIDS.register("molten_emerald",  moltenBuilder().color(0xff41f384).temperature(1234), Material.LAVA,  4);
  public static final FluidObject<ForgeFlowingFluid> moltenEnder    = FLUIDS.register("molten_ender",     stoneBuilder().color(0xff105e51).temperature( 777), Material.LAVA,  7);
  public static final FluidObject<ForgeFlowingFluid> moltenBlaze    = FLUIDS.register("molten_blaze",    hotBuilder(FluidIcons.BLAZE_STILL, FluidIcons.BLAZE_FLOWING).temperature(1800).density(3500), Material.LAVA, 14);

  // ores
  public static final FluidObject<ForgeFlowingFluid> moltenIron   = FLUIDS.register("molten_iron",   moltenBuilder().color(0xffa81212).temperature(1100), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenGold   = FLUIDS.register("molten_gold",   moltenBuilder().color(0xfff6d609).temperature(1000), Material.LAVA, 13);
  public static final FluidObject<ForgeFlowingFluid> moltenCopper = FLUIDS.register("molten_copper", moltenBuilder().color(0xfffba165).temperature( 800), Material.LAVA, 11);
  public static final FluidObject<ForgeFlowingFluid> moltenCobalt = FLUIDS.register("molten_cobalt", moltenBuilder().color(0xff2376dd).temperature(1250), Material.LAVA, 10);
  public static final FluidObject<ForgeFlowingFluid> moltenDebris = FLUIDS.register("molten_debris", moltenBuilder().color(0xff5d342c).temperature(1475), Material.LAVA,  9);
  // alloys
  public static final FluidObject<ForgeFlowingFluid> moltenSlimesteel    = FLUIDS.register("molten_slimesteel",     moltenBuilder().color(0xffb3e0dc).temperature(1200), Material.LAVA, 10);
  public static final FluidObject<ForgeFlowingFluid> moltenTinkersBronze = FLUIDS.register("molten_tinkers_bronze", moltenBuilder().color(0xfff9cf72).temperature(1000), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenRoseGold      = FLUIDS.register("molten_rose_gold",      moltenBuilder().color(0xfff7cdbb).temperature( 850), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenPigIron       = FLUIDS.register("molten_pig_iron",       moltenBuilder().color(0xfff0a8a4).temperature(1111), Material.LAVA, 10);

  public static final FluidObject<ForgeFlowingFluid> moltenManyullyn   = FLUIDS.register("molten_manyullyn",    moltenBuilder().color(0xff9261cc).temperature(1500), Material.LAVA,  9);
  public static final FluidObject<ForgeFlowingFluid> moltenHepatizon   = FLUIDS.register("molten_hepatizon",    moltenBuilder().color(0xff60496b).temperature(1700), Material.LAVA,  5);
  public static final FluidObject<ForgeFlowingFluid> moltenQueensSlime = FLUIDS.register("molten_queens_slime", moltenBuilder().color(0xff236c45).temperature(1450), Material.LAVA,  7);
  public static final FluidObject<ForgeFlowingFluid> moltenSoulsteel   = FLUIDS.register("molten_soulsteel",    moltenBuilder().color(0xc46a5244).temperature(1500), Material.LAVA,  9);
  public static final FluidObject<ForgeFlowingFluid> moltenNetherite   = FLUIDS.register("molten_netherite",    moltenBuilder().color(0xff3c3232).temperature(1550), Material.LAVA, 11);
  public static final FluidObject<ForgeFlowingFluid> moltenKnightslime = FLUIDS.register("molten_knightslime",  moltenBuilder().color(0xfff18ff0).temperature(1425), Material.LAVA,  9);

  // compat ores
  public static final FluidObject<ForgeFlowingFluid> moltenTin      = FLUIDS.register("molten_tin",      moltenBuilder().color(0xffc1cddc).temperature( 525), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenAluminum = FLUIDS.register("molten_aluminum", moltenBuilder().color(0xffefe0d5).temperature( 725), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenLead     = FLUIDS.register("molten_lead",     moltenBuilder().color(0xff4d4968).temperature( 630), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenSilver   = FLUIDS.register("molten_silver",   moltenBuilder().color(0xffd1ecf6).temperature(1090), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenNickel   = FLUIDS.register("molten_nickel",   moltenBuilder().color(0xffc8d683).temperature(1250), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenZinc     = FLUIDS.register("molten_zinc",     moltenBuilder().color(0xffd3efe8).temperature( 720), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenPlatinum = FLUIDS.register("molten_platinum", moltenBuilder().color(0xffb5aea4).temperature(1270), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenTungsten = FLUIDS.register("molten_tungsten", moltenBuilder().color(0xffd1c08b).temperature(1250), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenOsmium   = FLUIDS.register("molten_osmium",   moltenBuilder().color(0xffbed3cd).temperature(1700), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenUranium  = FLUIDS.register("molten_uranium",  moltenBuilder().color(0xff7f9374).temperature(1130), Material.LAVA, 12);

  // compat alloys
  public static final FluidObject<ForgeFlowingFluid> moltenBronze     = FLUIDS.register("molten_bronze",     moltenBuilder().color(0xffcea179).temperature(1000), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenBrass      = FLUIDS.register("molten_brass",      moltenBuilder().color(0xffede38b).temperature( 905), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenElectrum   = FLUIDS.register("molten_electrum",   moltenBuilder().color(0xffe8db49).temperature(1060), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenInvar      = FLUIDS.register("molten_invar",      moltenBuilder().color(0xffbab29b).temperature(1200), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenConstantan = FLUIDS.register("molten_constantan", moltenBuilder().color(0xffff9e7f).temperature(1220), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenSteel      = FLUIDS.register("molten_steel",      moltenBuilder().color(0xffa7a7a7).temperature(1510), Material.LAVA, 12);


  /** Creates a builder for a cool fluid */
  private static FluidAttributes.Builder coolBuilder() {
    return FluidAttributes.builder(FluidIcons.LIQUID_STILL, FluidIcons.LIQUID_FLOWING).sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY);
  }

  /** Creates a builder for a hot fluid */
  private static FluidAttributes.Builder hotBuilder(ResourceLocation stillTexture, ResourceLocation flowingTexture) {
    return FluidAttributes.builder(stillTexture, flowingTexture).density(2000).viscosity(10000).temperature(1000).sound(SoundEvents.ITEM_BUCKET_FILL_LAVA, SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
  }

  /** Creates a builder for a molten fluid */
  private static FluidAttributes.Builder stoneBuilder() {
    return hotBuilder(FluidIcons.STONE_STILL, FluidIcons.STONE_FLOWING);
  }

  /** Creates a builder for a molten fluid */
  private static FluidAttributes.Builder moltenBuilder() {
    return hotBuilder(FluidIcons.MOLTEN_STILL, FluidIcons.MOLTEN_FLOWING);
  }
}
