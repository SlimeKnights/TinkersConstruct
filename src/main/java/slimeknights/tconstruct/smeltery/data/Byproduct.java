package slimeknights.tconstruct.smeltery.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.fluid.Fluid;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.materials.MaterialValues;

import java.util.Locale;
import java.util.function.Supplier;

/** Standard ore byproducts for smeltery ores, this enum exists to simplify our builders to allow passing 3 args in varargs */
@RequiredArgsConstructor
public enum Byproduct {
  // base mod
  COPPER    (true, TinkerFluids.moltenCopper),
  IRON      (true, TinkerFluids.moltenIron),
  GOLD      (true, TinkerFluids.moltenGold),
  SMALL_GOLD("gold", true, TinkerFluids.moltenGold, MaterialValues.NUGGET),
  COBALT    (true, TinkerFluids.moltenCobalt),
  // compat
  TIN     (false, TinkerFluids.moltenTin),
  SILVER  (false, TinkerFluids.moltenSilver),
  NICKEL  (false, TinkerFluids.moltenNickel),
  LEAD    (false, TinkerFluids.moltenLead),
  PLATINUM("platinum", false, TinkerFluids.moltenPlatinum, MaterialValues.NUGGET);

  @Getter
  private final String name;
  @Getter
  private final boolean alwaysPresent;
  private final Supplier<? extends Fluid> fluidSupplier;
  @Getter
  private final int nuggets;

  Byproduct(boolean alwaysPresent, Supplier<? extends Fluid> fluidSupplier) {
    this.name = name().toLowerCase(Locale.ROOT);
    this.alwaysPresent = alwaysPresent;
    this.fluidSupplier = fluidSupplier;
    this.nuggets = MaterialValues.NUGGET * 3;
  }

  /** Gets the fluid of this byproduct */
  public Fluid getFluid() {
    return fluidSupplier.get();
  }
}
