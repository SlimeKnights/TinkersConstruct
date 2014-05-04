package tconstruct.items.armor;

import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import tconstruct.client.armor.WingModel;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.EnumArmorPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
        int suffix = this.armorType == 2 ? 2 : 1;
        return "tinker:textures/armor/" + textureName + "_" + suffix + ".png";
    }

    @Override
    public void damageArmor (EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot)
    {
        // Deimplemented for now
    }

    @SideOnly(Side.CLIENT)
    WingModel moel = new WingModel();

    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel (EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
    {
        if (armorSlot == 1)
            return moel;
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems (Item b, CreativeTabs par2CreativeTabs, List par3List)
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

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        if (!stack.hasTagCompound())
            return;
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag(SET_NAME);
        double protection = tags.getDouble("protection");
        if (protection > 0)
            list.add("\u00a7aProtection: " + protection + "%");
        boolean displayToolTips = true;
        int tipNum = 0;
        while (displayToolTips)
        {
            tipNum++;
            String tooltip = "Tooltip" + tipNum;
            if (tags.hasKey(tooltip))
            {
                String tipName = tags.getString(tooltip);
                if (!tipName.equals(""))
                    list.add(tipName);
            }
            else
                displayToolTips = false;
        }
    }

    @Override
    public void onArmorTickUpdate (World world, EntityPlayer player, ItemStack itemStack)
    {
        if (player.stepHeight < 1.0f)
            player.stepHeight = 1.0f;

        player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 15 * 20, 0, true));

        player.fallDistance = 0;
        float terminalVelocity = -0.32f;
        boolean flying = false;
        flying = player.capabilities.isFlying;
        if (!flying && player.motionY < terminalVelocity)
        {
            player.motionY = terminalVelocity;
        }
    }

}
