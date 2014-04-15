package tconstruct.modifiers.armor;

import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.armor.AModInteger;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.EnumArmorPart;
import net.minecraft.util.StatCollector;

public class AModDoubleJump extends AModInteger
{
    public AModDoubleJump(ItemStack[] items)
    {
        super(6, "Double-Jump", EnumSet.of(EnumArmorPart.SHOES), items, 5, 1, "\u00a7a", StatCollector.translateToLocal("modifier.armour.jump.double");
    }

    @Override
    protected boolean canModify (ItemStack armor, ItemStack[] input)
    {
        ArmorCore item = (ArmorCore) armor.getItem();
        if (armorTypes.contains(item.armorPart))
        {
            NBTTagCompound tags = armor.getTagCompound().getCompoundTag(getTagName());
            return tags.getInteger("Modifiers") >= modifyCount && tags.getInteger("Double-Jump") < 3;
        }
        return false;
    }

    @Override
    protected int addToolTip (ItemStack tool, String tooltip, String modifierTip)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag(getTagName());
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
                if (tag.contains("Double-Jump") || tag.contains("Triple-Jump"))
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

    @Override
    protected String getProperName (String tooltip, String tag)
    {
        if (tag.contains("Double-Jump"))
            return color + StatCollector.translateToLocal("modifier.armour.jump.triple");

        if (tag.contains("Triple-Jump"))
            return color + StatCollector.translateToLocal("modifier.armour.jump.quad");

        return color + StatCollector.translateToLocal("modifier.armour.jump.double");
    }
}
