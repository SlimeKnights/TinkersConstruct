package slimeknights.tconstruct.library.recipe.melting;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.tconstruct.library.recipe.RecipeUtil;
import slimeknights.tconstruct.library.recipe.inventory.ISingleItemInventory;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;

@AllArgsConstructor
public class MeltingRecipe implements IMeltingRecipe {
  // TODO: group?
  @Getter
  private final ResourceLocation id;
  private final Ingredient input;
  @Getter
  private final FluidStack output;
  @Getter
  private final int temperature;

  @Override
  public boolean matches(ISingleItemInventory inv, World world) {
    return input.test(inv.getStack());
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.from(input);
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.meltingSerializer.get();
  }

  public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<MeltingRecipe> {
    @Override
    public MeltingRecipe read(ResourceLocation id, JsonObject json) {
      Ingredient input = Ingredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
      FluidStack output = RecipeUtil.deserializeFluidStack(JSONUtils.getJsonObject(json, "result"));

      // temperature calculates
      int temperature;
      if (json.has("temperature")) {
        temperature = JSONUtils.getInt(json, "temperature");
      } else {
        temperature = IMeltingRecipe.calcTemperature(output);
      }
      // validate temperature
      if (temperature <= 0) {
        throw new JsonSyntaxException("Melting temperature must be greater than zero");
      }

      return new MeltingRecipe(id, input, output, temperature);
    }

    @Nullable
    @Override
    public MeltingRecipe read(ResourceLocation id, PacketBuffer buffer) {
      Ingredient input = Ingredient.read(buffer);
      FluidStack output = FluidStack.readFromPacket(buffer);
      int temperature = buffer.readInt();
      return new MeltingRecipe(id, input, output, temperature);
    }

    @Override
    public void write(PacketBuffer buffer, MeltingRecipe recipe) {
      recipe.input.write(buffer);
      recipe.output.writeToPacket(buffer);
      buffer.writeInt(recipe.temperature);
    }
  }
}
