package slimeknights.tconstruct.plugin.jei.melting;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MeltingFuelHandler {
  /**
   * List of pairs of temperature and list of fluids with that or greater temperature
   * Sorted from highest to lowest temperature
   */
  private static List<Pair<Integer,List<FluidStack>>> fuelLookup = Collections.emptyList();
  /** List of all solid fuels from the JEI recipe manager. */
  private static List<ItemStack> allSolidFuels = List.of();
  /** Map of fuel durations for all stacks */
  private static Object2IntMap<Object> fuelDurations = Object2IntMaps.emptyMap();
  /** Ingredient showing examples of fuels */
  private static final Ingredient FUEL_EXAMPLES = Ingredient.of(TinkerTags.Items.FUEL_EXAMPLES);
  /** List of solid fuels for solid melting examples */
  public static final Lazy<List<ItemStack>> SOLID_FUELS = Lazy.of(() -> List.of(FUEL_EXAMPLES.getItems()));
  /** Item stack helper for grabbing cache keys */
  private static IIngredientHelper<ItemStack> itemHelper = null;

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


  /* Solid fuels */

  /** Sets the solid fuels from the given fuel stacks */
  public static void registerSolidFuels(IIngredientManager ingredientManager) {
    Collection<ItemStack> allStacks = ingredientManager.getAllItemStacks();
    List<ItemStack> fuels = new ArrayList<>(allStacks.size());
    Object2IntMap<Object> newFuels = new Object2IntOpenHashMap<>(allStacks.size());
    itemHelper = ingredientManager.getIngredientHelper(VanillaTypes.ITEM_STACK);
    RecipeType<?> fuel = TinkerRecipeTypes.FUEL.get();
    for (ItemStack stack : allStacks) {
      try {
        int burnTime = ForgeHooks.getBurnTime(stack, fuel);
        if (burnTime > 0) {
          fuels.add(stack);
          newFuels.put(itemHelper.getUid(stack, UidContext.Ingredient), burnTime);
        }
      } catch (RuntimeException | LinkageError e) {
        TConstruct.LOG.error("Failed to check if item is fuel {}.", stack, e);
      }
    }
    allSolidFuels = List.copyOf(fuels);
    fuelDurations = Object2IntMaps.unmodifiable(newFuels);
  }

  /** Gets a list of all solid fuels for display in the fuel category */
  public static List<ItemStack> getAllSolidFuels() {
    if (allSolidFuels.isEmpty()) {
      return SOLID_FUELS.get();
    }
    return allSolidFuels;
  }

  /** Gets the duration of the given item stack */
  public static int getFuelDuration(ItemStack stack) {
    if (itemHelper != null) {
      return fuelDurations.getOrDefault(itemHelper.getUid(stack, UidContext.Ingredient), 0);
    }
    return 0;
  }
}
