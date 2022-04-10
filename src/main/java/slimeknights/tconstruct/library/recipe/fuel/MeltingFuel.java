package slimeknights.tconstruct.library.recipe.fuel;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Recipe for a fuel for the melter or smeltery
 */
public class MeltingFuel implements ICustomOutputRecipe<IFluidContainer> {
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

  /**
   * Serializer for {@link MeltingFuel}
   */
  public static class Serializer extends LoggingRecipeSerializer<MeltingFuel> {
    @Override
    public MeltingFuel fromJson(ResourceLocation id, JsonObject json) {
      String group = GsonHelper.getAsString(json, "group", "");
      FluidIngredient input = FluidIngredient.deserialize(json, "fluid");
      int duration = GsonHelper.getAsInt(json, "duration");
      int temperature = GsonHelper.getAsInt(json, "temperature");
      return new MeltingFuel(id, group, input, duration, temperature);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, MeltingFuel recipe) {
      buffer.writeUtf(recipe.group);
      recipe.input.write(buffer);
      buffer.writeInt(recipe.duration);
      buffer.writeInt(recipe.temperature);
    }

    @Nullable
    @Override
    protected MeltingFuel fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      String group = buffer.readUtf(Short.MAX_VALUE);
      FluidIngredient input = FluidIngredient.read(buffer);
      int duration = buffer.readInt();
      int temperature = buffer.readInt();
      return new MeltingFuel(id, group, input, duration, temperature);
    }
  }
}
