package tconstruct.library.util;

import java.util.*;
import net.minecraft.util.StatCollector;

/**
 * Lookup for the name of each harvest level. Use this clientside only for display purposes.
 */
public final class HarvestLevels
{
    private HarvestLevels()
    {
    } // non-instantiable

    public static final Map<Integer, String> harvestLevelNames = new HashMap<Integer, String>();

    public static String getHarvestLevelName (int num)
    {
        return harvestLevelNames.containsKey(num) ? harvestLevelNames.get(num) : String.valueOf(num);
    }

    // initialization
    static
    {
        String base = "gui.mining";
        int i = 0;
        while (StatCollector.canTranslate(String.format("%s%d", base, i + 1)))
        {
            harvestLevelNames.put(i, StatCollector.translateToLocal(String.format("%s%d", base, i + 1)));
            i++;
        }
    }
}
