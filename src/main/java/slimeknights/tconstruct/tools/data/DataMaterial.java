package slimeknights.tconstruct.tools.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.text.Color;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import java.util.List;
import java.util.function.Supplier;

/**
 * Material implementation used in data generation. Should never be directly registered with the material registry
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class DataMaterial implements IMaterial {
  @Getter
  private final MaterialId identifier;
  private final Supplier<? extends Fluid> fluid;
  @Getter
  private final int fluidPerUnit;
  @Getter
  private final boolean craftable;
  @Getter
  private final Color color;
  @Getter
  private final List<ModifierEntry> traits;

  @Override
  public Fluid getFluid() {
    return fluid.get();
  }

  @Override
  public int getTemperature() {
    Fluid fluid = this.fluid.get();
    if (fluid == Fluids.EMPTY) {
      return 0;
    }
    return fluid.getAttributes().getTemperature() - 300;
  }
}
