package tconstruct.library.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

/** Melting and hacking, churn and burn */
public class Smeltery
{
    public static Smeltery instance = new Smeltery();

    private final HashMap<ItemStack, FluidStack> smeltingList = new HashMap<ItemStack, FluidStack>();
    private final HashMap<ItemStack, Integer> temperatureList = new HashMap<ItemStack, Integer>();
    private final HashMap<ItemStack, ItemStack> renderIndex = new HashMap<ItemStack, ItemStack>();
    private final ArrayList<AlloyMix> alloys = new ArrayList<AlloyMix>();

    /**
     * Adds mappings between an itemstack and an output liquid Example:
     * Smeltery.addMelting(Block.oreIron, 0, 600, new
     * FluidStack(liquidMetalStill, TConstruct.ingotLiquidValue * 2, 0));
     * 
     * @param stack The itemstack to liquify. Must hold a block.
     * @param temperature How hot the block should be before liquifying. Max temp in the Smeltery is 800, other structures may vary
     * @param output The result of the process in liquid form
     */
    public static void addMelting (ItemStack stack, int temperature, FluidStack output)
    {
        if (stack.getItem() instanceof ItemBlock)
            addMelting(stack, ((ItemBlock) stack.getItem()).field_150939_a, stack.getItemDamage(), temperature, output);
        else
            throw new IllegalArgumentException("ItemStack must house a block.");
    }

    /**
     * Adds mappings between a block and its liquid Example:
     * Smeltery.addMelting(Block.oreIron, 0, 600, new
     * FluidStack(liquidMetalStill, TConstruct.ingotLiquidValue * 2, 0));
     * 
     * @param block The block to liquify and render
     * @param metadata The metadata of the block to liquify and render
     * @param temperature How hot the block should be before liquifying. Max temp in the Smeltery is 800, other structures may vary
     * @param output The result of the process in liquid form
     */
    public static void addMelting (Block block, int metadata, int temperature, FluidStack output)
    {
        addMelting(new ItemStack(block, 1, metadata), block, metadata, temperature, output);
    }

    /**
     * Adds mappings between an input and its liquid. Renders with the given
     * input's block ID and metadata Example: Smeltery.addMelting(Block.oreIron,
     * 0, 600, new FluidStack(liquidMetalStill,
     * TConstruct.ingotLiquidValue * 2, 0));
     * 
     * @param input The item to liquify
     * @param block The block to render
     * @param metadata The metadata of the block to render
     * @param temperature How hot the block should be before liquifying
     * @param liquid The result of the process
     */
    public static void addMelting (ItemStack input, Block block, int metadata, int temperature, FluidStack liquid)
    {
        instance.smeltingList.put(input, liquid);
        instance.temperatureList.put(input, temperature);
        instance.renderIndex.put(input, new ItemStack(block, input.stackSize, metadata));
    }

    /**
     * Adds an alloy mixing recipe. Example: Smeltery.addAlloyMixing(new
     * FluidStack(bronzeID, 2, 0), new FluidStack(copperID, 3, 0), new
     * FluidStack(tinID, 1, 0)); The example mixes 3 copper with 1 tin to make 2
     * bronze
     * 
     * @param result The output of the combination of mixers. The quantity is used for amount of a successful mix
     * @param mixers the liquids to be mixed. Quantities are used as ratios
     */
    public static void addAlloyMixing (FluidStack result, FluidStack... mixers)
    {
        ArrayList inputs = new ArrayList();
        for (FluidStack liquid : mixers)
            inputs.add(liquid);

        instance.alloys.add(new AlloyMix(result, inputs));
    }

    /**
     * Used to get the resulting temperature from a source ItemStack
     * 
     * @param item The Source ItemStack
     * @return The result temperature
     */
    public static Integer getLiquifyTemperature (ItemStack item)
    {
        if (item == null)
            return 20;

        Integer temp = (Integer) getValueIfContainsStack(instance.temperatureList, item);
        if (temp == null)
            return 20;
        else
            return temp;
    }

    /**
     * Used to get the resulting ItemStack from a source ItemStack
     * 
     * @param item The Source ItemStack
     * @return The result ItemStack
     */
    public static FluidStack getSmelteryResult (ItemStack item)
    {
        if (item == null)
            return null;

        FluidStack stack = (FluidStack) getValueIfContainsStack(instance.smeltingList, item);
        if (stack == null)
            return null;
        return stack.copy();
    }

    public static ItemStack getRenderIndex (ItemStack input)
    {
        return (ItemStack) getValueIfContainsStack(instance.renderIndex, input);
    }

    public static ArrayList mixMetals (ArrayList<FluidStack> moltenMetal)
    {
        ArrayList liquids = new ArrayList();
        for (AlloyMix alloy : instance.alloys)
        {
            FluidStack liquid = alloy.mix(moltenMetal);
            if (liquid != null)
                liquids.add(liquid);
        }
        return liquids;
    }

    public static HashMap<ItemStack, FluidStack> getSmeltingList ()
    {
        return instance.smeltingList;
    }

    public static HashMap<ItemStack, Integer> getTemperatureList ()
    {
        return instance.temperatureList;
    }

    public static HashMap<ItemStack, ItemStack> getRenderIndex ()
    {
        return instance.renderIndex;
    }

    public static ArrayList<AlloyMix> getAlloyList ()
    {
        return instance.alloys;
    }

    /**
     * Adds a mapping between FluidType and ItemStack
     * 
     * @author samtrion
     * 
     * @param type Type of Fluid
     * @param input The item to liquify
     * @param temperatureDifference  Difference between FluidType BaseTemperature
     * @param fluidAmount Amount of Fluid
     */
    public static void addMelting (FluidType type, ItemStack input, int temperatureDifference, int fluidAmount)
    {
        int temp = type.baseTemperature + temperatureDifference;
        if (temp <= 20)
            temp = type.baseTemperature;

        if (input.getItem() instanceof ItemBlock)
            addMelting(input, Block.getBlockFromItem(input.getItem()), input.getItemDamage(), type.baseTemperature + temperatureDifference, new FluidStack(type.fluid, fluidAmount));
        else
            addMelting(input, type.renderBlock, type.renderMeta, type.baseTemperature + temperatureDifference, new FluidStack(type.fluid, fluidAmount));
    }

    /**
     * Adds all Items to the Smeltery based on the oreDictionary Name
     * 
     * @author samtrion
     * 
     * @param oreName oreDictionary name e.g. oreIron
     * @param type Type of Fluid
     * @param temperatureDifference Difference between FluidType BaseTemperature
     * @param fluidAmount Amount of Fluid
     */
    public static void addDictionaryMelting (String oreName, FluidType type, int temperatureDifference, int fluidAmount)
    {
        for (ItemStack is : OreDictionary.getOres(oreName))
            addMelting(type, is, temperatureDifference, fluidAmount);
    }

    private static Object getValueIfContainsStack (Map<ItemStack, ?> map, ItemStack stack)
    {
        if (stack == null)
            return null;

        for (ItemStack i : map.keySet())
        {
            if (i != null && i.getItem() == stack.getItem() && i.getItemDamage() == stack.getItemDamage())
            {
                return map.get(i);
            }
        }

        return null;
    }

}