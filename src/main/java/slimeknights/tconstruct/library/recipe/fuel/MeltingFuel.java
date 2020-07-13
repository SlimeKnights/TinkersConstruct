package slimeknights.tconstruct.library.recipe.fuel;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.tconstruct.library.recipe.FluidIngredient;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.inventory.IFluidInventory;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.List;

@AllArgsConstructor
public class MeltingFuel implements IRecipe<IFluidInventory> {
  @Getter
  private final ResourceLocation id;
  private final FluidIngredient input;
  @Getter
  private final int duration;
  @Getter
  private final int temperature;

  /* Recipe methods */

  @Override
  public boolean matches(IFluidInventory inv, World worldIn) {
    return input.test(inv.getFluid());
  }

  /**
   * Gets the amount of fluid consumed for the given fluid
   * @param inv  Inventory instance
   * @return  Amount of fluid consumed
   */
  public int getAmount(IFluidInventory inv) {
    return input.getAmount(inv.getFluid().getFluid());
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

  /* Required methods */
  /** @deprecated unused method */
  @Deprecated
  @Override
  public boolean canFit(int width, int height) {
    return true;
  }

  /** @deprecated unused method */
  @Deprecated
  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  /* Hide from recipe book until we have a working modded recipe book */
  @Override
  public boolean isDynamic() {
    return true;
  }

  /** @deprecated unused method */
  @Deprecated
  @Override
  public ItemStack getCraftingResult(IFluidInventory inv) {
    return ItemStack.EMPTY;
  }

  public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<MeltingFuel> {
    @Override
    public MeltingFuel read(ResourceLocation id, JsonObject json) {
      FluidIngredient input = FluidIngredient.deserialize(json, "fluid");
      int duration = JSONUtils.getInt(json, "duration");
      int temperature = JSONUtils.getInt(json, "temperature");
      return new MeltingFuel(id, input, duration, temperature);
    }

    @Override
    public void write(PacketBuffer buffer, MeltingFuel recipe) {
      recipe.input.write(buffer);
      buffer.writeInt(recipe.duration);
      buffer.writeInt(recipe.temperature);
    }

    @Nullable
    @Override
    public MeltingFuel read(ResourceLocation id, PacketBuffer buffer) {
      FluidIngredient input = FluidIngredient.read(buffer);
      int duration = buffer.readInt();
      int temperature = buffer.readInt();
      return new MeltingFuel(id, input, duration, temperature);
    }
  }
}
