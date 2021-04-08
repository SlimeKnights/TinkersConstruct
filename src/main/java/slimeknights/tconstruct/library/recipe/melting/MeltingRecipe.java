package slimeknights.tconstruct.library.recipe.melting;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Recipe to melt an ingredient into a specific fuel
 */
@AllArgsConstructor
public class MeltingRecipe implements IMeltingRecipe {
  @Getter
  private final Identifier id;
  @Getter
  private final String group;
  private final Ingredient input;
  @Getter(AccessLevel.PROTECTED)
  private final FluidStack output;
  @Getter
  private final int temperature;
  /** Number of "steps" needed to melt this, by default lava increases steps by 5 every 4 ticks (25 a second) */
  @Getter
  private final int time;

  @Override
  public boolean matches(IMeltingInventory inv, World world) {
    return input.test(inv.getStack());
  }

  @Override
  public int getTemperature(IMeltingInventory inv) {
    return temperature;
  }

  @Override
  public int getTime(IMeltingInventory inv) {
    return time;
  }

  @Override
  public FluidStack getOutput(IMeltingInventory inv) {
    return output.copy();
  }

  @Override
  public DefaultedList<Ingredient> getPreviewInputs() {
    return DefaultedList.copyOf(Ingredient.EMPTY, input);
  }

  @Override
  public net.minecraft.recipe.RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.meltingSerializer.get();
  }

  /** If true, this recipe is an ore recipe with increased output based on the machine */
  public boolean isOre() {
    return false;
  }

  /** Gets the recipe output for display in JEI */
  public List<List<FluidStack>> getDisplayOutput() {
    return Collections.singletonList(Collections.singletonList(output));
  }

  /** Interface for use in the serializer */
  @FunctionalInterface
  public interface IFactory<T extends MeltingRecipe> {
    /** Creates a new instance of this recipe */
    T create(Identifier id, String group, Ingredient input, FluidStack output, int temperature, int time);
  }

  /**
   * Serializer for {@link MeltingRecipe}
   */
  @RequiredArgsConstructor
  public static class Serializer<T extends MeltingRecipe> extends RecipeSerializer<T> {
    private final IFactory<T> factory;

    @Override
    public T read(Identifier id, JsonObject json) {
      String group = JsonHelper.getString(json, "group", "");
      Ingredient input = Ingredient.fromJson(json.get("ingredient"));
      FluidStack output = RecipeHelper.deserializeFluidStack(JsonHelper.getObject(json, "result"));

      // temperature calculates
      int temperature = JsonHelper.getInt(json, "temperature");
      int time = JsonHelper.getInt(json, "time");
      // validate values
      if (temperature < 0) throw new JsonSyntaxException("Melting temperature must be greater than zero");
      if (time <= 0) throw new JsonSyntaxException("Melting time must be greater than zero");

      return factory.create(id, group, input, output, temperature, time);
    }

    @Nullable
    @Override
    public T read(Identifier id, PacketByteBuf buffer) {
      String group = buffer.readString(Short.MAX_VALUE);
      Ingredient input = Ingredient.fromPacket(buffer);
      FluidStack output = FluidStack.readFromPacket(buffer);
      int temperature = buffer.readInt();
      int time = buffer.readVarInt();
      return factory.create(id, group, input, output, temperature, time);
    }

    @Override
    public void write(PacketByteBuf buffer, MeltingRecipe recipe) {
      buffer.writeString(recipe.group);
      recipe.input.write(buffer);
      recipe.output.writeToPacket(buffer);
      buffer.writeInt(recipe.temperature);
      buffer.writeVarInt(recipe.time);
    }
  }
}
