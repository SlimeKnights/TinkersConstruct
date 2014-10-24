package tconstruct.library.crafting;

import java.util.*;
import net.minecraft.item.*;
import net.minecraftforge.oredict.OreDictionary;

public class StencilBuilder
{
    public static StencilBuilder instance = new StencilBuilder();

    public List<ItemStack> blanks = new LinkedList<ItemStack>(); // i wish ItemStack would support equals so i could use a Set here...
    public Map<Integer, ItemStack> stencils = new TreeMap<Integer, ItemStack>();

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

    public static void registerStencil (int id, Item item, int meta)
    {
        registerStencil(id, new ItemStack(item, 1, meta));
    }

    public static void registerStencil (int id, ItemStack pattern)
    {
        if(instance.stencils.containsKey(id))
            throw new IllegalArgumentException("[TCon API] Stencil ID " + id + " is already occupied by " + instance.stencils.get(id).getDisplayName());

        instance.stencils.put(id, pattern);
    }

    public static Collection<ItemStack> getStencils ()
    {
        return instance.stencils.values();
    }

    /**
     * Returns the index of the given stencil. If no stencil is found, returns -1.
     */
    public static int getId(ItemStack stencil)
    {
        for(Map.Entry<Integer, ItemStack> entry : instance.stencils.entrySet())
            if (OreDictionary.itemMatches(stencil, entry.getValue(), false))
                return entry.getKey();

        return -1;
    }

    // returns the stencil with the given index
    public static ItemStack getStencil (int num)
    {
        if (!instance.stencils.containsKey(num))
            return null;

        return instance.stencils.get(num).copy();
    }

    public static int getStencilCount ()
    {
        return instance.stencils.size();
    }
}
