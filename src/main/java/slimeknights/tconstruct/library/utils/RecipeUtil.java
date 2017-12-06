package slimeknights.tconstruct.library.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import slimeknights.mantle.client.CreativeTab;

public final class RecipeUtil {
  // list of orePreferences, to be filled by the config
  private static String[] orePreferences = new String[0];
  // cache to avoid grabing the same name twice
  private static Map<String,ItemStack> preferenceCache = new HashMap<>();

  private RecipeUtil() {}

  /**
   * Called by the config to add the preferences. Do not call outside of Tinkers Construct
   * @param preferences
   */
  public static void setOrePreferences(String[] preferences) {
    orePreferences = preferences;
  }

  /**
   * Gets the preferred ore for the given oredict name
   * @param oreName  Oredictionary key
   * @return  The preferred ItemStack, or ItemStack.EMPTY if the name is empty
   */
  public static ItemStack getPreference(String oreName) {
    // if we found one before, use that
    if(preferenceCache.containsKey(oreName)) {
      return preferenceCache.get(oreName).copy();
    }

    List<ItemStack> items = OreDictionary.getOres(oreName, false);

    // if nothing matches the name, just return nothing
    if(items.isEmpty()) {
      return ItemStack.EMPTY;
    }

    cachePreference(oreName, items);
    return preferenceCache.get(oreName).copy();
  }

  /**
   * Function called to actually do the caching
   * @param oreName  String to check
   * @param items    Items to search
   */
  private static void cachePreference(String oreName, List<ItemStack> items) {
    // search through each preference name, finding the first item for the name
    ItemStack preference = null;
    for(String mod : orePreferences) {
      Optional<ItemStack> optional = items
          .stream()
          .filter(stack -> !stack.isEmpty() && stack.getItem().getRegistryName().getResourceDomain().equals(mod))
          .findFirst();

      // if we found something, use that stack and stop searching
      if(optional.isPresent()) {
        preference = optional.get();
        break;
      }
    }
    // if we found no preference, just use the first available stack
    if(preference == null) {
      preference = items.get(0);
    }

    // ensure we do not return a wildcard value
    if(preference.getMetadata() == OreDictionary.WILDCARD_VALUE) {
      NonNullList<ItemStack> subItems = NonNullList.create();
      preference.getItem().getSubItems(CreativeTab.SEARCH, subItems);
      preference = subItems.get(0);
    }

    // finally cache the preference
    preferenceCache.put(oreName, preference);
  }
}
