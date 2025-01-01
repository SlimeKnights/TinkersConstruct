package slimeknights.tconstruct.library.recipe.casting.material;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator.DuelSidedListener;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.utils.SimpleCache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class serving as a lookup to get part costs for any material item
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaterialCastingLookup {
  /** Map containing a lookup from a material item to the cost in mb */
  private static final Object2IntMap<IMaterialItem> ITEM_COST_LOOKUP = new Object2IntOpenHashMap<>(50);

  /** Fluids that cast into materials */
  private static final List<MaterialFluidRecipe> CASTING_FLUIDS = new ArrayList<>();
  /** Fluids that composite into materials */
  private static final List<MaterialFluidRecipe> COMPOSITE_FLUIDS = new ArrayList<>();

  /** Cache for casting recipe for a given fluid */
  private static final SimpleCache<Fluid,MaterialFluidRecipe> CASTING_CACHE = new SimpleCache<>(fluid -> {
    for (MaterialFluidRecipe recipe : CASTING_FLUIDS) {
      if (recipe.matches(fluid)) {
        return recipe;
      }
    }
    return MaterialFluidRecipe.EMPTY;
  });

  private record CompositeCacheKey(Fluid fluid, MaterialVariantId input) {}

  /** Cache for a composite recipe for a given material */
  private static final SimpleCache<CompositeCacheKey,MaterialFluidRecipe> COMPOSITE_CACHE = new SimpleCache<>(key -> {
    for (MaterialFluidRecipe recipe : COMPOSITE_FLUIDS) {
      if (recipe.matches(key.fluid, key.input)) {
        return recipe;
      }
    }
    return MaterialFluidRecipe.EMPTY;
  });

  /** Listener for clearing the recipe cache on recipe reload */
  private static final DuelSidedListener LISTENER = RecipeCacheInvalidator.addDuelSidedListener(() -> {
    ITEM_COST_LOOKUP.clear();
    CASTING_FLUIDS.clear();
    CASTING_CACHE.clear();
    COMPOSITE_FLUIDS.clear();
    COMPOSITE_CACHE.clear();
  });

  /** Shared logic to register parts */
  public static void registerItemCost(IMaterialItem item, int cost) {
    LISTENER.checkClear();
    // if it already exists
    if (ITEM_COST_LOOKUP.containsKey(item)) {
      int original = ITEM_COST_LOOKUP.getInt(item);
      if (cost != original) {
        TConstruct.LOG.error("Inconsistent cost for item {}", BuiltInRegistries.ITEM.getKey(item.asItem()));
        ITEM_COST_LOOKUP.put(item, Math.min(cost, original));
      }
    } else {
      ITEM_COST_LOOKUP.put(item, cost);
    }
  }

  /**
   * Registers a fluid recipe to be detected
   * @param recipe  Recipe to add
   */
  public static void registerFluid(MaterialFluidRecipe recipe) {
    LISTENER.checkClear();
    if (recipe.getInput() == null) {
      CASTING_FLUIDS.add(recipe);
    } else {
      COMPOSITE_FLUIDS.add(recipe);
    }
  }

  /**
   * Gets the cost for the given material item in a table
   * @param item  Item
   * @return  Item cost
   */
  public static int getItemCost(IMaterialItem item) {
    return ITEM_COST_LOOKUP.getOrDefault(item, 0);
  }

  /**
   * Gets the cost for the given material item in a table
   * @param item  Item
   * @return  Item cost
   */
  public static int getItemCost(Item item) {
    return ITEM_COST_LOOKUP.getOrDefault(item, 0);
  }

  /**
   * Gets a collection of all registered table parts
   * @return Collection of parts
   */
  public static Collection<Entry<IMaterialItem>> getAllItemCosts() {
    return ITEM_COST_LOOKUP.object2IntEntrySet();
  }

  /**
   * Gets the material the given fluid casts into
   * @param fluid  Fluid
   * @return  Recipe
   */
  public static MaterialFluidRecipe getCastingFluid(Fluid fluid) {
    return CASTING_CACHE.apply(fluid);
  }

  /**
   * Gets the composite fluid recipe for the given inventory
   * @param fluid     Fluid
   * @param material  Material input
   * @return  Composite fluid recipe
   */
  public static MaterialFluidRecipe getCompositeFluid(Fluid fluid, MaterialVariantId material) {
    return COMPOSITE_CACHE.apply(new CompositeCacheKey(fluid, material));
  }

  /**
   * Gets all recipes for the given material
   * @param material  Fluid
   * @return  Recipe
   */
  public static List<MaterialFluidRecipe> getCastingFluids(MaterialVariantId material) {
    return CASTING_FLUIDS.stream()
                         .filter(recipe -> material.matchesVariant(recipe.getOutput()))
                         .collect(Collectors.toList());
  }

  /**
   * Gets all recipes for the given material
   * @param material  Fluid
   * @return  Recipe
   */
  public static List<MaterialFluidRecipe> getCompositeFluids(MaterialVariantId material) {
    return COMPOSITE_FLUIDS.stream()
                           .filter(recipe -> material.matchesVariant(recipe.getOutput()))
                           .collect(Collectors.toList());
  }

  /**
   * Gets all casting fluid recipes
   * @return  Collection of all recipes
   */
  public static Collection<MaterialFluidRecipe> getAllCastingFluids() {
    return CASTING_FLUIDS;
  }

  /**
   * Gets all composite fluid recipes
   * @return  Collection of all recipes
   */
  public static Collection<MaterialFluidRecipe> getAllCompositeFluids() {
    return COMPOSITE_FLUIDS;
  }
}
