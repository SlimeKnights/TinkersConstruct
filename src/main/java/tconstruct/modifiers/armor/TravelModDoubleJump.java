package tconstruct.modifiers.armor;

import java.util.EnumSet;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.armor.*;
import tconstruct.library.modifier.IModifyable;

public class TravelModDoubleJump extends ArmorMod
{
    String color = "\u00a77";
    String tooltipName = "Double-Jump";

    public TravelModDoubleJump(EnumSet<ArmorPart> armorTypes, ItemStack[] items)
    {
        super(0, "Double-Jump", armorTypes, items);
    }

    @Override
    public boolean validType (IModifyable type)
    {
        return type.getModifyType().equals("Clothing");
    }

    @Override
    public void modify (ItemStack[] recipe, ItemStack input)
    {
        NBTTagCompound tags = input.getTagCompound().getCompoundTag(getTagName(input));
        int amount = 1;
        if (tags.hasKey(key))
        {
            int increase = tags.getInteger(key);
            increase += 1;
            tags.setInteger(key, increase);
        }
        else
        {
            tags.setInteger(key, amount);
        }

        int modifiers = tags.getInteger("Modifiers");
        modifiers -= 1;
        tags.setInteger("Modifiers", modifiers);

        addToolTip(input, color + tooltipName, color + key);
    }

    @Override
    protected int addToolTip (ItemStack tool, String tooltip, String modifierTip)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag(getTagName(tool));
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
            return color + "Triple-Jump";

        if (tag.contains("Triple-Jump"))
            return color + "Quadruple-Jump";

        if (tag.contains("Quadruple-Jump"))
            return color + "Quintuple-Jump";

        if (tag.contains("Quintuple-Jump"))
            return color + "Sextuple-Jump";

        if (tag.contains("Sextuple-Jump"))
            return color + "Septuple-Jump";

        return color + "Double-Jump";
    }
}
