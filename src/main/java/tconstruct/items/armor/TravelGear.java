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
import tconstruct.client.armor.BootBump;
import tconstruct.client.armor.GloveModel;
import tconstruct.client.armor.WingModel;
import tconstruct.common.TContent;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.EnumArmorPart;
import tconstruct.library.tools.ItemModifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TravelGear extends ArmorCore
{
    String textureName;

    public TravelGear(int id, EnumArmorPart part, String texture)
    {
        super(id, 0, part);
        this.textureName = texture;
        this.setCreativeTab(TConstructRegistry.materialTab);
    }
    
    @Override
    public String getModifyType()
    {
        return "Clothing";
    }

    @Override
    public void registerIcons (IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("tinker:armor/" + textureName + "_"
                + (this.armorType == 0 ? "goggles" : this.armorType == 1 ? "wings" : this.armorType == 2 ? "gloves" : this.armorType == 3 ? "boots" : "helmet"));
    }

    @Override
    public String getArmorTexture (ItemStack stack, Entity entity, int slot, int layer)
    {
        if (slot == 1)
            return "tinker:textures/armor/" + textureName + "_" + 3 + ".png";
        return "tinker:textures/armor/" + textureName + "_" + layer + ".png";
    }

    @Override
    public void damageArmor (EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot)
    {
        //Deimplemented for now
    }

    @SideOnly(Side.CLIENT)
    static WingModel wings = new WingModel();
    @SideOnly(Side.CLIENT)
    static BootBump bump = new BootBump();
    @SideOnly(Side.CLIENT)
    static GloveModel glove = new GloveModel();

    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel (EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
    {
        if (armorSlot == 1)
            return wings;
        if (armorSlot == 2)
            return glove;
        if (armorSlot == 3)
            return bump;
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems (int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        ItemStack armor = new ItemStack(par1, 1, 0);
        NBTTagCompound baseTag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();

        NBTTagCompound armorTag = new NBTTagCompound();
        armorTag.setInteger("Modifiers", 30);
        baseTag.setTag(SET_NAME, armorTag);

        if (par1 == TContent.exoChest.itemID)
        {
            NBTTagList attributes = new NBTTagList();
            baseTag.setTag("AttributeModifiers", attributes);
            attributes.appendTag(ItemModifier.getAttributeTag("generic.maxHunger", "ExoHunger", 20, true, UUID.fromString("8d39e761-cccc-4f81-853d-12dc8b424c14")));
        }

        armor.setTagCompound(baseTag);
        par3List.add(armor);
    }

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
        if (armorPart == EnumArmorPart.SHOES)
        {
            if (player.stepHeight < 1.0f)
                player.stepHeight = 1.0f;
        }

        if (armorPart == EnumArmorPart.HELMET)
        {
            player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 15 * 20, 0, true));
        }

        if (armorPart == EnumArmorPart.SHOES)
        {
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
}
