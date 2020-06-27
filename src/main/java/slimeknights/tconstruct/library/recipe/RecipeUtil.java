package slimeknights.tconstruct.library.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Helpers used in creation of recipes
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RecipeUtil {
  /**
   * Gets a list of all recipes from the manager, safely casting to the specified type
   * @param manager  Recipe manager
   * @param type     Recipe type
   * @param clazz    Preferred recipe class type
   * @param <I>  Inventory interface type
   * @param <T>  Recipe class
   * @param <C>  Return type
   * @return  List of recipes from the manager
   */
  public static <I extends IInventory, T extends IRecipe<I>, C extends T> List<C> getRecipes(RecipeManager manager, IRecipeType<T> type, Class<C> clazz) {
    return manager.getRecipes(type).values().stream()
                  .filter(clazz::isInstance)
                  .map(clazz::cast)
                  .collect(Collectors.toList());
  }

  /**
   * Serializes the fluid stack into JSON
   * @param stack  Stack to serialize
   * @return  JSON data
   */
  public static JsonObject serializeFluidStack(FluidStack stack) {
    JsonObject json = new JsonObject();
    json.addProperty("fluid", stack.getFluid().getRegistryName().toString());
    json.addProperty("amount", stack.getAmount());
    return json;
  }

  /**
   * Deserializes the fluid stack from JSON
   * @param json  JSON data
   * @return  Fluid stack instance
   * @throws JsonSyntaxException if syntax is invalid
   */
  public static FluidStack deserializeFluidStack(JsonObject json) {
    String fluidName = JSONUtils.getString(json, "fluid");
    Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
    if (fluid == null || fluid == Fluids.EMPTY) {
      throw new JsonSyntaxException("Unknown fluid " + fluidName);
    }
    int amount = JSONUtils.getInt(json, "amount");
    return new FluidStack(fluid, amount);
  }
}
