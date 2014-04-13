package tconstruct.modifiers.armor;

import java.util.EnumSet;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.ArmorModTypeFilter;
import tconstruct.library.armor.EnumArmorPart;

//TODO: Condense attribute modifiers into one class
public class AModHealthBoost extends ArmorModTypeFilter
{
    final boolean modifierType;
    final int modifyAmount = 3;

    public AModHealthBoost(int effect, EnumSet<EnumArmorPart> armorTypes, ItemStack[] items, int[] values, boolean type)
    {
        super(effect, "ExoHealth" + (type ? "Percent" : "Flat"), armorTypes, items, values);
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
            attributes = baseTag.getTagList("AttributeModifiers");
            for (int iter = 0; iter < attributes.tagCount(); iter++)
            {
                NBTTagCompound tag = (NBTTagCompound) attributes.tagAt(iter);
                if (tag.getString("AttributeName").equals("generic.maxHealth"))
                    attributes.removeTag(iter);
            }
        }
        else
        {
            attributes = new NBTTagList();
            baseTag.setTag("AttributeModifiers", attributes);
        }
        attributes.appendTag(getAttributeTag("generic.maxHealth", key, amount, modifierType, getUUIDFromItem(armor)));
    }

    private static final UUID head = UUID.fromString("a2eac357-cae3-4a8f-994c-a8bcbbd6dab8");
    private static final UUID chest = UUID.fromString("a2eac357-cae3-4a8f-994c-a8bcbbd6dab9");
    private static final UUID pants = UUID.fromString("a2eac357-cae3-4a8f-994c-a8bcbbd6daba");
    private static final UUID shoes = UUID.fromString("a2eac357-cae3-4a8f-994c-a8bcbbd6dabb");

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
