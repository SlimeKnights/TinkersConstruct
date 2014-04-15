package tconstruct.library.armor;

import java.util.EnumSet;
import java.util.UUID;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.IModifyable;
import tconstruct.library.tools.ItemModifier;

public abstract class ArmorMod extends ItemModifier
{
    protected final EnumSet<EnumArmorPart> armorTypes;

    public ArmorMod(int effect, String dataKey, EnumSet<EnumArmorPart> armorTypes, ItemStack[] items)
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
            NBTTagCompound tags = armor.getTagCompound().getCompoundTag(getTagName(armor));
            return tags.getInteger("Modifiers") > 0;
        }
        return false;
    }
    
    @Override
    public boolean validType (IModifyable tool)
    {
        return tool.getModifyType().equals("Armor");
    }
}
