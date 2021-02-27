package slimeknights.tconstruct.tools.data;

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
public class DataMaterial implements IMaterial {
  @Getter
  private final MaterialId identifier;
  @Getter
  private final int tier;
  @Getter
  private final int sortOrder;
  private final Supplier<? extends Fluid> fluid;
  @Getter
  private final int fluidPerUnit;
  @Getter
  private final boolean craftable;
  @Getter
  private final Color color;
  private final Supplier<List<ModifierEntry>> traits;

  public DataMaterial(MaterialId identifier, int tier, int sortOrder, Supplier<? extends Fluid> fluid, int fluidPerUnit, boolean craftable, int color, Supplier<List<ModifierEntry>> traits) {
    this.identifier = identifier;
    this.tier = tier;
    this.sortOrder = sortOrder;
    this.fluid = fluid;
    this.fluidPerUnit = fluidPerUnit;
    this.craftable = craftable;
    this.color = Color.fromInt(color);
    this.traits = traits;
  }

  public DataMaterial(MaterialId identifier, int tier, int sortOrder, boolean craftable, int color, Supplier<List<ModifierEntry>> traits) {
    this(identifier, tier, sortOrder, Fluids.EMPTY.delegate, 0, craftable, color, traits);
  }

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

  @Override
  public List<ModifierEntry> getTraits() {
    return traits.get();
  }
}
