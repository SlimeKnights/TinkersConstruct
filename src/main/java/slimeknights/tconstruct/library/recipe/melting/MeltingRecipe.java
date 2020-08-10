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
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.recipe.inventory.ISingleItemInventory;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;

/**
 * Recipe to melt an ingredient into a specific fuel
 */
@AllArgsConstructor
public class MeltingRecipe implements IMeltingRecipe {
  @Getter
  private final ResourceLocation id;
  @Getter
  private final String group;
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
  public int getTemperature(ISingleItemInventory inv) {
    return temperature;
  }

  @Override
  public FluidStack getOutput(ISingleItemInventory inv) {
    return output.copy();
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.from(Ingredient.EMPTY, input);
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.meltingSerializer.get();
  }

  /**
   * Serializer for {@link MeltingRecipe}
   */
  public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<MeltingRecipe> {
    @Override
    public MeltingRecipe read(ResourceLocation id, JsonObject json) {
      String group = JSONUtils.getString(json, "group", "");
      Ingredient input = Ingredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
      FluidStack output = RecipeHelper.deserializeFluidStack(JSONUtils.getJsonObject(json, "result"));

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

      return new MeltingRecipe(id, group, input, output, temperature);
    }

    @Nullable
    @Override
    public MeltingRecipe read(ResourceLocation id, PacketBuffer buffer) {
      String group = buffer.readString(Short.MAX_VALUE);
      Ingredient input = Ingredient.read(buffer);
      FluidStack output = FluidStack.readFromPacket(buffer);
      int temperature = buffer.readInt();
      return new MeltingRecipe(id, group, input, output, temperature);
    }

    @Override
    public void write(PacketBuffer buffer, MeltingRecipe recipe) {
      buffer.writeString(recipe.group);
      recipe.input.write(buffer);
      recipe.output.writeToPacket(buffer);
      buffer.writeInt(recipe.temperature);
    }
  }
}
