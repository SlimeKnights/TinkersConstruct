package slimeknights.tconstruct.smeltery.data;

import lombok.Getter;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.fluids.TinkerFluids;

import java.util.Locale;

/**
 * Enum holding all relevant smeltery compat, used in datagen and JEI
 * @see slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder
 */
public enum SmelteryCompat {
  // ores
  TIN     (TinkerFluids.moltenTin,      true),
  ALUMINUM(TinkerFluids.moltenAluminum, true),
  LEAD    (TinkerFluids.moltenLead,     true),
  SILVER  (TinkerFluids.moltenSilver,   true),
  NICKEL  (TinkerFluids.moltenNickel,   true),
  ZINC    (TinkerFluids.moltenZinc,     true),
  PLATINUM(TinkerFluids.moltenPlatinum, true),
  TUNGSTEN(TinkerFluids.moltenTungsten, true),
  OSMIUM  (TinkerFluids.moltenOsmium,   true),
  URANIUM (TinkerFluids.moltenUranium,  true),
  // alloys
  BRONZE    (TinkerFluids.moltenBronze, "tin"),
  BRASS     (TinkerFluids.moltenBrass, "zinc"),
  ELECTRUM  (TinkerFluids.moltenElectrum, "silver"),
  INVAR     (TinkerFluids.moltenInvar, "nickel"),
  CONSTANTAN(TinkerFluids.moltenConstantan, "nickel"),
  PEWTER    (TinkerFluids.moltenPewter, false),
  STEEL     (TinkerFluids.moltenSteel, false),
  // thermal alloys
  ENDERIUM(TinkerFluids.moltenEnderium, false),
  LUMIUM  (TinkerFluids.moltenLumium, false),
  SIGNALUM(TinkerFluids.moltenSignalum, false),
  // mekanism alloys
  REFINED_GLOWSTONE(TinkerFluids.moltenRefinedGlowstone, false),
  REFINED_OBSIDIAN (TinkerFluids.moltenRefinedObsidian, false);

  @Getter
  private final String name = this.name().toLowerCase(Locale.US);
  private final FluidObject<? extends ForgeFlowingFluid> fluid;
  @Getter
  private final boolean isOre;
  @Getter
  private final String altTag;

  SmelteryCompat(FluidObject<? extends ForgeFlowingFluid> fluid, boolean isOre) {
    this.fluid = fluid;
    this.isOre = isOre;
    this.altTag = "";
  }

  /** Byproducts means its an ore, no byproucts are alloys */
  SmelteryCompat(FluidObject<? extends ForgeFlowingFluid> fluid, String altTag) {
    this.fluid = fluid;
    this.isOre = false;
    this.altTag = altTag;
  }

  /** Gets teh fluid for this compat */
  public FluidObject<?> getFluid() {
    return fluid;
  }
}
