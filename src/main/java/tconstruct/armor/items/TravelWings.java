package tconstruct.armor.items;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import tconstruct.library.armor.ArmorPart;
import tconstruct.tools.TinkerTools;

public class TravelWings extends TravelGear
{

    public TravelWings()
    {
        super(ArmorPart.Legs);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void registerModifiers (IIconRegister IIconRegister)
    {
        String base = "tinker:armor/travel_wings_";
        modifiers = new IIcon[5];
        modifiers[0] = IIconRegister.registerIcon("tinker:" + textureFolder + "/" + "wings" + "_" + "doublejump");
        modifiers[1] = IIconRegister.registerIcon("tinker:" + textureFolder + "/" + "wings" + "_" + "featherfall");
        modifiers[4] = IIconRegister.registerIcon("tinker:" + textureFolder + "/" + "wings" + "_" + "moss");
        /*modifiers = new IIcon[3];
        modifiers[0] = IIconRegister.registerIcon(base + "slimewings");
        modifiers[1] = IIconRegister.registerIcon(base + "piston");
        modifiers[2] = IIconRegister.registerIcon(base + "pearl");*/
    }

    @Override
    public void onArmorTick (World world, EntityPlayer player, ItemStack itemStack)
    {
        NBTTagCompound tag = itemStack.getTagCompound().getCompoundTag(getBaseTagName());
        int feather = tag.getInteger("Feather Fall");
        if (feather > 0)
        {
            if (player.fallDistance > 2.5)
                player.fallDistance = 2.5f;
            float terminalVelocity = -0.4f + (feather * 0.08f);
            if (terminalVelocity > -0.05f)
                terminalVelocity = -0.05f;
            if (player.isSneaking() && terminalVelocity > -0.8f)
                terminalVelocity = -0.8F;
            boolean flying = false;
            flying = player.capabilities.isFlying;
            if (!flying && player.motionY < terminalVelocity)
            {
                player.motionY = terminalVelocity;
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture (ItemStack stack, Entity entity, int slot, String layer)
    {
        return "tinker:textures/armor/travel_wings.png";
    }

    @Override
    public ItemStack getRepairMaterial (ItemStack input)
    {
        return new ItemStack(TinkerTools.materials, 1, 13);
    }
}
