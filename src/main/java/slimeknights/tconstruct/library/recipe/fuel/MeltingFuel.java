package slimeknights.tconstruct.library.recipe.fuel;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;

import java.util.List;

/**
 * Recipe for a fuel for the melter or smeltery
 */
public class MeltingFuel implements ICustomOutputRecipe<IFluidContainer> {
  public static final RecordLoadable<MeltingFuel> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    LoadableRecipeSerializer.RECIPE_GROUP,
    FluidIngredient.LOADABLE.requiredField("fluid", r -> r.input),
    IntLoadable.FROM_ONE.requiredField("duration", MeltingFuel::getDuration),
    IntLoadable.ANY_SHORT.requiredField("temperature", MeltingFuel::getTemperature),
    MeltingFuel::new);

  @Getter
  private final ResourceLocation id;
  @Getter
  private final String group;
  private final FluidIngredient input;
  @Getter
  private final int duration;
  @Getter
  private final int temperature;

  public MeltingFuel(ResourceLocation id, String group, FluidIngredient input, int duration, int temperature) {
    this.id = id;
    this.group = group;
    this.input = input;
    this.duration = duration;
    this.temperature = temperature;
    // register this recipe with the lookup
    for (FluidStack fluid : input.getFluids()) {
      MeltingFuelLookup.addFuel(fluid.getFluid(), this);
    }
  }

  /* Recipe methods */

  @Override
  public boolean matches(IFluidContainer inv, Level worldIn) {
    return matches(inv.getFluid());
  }

  /**
   * Checks if this fuel matches the given fluid
   * @param fluid  Fluid
   * @return  True if matches
   */
  public boolean matches(Fluid fluid) {
    return input.test(fluid);
  }

  /**
   * Gets the amount of fluid consumed for the given fluid
   * @param inv  Inventory instance
   * @return  Amount of fluid consumed
   */
  public int getAmount(IFluidContainer inv) {
    return getAmount(inv.getFluid());
  }

  /**
   * Gets the amount of fluid consumed for the given fluid
   * @param fluid  Fluid
   * @return  Amount of fluid consumed
   */
  public int getAmount(Fluid fluid) {
    return input.getAmount(fluid);
  }

  /**
   * Gets a list of all valid input fluids for this recipe
   * @return  Input fluids
   */
  public List<FluidStack> getInputs() {
    return input.getFluids();
  }

  /* Recipe type methods */

  @Override
  public RecipeType<?> getType() {
    return TinkerRecipeTypes.FUEL.get();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.fuelSerializer.get();
  }

  @Override
  public ItemStack getToastSymbol() {
    return new ItemStack(TinkerSmeltery.searedTank.get(TankType.FUEL_TANK));
  }
}
