package tconstruct.modifiers.tools;

import java.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.tools.ToolCore;

public class ModPiston extends ItemModTypeFilter
{
    String tooltipName;
    int max = 10;

    public ModPiston(int effect, ItemStack[] items, int[] values)
    {
        super(effect, "Piston", items, values);
        tooltipName = "\u00a77Knockback";
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        if (tool.getItem() instanceof ToolCore)
        {
            ToolCore toolItem = (ToolCore) tool.getItem();
            if (!validType(toolItem))
                return false;

            if (matchingAmount(input) > max)
                return false;

            NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
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
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        int increase = matchingAmount(input);
        if (tags.hasKey(key))
        {
            int[] keyPair = tags.getIntArray(key);
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
            String modName = "\u00a74Knockback (" + increase + "/" + max + ")";
            int tooltipIndex = addToolTip(tool, tooltipName, modName);
            int[] keyPair = new int[] { increase, max, tooltipIndex };
            tags.setIntArray(key, keyPair);
        }

        float knockback = tags.getFloat("Knockback");

        knockback += 0.1 * increase;
        tags.setFloat("Knockback", knockback);
    }

    void updateModTag (ItemStack tool, int[] keys)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        String tip = "ModifierTip" + keys[2];
        String modName = "\u00a77Knockback (" + keys[0] + "/" + keys[1] + ")";
        tags.setString(tip, modName);
    }

    public boolean validType (ToolCore tool)
    {
        List list = Arrays.asList(tool.getTraits());
        return list.contains("weapon");
    }
}
