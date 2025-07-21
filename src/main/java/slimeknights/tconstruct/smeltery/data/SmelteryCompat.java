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
  PEWTER    (TinkerFluids.moltenPewter, "tin", "lead"),
  // thermal alloys
  ENDERIUM(TinkerFluids.moltenEnderium, false),
  LUMIUM  (TinkerFluids.moltenLumium, false),
  SIGNALUM(TinkerFluids.moltenSignalum, false),
  // mekanism alloys
  REFINED_GLOWSTONE(TinkerFluids.moltenRefinedGlowstone, false),
  REFINED_OBSIDIAN (TinkerFluids.moltenRefinedObsidian, false),
  // metalborn
  NICROSIL(TinkerFluids.moltenNicrosil, false);

  @Getter
  private final String name = this.name().toLowerCase(Locale.US);
  private final FluidObject<? extends ForgeFlowingFluid> fluid;
  @Getter
  private final boolean isOre;
  /** If any of these tags contains no values, skips */
  @Getter
  private final String[] tags;

  SmelteryCompat(FluidObject<? extends ForgeFlowingFluid> fluid, boolean isOre) {
    this.fluid = fluid;
    this.isOre = isOre;
    this.tags = new String[] { this.name };
  }

  /** Byproducts means its an ore, no byproucts are alloys */
  SmelteryCompat(FluidObject<? extends ForgeFlowingFluid> fluid, String... altTags) {
    this.fluid = fluid;
    this.isOre = false;
    this.tags = Util.append(altTags, name);
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
}
