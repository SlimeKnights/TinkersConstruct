package slimeknights.tconstruct.smeltery.data;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.world.item.Item;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.fluids.TinkerFluids;

import java.util.Locale;

/** Enum holding all relevant smeltery compat */
public enum SmelteryCompat {
  // ores
  TIN     (TinkerFluids.moltenTin,      Byproduct.COPPER),
  ALUMINUM(TinkerFluids.moltenAluminum, Byproduct.IRON),
  LEAD    (TinkerFluids.moltenLead,     Byproduct.SILVER, Byproduct.GOLD),
  SILVER  (TinkerFluids.moltenSilver,   Byproduct.LEAD, Byproduct.GOLD),
  NICKEL  (TinkerFluids.moltenNickel,   Byproduct.PLATINUM, Byproduct.IRON),
  ZINC    (TinkerFluids.moltenZinc,     Byproduct.TIN, Byproduct.COPPER),
  PLATINUM(TinkerFluids.moltenPlatinum, Byproduct.GOLD),
  TUNGSTEN(TinkerFluids.moltenTungsten, Byproduct.PLATINUM, Byproduct.GOLD),
  OSMIUM  (TinkerFluids.moltenOsmium,   Byproduct.IRON),
  URANIUM (TinkerFluids.moltenUranium,  Byproduct.LEAD, Byproduct.COPPER),
  // alloys
  BRONZE    (TinkerFluids.moltenBronze, "tin"),
  BRASS     (TinkerFluids.moltenBrass, "zinc"),
  ELECTRUM  (TinkerFluids.moltenElectrum, "silver"),
  INVAR     (TinkerFluids.moltenInvar, "nickel"),
  CONSTANTAN(TinkerFluids.moltenConstantan, "nickel"),
  PEWTER    (TinkerFluids.moltenPewter),
  STEEL     (TinkerFluids.moltenSteel),
  // thermal alloys
  ENDERIUM(TinkerFluids.moltenEnderium),
  LUMIUM  (TinkerFluids.moltenLumium),
  SIGNALUM(TinkerFluids.moltenSignalum),
  // mekanism alloys, they use dust as the not refined version of refined obsidian, so skip
  REFINED_GLOWSTONE(TinkerFluids.moltenRefinedGlowstone, false),
  REFINED_OBSIDIAN (TinkerFluids.moltenRefinedObsidian, false);

  @Getter
  private final String name = this.name().toLowerCase(Locale.US);
  private final FluidObject<? extends ForgeFlowingFluid> fluid;
  @Getter
  private final boolean isOre;
  @Accessors(fluent = true)
  @Getter
  private final boolean hasDust;
  @Getter
  private final Byproduct[] byproducts;
  @Getter
  private final String altTag;

  SmelteryCompat(FluidObject<? extends ForgeFlowingFluid> fluid, boolean hasDust) {
    this.fluid = fluid;
    this.isOre = false;
    this.byproducts = new Byproduct[0];
    this.hasDust = hasDust;
    this.altTag = "";
  }

  /** Byproducts means its an ore, no byproucts are alloys */
  SmelteryCompat(FluidObject<? extends ForgeFlowingFluid> fluid, String altTag, Byproduct... byproducts) {
    this.fluid = fluid;
    this.isOre = byproducts.length > 0;
    this.byproducts = byproducts;
    this.hasDust = true;
    this.altTag = altTag;
  }

  /** Byproducts means its an ore, no byproucts are alloys */
  SmelteryCompat(FluidObject<? extends ForgeFlowingFluid> fluid, Byproduct... byproducts) {
    this(fluid, "", byproducts);
  }

  /** Gets teh fluid for this compat */
  public FluidObject<?> getFluid() {
    return fluid;
  }

  /** Gets teh bucket for this compat */
  public Item getBucket() {
    return fluid.asItem();
  }
}
