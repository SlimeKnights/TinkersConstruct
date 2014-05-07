package tconstruct.modifiers.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.util.config.PHConstruct;

/* Adds an integer NBTTag */

public class ModDurability extends ItemModifier
{
    String tooltipName;
    String color;
    int durability;
    float modifier;
    int miningLevel;

    public ModDurability(ItemStack[] items, int effect, int dur, float mod, int level, String k, String tip, String c)
    {
        super(items, effect, k);
        durability = dur;
        modifier = mod;
        miningLevel = level;
        tooltipName = tip;
        color = c;
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        if (tags.hasKey(key))
            return false;
        return super.canModify(tool, input);
    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");

        int base = tags.getInteger("BaseDurability");
        int bonus = tags.getInteger("BonusDurability");
        float modDur = tags.getFloat("ModDurability");

        bonus += durability;
        modDur += modifier;

        int total = (int) ((base + bonus) * (modDur + 1f));
        if (total <= 0)
            total = 1;

        tags.setInteger("TotalDurability", total);
        tags.setInteger("BonusDurability", bonus);
        tags.setFloat("ModDurability", modDur);

        if (PHConstruct.miningLevelIncrease)
        {
            int mLevel = tags.getInteger("HarvestLevel");
            if (mLevel < miningLevel)
                tags.setInteger("HarvestLevel", miningLevel);
        }

        int modifiers = tags.getInteger("Modifiers");
        modifiers -= 1;
        tags.setInteger("Modifiers", modifiers);

        tags.setBoolean(key, true);
        String modTip = color + key;
        addToolTip(tool, tooltipName, modTip);
    }

}
