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
public class AModDamageBoost extends ArmorModTypeFilter
{
    final boolean modifierType;
    final int modifyAmount;
    final double scaleAmount;

    public AModDamageBoost(int effect, EnumSet<EnumArmorPart> armorTypes, ItemStack[] items, int[] values, boolean type, int modifiers, double scale)
    {
        super(effect, "ExoAttack" + (type ? "Percent" : "Flat"), armorTypes, items, values);
        this.modifierType = type;
        this.modifyAmount = modifiers;
        this.scaleAmount = scale;
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag(getTagName());
        int amount = matchingAmount(input) * modifyAmount;
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
                if (tag.getString("AttributeName").equals("generic.attackDamage"))
                {
                    attributes.removeTag(iter);
                }
            }
        }
        else
        {
            attributes = new NBTTagList();
            baseTag.setTag("AttributeModifiers", attributes);
        }
        attributes.appendTag(getAttributeTag("generic.attackDamage", key, amount * scaleAmount, modifierType, getUUIDFromItem(armor)));
    }

    private static final UUID headScale = UUID.fromString("8d39e761-c100-4f81-853d-12dc8b424c10");
    private static final UUID chestScale = UUID.fromString("8d39e761-c100-4f81-853d-12dc8b424c11");
    private static final UUID pantsScale = UUID.fromString("8d39e761-c100-4f81-853d-12dc8b424c12");
    private static final UUID shoesScale = UUID.fromString("8d39e761-c100-4f81-853d-12dc8b424c13");
    private static final UUID headFlat = UUID.fromString("8d39e761-c100-4f81-853d-12dc8b424c14");
    private static final UUID chestFlat = UUID.fromString("8d39e761-c100-4f81-853d-12dc8b424c15");
    private static final UUID pantsFlat = UUID.fromString("8d39e761-c100-4f81-853d-12dc8b424c16");
    private static final UUID shoesFlat = UUID.fromString("8d39e761-c100-4f81-853d-12dc8b424c17");

    UUID getUUIDFromItem (ItemStack stack)
    {
        ArmorCore item = (ArmorCore) stack.getItem();
        switch (item.armorPart)
        {
        case HELMET:
            return modifierType ? headScale : headFlat;
        case CHEST:
            return modifierType ? chestScale : chestFlat;
        case PANTS:
            return modifierType ? pantsScale : pantsFlat;
        case SHOES:
            return modifierType ? shoesScale : pantsFlat;
        }
        return null;
    }
}