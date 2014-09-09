package tconstruct.library.crafting;

import java.util.*;
import mantle.utils.ItemMetaWrapper;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.oredict.OreDictionary;

/** Melting and hacking, churn and burn */
public class Smeltery
{
    public static Smeltery instance = new Smeltery();

    private final Map<ItemMetaWrapper, FluidStack> smeltingList = new HashMap<ItemMetaWrapper, FluidStack>();
    private final Map<ItemMetaWrapper, Integer> temperatureList = new HashMap<ItemMetaWrapper, Integer>();
    private final Map<ItemMetaWrapper, ItemStack> renderIndex = new HashMap<ItemMetaWrapper, ItemStack>();
    private final List<AlloyMix> alloys = new ArrayList<AlloyMix>();
    private final Map<Fluid, Integer[]> smelteryFuels = new HashMap<Fluid, Integer[]>(); // fluid -> [power, duration]

    /**
     * Add a new fluid as a valid Smeltery fuel.
     * @param fluid The fluid.
     * @param power The temperature of the fluid. This also influences the melting speed. Lava is 1000.
     * @param duration How long one "portion" of liquid fuels the smeltery. Lava is 10.
     */
    public static void addSmelteryFuel (Fluid fluid, int power, int duration)
    {
        instance.smelteryFuels.put(fluid, new Integer[] { power, duration });
    }

    /**
     * Returns true if the liquid is a valid smeltery fuel.
     */
    public static boolean isSmelteryFuel (Fluid fluid)
    {
        return instance.smelteryFuels.containsKey(fluid);
    }

    /**
     * Returns the power of a smeltery fuel or 0 if it's not a fuel.
     */
    public static int getFuelPower (Fluid fluid)
    {
        Integer[] power = instance.smelteryFuels.get(fluid);
        return power == null ? 0 : power[0];
    }

    /**
     * Returns the duration of a smeltery fuel or 0 if it's not a fuel.
     */
    public static int getFuelDuration (Fluid fluid)
    {
        Integer[] power = instance.smelteryFuels.get(fluid);
        return power == null ? 0 : power[1];
    }

    /**
     * Adds mappings between an itemstack and an output liquid Example:
     * Smeltery.addMelting(Block.oreIron, 0, 600, new
     * FluidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 0));
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
     * FluidStack(liquidMetalStill.blockID, TConstruct.ingotLiquidValue * 2, 0));
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
     * 0, 600, new FluidStack(liquidMetalStill.blockID,
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
        ItemMetaWrapper in = new ItemMetaWrapper(input);
        instance.smeltingList.put(in, liquid);
        instance.temperatureList.put(in, temperature);
        instance.renderIndex.put(in, new ItemStack(block, input.stackSize, metadata));
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

        Integer temp = instance.temperatureList.get(new ItemMetaWrapper(item));
        if (temp == null)
            return 20;
        else
            return temp;
    }

    /**
     * Used to get the resulting temperature from a source Block
     * 
     * @param block The Source Block
     * @return The result ItemStack
     */
    public static Integer getLiquifyTemperature (Block block, int metadata)
    {
        return instance.getLiquifyTemperature(new ItemStack(block, 1, metadata));
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

        FluidStack stack = instance.smeltingList.get(new ItemMetaWrapper(item));
        if (stack == null)
            return null;
        return stack.copy();
    }

    /**
     * Used to get the resulting ItemStack from a source Block
     * 
     * @param block The Source Block
     * @return The result ItemStack
     */
    public static FluidStack getSmelteryResult (Block block, int metadata)
    {
        return instance.getSmelteryResult(new ItemStack(block, 1, metadata));
    }

    public static ItemStack getRenderIndex (ItemStack input)
    {
        return instance.renderIndex.get(new ItemMetaWrapper(input));
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

    public static Map<ItemMetaWrapper, FluidStack> getSmeltingList ()
    {
        return instance.smeltingList;
    }

    public static Map<ItemMetaWrapper, Integer> getTemperatureList ()
    {
        return instance.temperatureList;
    }

    public static Map<ItemMetaWrapper, ItemStack> getRenderIndex ()
    {
        return instance.renderIndex;
    }

    public static List<AlloyMix> getAlloyList ()
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
            addMelting(input, ((ItemBlock) input.getItem()).field_150939_a, input.getItemDamage(), type.baseTemperature + temperatureDifference, new FluidStack(type.fluid, fluidAmount));
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
}