package tconstruct.library.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

/** Melting and hacking, churn and burn */
public class Smeltery
{
    public static Smeltery instance = new Smeltery();

    private final Map<SmelteryInput, FluidStack> smeltingList = new HashMap<SmelteryInput, FluidStack>();
    private final Map<SmelteryInput, Integer> temperatureList = new HashMap<SmelteryInput, Integer>();
    private final Map<SmelteryInput, ItemStack> renderIndex = new HashMap<SmelteryInput, ItemStack>();
    private final List<AlloyMix> alloys = new ArrayList<AlloyMix>();

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
     * @param blockID The ID of the block to liquify and render
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
     * @param block The ID of the block to render
     * @param metadata The metadata of the block to render
     * @param temperature How hot the block should be before liquifying
     * @param liquid The result of the process
     */
    public static void addMelting (ItemStack input, Block block, int metadata, int temperature, FluidStack liquid)
    {
        SmelteryInput in = new SmelteryInput(input);
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

        Integer temp = instance.temperatureList.get(new SmelteryInput(item));
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

        FluidStack stack = instance.smeltingList.get(new SmelteryInput(item));
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
        return instance.renderIndex.get(new SmelteryInput(input));
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

    public static Map<SmelteryInput, FluidStack> getSmeltingList ()
    {
        return instance.smeltingList;
    }

    public static Map<SmelteryInput, Integer> getTemperatureList ()
    {
        return instance.temperatureList;
    }

    public static Map<SmelteryInput, ItemStack> getRenderIndex ()
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

    public static class SmelteryInput
    {
        public final Item input;
        public final int meta;

        public SmelteryInput(ItemStack inputStack)
        {
            this(inputStack.getItem(), inputStack.getItemDamage());
        }

        public SmelteryInput(Item input, int meta)
        {
            this.input = input;
            this.meta = meta;
        }

        @Override
        public int hashCode ()
        {
            return Item.getIdFromItem(this.input) << 16 | this.meta;
        }

        @Override
        public boolean equals (Object o)
        {
            if (o == this)
                return true;
            else if (o instanceof SmelteryInput)
                return this.input == ((SmelteryInput) o).input && this.meta == ((SmelteryInput) o).meta;

            return false;
        }
    }
}