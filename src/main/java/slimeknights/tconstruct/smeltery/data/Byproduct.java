package slimeknights.tconstruct.smeltery.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.material.Fluid;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.recipe.IByproduct;
import slimeknights.tconstruct.library.recipe.FluidValues;

import java.util.Locale;
import java.util.function.Supplier;

/** Standard ore byproducts for smeltery ores, this enum exists to simplify our builders to allow passing 3 args in varargs */
@RequiredArgsConstructor
public enum Byproduct implements IByproduct {
  // base mod
  COPPER    (true, TinkerFluids.moltenCopper),
  IRON      (true, TinkerFluids.moltenIron),
  GOLD      (true, TinkerFluids.moltenGold),
  SMALL_GOLD("gold", true, TinkerFluids.moltenGold, FluidValues.NUGGET),
  COBALT    (true, TinkerFluids.moltenCobalt),
  // compat
  TIN     (false, TinkerFluids.moltenTin),
  SILVER  (false, TinkerFluids.moltenSilver),
  NICKEL  (false, TinkerFluids.moltenNickel),
  LEAD    (false, TinkerFluids.moltenLead),
  PLATINUM("platinum", false, TinkerFluids.moltenPlatinum, FluidValues.NUGGET);

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
    this.nuggets = FluidValues.NUGGET * 3;
  }

  @Override
  public Fluid getFluid() {
    return fluidSupplier.get();
  }
}
