package tconstruct.items.armor;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Multimap;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.EnumArmorPart;

public class ExoArmor extends ArmorCore
{
    String textureName;
    protected static final UUID speed = UUID.fromString("CB3F55A9-629C-4F38-A497-9C13A33DB5CF"); //These are temporary. Do not use them in release
    protected static final UUID attack = UUID.fromString("CB3F55A9-629C-4F38-A497-9C13A33DB5CE");
    protected static final UUID health = UUID.fromString("CB3F55A9-629C-4F38-A497-9C13A33DB5CD");
    protected static final UUID knockback = UUID.fromString("CB3F55A9-629C-4F38-A497-9C13A33DB5CC");
    public ExoArmor(int id, EnumArmorPart part, String texture)
    {
        super(id, 0, part);
        this.textureName = texture;
        this.setCreativeTab(TConstructRegistry.materialTab);
    }
    
    @Override
    public void registerIcons (IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("tinker:armor/" + textureName + "_"
                + (this.armorType == 0 ? "helmet" : this.armorType == 1 ? "chestplate" : this.armorType == 2 ? "leggings" : this.armorType == 3 ? "boots" : "helmet"));
    }

    @Override
    public String getArmorTexture (ItemStack stack, Entity entity, int slot, int layer)
    {
        return "tinker:textures/armor/" + textureName + "_" + layer + ".png";
    }
    
    public void getSubItems (int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        ItemStack armor = new ItemStack(par1, 1, 0);
        NBTTagCompound baseTag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        
        //list.appendTag(getAttributeTag("generic.attackDamage", "ExoAttack", 10.0, true, knockback));
        //list.appendTag(getAttributeTag("generic.movementSpeed", "ExoSpeed", 1.5, false, speed));
        //list.appendTag(getAttributeTag("generic.maxHealth", "ExoHealth", 10.0, true, health));
        //list.appendTag(getAttributeTag("generic.knockbackResistance", "ExoKnockback", 0.5, false, knockback));
        //baseTag.setTag("AttributeModifiers", list);
        
        NBTTagCompound armorTag = new NBTTagCompound();
        armorTag.setInteger("Modifiers", 30);
        baseTag.setTag(SET_NAME, armorTag);
        
        armor.setTagCompound(baseTag);
        par3List.add(armor);
    }
    
    private NBTTagCompound getAttributeTag(String attributeType, String modifierName, double amount, boolean flat, UUID uuid)
    {
        NBTTagCompound knockbackTag = new NBTTagCompound();
        knockbackTag.setString("AttributeName", attributeType);
        knockbackTag.setString("Name", modifierName);
        knockbackTag.setDouble("Amount", amount);
        knockbackTag.setInteger("Operation", flat ? 0 : 1);//0 = flat increase, 1 = % increase
        knockbackTag.setLong("UUIDMost", uuid.getMostSignificantBits());
        knockbackTag.setLong("UUIDLeast", uuid.getLeastSignificantBits());
        return knockbackTag;
    }
}
