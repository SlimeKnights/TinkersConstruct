package slimeknights.tconstruct.library.recipe.casting.material;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator.DuelSidedListener;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

import java.util.Collection;

/**
 * Class serving as a lookup to get part costs for any material item
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaterialItemCostLookup {
  /** Map containing a lookup from a material item to the cost in mb, for table parts */
  private static final Object2IntMap<IMaterialItem> TABLE_LOOKUP = new Object2IntOpenHashMap<>(50);
  /** Map containing a lookup from a material item to the cost in mb, for basin parts */
  private static final Object2IntMap<IMaterialItem> BASIN_LOOKUP = new Object2IntOpenHashMap<>(50);

  /** Listener for clearing the recipe cache on recipe reload */
  private static final DuelSidedListener LISTENER = RecipeCacheInvalidator.addDuelSidedListener(() -> {
    TABLE_LOOKUP.clear();
    BASIN_LOOKUP.clear();
  });

  /**
   * Registers a new basin material item
   * @param item  Material item
   * @param cost  Cost in mb for that item
   */
  public static void registerBasin(IMaterialItem item, int cost) {
    LISTENER.checkClear();
    BASIN_LOOKUP.put(item, cost);
  }

  /**
   * Registers a new table material item
   * @param item  Material item
   * @param cost  Cost in mb for that item
   */
  public static void registerTable(IMaterialItem item, int cost) {
    LISTENER.checkClear();
    TABLE_LOOKUP.put(item, cost);
  }

  /**
   * Gets the cost for the given material item in a basin
   * @param item  Item
   * @return  Item cost
   */
  public static int getBasinCost(IMaterialItem item) {
    return BASIN_LOOKUP.getOrDefault(item, 0);
  }

  /**
   * Gets the cost for the given material item in a table
   * @param item  Item
   * @return  Item cost
   */
  public static int getTableCost(IMaterialItem item) {
    return TABLE_LOOKUP.getOrDefault(item, 0);
  }

  /**
   * Gets a collection of all registered basin parts
   * @return  Collection of parts
   */
  public static Collection<Entry<IMaterialItem>> getAllBasinParts() {
    return BASIN_LOOKUP.object2IntEntrySet();
  }

  /**
   * Gets a collection of all registered table parts
   * @return Collection of parts
   */
  public static Collection<Entry<IMaterialItem>> getAllTableParts() {
    return TABLE_LOOKUP.object2IntEntrySet();
  }
}
