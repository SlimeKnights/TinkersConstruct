package mods.tinker.tconstruct.library.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;

/** Melting and hacking, churn and burn */
public class Smeltery
{
    public static Smeltery instance = new Smeltery();

    private HashMap<List<Integer>, LiquidStack> smeltingList = new HashMap<List<Integer>, LiquidStack>();
    private HashMap<List<Integer>, Integer> temperatureList = new HashMap<List<Integer>, Integer>();
    private HashMap<List<Integer>, ItemStack> renderIndex = new HashMap<List<Integer>, ItemStack>();
    private ArrayList<AlloyMix> alloys = new ArrayList<AlloyMix>();

    /** Adds mappings between an itemstack and an output liquid
     * Example: Smeltery.addMelting(Block.oreIron, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 0));
	 * 
	 * @param stack The itemstack to liquify
	 * @param temperature How hot the block should be before liquifying. Max temp in the Smeltery is 800, other structures may vary
	 * @param output The result of the process in liquid form
	 */
    public static void addMelting(ItemStack stack, int temperature, LiquidStack output)
    {
    	addMelting(stack, stack.itemID, stack.getItemDamage(), temperature, output);
    }
    
	/** Adds mappings between a block and its liquid
	 * Example: Smeltery.addMelting(Block.oreIron, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 0));
	 * 
	 * @param blockID The ID of the block to liquify and render
	 * @param metadata The metadata of the block to liquify and render
	 * @param temperature How hot the block should be before liquifying. Max temp in the Smeltery is 800, other structures may vary
	 * @param output The result of the process in liquid form
	 */
    public static void addMelting(Block block, int metadata, int temperature, LiquidStack output)
    {
    	addMelting(new ItemStack(block, 1, metadata), block.blockID, metadata, temperature, output);
    }
    
    /** Adds mappings between an input and its liquid.
     * Renders with the given input's block ID and metadata
     * Example: Smeltery.addMelting(Block.oreIron, 0, 600, new LiquidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 0));
	 * 
	 * @param input The item to liquify
	 * @param blockID The ID of the block to render
	 * @param metadata The metadata of the block to render
	 * @param temperature How hot the block should be before liquifying
	 * @param liquid The result of the process
	 */
    public static void addMelting(ItemStack input, int blockID, int metadata, int temperature, LiquidStack liquid)
    {
        instance.smeltingList.put(Arrays.asList(input.itemID, input.getItemDamage()), liquid);
        instance.temperatureList.put(Arrays.asList(input.itemID, input.getItemDamage()), temperature);
        instance.renderIndex.put(Arrays.asList(input.itemID, input.getItemDamage()), new ItemStack(blockID, input.stackSize, metadata));
    }
    
    /** Adds an alloy mixing recipe.
     * Example: Smeltery.addAlloyMixing(new LiquidStack(bronzeID, 2, 0), new LiquidStack(copperID, 3, 0), new LiquidStack(tinID, 1, 0));
     * The example mixes 3 copper with 1 tin to make 2 bronze
     * 
     * @param result The output of the combination of mixers. The quantity is used for amount of a successful mix
     * @param mixers the liquids to be mixed. Quantities are used as ratios
     */
    public static void addAlloyMixing(LiquidStack result, LiquidStack... mixers)
    {
    	ArrayList inputs = new ArrayList();
    	for (LiquidStack liquid : mixers)
    		inputs.add(liquid);
    	
    	instance.alloys.add(new AlloyMix(result, inputs));
    }
    
    /**
     * Used to get the resulting temperature from a source ItemStack
     * @param item The Source ItemStack
     * @return The result temperature
     */
    public static Integer getLiquifyTemperature(ItemStack item) 
    {
        if (item == null)
            return 20;
        
        Integer temp = instance.temperatureList.get(Arrays.asList(item.itemID, item.getItemDamage()));
        if (temp == null)
        	return 20;
        else
        	return temp;
    }
    
    /**
     * Used to get the resulting temperature from a source Block
     * @param item The Source ItemStack
     * @return The result ItemStack
     */
    public static Integer getLiquifyTemperature(int blockID, int metadata) 
    {
        return instance.temperatureList.get(Arrays.asList(blockID, metadata));
    }

    /**
     * Used to get the resulting ItemStack from a source ItemStack
     * @param item The Source ItemStack
     * @return The result ItemStack
     */
    public static LiquidStack getSmelteryResult(ItemStack item) 
    {
        if (item == null)
            return null;
        
        LiquidStack stack = (LiquidStack) instance.smeltingList.get(Arrays.asList(item.itemID, item.getItemDamage()));
        if (stack == null)
        	return null;
        return stack.copy();
    }
    
    /**
     * Used to get the resulting ItemStack from a source Block
     * @param item The Source ItemStack
     * @return The result ItemStack
     */
    public static LiquidStack getSmelteryResult(int blockID, int metadata) 
    {
    	LiquidStack stack = (LiquidStack) instance.smeltingList.get(Arrays.asList(blockID, metadata));
         if (stack == null)
         	return null;
         return stack.copy();
    }
    
    public static ItemStack getRenderIndex(ItemStack input)
    {
    	return instance.renderIndex.get(Arrays.asList(input.itemID, input.getItemDamage()));
    }
    
    public static ArrayList mixMetals(ArrayList<LiquidStack> moltenMetal)
    {
    	ArrayList liquids = new ArrayList();
    	for (AlloyMix alloy : instance.alloys)
    	{
    		LiquidStack liquid = alloy.mix(moltenMetal);
    		if (liquid != null)
    			liquids.add(liquid);
    	}
    	return liquids;
    }
    
    public static HashMap<List<Integer>, LiquidStack> getSmeltingList()
    {
        return instance.smeltingList;
    }
    
    public static HashMap<List<Integer>, Integer> getTemperatureList()
    {
        return instance.temperatureList;
    }
    
    public static HashMap<List<Integer>, ItemStack> getRenderIndex()
    {
        return instance.renderIndex;
    }
    
    public static ArrayList<AlloyMix> getAlloyList()
    {
        return instance.alloys;
    }
}
