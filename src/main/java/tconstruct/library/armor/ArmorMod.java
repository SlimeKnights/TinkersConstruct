package tconstruct.library.armor;

import java.util.EnumSet;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.*;

public abstract class ArmorMod extends ItemModifier
{
    protected final EnumSet<ArmorPart> armorTypes;

    public ArmorMod(int effect, String dataKey, EnumSet<ArmorPart> armorTypes, ItemStack[] items)
    {
        super(items, effect, dataKey);
        this.armorTypes = armorTypes;
    }

    @Override
    protected boolean canModify (ItemStack armor, ItemStack[] input)
    {
        Item i = armor.getItem();
        if (!(i instanceof ArmorCore))
            return false;
        ArmorCore item = (ArmorCore) armor.getItem();
        if (armorTypes.contains(item.armorPart))
        {
            NBTTagCompound tags = getModifierTag(armor);
            return tags.getInteger("Modifiers") > 0;
        }
        return false;
    }

    @Override
    public boolean validType (IModifyable type)
    {
        return type.getModifyType().equals("Armor");
    }
}
