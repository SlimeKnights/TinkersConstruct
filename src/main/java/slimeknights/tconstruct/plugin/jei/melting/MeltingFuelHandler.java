package slimeknights.tconstruct.plugin.jei.melting;

import com.mojang.datafixers.util.Pair;
import mezz.jei.api.recipe.vanilla.IJeiFuelingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class MeltingFuelHandler {
  /**
   * List of pairs of temperature and list of fluids with that or greater temperature
   * Sorted from highest to lowest temperature
   */
  private static List<Pair<Integer,List<FluidStack>>> fuelLookup = Collections.emptyList();
  /** List of all solid fuels from the JEI recipe manager. */
  private static List<ItemStack> allSolidFuels = List.of();

  /** Ingredient showing examples of fuels */
  private static final Ingredient FUEL_EXAMPLES = Ingredient.of(TinkerTags.Items.FUEL_EXAMPLES);
  /** List of solid fuels for solid melting examples */
  public static final Lazy<List<ItemStack>> SOLID_FUELS = Lazy.of(() -> List.of(FUEL_EXAMPLES.getItems()));

  /**
   * Updates the melting cache, called on JEI load.
   * TODO 1.21: fix method name.
   * @param fuels  List of fuel recipes
   */
  public static void setMeltngFuels(List<MeltingFuel> fuels) {
    // sort the fuels highest first
    fuels.sort(Comparator.comparingInt(MeltingFuel::getTemperature));
    // get a list of temperature to fuel
    fuelLookup = fuels.stream()
      .mapToInt(MeltingFuel::getTemperature)
      .distinct()
      .mapToObj((temperature) -> Pair.of(temperature, fuels.stream()
        .filter(fuel -> fuel.getTemperature() >= temperature && !fuel.getInputs().isEmpty())
        .flatMap(fuel -> fuel.getInputs().stream())
        .toList()))
      .toList();
  }

  /**
   * Gets a fluid stack list for the given temperature
   * @param temperature  Recipe temperature
   * @return  List of fuels for the given temperature
   */
  public static List<FluidStack> getUsableFuels(int temperature) {
    // first fuel list with a temperature bigger is the one to use
    for (Pair<Integer, List<FluidStack>> pair : fuelLookup) {
      if (temperature <= pair.getFirst()) {
        return pair.getSecond();
      }
    }
    return Collections.emptyList();
  }

  /** Sets the solid fuels from the given fuel stacks */
  public static void setAllSolidFuels(Stream<IJeiFuelingRecipe> fuels) {
    allSolidFuels = fuels.flatMap(fuel -> fuel.getInputs().stream()).toList();
  }

  /** Gets a list of all solid fuels for display in the fuel category */
  public static List<ItemStack> getAllSolidFuels() {
    if (allSolidFuels.isEmpty()) {
      return SOLID_FUELS.get();
    }
    return allSolidFuels;
  }
}
