package tconstruct.modifiers;

import java.util.Arrays;
import java.util.List;

import tconstruct.library.tools.ToolCore;
import tconstruct.library.tools.ToolMod;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModRedstone extends ToolMod
{
    String tooltipName;
    int increase;
    int max;

    public ModRedstone(ItemStack[] items, int effect, int inc)
    {
        super(items, effect, "Redstone");
        tooltipName = "\u00a74Haste";
        increase = inc;
        max = 50;
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        ToolCore toolItem = (ToolCore) tool.getItem();
        if (!validType(toolItem))
            return false;

        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        if (!tags.hasKey(key))
            return tags.getInteger("Modifiers") > 0;

        int keyPair[] = tags.getIntArray(key);
        if (keyPair[0] + increase <= keyPair[1])
            return true;

        else if (keyPair[0] == keyPair[1])
            return tags.getInteger("Modifiers") > 0;

        else
            return false;
    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        int[] keyPair;
        if (tags.hasKey(key))
        {
            keyPair = tags.getIntArray(key);
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
            String modName = "\u00a74Redstone (" + increase + "/" + max + ")";
            int tooltipIndex = addToolTip(tool, tooltipName, modName);
            keyPair = new int[] { increase, max, tooltipIndex };
            tags.setIntArray(key, keyPair);
        }

        int miningSpeed = tags.getInteger("MiningSpeed");
        miningSpeed += (increase * 8);
        tags.setInteger("MiningSpeed", miningSpeed);

        String[] type = { "MiningSpeed2", "MiningSpeedHandle", "MiningSpeedExtra" };

        for (int i = 0; i < 3; i++)
        {
            if (tags.hasKey(type[i]))
            {
                int speed = tags.getInteger(type[i]);
                speed += (increase * 8);
                tags.setInteger(type[i], speed);
            }
        }

        if (tags.hasKey("DrawSpeed"))
        {
            //int drawSpeed = tags.getInteger("DrawSpeed");
            int baseDrawSpeed = tags.getInteger("BaseDrawSpeed");
            int drawSpeed = (int) (baseDrawSpeed - (0.1f * baseDrawSpeed * (keyPair[0] / 50f)));
            tags.setInteger("DrawSpeed", drawSpeed);
        }
    }

    void updateModTag (ItemStack tool, int[] keys)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        String tip = "ModifierTip" + keys[2];
        String modName = "\u00a74Redstone (" + keys[0] + "/" + keys[1] + ")";
        tags.setString(tip, modName);
    }

    public boolean validType (ToolCore tool)
    {
        List list = Arrays.asList(tool.toolCategories());
        return list.contains("harvest") || list.contains("utility") || list.contains("bow");
    }
}
