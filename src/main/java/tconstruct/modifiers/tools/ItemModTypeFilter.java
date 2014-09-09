package tconstruct.modifiers.tools;

import java.util.*;
import net.minecraft.item.ItemStack;
import tconstruct.library.modifier.ItemModifier;

public abstract class ItemModTypeFilter extends ItemModifier
{
    public final List<Integer> increase;

    public ItemModTypeFilter(int effect, String dataKey, ItemStack[] items, int[] values)
    {
        super(items, effect, dataKey);
        assert items.length == values.length : "Itemstacks and their values for tool modifiers must be the same length";
        this.increase = new ArrayList<Integer>();
        for (int i = 0; i < values.length; i++)
        {
            increase.add(values[i]);
        }
    }

    /** Checks to see if the inputs match the stored items
     * Note: Filters types, doesn't care about amount
     * 
     * @param input The ItemStacks to compare against
     * @param tool Item to modify, used for restrictions
     * @return Whether the recipe matches the input
     */
    @Override
    public boolean matches (ItemStack[] input, ItemStack tool)
    {
        if (!canModify(tool, input))
            return false;

        boolean minimumMatch = false;
        for (ItemStack inputStack : input)
        {
            if (inputStack == null)
                continue;

            boolean match = false;
            for (Object check : stacks)
            {
                ItemStack stack = (ItemStack) check;
                if (stack.getItemDamage() == Short.MAX_VALUE)
                {
                    if (this.areItemsEquivalent(inputStack, stack))
                        match = true;
                }
                else
                {
                    if (this.areItemStacksEquivalent(inputStack, stack))
                        match = true;
                }
            }
            if (!match)
                return false;

            minimumMatch = true;
        }
        return minimumMatch;
    }

    public int matchingAmount (ItemStack[] input)
    {
        int amount = 0;
        for (ItemStack inputStack : input)
        {
            if (inputStack == null)
                continue;
            else
            {
                for (int iter = 0; iter < stacks.size(); iter++)
                {
                    ItemStack stack = (ItemStack) stacks.get(iter);
                    if (stack.getItemDamage() == Short.MAX_VALUE)
                    {
                        if (this.areItemsEquivalent(inputStack, stack))
                            amount += increase.get(iter);
                    }
                    else
                    {
                        if (this.areItemStacksEquivalent(inputStack, stack))
                            amount += increase.get(iter);
                    }
                }
            }
        }
        return amount;
    }

    /** Adds a new itemstack to the list for increases
     * 
     * @param stack ItemStack to compare against
     * @param amount Amount to increase
     */
    public void addStackToMatchList (ItemStack stack, int amount)
    {
        if (stack == null)
            throw new NullPointerException("ItemStack added to " + this.getClass().getSimpleName() + " cannot be null.");
        stacks.add(stack);
        increase.add(amount);
    }
}
