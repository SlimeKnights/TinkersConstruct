package slimeknights.tconstruct.smeltery.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.Item;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.fluids.TinkerFluids;

import java.util.Locale;

/** Enum holding all relevant smeltery compat */
@RequiredArgsConstructor
public enum SmelteryCompat {
  // ores
  TIN(TinkerFluids.moltenTin, true),
  ALUMINUM(TinkerFluids.moltenAluminum, true),
  LEAD(TinkerFluids.moltenLead, true),
  SILVER(TinkerFluids.moltenSilver, true),
  NICKEL(TinkerFluids.moltenNickel, true),
  ZINC(TinkerFluids.moltenZinc, true),
  PLATINUM(TinkerFluids.moltenPlatinum, true),
  TUNGSTEN(TinkerFluids.moltenTungsten, true),
  OSMIUM(TinkerFluids.moltenOsmium, true),
  URANIUM(TinkerFluids.moltenUranium, true),
  // alloys
  BRONZE(TinkerFluids.moltenBronze, false),
  BRASS(TinkerFluids.moltenBrass, false),
  ELECTRUM(TinkerFluids.moltenElectrum, false),
  INVAR(TinkerFluids.moltenInvar, false),
  CONSTANTAN(TinkerFluids.moltenConstantan, false),
  PEWTER(TinkerFluids.moltenPewter, false),
  STEEL(TinkerFluids.moltenSteel, false);

  @Getter
  private final String name = this.name().toLowerCase(Locale.US);
  private final FluidObject<? extends ForgeFlowingFluid> fluid;
  @Getter
  private final boolean isOre;

  /** Gets teh fluid for this compat */
  public ForgeFlowingFluid getFluid() {
    return fluid.get();
  }

  /** Gets teh bucket for this compat */
  public Item getBucket() {
    return fluid.asItem();
  }
}
