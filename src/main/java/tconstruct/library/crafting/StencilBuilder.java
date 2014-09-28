package tconstruct.library.crafting;

import java.util.*;
import net.minecraft.item.*;
import net.minecraftforge.oredict.OreDictionary;

public class StencilBuilder
{
    public static StencilBuilder instance = new StencilBuilder();

    public List<ItemStack> blanks = new LinkedList<ItemStack>(); // i wish ItemStack would support equals so i could use a Set here...
    public List<ItemStack> stencils = new ArrayList<ItemStack>();

    /**
     * Returns whether the given ItemStack is a blank pattern and therefore usable for stencil crafting.
     */
    public static boolean isBlank (ItemStack stack)
    {
        for (ItemStack blank : instance.blanks)
            if (OreDictionary.itemMatches(stack, blank, false)) // this has nothing to do with the oredictionary.
                return true;

        return false;
    }

    public static void registerBlankStencil (ItemStack itemStack)
    {
        instance.blanks.add(itemStack);
    }

    public static void registerStencil (Item item, int meta)
    {
        instance.stencils.add(new ItemStack(item, 1, meta));
    }

    public static void registerStencil (ItemStack pattern)
    {
        instance.stencils.add(pattern);
    }

    public static List<ItemStack> getStencils ()
    {
        return instance.stencils;
    }

    /**
     * Returns the index of the given stencil. If no stencil is found, returns -1.
     */
    public static int getIndex (ItemStack stencil)
    {
        for (int i = 0; i < instance.stencils.size(); i++)
            if (OreDictionary.itemMatches(stencil, getStencil(i), false))
                return i;

        return -1;
    }

    // returns the stencil with the given index
    public static ItemStack getStencil (int num)
    {
        if (num >= instance.stencils.size())
            return null;

        return instance.stencils.get(num).copy();
    }

    public static int getStencilCount ()
    {
        return instance.stencils.size();
    }
}
