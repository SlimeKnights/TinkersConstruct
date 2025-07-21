package slimeknights.tconstruct.library.recipe.melting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator.DuelSidedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Lookup for melting recipe, used for a few modifiers.
 * Notably does not contain information on dynamic melting recipes such as tool parts.
 */
public class MeltingRecipeLookup {
  private MeltingRecipeLookup() {}

  /** Record holding data from the melting recipe */
  public record MeltingFluid(Ingredient ingredient, FluidOutput result, int temperature) {
    /** Empty instance, used to cache missing results */
    public static final MeltingFluid EMPTY = new MeltingFluid(Ingredient.EMPTY, FluidOutput.EMPTY, 0);

    /** Checks if this result is present */
    public boolean isEmpty() {
      return result.isEmpty();
    }
  }

  /** If true, cache is currently frozen and will now allow changing {@link #FLUIDS} */
  private static boolean meltingFrozen = false;
  /** List of fluid recipes stored from melting recipe constructors */
  private static final List<MeltingFluid> FLUIDS = new ArrayList<>();
  /** Cache of lookup by item, assumes no recipes vary by NBT. We mostly use this for blocks anyways */
  private static final Map<Item,MeltingFluid> LOOKUP = new HashMap<>();
  /** Cache invalidator */
  private static final DuelSidedListener CACHE = RecipeCacheInvalidator.addDuelSidedListener(() -> {
    FLUIDS.clear();
    LOOKUP.clear();
  });

  /** Adds a fluid to the lookup */
  public static void addMeltingFluid(Ingredient ingredient, FluidOutput result, int temperature) {
    if (meltingFrozen) {
      return;
    }
    CACHE.checkClear();
    FLUIDS.add(new MeltingFluid(ingredient, result, temperature));
  }

  /** Cache populator */
  private static final Function<Item,MeltingFluid> LOOKUP_FUNCTION = item -> {
    if (item != Items.AIR) {
      ItemStack stack = new ItemStack(item);
      for (MeltingFluid fluid : FLUIDS) {
        if (fluid.ingredient.test(stack)) {
          return fluid;
        }
      }
    }
    return MeltingFluid.EMPTY;
  };

  /** Logic to find a fluid for a given item */
  public static MeltingFluid findFluid(ItemLike item) {
    return LOOKUP.computeIfAbsent(item.asItem(), LOOKUP_FUNCTION);
  }

  /** Finds the result fluid for the given input and temperature */
  public static FluidStack findResult(ItemLike input, int temperature) {
    MeltingFluid fluid = findFluid(input);
    if (fluid.temperature > temperature) {
      return FluidStack.EMPTY;
    }
    return fluid.result.get();
  }

  /** Checks if the given item can melt */
  public static boolean canMelt(ItemLike input) {
    return !findFluid(input).isEmpty();
  }

  /** Freezes the fluid list in the cache. Used to allow command driven datagen. */
  @Internal
  public static void freeze() {
    meltingFrozen = true;
  }

  /** Unfreezes the fluid list in the cache. Used to allow resuming the normal game without running reload. */
  @Internal
  public static void unfreeze() {
    meltingFrozen = false;
  }
}
