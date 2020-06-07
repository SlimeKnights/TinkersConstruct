package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import slimeknights.tconstruct.library.recipe.RecipeUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CastingRecipeSerializer<T extends AbstractCastingRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>>
  implements IRecipeSerializer<T> {
  private final IFactory<T> factory;

  public CastingRecipeSerializer(IFactory<T> factoryIn) {
    this.factory = factoryIn;
  }

  @Override
  public T read(ResourceLocation recipeId, JsonObject json) {
    Ingredient cast = Ingredient.EMPTY;
    String group = JSONUtils.getString(json, "group", "");
    boolean consumed = false;
    boolean switchSlots = JSONUtils.getBoolean(json, "switch_slots", false);
    if (json.has("cast")) {
      JsonElement jsonelement = JSONUtils.getJsonObject(json, "cast");
      cast = Ingredient.deserialize(jsonelement);
      consumed = JSONUtils.getBoolean(json, "cast_consumed", false);
    }

    if (!json.has("fluidstack"))
      throw new JsonSyntaxException("Missing fluid input definition!");
    JsonObject jsonFluid = JSONUtils.getJsonObject(json, "fluidstack");
    FluidStack fluidStack = RecipeUtil.deserializeFluidStack(jsonFluid);
    ItemStack item = new ItemStack(JSONUtils.getItem(json, "result"));
    int coolingtime;
    if (!json.has("cooling_time")) {
      int time = 24;
      int temperature = fluidStack.getFluid().getAttributes().getTemperature() - 300;
      coolingtime = time + (temperature * fluidStack.getAmount()) / 1600;
    }
    else {
      coolingtime = JSONUtils.getInt(json, "cooling_time");
    }
    return this.factory.create(recipeId, group, cast, fluidStack, item, coolingtime, consumed, switchSlots);
  }

  @Nullable
  @Override
  public T read(ResourceLocation recipeId, PacketBuffer buffer) {
    String group = buffer.readString(Short.MAX_VALUE);
    Ingredient cast = Ingredient.read(buffer);
    FluidStack fluidStack = FluidStack.readFromPacket(buffer);
    ItemStack output = buffer.readItemStack();
    int coolingtime = buffer.readInt();
    boolean consumed = buffer.readBoolean();
    boolean switchSlots = buffer.readBoolean();
    return this.factory.create(recipeId, group, cast, fluidStack, output, coolingtime, consumed, switchSlots);
  }

  @Override
  public void write(PacketBuffer buffer, AbstractCastingRecipe recipe) {
    buffer.writeString(recipe.getGroup());
    recipe.getCast().write(buffer);
    recipe.getFluid().writeToPacket(buffer);
    buffer.writeItemStack(recipe.getRecipeOutput());
    buffer.writeInt(recipe.getCoolingTime());
    buffer.writeBoolean(recipe.isConsumed());
    buffer.writeBoolean(recipe.switchSlots());
  }

  public interface IFactory<T extends AbstractCastingRecipe> {
    T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, @Nonnull FluidStack fluidIn,
             ItemStack result, int coolingTime, boolean consumed, boolean switchSlots);
  }
}
