package tconstruct.modifiers.tools;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.tools.ToolCore;

import java.util.Arrays;
import java.util.List;

public class ModWindup extends ModRedstone {
    public ModWindup(int effect, ItemStack[] items, int[] values) {
        super(effect, items, values);
    }

    public boolean validType (ToolCore tool)
    {
        List list = Arrays.asList(tool.getTraits());
        return list.contains("windup");
    }

    @Override
    protected boolean canModify(ItemStack tool, ItemStack[] input) {
        if(!super.canModify(tool, input))
            return false;

        float drawSSpeed = tool.getTagCompound().getCompoundTag("InfiTool").getFloat("DrawSpeed");
        return drawSSpeed > 0.25f * 20f; // can't get below 1/4s
    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        int[] keyPair;
        int increase = matchingAmount(input);
        int current = 0;
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
            current = keyPair[0];
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
            current = keyPair[0];
            tags.setIntArray(key, keyPair);
        }

        // 0.005 reduction per second, numbers are in ticks -> 0.10 == 0.005s
        float boost = 0.10f * current;
        // with added bonus for multiple modifiers of redstone
        // so every 50 redstone you get a bonus speed
        for(int i = 0; i < current/50; i++)
            boost += 2.0f;

        int baseDrawSpeed = tags.getInteger("BaseDrawSpeed");
        int drawSpeed = baseDrawSpeed - (int)boost;
        tags.setInteger("DrawSpeed", drawSpeed);
    }
}
