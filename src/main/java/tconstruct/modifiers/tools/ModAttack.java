package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.IModifyable;

public class ModAttack extends ItemModTypeFilter
{
    String tooltipName;
    int max;
    int threshold;
    String guiType;
    String modifierType;

    public ModAttack(String type, int effect, ItemStack[] items, int[] value)
    {
        super(effect, "ModAttack", items, value);
        tooltipName = "\u00a7fSharpness";
        guiType = type;
        max = 72;
        threshold = 24;
        modifierType = "Tool";
    }

    public ModAttack(String type, int effect, ItemStack[] items, int[] value, int max, int threshold, String modifierType)
    {
        super(effect, "ModAttack", items, value);
        tooltipName = "\u00a7fKnuckles";
        guiType = type;
        this.max = max;
        this.threshold = threshold;
        this.modifierType = modifierType;
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        if (tool.getItem() instanceof IModifyable)
        {
            IModifyable toolItem = (IModifyable) tool.getItem();
            if (!validType(toolItem))
                return false;

            if (matchingAmount(input) > max)
                return false;

            NBTTagCompound tags = tool.getTagCompound().getCompoundTag(toolItem.getBaseTagName());
            if (!tags.hasKey(key))
                return tags.getInteger("Modifiers") > 0 && matchingAmount(input) <= max;

            int keyPair[] = tags.getIntArray(key);
            if (keyPair[0] + matchingAmount(input) <= keyPair[1])
                return true;

            else if (keyPair[0] == keyPair[1])
                return tags.getInteger("Modifiers") > 0;
        }
        return false;
    }

    @Override
    public boolean validType (IModifyable input)
    {
        String type = input.getModifyType();
        return type.equals(modifierType);
    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        IModifyable toolItem = (IModifyable) tool.getItem();
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag(toolItem.getBaseTagName());
        if (tags.hasKey(key))
        {
            int[] keyPair = tags.getIntArray(key);
            int increase = matchingAmount(input);

            int leftToBoost = threshold - (keyPair[0] % threshold);
            if (increase >= leftToBoost)
            {
                int attack = tags.getInteger("Attack");
                attack += 1;
                tags.setInteger("Attack", attack);
            }

            if (keyPair[0] % max == 0)
            {
                keyPair[0] += increase;
                keyPair[1] += max;
                tags.setIntArray(key, keyPair);

                int modifiers = tags.getInteger("Modifiers");
                modifiers -= 1;
                tags.setInteger("Modifiers", modifiers);
            }
            else
            {
                keyPair[0] += increase;
                tags.setIntArray(key, keyPair);
            }
            updateModTag(tool, keyPair);

        }
        else
        {
            int modifiers = tags.getInteger("Modifiers");
            modifiers -= 1;
            tags.setInteger("Modifiers", modifiers);
            int increase = matchingAmount(input);
            String modName = "\u00a7f" + guiType + " (" + increase + "/" + max + ")";
            int tooltipIndex = addToolTip(tool, tooltipName, modName);
            int[] keyPair = new int[] { increase, max, tooltipIndex };
            tags.setIntArray(key, keyPair);

            int attack = tags.getInteger("Attack");
            attack += 1;
            tags.setInteger("Attack", attack);
        }
    }

    void updateModTag (ItemStack tool, int[] keys)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag(getTagName(tool));
        String tip = "ModifierTip" + keys[2];
        String modName = "\u00a7f" + guiType + " (" + keys[0] + "/" + keys[1] + ")";
        tags.setString(tip, modName);
    }
}
