package tconstruct.modifiers.armor;

import java.util.EnumSet;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.EnumArmorPart;

//TODO: Condense attribute modifiers into one class
public class AModMoveSpeed extends ArmorModTypeFilter
{
    final boolean modifierType;

    public AModMoveSpeed(int effect, EnumSet<EnumArmorPart> armorTypes, ItemStack[] items, int[] values, boolean type)
    {
        super(effect, "ExoSpeed" + (type ? "Percent" : "Flat"), armorTypes, items, values);
        this.modifierType = type;
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag(getTagName());
        int amount = matchingAmount(input);
        return tags.getInteger("Modifiers") >= amount;
    }

    @Override
    public void modify (ItemStack[] input, ItemStack armor)
    {
        NBTTagCompound baseTag = armor.getTagCompound();
        NBTTagCompound armorTag = armor.getTagCompound().getCompoundTag(getTagName());

        int modifiers = armorTag.getInteger("Modifiers");
        modifiers -= matchingAmount(input);
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
                if (tag.getString("AttributeName").equals("generic.movementSpeed"))
                    attributes.removeTag(iter);
            }
        }
        else
        {
            attributes = new NBTTagList();
            baseTag.setTag("AttributeModifiers", attributes);
        }
        attributes.appendTag(getAttributeTag("generic.movementSpeed", key, amount / 100d, modifierType, getUUIDFromItem(armor)));
    }

    private static final UUID head = UUID.fromString("2ba6c8ae-4a19-49c6-aab4-ca3a5eb7c730");
    private static final UUID chest = UUID.fromString("2ba6c8ae-4a19-49c6-aab4-ca3a5eb7c731");
    private static final UUID pants = UUID.fromString("2ba6c8ae-4a19-49c6-aab4-ca3a5eb7c732");
    private static final UUID shoes = UUID.fromString("2ba6c8ae-4a19-49c6-aab4-ca3a5eb7c733");

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
