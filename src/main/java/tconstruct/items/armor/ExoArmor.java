package tconstruct.items.armor;

import java.util.List;
import java.util.UUID;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import tconstruct.client.armor.WingModel;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.ArmorPart;
import tconstruct.library.modifier.ItemModifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ExoArmor extends ArmorCore
{

    public ExoArmor(int id, ArmorPart part, String texture)
    {
        super(id, 0, part, "ExoArmor", texture);
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

    @Override
    public void damageArmor (EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot)
    {
        //Deimplemented for now
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems (int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        ItemStack armor = new ItemStack(par1, 1, 0);
        NBTTagCompound baseTag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();

        NBTTagCompound armorTag = new NBTTagCompound();
        armorTag.setInteger("Modifiers", 30);
        baseTag.setTag(getBaseTagName(), armorTag);

        armor.setTagCompound(baseTag);
        par3List.add(armor);
    }

    @Override
    public ItemStack getRepairMaterial (ItemStack input)
    {
        return null;
    }

    @Override
    protected double getBaseDefense ()
    {
        return 0;
    }

    @Override
    protected double getMaxDefense ()
    {
        return 0;
    }
    
    @Override
    protected int getDurability()
    {
        return 1035;
    }
}
