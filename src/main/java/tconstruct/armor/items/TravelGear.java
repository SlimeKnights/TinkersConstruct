package tconstruct.armor.items;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import tconstruct.armor.ArmorProxyClient;
import tconstruct.library.armor.*;

public class TravelGear extends ArmorCore
{

    public TravelGear(ArmorPart part)
    {
        super(0, part, "Clothing", "travelgear", "travel");
        this.setMaxDamage(1035);
    }

    @Override
    public void onArmorTick (World world, EntityPlayer player, ItemStack itemStack)
    {
        super.onArmorTick(world, player, itemStack);
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
    protected int getDurability ()
    {
        return 1035;
    }

    //Temporary?
    public ItemStack getRepairMaterial (ItemStack input)
    {
        return new ItemStack(Items.leather);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IIconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon("tinker:" + textureFolder + "/" + textureName + "_" + (this.armorType == 0 ? "goggles" : this.armorType == 1 ? "vest" : this.armorType == 2 ? "wings" : this.armorType == 3 ? "boots" : "helmet"));
        registerModifiers(iconRegister);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel (EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
    {
        if (armorSlot == 1)
            return ArmorProxyClient.vest;
        if (armorSlot == 2)
            return ArmorProxyClient.wings;
        if (armorSlot == 3)
            return ArmorProxyClient.bootbump;
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void registerModifiers (IIconRegister iconRegister) //temporary
    {
        switch (armorType)
        {
        case 0:
            modifiers = new IIcon[5];
            modifiers[0] = iconRegister.registerIcon("tinker:" + textureFolder + "/" + "goggles" + "_" + "nightvision");
            modifiers[4] = iconRegister.registerIcon("tinker:" + textureFolder + "/" + "goggles" + "_" + "moss");
            break;
        case 1:
            modifiers = new IIcon[5];
            modifiers[0] = iconRegister.registerIcon("tinker:" + textureFolder + "/" + "vest" + "_" + "dodge");
            modifiers[1] = iconRegister.registerIcon("tinker:" + textureFolder + "/" + "vest" + "_" + "stealth");
            modifiers[4] = iconRegister.registerIcon("tinker:" + textureFolder + "/" + "vest" + "_" + "moss");
            break;
        case 2:
            modifiers = new IIcon[5];
            modifiers[0] = iconRegister.registerIcon("tinker:" + textureFolder + "/" + "wings" + "_" + "doublejump");
            modifiers[1] = iconRegister.registerIcon("tinker:" + textureFolder + "/" + "wings" + "_" + "featherfall");
            modifiers[4] = iconRegister.registerIcon("tinker:" + textureFolder + "/" + "wings" + "_" + "moss");
            break;
        case 3:
            modifiers = new IIcon[5];
            modifiers[0] = iconRegister.registerIcon("tinker:" + textureFolder + "/" + "boots" + "_" + "doublejump");
            modifiers[1] = iconRegister.registerIcon("tinker:" + textureFolder + "/" + "boots" + "_" + "waterwalk");
            modifiers[2] = iconRegister.registerIcon("tinker:" + textureFolder + "/" + "boots" + "_" + "leadweight");
            modifiers[3] = iconRegister.registerIcon("tinker:" + textureFolder + "/" + "boots" + "_" + "slimysole");
            modifiers[4] = iconRegister.registerIcon("tinker:" + textureFolder + "/" + "boots" + "_" + "moss");
            break;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {

        switch (armorPart)
        {
        case Head:
            list.add("\u00a76Ability: Zoom with " + GameSettings.getKeyDisplayString(tconstruct.client.ArmorControls.zoomKey.getKeyCode()));
            list.add("\u00a76Toggle Abilities: " + GameSettings.getKeyDisplayString(tconstruct.client.ArmorControls.toggleGoggles.getKeyCode()));
            break;
        case Chest:
            list.add("\u00a76Ability: Swift Swim");
            break;
        case Legs:
            list.add("\u00a76Ability: High Jump");
            break;
        case Feet:
            list.add("\u00a76Ability: High Step");
            break;
        default:
        }

        super.addInformation(stack, player, list, par4);
    }
}
