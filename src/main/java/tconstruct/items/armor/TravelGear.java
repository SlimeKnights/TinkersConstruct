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
import tconstruct.library.armor.ArmorPart;
import tconstruct.library.tools.ToolCore;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TravelGear extends ArmorCore
{

    public TravelGear(int id, ArmorPart part)
    {
        super(id, 0, part, "Clothing", "travelgear", "travel");
        this.setMaxDamage(1035);
    }

    @Override
    public void onArmorTickUpdate (World world, EntityPlayer player, ItemStack itemStack)
    {
        super.onArmorTickUpdate(world, player, itemStack);
        if (armorPart == ArmorPart.Chest)
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

        else if (armorPart == ArmorPart.Head)
        {
            TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
            if (stats.activeGoggles)
            {
                player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 15 * 20, 0, true));
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
    public void registerIcons (IconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon("tinker:" + textureFolder + "/" + textureName + "_"
                + (this.armorType == 0 ? "goggles" : this.armorType == 1 ? "vest" : this.armorType == 2 ? "wings" : this.armorType == 3 ? "boots" : "helmet"));
        registerModifiers(iconRegister);
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
