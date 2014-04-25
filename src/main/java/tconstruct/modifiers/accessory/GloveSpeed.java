package tconstruct.modifiers.accessory;

import java.util.logging.Level;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.TConstruct;
import tconstruct.library.IModifyable;
import tconstruct.modifiers.tools.ItemModTypeFilter;

public class GloveSpeed extends ItemModTypeFilter
{
    String tooltipName;
    int max = 100;

    public GloveSpeed(int effect, ItemStack[] items, int[] values)
    {
        super(effect, "Redstone", items, values);
        tooltipName = "\u00a74Haste";
    }

    @Override
    protected boolean canModify (ItemStack input, ItemStack[] modifiers)
    {
        IModifyable imod = (IModifyable) input.getItem();
        TConstruct.logger.info("Beginning modifier check");
        if (imod.getModifyType().equals("Accessory"))
        {
            TConstruct.logger.info("Item is an accessory");
            NBTTagCompound tags = getModifierTag(input);
            if (!tags.hasKey(key))
                return tags.getInteger("Modifiers") > 0 && matchingAmount(modifiers) <= max;

            TConstruct.logger.info("Item has modifiers");
            int keyPair[] = tags.getIntArray(key);
            if (keyPair[0] + matchingAmount(modifiers) <= keyPair[1])
            {
                TConstruct.logger.info("Item can add more to current modifier");
                return true;
            }

            else if (keyPair[0] == keyPair[1])
                return tags.getInteger("Modifiers") > 0;
        }

        return false;
    }

    @Override
    public void modify (ItemStack[] modifiers, ItemStack input)
    {
        TConstruct.logger.info("Adding redstone to accessory");
        NBTTagCompound tags = getModifierTag(input);
        int[] keyPair;
        int increase = matchingAmount(modifiers);
        int current = 0;
        if (tags.hasKey(key))
        {
            TConstruct.logger.info("Item has tag");
            keyPair = tags.getIntArray(key);
            if (keyPair[0] % max == 0)
            {
                keyPair[0] += increase;
                keyPair[1] += max;
                tags.setIntArray(key, keyPair);

                int mods = tags.getInteger("Modifiers");
                mods -= 1;
                tags.setInteger("Modifiers", mods);
            }
            else
            {
                keyPair[0] += increase;
                tags.setIntArray(key, keyPair);
            }
            current = keyPair[0];
            updateModTag(input, keyPair);
        }
        else
        {
            TConstruct.logger.info("Item does not have tag");
            int mods = tags.getInteger("Modifiers");
            mods -= 1;
            tags.setInteger("Modifiers", mods);
            String modName = "\u00a74Redstone (" + increase + "/" + max + ")";
            int tooltipIndex = addToolTip(input, tooltipName, modName);
            keyPair = new int[] { increase, max, tooltipIndex };
            current = keyPair[0];
            tags.setIntArray(key, keyPair);
        }

        int miningSpeed = tags.getInteger("MiningSpeed");
        int boost = 5;

        miningSpeed += (increase * boost);
        tags.setInteger("MiningSpeed", miningSpeed);
    }

    void updateModTag (ItemStack input, int[] keys)
    {
        TConstruct.logger.info("Updating modifier tag");
        NBTTagCompound tags = getModifierTag(input);
        String tip = "ModifierTip" + keys[2];
        String modName = "\u00a74Redstone (" + keys[0] + "/" + keys[1] + ")";
        tags.setString(tip, modName);
    }
}
