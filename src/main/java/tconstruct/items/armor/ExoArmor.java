package tconstruct.items.armor;

import java.util.List;
import java.util.UUID;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.DamageSource;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.EnumArmorPart;

public class ExoArmor extends ArmorCore
{
    String textureName;

    public ExoArmor(EnumArmorPart part, String texture)
    {
        super(0, part);
        this.textureName = texture;
        this.setCreativeTab(TConstructRegistry.materialTab);
    }

    @Override
    public void registerIcons (IIconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("tinker:armor/" + textureName + "_"
                + (this.armorType == 0 ? "helmet" : this.armorType == 1 ? "chestplate" : this.armorType == 2 ? "leggings" : this.armorType == 3 ? "boots" : "helmet"));
    }

    @Override
    public String getArmorTexture (ItemStack stack, Entity entity, int slot, String layer)
    {
        return "tinker:textures/armor/" + textureName + "_" + layer + ".png";
    }

    @Override
    public void damageArmor (EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot)
    {
        //Deimplemented for now
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems (Block b, CreativeTabs par2CreativeTabs, List par3List)
    {
        ItemStack armor = new ItemStack(b, 1, 0);
        NBTTagCompound baseTag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();

        NBTTagCompound armorTag = new NBTTagCompound();
        armorTag.setInteger("Modifiers", 30);
        baseTag.setTag(SET_NAME, armorTag);

        armor.setTagCompound(baseTag);
        par3List.add(armor);
    }

    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        if (!stack.hasTagCompound())
            return;
        NBTTagCompound tag = stack.getTagCompound().getCompoundTag(SET_NAME);
        double protection = tag.getDouble("protection");
        if (protection > 0)
            list.add("\u00a7aProtection: " + protection + "%");
    }
}
