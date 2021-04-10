package slimeknights.tconstruct.tools.data;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.text.TextColor;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import java.util.List;
import java.util.function.Supplier;

/**
 * Material implementation used in data generation. Should never be directly registered with the material registry
 */
public class DataMaterial implements IMaterial {
  private final MaterialId identifier;
  private final int tier;
  private final int sortOrder;
  private final Supplier<? extends Fluid> fluid;
  private final int fluidPerUnit;
  private final boolean craftable;
  private final TextColor color;
  private final Supplier<List<ModifierEntry>> traits;

  public DataMaterial(MaterialId identifier, int tier, int sortOrder, Supplier<? extends Fluid> fluid, int fluidPerUnit, boolean craftable, int color, Supplier<List<ModifierEntry>> traits) {
    this.identifier = identifier;
    this.tier = tier;
    this.sortOrder = sortOrder;
    this.fluid = fluid;
    this.fluidPerUnit = fluidPerUnit;
    this.craftable = craftable;
    this.color = TextColor.fromRgb(color);
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

  public MaterialId getIdentifier() {
    return this.identifier;
  }

  public int getTier() {
    return this.tier;
  }

  public int getSortOrder() {
    return this.sortOrder;
  }

  public int getFluidPerUnit() {
    return this.fluidPerUnit;
  }

  public boolean isCraftable() {
    return this.craftable;
  }

  public TextColor getColor() {
    return this.color;
  }
}
