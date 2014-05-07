package tconstruct.modifiers.armor;

import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.armor.ArmorMod;
import tconstruct.library.armor.ArmorPart;
import tconstruct.library.modifier.IModifyable;

public class TravelModDoubleJump extends ArmorMod
{
    String color = "\u00a7a";
    String tooltipName = "Double-Jump";
    public TravelModDoubleJump(EnumSet<ArmorPart> armorTypes, ItemStack[] items)
    {
        super(1, "Double-Jump", armorTypes, items);
    }

    @Override
    public boolean validType (IModifyable type)
    {
        System.out.println("Valid type check");
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
