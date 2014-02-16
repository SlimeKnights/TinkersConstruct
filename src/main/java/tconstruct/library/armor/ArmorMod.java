package tconstruct.library.armor;

import java.util.EnumSet;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.tools.ToolMod;

public abstract class ArmorMod extends ToolMod
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
        ArmorCore item = (ArmorCore) armor.getItem();
        if (armorTypes.contains(item.armorPart))
        {
            NBTTagCompound tags = armor.getTagCompound().getCompoundTag(getTagName());
            return tags.getInteger("Modifiers") > 0;
        }
        return false;
    }

    @Override
    protected String getTagName ()
    {
        return "TinkerArmor";
    }

    public boolean validArmorType (ArmorCore armor)
    {
        return true;
    }

    protected NBTTagCompound getAttributeTag (String attributeType, String modifierName, double amount, boolean flat, UUID uuid)
    {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("AttributeName", attributeType);
        tag.setString("Name", modifierName);
        tag.setDouble("Amount", amount);
        tag.setInteger("Operation", flat ? 0 : 1);// 0 = flat increase, 1 = %
                                                  // increase
        tag.setLong("UUIDMost", uuid.getMostSignificantBits());
        tag.setLong("UUIDLeast", uuid.getLeastSignificantBits());
        return tag;
    }
}
