package tconstruct.armor.modifiers;

import java.util.EnumSet;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.ArmorModTypeFilter;
import tconstruct.library.armor.EnumArmorPart;

//TODO: Condense attribute modifiers into one class
public class AModKnockbackResistance extends ArmorModTypeFilter
{
    final boolean modifierType;
    final int modifyAmount = 2;

    public AModKnockbackResistance(int effect, EnumSet<EnumArmorPart> armorTypes, ItemStack[] items, int[] values, boolean type)
    {
        super(effect, "ExoKnockback" + (type ? "Percent" : "Flat"), armorTypes, items, values);
        this.modifierType = type;
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
        modifiers -= matchingAmount(input) * modifyAmount;
        armorTag.setInteger("Modifiers", modifiers);

        int amount = matchingAmount(input);
        if (armorTag.hasKey(key))
        {
            amount += armorTag.getInteger(key);
        }
        armorTag.setInteger(key, amount);

        NBTTagList attributes;
        if (baseTag.hasKey("AttributeModifiers"))
        {
            attributes = baseTag.getTagList("AttributeModifiers", 10);
            for (int iter = 0; iter < attributes.tagCount(); iter++)
            {
                NBTTagCompound tag = (NBTTagCompound) attributes.getCompoundTagAt(iter);
                if (tag.getString("AttributeName").equals("generic.knockbackResistance"))
                    attributes.removeTag(iter);
            }
        }
        else
        {
            attributes = new NBTTagList();
            baseTag.setTag("AttributeModifiers", attributes);
        }
        attributes.appendTag(getAttributeTag("generic.knockbackResistance", key, amount * 0.01, modifierType, getUUIDFromItem(armor)));
    }

    private static final UUID head = UUID.fromString("4188779d-69d4-487c-b307-c4c182522c44");
    private static final UUID chest = UUID.fromString("4188779d-69d4-487c-b307-c4c182522c45");
    private static final UUID pants = UUID.fromString("4188779d-69d4-487c-b307-c4c182522c46");
    private static final UUID shoes = UUID.fromString("4188779d-69d4-487c-b307-c4c182522c47");

    UUID getUUIDFromItem (ItemStack stack)
    {
        ArmorCore item = (ArmorCore) stack.getItem();
        switch (item.armorPart)
        {
        case HELMET:
            return head;
        case CHEST:
            return chest;
        case PANTS:
            return pants;
        case SHOES:
            return shoes;
        }
        return null;
    }
}