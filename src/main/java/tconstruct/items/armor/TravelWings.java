package tconstruct.items.armor;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import tconstruct.common.TContent;
import tconstruct.library.armor.ArmorPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TravelWings extends TravelGear
{

    public TravelWings(int id)
    {
        super(id, ArmorPart.Legs);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void registerModifiers (IconRegister iconRegister)
    {
        String base = "tinker:armor/travel_wings_";
        modifiers = new Icon[5];
        modifiers[0] = iconRegister.registerIcon("tinker:" + textureFolder + "/" + "wings" + "_"+"doublejump");
        modifiers[1] = iconRegister.registerIcon("tinker:" + textureFolder + "/" + "wings" + "_"+"featherfall");
        modifiers[4] = iconRegister.registerIcon("tinker:" + textureFolder + "/" + "wings" + "_"+"moss");
        /*modifiers = new Icon[3];
        modifiers[0] = iconRegister.registerIcon(base + "slimewings");
        modifiers[1] = iconRegister.registerIcon(base + "piston");
        modifiers[2] = iconRegister.registerIcon(base + "pearl");*/
    }

    @Override
    public void onArmorTickUpdate (World world, EntityPlayer player, ItemStack itemStack)
    {
        NBTTagCompound tag = itemStack.getTagCompound().getCompoundTag(getBaseTagName());
        int feather = tag.getInteger("Feather Fall");
        if (feather > 0)
        {
            if (player.fallDistance > 2.5)
                player.fallDistance = 2.5f;
            float terminalVelocity = -0.4f + (feather * 0.08f);
            if (terminalVelocity > -0.05f)
                terminalVelocity = - 0.05f;
            if (player.isSneaking() && terminalVelocity > -0.4f)
                terminalVelocity = -0.4F;
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
    public String getArmorTexture (ItemStack stack, Entity entity, int slot, int layer)
    {
        return "tinker:textures/armor/travel_wings.png";
    }

    @Override
    public ItemStack getRepairMaterial (ItemStack input)
    {
        return new ItemStack(TContent.materials, 1, 13);
    }
}
