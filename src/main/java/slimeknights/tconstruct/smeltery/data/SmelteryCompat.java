package slimeknights.tconstruct.smeltery.data;

import lombok.Getter;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.utils.Util;

import java.util.Locale;

/**
 * Enum holding all relevant smeltery compat, used in datagen and JEI.
 * Internal usage - you can do all the same things this does through your own datagen easily.
 * @see slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder
 */
@Internal
public enum SmelteryCompat {
  // ores
  TIN     (TinkerFluids.moltenTin,      CompatType.ORE),
  ALUMINUM(TinkerFluids.moltenAluminum, CompatType.ORE),
  LEAD    (TinkerFluids.moltenLead,     CompatType.ORE),
  SILVER  (TinkerFluids.moltenSilver,   CompatType.ORE),
  NICKEL  (TinkerFluids.moltenNickel,   CompatType.ORE),
  ZINC    (TinkerFluids.moltenZinc,     CompatType.ORE),
  PLATINUM(TinkerFluids.moltenPlatinum, CompatType.ORE),
  TUNGSTEN(TinkerFluids.moltenTungsten, CompatType.ORE),
  OSMIUM  (TinkerFluids.moltenOsmium,   CompatType.ORE),
  URANIUM (TinkerFluids.moltenUranium,  CompatType.ORE),
  CHROMIUM(TinkerFluids.moltenChromium, CompatType.ORE),
  CADMIUM (TinkerFluids.moltenCadmium,  CompatType.ORE),
  // alloys
  BRONZE    (TinkerFluids.moltenBronze, "tin"),
  BRASS     (TinkerFluids.moltenBrass, "zinc"),
  ELECTRUM  (TinkerFluids.moltenElectrum, "silver"),
  INVAR     (TinkerFluids.moltenInvar, "nickel"),
  CONSTANTAN(TinkerFluids.moltenConstantan, "nickel"),
  PEWTER    (TinkerFluids.moltenPewter, "tin", "lead"),
  // thermal alloys
  ENDERIUM(TinkerFluids.moltenEnderium, CompatType.ALLOY),
  LUMIUM  (TinkerFluids.moltenLumium,   CompatType.ALLOY),
  SIGNALUM(TinkerFluids.moltenSignalum, CompatType.ALLOY),
  // mekanism alloys
  REFINED_GLOWSTONE(TinkerFluids.moltenRefinedGlowstone, CompatType.ALLOY),
  REFINED_OBSIDIAN (TinkerFluids.moltenRefinedObsidian,  CompatType.ALLOY),
  // cosmere
  NICROSIL(TinkerFluids.moltenNicrosil,   CompatType.ALLOY),
  DURALUMIN(TinkerFluids.moltenDuralumin, CompatType.ALLOY),
  BENDALLOY(TinkerFluids.moltenBendalloy, CompatType.ALLOY),
  // twilight
  STEELEAF(TinkerFluids.moltenSteeleaf, CompatType.NONE),
  FIERY   (TinkerFluids.fieryLiquid,    CompatType.ALLOY);

  @Getter
  private final String name = this.name().toLowerCase(Locale.US);
  private final FluidObject<? extends ForgeFlowingFluid> fluid;
  @Getter
  private final CompatType type;
  /** If any of these tags contains no values, skips */
  @Getter
  private final String[] tags;

  SmelteryCompat(FluidObject<? extends ForgeFlowingFluid> fluid, CompatType type) {
    this.fluid = fluid;
    this.type = type;
    this.tags = new String[] { this.name };
  }

  /** Byproducts means its an ore, no byproucts are alloys */
  SmelteryCompat(FluidObject<? extends ForgeFlowingFluid> fluid, String... altTags) {
    this.fluid = fluid;
    this.type = CompatType.ALLOY;
    this.tags = Util.append(altTags, name);
  }

  /** @deprecated use {@link #getType()} */
  @Deprecated(forRemoval = true)
  public boolean isOre() {
    return type == CompatType.ORE;
  }

  /** @deprecated use {@link #getTags()} */
  @Deprecated(forRemoval = true)
  public String getAltTag() {
    // only 1 tag means just the name, we don't want that
    return tags.length < 2 ? "" : tags[0];
  }

  /** Gets teh fluid for this compat */
  public FluidObject<?> getFluid() {
    return fluid;
  }

  /** Helper for tracking types of ores */
  public enum CompatType {
    /** Fluid has ores, and should get support from lustrous */
    ORE,
    /** Fluid is an alloy, and should get an anvil variant */
    ALLOY,
    /** Fluid is neither ore nor alloy */
    NONE
  }
}
