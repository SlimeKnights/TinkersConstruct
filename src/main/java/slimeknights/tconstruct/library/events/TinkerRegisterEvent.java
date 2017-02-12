package slimeknights.tconstruct.library.events;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import slimeknights.tconstruct.library.DryingRecipe;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

/** Base event used when something is registered in the Tinker Registry */
@Cancelable
public abstract class TinkerRegisterEvent<T> extends TinkerEvent {

  protected final T recipe;

  public TinkerRegisterEvent(T recipe) {
    this.recipe = recipe;
  }

  public T getRecipe() {
    return recipe;
  }

  /** Returns true on success, false if cancelled */
  public boolean fire() {
    return !MinecraftForge.EVENT_BUS.post(this);
  }

  /** Register a modifier */
  public static class ModifierRegisterEvent extends TinkerRegisterEvent<IModifier> {

    public ModifierRegisterEvent(IModifier recipe) {
      super(recipe);
    }
  }

  /** Register a drying rack recipe */
  public static class DryingRackRegisterEvent extends TinkerRegisterEvent<DryingRecipe> {

    public DryingRackRegisterEvent(DryingRecipe recipe) {
      super(recipe);
    }
  }

  /** Register a recipe for melting something in the smeltery */
  public static class MeltingRegisterEvent extends TinkerRegisterEvent<MeltingRecipe> {

    public MeltingRegisterEvent(MeltingRecipe recipe) {
      super(recipe);
    }
  }

  /** Register a recipe for alloying multiple liquids */
  public static class AlloyRegisterEvent extends TinkerRegisterEvent<AlloyRecipe> {

    public AlloyRegisterEvent(AlloyRecipe recipe) {
      super(recipe);
    }
  }

  /** Register a casting table recipe */
  public static class TableCastingRegisterEvent extends TinkerRegisterEvent<ICastingRecipe> {

    public TableCastingRegisterEvent(ICastingRecipe recipe) {
      super(recipe);
    }
  }

  /** Register a casting basin recipe */
  public static class BasinCastingRegisterEvent extends TinkerRegisterEvent<ICastingRecipe> {

    public BasinCastingRegisterEvent(ICastingRecipe recipe) {
      super(recipe);
    }
  }

  public static class SmelteryFuelRegisterEvent extends TinkerRegisterEvent<FluidStack> {

    private final int fuelDuration;

    public SmelteryFuelRegisterEvent(FluidStack recipe, int fuelDuration) {
      super(recipe);
      this.fuelDuration = fuelDuration;
    }

    public int getFuelDuration() {
      return fuelDuration;
    }
  }

  public static class EntityMeltingRegisterEvent extends TinkerRegisterEvent<Class<? extends Entity>> {
    protected final FluidStack fluidStack;
    protected FluidStack newFluidStack;

    public EntityMeltingRegisterEvent(Class<? extends Entity> entity, FluidStack fluidStack) {
      super(entity);
      this.fluidStack = fluidStack;
      this.newFluidStack = fluidStack;
    }

    public void setNewFluidStack(FluidStack fluidStack) {
      this.newFluidStack = fluidStack;
    }

    public FluidStack getNewFluidStack() {
      return newFluidStack;
    }

    public FluidStack getFluidStack() {
      return fluidStack;
    }
  }

}
