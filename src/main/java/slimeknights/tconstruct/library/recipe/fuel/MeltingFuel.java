package slimeknights.tconstruct.library.recipe.fuel;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.SearedTankBlock.TankType;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Recipe for a fuel for the melter or smeltery
 */
@AllArgsConstructor
public class MeltingFuel implements ICustomOutputRecipe<IFluidInventory> {
  @Getter
  private final ResourceLocation id;
  @Getter
  private final String group;
  private final FluidIngredient input;
  @Getter
  private final int duration;
  @Getter
  private final int temperature;

  /* Recipe methods */

  @Override
  public boolean matches(IFluidInventory inv, World worldIn) {
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
  public int getAmount(IFluidInventory inv) {
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
  public IRecipeType<?> getType() {
    return RecipeTypes.FUEL;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.fuelSerializer.get();
  }

  @Override
  public ItemStack getIcon() {
    return new ItemStack(TinkerSmeltery.searedTank.get(TankType.TANK));
  }

  /**
   * Serializer for {@link MeltingFuel}
   */
  public static class Serializer extends RecipeSerializer<MeltingFuel> {
    @Override
    public MeltingFuel read(ResourceLocation id, JsonObject json) {
      String group = JSONUtils.getString(json, "group", "");
      FluidIngredient input = FluidIngredient.deserialize(json, "fluid");
      int duration = JSONUtils.getInt(json, "duration");
      int temperature = JSONUtils.getInt(json, "temperature");
      return new MeltingFuel(id, group, input, duration, temperature);
    }

    @Override
    public void write(PacketBuffer buffer, MeltingFuel recipe) {
      buffer.writeString(recipe.group);
      recipe.input.write(buffer);
      buffer.writeInt(recipe.duration);
      buffer.writeInt(recipe.temperature);
    }

    @Nullable
    @Override
    public MeltingFuel read(ResourceLocation id, PacketBuffer buffer) {
      String group = buffer.readString(Short.MAX_VALUE);
      FluidIngredient input = FluidIngredient.read(buffer);
      int duration = buffer.readInt();
      int temperature = buffer.readInt();
      return new MeltingFuel(id, group, input, duration, temperature);
    }
  }
}
