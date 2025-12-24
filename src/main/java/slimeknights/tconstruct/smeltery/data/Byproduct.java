package slimeknights.tconstruct.smeltery.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.recipe.IByproduct;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;

import java.util.Locale;

/** Standard ore byproducts for smeltery ores, this enum exists to simplify our builders to allow passing 3 args in varargs */
@RequiredArgsConstructor
@Getter
public enum Byproduct implements IByproduct {
  // base metals
  COPPER    (true, TinkerFluids.moltenCopper),
  IRON      (true, TinkerFluids.moltenIron),
  GOLD      (true, TinkerFluids.moltenGold),
  SMALL_GOLD("gold", true, TinkerFluids.moltenGold, FluidValues.NUGGET * 3, FluidValues.NUGGET, OreRateType.METAL),
  TINY_GOLD ("gold", true, TinkerFluids.moltenGold, FluidValues.NUGGET, FluidValues.NUGGET, OreRateType.NONE),
  COBALT    (true, TinkerFluids.moltenCobalt),
  STEEL     (true, TinkerFluids.moltenSteel),
  DEBRIS    ("netherite_scrap", true, TinkerFluids.moltenDebris, FluidValues.INGOT, FluidValues.NUGGET, OreRateType.METAL),
  // compat metals
  ALUMINUM(false, TinkerFluids.moltenAluminum),
  TIN     (false, TinkerFluids.moltenTin),
  SILVER  (false, TinkerFluids.moltenSilver),
  NICKEL  (false, TinkerFluids.moltenNickel),
  LEAD    (false, TinkerFluids.moltenLead),
  PLATINUM(false, TinkerFluids.moltenPlatinum),
  ZINC    (false, TinkerFluids.moltenZinc),
  CHROMIUM(false, TinkerFluids.moltenChromium),
  CADMIUM (false, TinkerFluids.moltenCadmium),
  // gems
  DIAMOND(true, TinkerFluids.moltenDiamond, FluidValues.GEM, FluidValues.GEM_SHARD, OreRateType.GEM),
  SMALL_DIAMOND("diamond", true, TinkerFluids.moltenDiamond, FluidValues.GEM_SHARD, FluidValues.GEM_SHARD, OreRateType.GEM); // quarter diamond is comparable to third of an ingot

  private final String name;
  private final boolean alwaysPresent;
  private final FluidObject<?> fluid;
  private final int amount;
  private final int damageUnit;
  private final OreRateType oreRate;

  Byproduct(boolean alwaysPresent, FluidObject<?> fluid, int amount, int damageUnit, OreRateType oreRate) {
    this.name = name().toLowerCase(Locale.ROOT);
    this.alwaysPresent = alwaysPresent;
    this.fluid = fluid;
    this.amount = amount;
    this.damageUnit = damageUnit;
    this.oreRate = oreRate;
  }

  Byproduct(boolean alwaysPresent, FluidObject<?> fluid) {
    this(alwaysPresent, fluid, FluidValues.INGOT, FluidValues.NUGGET, OreRateType.METAL);
  }

  @Override
  public FluidOutput getFluid(float scale) {
    return fluid.result((int)(amount * scale));
  }
}
