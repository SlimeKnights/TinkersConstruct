package tconstruct.modifiers.armor;

import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.IModifyable;
import tconstruct.library.armor.ArmorMod;
import tconstruct.library.armor.EnumArmorPart;

public class TravelModDoubleJump extends ArmorMod
{
    String color = "\u00a7a";
    String tooltipName = "Double-Jump";
    public TravelModDoubleJump(EnumSet<EnumArmorPart> armorTypes, ItemStack[] items)
    {
        super(1, "Double-Jump", armorTypes, items);
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
}
