package tconstruct.items.armor;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import tconstruct.TConstruct;
import tconstruct.client.TControls;
import tconstruct.client.TProxyClient;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.EnumArmorPart;
import tconstruct.library.tools.ToolCore;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TravelGear extends ArmorCore
{

    public TravelGear(int id, EnumArmorPart part, String texture)
    {
        super(id, 0, part, "Clothing", texture);
        this.setCreativeTab(TConstructRegistry.materialTab);
        this.setMaxDamage(1035);
    }

    @Override
    public void onArmorTickUpdate (World world, EntityPlayer player, ItemStack itemStack)
    {
        if (armorPart == EnumArmorPart.Chest)
        {
            if (player.isInWater())
            {
                player.motionX *= 1.2D;
                if (player.motionY > 0.0D)
                {
                    player.motionY *= 1.2D;
                }
                player.motionZ *= 1.2D;
                double maxSpeed = 0.2D;
                if (player.motionX > maxSpeed)
                {
                    player.motionX = maxSpeed;
                }
                else if (player.motionX < -maxSpeed)
                {
                    player.motionX = -maxSpeed;
                }
                if (player.motionY > maxSpeed)
                {
                    player.motionY = maxSpeed;
                }
                if (player.motionZ > maxSpeed)
                {
                    player.motionZ = maxSpeed;
                }
                else if (player.motionZ < -maxSpeed)
                {
                    player.motionZ = -maxSpeed;
                }
            }
        }

        /*else if (armorPart == EnumArmorPart.Feet)
        {
            if (player.stepHeight < 1.0f)
                player.stepHeight = 1.0f;
        }*/

        else if (armorPart == EnumArmorPart.Head)
        {
            TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
            if (stats.activeGoggles)
            {
                player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 15 * 20, 0, true));
                //player.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 1, 0, true));
            }
        }
    }

    @Override
    protected double getBaseDefense ()
    {
        switch (armorPart)
        {
        case Head:
            return 0;
        case Chest:
            return 4;
        case Legs:
            return 2;
        case Feet:
            return 2;
        }
        return 0;
    }
    
    @Override
    protected double getMaxDefense ()
    {
        switch (armorPart)
        {
        case Head:
            return 4;
        case Chest:
            return 10;
        case Legs:
            return 8;
        case Feet:
            return 6;
        }
        return 0;
    }
    
    @Override
    protected int getDurability()
    {
        return 1035;
    }

    //Temporary?
    public ItemStack getRepairMaterial (ItemStack input)
    {
        return new ItemStack(Item.leather);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (ItemStack stack, int renderPass)
    {
        if (renderPass > 0)
        {
            if (stack.hasTagCompound())
            {
                NBTTagCompound tags = stack.getTagCompound().getCompoundTag(getBaseTagName());
                if (renderPass == 1 && tags.hasKey("Effect1"))
                {
                    return modifiers[tags.getInteger("Effect1")];
                }
                if (renderPass == 2 && tags.hasKey("Effect2"))
                {
                    return modifiers[tags.getInteger("Effect2")];
                }
                if (renderPass == 3 && tags.hasKey("Effect3"))
                {
                    return modifiers[tags.getInteger("Effect3")];
                }
            }
            return ToolCore.blankSprite;
        }

        return itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel (EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
    {
        if (armorSlot == 1)
            return TProxyClient.vest;
        if (armorSlot == 2)
            return TProxyClient.wings;
        if (armorSlot == 3)
            return TProxyClient.bootbump;
        return null;
    }
}
