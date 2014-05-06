package tconstruct.items.armor;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import tconstruct.common.TContent;
import tconstruct.library.armor.EnumArmorPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TravelWings extends TravelGear
{

    public TravelWings(int id, String texture)
    {
        super(id, EnumArmorPart.Legs, texture);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void registerModifiers (IconRegister iconRegister)
    {
        String base = "tinker:armor/travel_wings_";
        modifiers = new Icon[3];
        modifiers[0] = iconRegister.registerIcon(base + "slimewings");
        modifiers[1] = iconRegister.registerIcon(base + "piston");
        modifiers[2] = iconRegister.registerIcon(base + "pearl");
    }

    @Override
    public void onArmorTickUpdate (World world, EntityPlayer player, ItemStack itemStack)
    {
        if (player.fallDistance > 2.5)
            player.fallDistance = 2.5f;
        float terminalVelocity = -0.32f;
        boolean flying = false;
        flying = player.capabilities.isFlying;
        if (!flying && player.motionY < terminalVelocity)
        {
            player.motionY = terminalVelocity;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture (ItemStack stack, Entity entity, int slot, int layer)
    {
        return "tinker:textures/armor/travel_wings.png";
    }
    
    @Override
    public ItemStack getRepairMaterial(ItemStack input)
    {
        return new ItemStack(TContent.materials, 1, 13);
    }
}
