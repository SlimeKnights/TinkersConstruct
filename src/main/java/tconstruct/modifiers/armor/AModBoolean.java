package tconstruct.modifiers.armor;

import java.util.EnumSet;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.armor.*;

/* Adds a boolean NBTTag */

public class AModBoolean extends ArmorMod
{
    String color;
    String tooltipName;

    public AModBoolean(int effect, String tag, EnumSet<ArmorPart> armorTypes, ItemStack[] items, String c, String tip)
    {
        super(effect, tag, armorTypes, items);
        color = c;
        tooltipName = tip;
    }

    @Override
    protected boolean canModify (ItemStack armor, ItemStack[] recipe)
    {
        Item i = armor.getItem();
        if (!(i instanceof ArmorCore))
            return false;
        ArmorCore item = (ArmorCore) armor.getItem();
        if (!armorTypes.contains(item.armorPart))
            return false;
        NBTTagCompound tags = getModifierTag(armor);
        return tags.getInteger("Modifiers") > 0 && !tags.getBoolean(key); //Will fail if the modifier is false or the tag doesn't exist
    }

    @Override
    public void modify (ItemStack[] recipe, ItemStack input)
    {
        NBTTagCompound tags = getModifierTag(input);

        tags.setBoolean(key, true);

        int modifiers = tags.getInteger("Modifiers");
        modifiers -= 1;
        tags.setInteger("Modifiers", modifiers);

        addToolTip(input, color + tooltipName, color + key);
    }
}
