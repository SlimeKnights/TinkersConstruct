package tconstruct.library.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.IModifyable;

public abstract class ItemModifier
{
    public final String key;
    public final List stacks;
    public final int effectIndex;
    public static Random random = new Random();

    /** Default constructor
     * 
     * @param recipe Items to compare against when checking the modifier
     * @param effect Render index for sprite layering
     * @param dataKey NBT string to put on the item
     */
    public ItemModifier(ItemStack[] recipe, int effect, String dataKey)
    {
        // recipeItems = items;
        List<ItemStack> itemstacks = new ArrayList<ItemStack>();
        for (int iter = 0; iter < recipe.length; iter++)
            itemstacks.add(recipe[iter]);
        stacks = itemstacks;
        effectIndex = effect;
        key = dataKey;
    }

    /**
     * Checks to see if the inputs match the stored items Note: Works like
     * ShapelessRecipes
     * 
     * @param recipe The ItemStacks to compares against
     * @param input Item to modify, used for restrictions
     * @return Whether the recipe matches the input
     */
    public boolean matches (ItemStack[] recipe, ItemStack input)
    {
        if (!canModify(input, recipe))
            return false;

        ArrayList list = new ArrayList(this.stacks);

        for (int iter = 0; iter < recipe.length; ++iter)
        {
            ItemStack craftingStack = recipe[iter];

            if (craftingStack != null)
            {
                boolean canCraft = false;
                Iterator iterate = list.iterator();

                while (iterate.hasNext())
                {
                    ItemStack removeStack = (ItemStack) iterate.next();

                    if (craftingStack.getItem() == removeStack.getItem() && (removeStack.getItemDamage() == Short.MAX_VALUE || craftingStack.getItemDamage() == removeStack.getItemDamage()))
                    {
                        canCraft = true;
                        list.remove(removeStack);
                        break;
                    }
                }

                if (!canCraft)
                {
                    return false;
                }
            }
        }

        return list.isEmpty();
    }

    protected String getTagName (ItemStack stack)
    {
        return ((IModifyable) stack.getItem()).getBaseTagName();
    }

    /**
     * 
     * @param input Tool to compare against
     * @param recipe Items to modify with
     * @return Whether the tool can be modified
     */

    protected boolean canModify (ItemStack input, ItemStack[] recipe)
    {
        NBTTagCompound tags = input.getTagCompound().getCompoundTag(getTagName(input));
        return tags.getInteger("Modifiers") > 0;
    }

    /**
     * Modifies the tool. Adds nbttags, changes existing ones, ticks down
     * modification counter, etc
     * 
     * @param recipe ItemStacks pull info from
     * @param input The tool to modify
     */
    public abstract void modify (ItemStack[] recipe, ItemStack input);

    public void addMatchingEffect (ItemStack input)
    {
        NBTTagCompound tags = input.getTagCompound().getCompoundTag(getTagName(input));
        if (tags.hasKey("Effect6") || tags.hasKey(key))
            return;

        else if (tags.hasKey("Effect5"))
        {
            tags.setInteger("Effect6", effectIndex);
        }
        else if (tags.hasKey("Effect4"))
        {
            tags.setInteger("Effect5", effectIndex);
        }
        else if (tags.hasKey("Effect3"))
        {
            tags.setInteger("Effect4", effectIndex);
        }
        else if (tags.hasKey("Effect2"))
        {
            tags.setInteger("Effect3", effectIndex);
        }
        else if (tags.hasKey("Effect1"))
        {
            tags.setInteger("Effect2", effectIndex);
        }
        else
        {
            tags.setInteger("Effect1", effectIndex);
        }
    }

    protected int addModifierTip (ItemStack input, String modifierTip)
    {
        NBTTagCompound tags = input.getTagCompound().getCompoundTag(getTagName(input));
        int tipNum = 0;
        while (true)
        {
            tipNum++;
            String tip = "Tooltip" + tipNum;
            if (!tags.hasKey(tip))
            {
                tags.setString(tip, "");
                String modTip = "ModifierTip" + tipNum;
                tags.setString(modTip, modifierTip);
                return tipNum;
            }
        }
    }

    protected int addToolTip (ItemStack input, String tooltip, String modifierTip)
    {
        NBTTagCompound tags = input.getTagCompound().getCompoundTag(getTagName(input));
        int tipNum = 0;
        while (true)
        {
            tipNum++;
            String tip = "Tooltip" + tipNum;
            if (!tags.hasKey(tip))
            {
                tags.setString(tip, tooltip);
                String modTip = "ModifierTip" + tipNum;
                tags.setString(modTip, modifierTip);
                return tipNum;
            }
            else
            {
                String tag = tags.getString(tip);
                if (tag.contains(tooltip))
                {
                    tags.setString(tip, getProperName(tooltip, tag));
                    String modTip = "ModifierTip" + tipNum;
                    tag = tags.getString(modTip);
                    tags.setString(modTip, getProperName(modifierTip, tag));
                    return tipNum;
                }
            }
        }
    }

    protected String getProperName (String tooltip, String tag)
    {
        if (tag.equals(tooltip))
            return tooltip + " II";

        if (tag.equals(tooltip + " II"))
            return tooltip + " III";

        if (tag.equals(tooltip + " III"))
            return tooltip + " IV";

        if (tag.equals(tooltip + " IV"))
            return tooltip + " V";

        if (tag.equals(tooltip + " V"))
            return tooltip + " VI";

        if (tag.equals(tooltip + " VI"))
            return tooltip + " VII";

        if (tag.equals(tooltip + " VII"))
            return tooltip + " VIII";

        if (tag.equals(tooltip + " VIII"))
            return tooltip + " IX";

        if (tag.equals(tooltip + " IX"))
            return tooltip + " X";

        return tooltip + " X+";
    }

    public boolean validType (ToolCore tool)
    {
        return true;
    }
    
    protected NBTTagCompound getModifierTag(ItemStack stack)
    {
        return stack.getTagCompound().getCompoundTag(getTagName(stack));
    }


    public boolean areItemsEquivalent (ItemStack stack1, ItemStack stack2)
    {
        if (stack1.getItem() != stack2.getItem())
            return false;
        return ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    public boolean areItemStacksEquivalent (ItemStack stack1, ItemStack stack2)
    {
        if (stack1.getItem() != stack2.getItem())
            return false;
        if (stack1.getItemDamage() != stack2.getItemDamage())
            return false;
        return ItemStack.areItemStackTagsEqual(stack1, stack2);
    }
}
