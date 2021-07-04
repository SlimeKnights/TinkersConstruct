package slimeknights.tconstruct.smeltery.data;

import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.fluids.TinkerFluids;

import java.util.Locale;

/** Enum holding all relevant smeltery compat */
public enum SmelteryCompat {
  // ores
  TIN     (TinkerFluids.moltenTin,      true, Byproduct.COPPER),
  ALUMINUM(TinkerFluids.moltenAluminum, true, Byproduct.IRON),
  LEAD    (TinkerFluids.moltenLead,     true, Byproduct.SILVER, Byproduct.GOLD),
  SILVER  (TinkerFluids.moltenSilver,   true, Byproduct.LEAD, Byproduct.GOLD),
  NICKEL  (TinkerFluids.moltenNickel,   true, Byproduct.PLATINUM, Byproduct.IRON),
  ZINC    (TinkerFluids.moltenZinc,     true, Byproduct.TIN, Byproduct.COPPER),
  PLATINUM(TinkerFluids.moltenPlatinum, true, Byproduct.GOLD),
  TUNGSTEN(TinkerFluids.moltenTungsten, true, Byproduct.PLATINUM, Byproduct.GOLD),
  OSMIUM  (TinkerFluids.moltenOsmium,   true, Byproduct.IRON),
  URANIUM (TinkerFluids.moltenUranium,  true, Byproduct.LEAD, Byproduct.COPPER),
  // alloys
  BRONZE    (TinkerFluids.moltenBronze, false),
  BRASS     (TinkerFluids.moltenBrass, false),
  ELECTRUM  (TinkerFluids.moltenElectrum, false),
  INVAR     (TinkerFluids.moltenInvar, false),
  CONSTANTAN(TinkerFluids.moltenConstantan, false),
  PEWTER    (TinkerFluids.moltenPewter, false),
  STEEL     (TinkerFluids.moltenSteel, false);

  @Getter
  private final String name = this.name().toLowerCase(Locale.US);
  private final FluidObject<? extends ForgeFlowingFluid> fluid;
  @Getter
  private final boolean isOre;
  @Getter
  private final Byproduct[] byproducts;

  SmelteryCompat(FluidObject<? extends ForgeFlowingFluid> fluid, boolean isOre, Byproduct... byproducts) {
    this.fluid = fluid;
    this.isOre = isOre;
    this.byproducts = byproducts;
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
