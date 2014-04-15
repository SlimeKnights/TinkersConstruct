package tconstruct.library.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import tconstruct.library.IModifyable;
import tconstruct.library.armor.ArmorMod;
import tconstruct.library.tools.ItemModifier;

public class ModifyBuilder
{
    public static ModifyBuilder instance = new ModifyBuilder();
    public List<ItemModifier> toolMods = new ArrayList<ItemModifier>();
    public List<ArmorMod> armorMods = new ArrayList<ArmorMod>();
    
    public ItemStack modifyItem(ItemStack input, ItemStack[] modifiers)
    {
        ItemStack item = input.copy();
        Item temp = item.getItem();
        if (temp instanceof IModifyable)
        {
            
        }
        return null;
    }
    
    @Deprecated
    public ItemStack modifyTool (ItemStack input, ItemStack[] slots, String name)
    {
        ItemStack tool = input.copy();
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        tags.removeTag("Built");

        boolean built = false;
        for (ItemModifier mod : toolMods)
        {
            if (mod.matches(slots, tool))
            {
                built = true;
                mod.addMatchingEffect(tool);
                mod.modify(slots, tool);
            }
        }

        tags = tool.getTagCompound();
        if (name != null && !name.equals("") && !tags.hasKey("display"))
        {
            tags.setCompoundTag("display", new NBTTagCompound());
            tags.getCompoundTag("display").setString("Name", "\u00A7f" + name);
        }

        if (built)
            return tool;
        else
            return null;
    }

    @Deprecated
    public ItemStack modifyArmor (ItemStack input, ItemStack[] slots, String name)
    {
        ItemStack armor = input.copy();
        if (!armor.hasTagCompound())
            addArmorTag(input);
        NBTTagCompound tags = armor.getTagCompound().getCompoundTag("TinkerArmor");
        tags.removeTag("Built");

        boolean built = false;
        for (ArmorMod mod : armorMods)
        {
            if (mod.matches(slots, armor))
            {
                built = true;
                mod.addMatchingEffect(armor);
                mod.modify(slots, armor);
            }
        }

        tags = armor.getTagCompound();
        if (name != null && !name.equals("") && !tags.hasKey("display"))
        {
            tags.setCompoundTag("display", new NBTTagCompound());
            tags.getCompoundTag("display").setString("Name", "\u00A7f" + name);
        }

        if (built)
            return armor;
        else
            return null;
    }
    
    public void addArmorTag (ItemStack armor) //Not sure if temporary or not
    {
        NBTTagCompound baseTag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();

        NBTTagCompound armorTag = new NBTTagCompound();
        armorTag.setInteger("Modifiers", 30);
        baseTag.setTag("TinkerArmor", armorTag);

        armor.setTagCompound(baseTag);
    }
    
    @Deprecated
    public static void registerToolMod (ItemModifier mod)
    {
        if (mod == null)
            throw new NullPointerException("Tool modifier cannot be null.");
        instance.toolMods.add(mod);
    }

    @Deprecated
    public static void registerArmorMod (ArmorMod mod)
    {
        if (mod == null)
            throw new NullPointerException("Armor modifier cannot be null.");
        instance.armorMods.add(mod);
    }
}
