package tconstruct.blocks.slime;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import tconstruct.blocks.TConstructBlock;
import tconstruct.library.TConstructRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SlimeGel extends TConstructBlock
{
    public SlimeGel()
    {
        super(Material.field_151583_m, 0.5f, new String[] { "slimeblock_blue", "slimeblock_green", "slimeblock_purple" });
        func_149647_a(TConstructRegistry.blockTab);
    }

    public boolean getEnableStats ()
    {
        return false;
    }

    public int getMobilityFlag ()
    {
        return 0;
    }

    public int damageDropped (int meta)
    {
        return meta;
    }

    @Override
    public void func_149670_a (World world, int x, int y, int z, Entity entity)
    {
        if (entity.motionY < 0)
        {
            if (entity.motionY < -0.08F)
            {
                Block var9 = (Block) this;
                world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, var9.field_149762_H.getStepSound(), (var9.field_149762_H.getVolume()) / 2.0F, var9.field_149762_H.getPitch() * 0.65F);
            }
            entity.motionY *= -1.2F;
            if (entity instanceof EntityLivingBase)
            {
                ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.jump.id, 1, 2));
            }
        }
        entity.fallDistance = 0;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool (World world, int x, int y, int z)
    {
        return AxisAlignedBB.getBoundingBox(x, y, z, (double) x + 1.0D, (double) y + 0.625D, (double) z + 1.0D);
    }

    @SideOnly(Side.CLIENT)
    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks (Block b, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(new ItemStack(b, 1, 0));
        par3List.add(new ItemStack(b, 1, 1));
        //par3List.add(new ItemStack(par1, 1, 2));
    }

    public boolean canSustainLeaves (World world, int x, int y, int z)
    {
        return true;
    }
}
