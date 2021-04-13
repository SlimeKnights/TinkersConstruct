package slimeknights.tconstruct.library.recipe.fuel;

import alexiil.mc.lib.attributes.fluid.volume.FluidKeys;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;

import java.util.List;

/**
 * Recipe for a fuel for the melter or smeltery
 */
@AllArgsConstructor
public class MeltingFuel implements ICustomOutputRecipe<IFluidInventory> {
  @Getter
  private final Identifier id;
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
    return input.test(FluidKeys.get(fluid));
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
    return input.getAmount(FluidKeys.get(fluid)).as1620();
  }

  /**
   * Gets a list of all valid input fluids for this recipe
   * @return  Input fluids
   */
  public List<FluidVolume> getInputs() {
    return input.getFluids();
  }

  /* Recipe type methods */

  @Override
  public RecipeType<?> getType() {
    return RecipeTypes.FUEL;
  }

  @Override
  public net.minecraft.recipe.RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.fuelSerializer;
  }

  @Override
  public ItemStack getRecipeKindIcon() {
    return new ItemStack(TinkerSmeltery.searedTank.get(TankType.TANK));
  }

  /**
   * Serializer for {@link MeltingFuel}
   */
  public static class Serializer implements RecipeSerializer<MeltingFuel> {
    @Override
    public MeltingFuel read(Identifier id, JsonObject json) {
      String group = JsonHelper.getString(json, "group", "");
      FluidIngredient input = FluidIngredient.deserialize(json, "fluid");
      int duration = JsonHelper.getInt(json, "duration");
      int temperature = JsonHelper.getInt(json, "temperature");
      return new MeltingFuel(id, group, input, duration, temperature);
    }

    @Override
    public void write(PacketByteBuf buffer, MeltingFuel recipe) {
      buffer.writeString(recipe.group);
      recipe.input.write(buffer);
      buffer.writeInt(recipe.duration);
      buffer.writeInt(recipe.temperature);
    }

    @Nullable
    @Override
    public MeltingFuel read(Identifier id, PacketByteBuf buffer) {
      String group = buffer.readString(Short.MAX_VALUE);
      FluidIngredient input = FluidIngredient.read(buffer);
      int duration = buffer.readInt();
      int temperature = buffer.readInt();
      return new MeltingFuel(id, group, input, duration, temperature);
    }
  }
}
