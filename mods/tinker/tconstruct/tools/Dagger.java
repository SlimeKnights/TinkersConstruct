package mods.tinker.tconstruct.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.tinker.tconstruct.TContent;
import mods.tinker.tconstruct.entity.projectile.DaggerEntity;
import mods.tinker.tconstruct.library.Weapon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class Dagger extends Weapon
{
    public Dagger(int id)
    {
        super(id, 1);
    }

    public ItemStack onItemRightClick (ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        ItemStack is = itemstack.copy();
        is.stackSize--;
        if (!world.isRemote)
        {
            DaggerEntity dagger = new DaggerEntity(itemstack, world, entityplayer);
            world.spawnEntityInWorld(dagger);
        }
        return is;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses (int metadata)
    {
        return 8;
    }
    
    @Override
    public int getPartAmount()
    {
        return 2;
    }
    
    @Override
    public void registerPartPaths (int index, String[] location)
    {
        headStrings.put(index, location[0]);
        brokenHeadStrings.put(index, location[1]);
        handleStrings.put(index, location[2]);
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_dagger_blade";
        case 1:
            return "_dagger_blade_broken";
        case 2:
            return "_dagger_handle";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_dagger_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "dagger";
    }

    @Override
    protected Item getHeadItem ()
    {
        return TContent.swordBlade;
    }

    @Override
    protected Item getAccessoryItem ()
    {
        return null;
    }
}
