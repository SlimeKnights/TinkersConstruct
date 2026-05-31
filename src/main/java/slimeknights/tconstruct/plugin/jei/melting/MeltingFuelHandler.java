package slimeknights.tconstruct.plugin.jei.melting;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MeltingFuelHandler {
  /**
   * List of pairs of temperature and list of fluids with that or greater temperature
   * Sorted from highest to lowest temperature
   */
  private static List<Pair<Integer,List<FluidStack>>> fuelLookup = Collections.emptyList();

  /** List of solid fuels for solid melting */
  public static final Lazy<List<ItemStack>> SOLID_FUELS = Lazy.of(() -> Arrays.asList(
    new ItemStack(Items.COAL), new ItemStack(Items.CHARCOAL), new ItemStack(Blocks.OAK_LOG), new ItemStack(Blocks.OAK_PLANKS), new ItemStack(Items.BLAZE_ROD)));

  /**
   * Updates the melting cache, called on JEI load
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
                          .filter(fuel -> fuel.getTemperature() >= temperature)
                          .flatMap(fuel -> fuel.getInputs().stream())
                          .collect(Collectors.toList())))
                      .collect(Collectors.toList());
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
}
