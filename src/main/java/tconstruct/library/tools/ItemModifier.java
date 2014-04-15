package tconstruct.library.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.IModifyable;

public abstract class ItemModifier
{
    public final String key;
    public final List stacks;
    public final int effectIndex;
    public static Random random = new Random();

    public ItemModifier(ItemStack[] items, int effect, String dataKey)
    {
        List<ItemStack> itemstacks = new ArrayList<ItemStack>();
        for (int iter = 0; iter < items.length; iter++)
            itemstacks.add(items[iter]);
        stacks = itemstacks;
        effectIndex = effect;
        key = dataKey;
    }

    /** Checks to see if the inputs match the stored items
     * Note: Works like ShapelessRecipes
     * 
     * @param modifiers The ItemStacks to compare against
     * @param input Item to modify, used for restrictions
     * @return Whether the recipe matches the input
     */
    public boolean matches (ItemStack[] modifiers, ItemStack input)
    {
        if (!canModify(input, modifiers))
            return false;

        ArrayList list = new ArrayList(this.stacks);

        for (int iter = 0; iter < modifiers.length; ++iter)
        {
            ItemStack craftingStack = modifiers[iter];

            if (craftingStack != null)
            {
                boolean canCraft = false;
                Iterator iterate = list.iterator();

                while (iterate.hasNext())
                {
                    ItemStack removeStack = (ItemStack) iterate.next();

                    if (craftingStack.itemID == removeStack.itemID && (removeStack.getItemDamage() == Short.MAX_VALUE || craftingStack.getItemDamage() == removeStack.getItemDamage()))
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

    protected String getTagName(ItemStack stack)
    {
         return ((IModifyable)stack.getItem()).getBaseTag();
    }

    /**
     * @param input Tool to compare against
     * @param modifiers Items to modify with
     * @return Whether the tool can be modified
     */

    protected boolean canModify (ItemStack input, ItemStack[] modifiers)
    {
        NBTTagCompound tags = input.getTagCompound().getCompoundTag(getTagName(input));
        return tags.getInteger("Modifiers") > 0;
    }

    /** Modifies the tool. Adds nbttags, changes existing ones, ticks down modification counter, etc
     * 
     * @param input ItemStacks to pull info from
     * @param tool The tool to modify
     */
    public abstract void modify (ItemStack[] modifiers, ItemStack input);

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
        if (tag.isEmpty())
            return tooltip + " I";

        if (tag.equals(tooltip) || tag.equals(tooltip + " I"))
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

    public boolean validType (IModifyable input)
    {
        return input.getModifyType().equals("Tool");
    }

    // Helper methods
    public static boolean areItemsEquivalent (ItemStack stack1, ItemStack stack2)
    {
        if (stack1.itemID != stack2.itemID)
            return false;
        return ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    public static boolean areItemStacksEquivalent (ItemStack stack1, ItemStack stack2)
    {
        if (stack1.itemID != stack2.itemID)
            return false;
        if (stack1.getItemDamage() != stack2.getItemDamage())
            return false;
        return ItemStack.areItemStackTagsEqual(stack1, stack2);
    }
    
    public static NBTTagCompound getAttributeTag (String attributeType, String modifierName, double amount, boolean flat, UUID uuid)
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("AttributeName", attributeType);
        tag.setString("Name", modifierName);
        tag.setDouble("Amount", amount);
        tag.setInteger("Operation", flat ? 0 : 1);//0 = flat increase, 1 = % increase
        tag.setLong("UUIDMost", uuid.getMostSignificantBits());
        tag.setLong("UUIDLeast", uuid.getLeastSignificantBits());
        return tag;
    }
}
