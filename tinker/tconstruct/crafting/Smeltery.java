package tinker.tconstruct.crafting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;

/** Melting and hacking, churn and burn */
public class Smeltery
{
    public static Smeltery instance = new Smeltery();

    private HashMap<List<Integer>, LiquidStack> smeltingList = new HashMap<List<Integer>, LiquidStack>();
    private HashMap<List<Integer>, Integer> temperatureList = new HashMap<List<Integer>, Integer>();

	/** Adds a mapping between an input and an itemstack
	 * 
	 * @param itemID The block or item's main ID
	 * @param metadata Damage or use
	 * @param itemstack
	 */
    public static void addLiquidMelting(int itemID, int metadata, int temperature, LiquidStack itemstack)
    {
        instance.smeltingList.put(Arrays.asList(itemID, metadata), itemstack);
        instance.temperatureList.put(Arrays.asList(itemID, metadata), temperature);
    }
    
    /**
     * Used to get the resulting temperature from a source ItemStack
     * @param item The Source ItemStack
     * @return The result temperature
     */
    public static Integer getSmeltingTemperature(ItemStack item) 
    {
        if (item == null)
            return 20;
        
        Integer temp = instance.temperatureList.get(Arrays.asList(item.itemID, item.getItemDamage()));
        if (temp == null)
        	return 20;
        else
        	return temp;
        //return instance.temperatureList.get(Arrays.asList(item.itemID, item.getItemDamage()));
    }
    
    public static Integer getSmeltingTemperature(int blockID)
    {
    	return getSmeltingTemperature(blockID, 0);
    }
    
    /**
     * Used to get the resulting temperature from a source Block
     * @param item The Source ItemStack
     * @return The result ItemStack
     */
    public static Integer getSmeltingTemperature(int blockID, int metadata) 
    {
        return instance.temperatureList.get(Arrays.asList(blockID, metadata));
    }

    /**
     * Used to get the resulting ItemStack from a source ItemStack
     * @param item The Source ItemStack
     * @return The result ItemStack
     */
    public static LiquidStack getSmeltingResult(ItemStack item) 
    {
        if (item == null)
            return null;
        
        LiquidStack stack = (LiquidStack) instance.smeltingList.get(Arrays.asList(item.itemID, item.getItemDamage()));
        if (stack == null)
        	return null;
        return stack.copy();
    }
    
    public static LiquidStack getSmeltingResult(int blockID)
    {
    	return getSmeltingResult(blockID, 0);
    }
    
    /**
     * Used to get the resulting ItemStack from a source Block
     * @param item The Source ItemStack
     * @return The result ItemStack
     */
    public static LiquidStack getSmeltingResult(int blockID, int metadata) 
    {
    	LiquidStack stack = (LiquidStack) instance.smeltingList.get(Arrays.asList(blockID, metadata));
         if (stack == null)
         	return null;
         return stack.copy();
    }
}
