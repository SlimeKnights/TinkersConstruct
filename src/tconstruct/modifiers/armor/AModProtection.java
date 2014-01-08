package tconstruct.modifiers.armor;

import java.util.EnumSet;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.ArmorModTypeFilter;
import tconstruct.library.armor.EnumArmorPart;

public class AModProtection extends ArmorModTypeFilter
{
    int modifyAmount = 3;
    public AModProtection(int effect, EnumSet<EnumArmorPart> armorTypes, ItemStack[] items, int[] values)
    {
        super(effect, "ExoProtection", armorTypes, items, values);
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag(getTagName());
        int amount = matchingItems(input) * modifyAmount;
        return tags.getInteger("Modifiers") >= amount;
    }

    @Override
    public void modify (ItemStack[] input, ItemStack armor)
    {
        NBTTagCompound baseTag = armor.getTagCompound();
        NBTTagCompound armorTag = armor.getTagCompound().getCompoundTag(getTagName());

        int modifiers = armorTag.getInteger("Modifiers");
        modifiers -= matchingItems(input) * modifyAmount;
        armorTag.setInteger("Modifiers", modifiers);

        int amount = matchingAmount(input);
        double absorb = armorTag.getDouble("protection");
        absorb += amount;
        armorTag.setDouble("protection", absorb);     
    }
}
