package tconstruct.world.blocks;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;
import net.minecraft.world.*;
import tconstruct.blocks.TConstructBlock;
import tconstruct.mechworks.entity.item.ExplosivePrimed;

public class SlimeExplosive extends TConstructBlock
{

    public SlimeExplosive()
    {
        super(Material.tnt, 0f, getTextureNames());
    }

    static String[] getTextureNames ()
    {
        String[] names = new String[6];
        String[] types = new String[] { "green", "blue" };
        for (int i = 0; i < 2; i++)
        {
            names[i * 3 + 0] = "sdx_bottom_" + types[i];
            names[i * 3 + 1] = "sdx_side_" + types[i];
            names[i * 3 + 2] = "sdx_top_" + types[i];
        }
        return names;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta)
    {
        return icons[(meta / 2) * 3 + getSideTextureIndex(side)];
    }

    @Override
    public void getSubBlocks (Item b, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 2; iter++)
        {
            list.add(new ItemStack(b, 1, iter * 2));
        }
    }

    @Override
    public void onBlockAdded (World par1World, int par2, int par3, int par4)
    {
        super.onBlockAdded(par1World, par2, par3, par4);

        if (par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
        {
            this.onBlockDestroyedByPlayer(par1World, par2, par3, par4, 1);
            WorldHelper.setBlockToAir(par1World, par2, par3, par4);
        }
    }

    @Override
    public void onNeighborBlockChange (World par1World, int par2, int par3, int par4, Block par5Block)
    {
        if (par1World.isBlockIndirectlyGettingPowered(par2, par3, par4))
        {
            this.onBlockDestroyedByPlayer(par1World, par2, par3, par4, 1);
            WorldHelper.setBlockToAir(par1World, par2, par3, par4);
        }
    }

    @Override
    public void onBlockDestroyedByExplosion (World par1World, int par2, int par3, int par4, Explosion par5Explosion)
    {
        if (!par1World.isRemote)
        {
            ExplosivePrimed entitytntprimed = new ExplosivePrimed(par1World, (double) ((float) par2 + 0.5F), (double) ((float) par3 + 0.5F), (double) ((float) par4 + 0.5F), par5Explosion.getExplosivePlacedBy());
            entitytntprimed.fuse = par1World.rand.nextInt(entitytntprimed.fuse / 4) + entitytntprimed.fuse / 8;
            par1World.spawnEntityInWorld(entitytntprimed);
        }
    }

    @Override
    public void onBlockDestroyedByPlayer (World par1World, int par2, int par3, int par4, int par5)
    {
        this.primeTnt(par1World, par2, par3, par4, par5, (EntityLivingBase) null);
    }

    public void primeTnt (World par1World, int par2, int par3, int par4, int par5, EntityLivingBase par6EntityLivingBase)
    {
        if (!par1World.isRemote)
        {
            if ((par5 % 2) == 1)
            {
                ExplosivePrimed entitytntprimed = new ExplosivePrimed(par1World, (double) ((float) par2 + 0.5F), (double) ((float) par3 + 0.5F), (double) ((float) par4 + 0.5F), par6EntityLivingBase);
                par1World.spawnEntityInWorld(entitytntprimed);
                par1World.playSoundAtEntity(entitytntprimed, "random.fuse", 1.0F, 1.0F);
            }
        }
    }

    @Override
    public boolean onBlockActivated (World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)
    {
        if (par5EntityPlayer.getCurrentEquippedItem() != null && par5EntityPlayer.getCurrentEquippedItem().getItem() == Items.flint_and_steel)
        {
            this.primeTnt(par1World, par2, par3, par4, 1, par5EntityPlayer);
            WorldHelper.setBlockToAir(par1World, par2, par3, par4);
            par5EntityPlayer.getCurrentEquippedItem().damageItem(1, par5EntityPlayer);
            return true;
        }
        else
        {
            return super.onBlockActivated(par1World, par2, par3, par4, par5EntityPlayer, par6, par7, par8, par9);
        }
    }

    @Override
    public void onEntityCollidedWithBlock (World par1World, int par2, int par3, int par4, Entity par5Entity)
    {
        if (par5Entity instanceof EntityArrow && !par1World.isRemote)
        {
            EntityArrow entityarrow = (EntityArrow) par5Entity;

            if (entityarrow.isBurning())
            {
                this.primeTnt(par1World, par2, par3, par4, 1, entityarrow.shootingEntity instanceof EntityLivingBase ? (EntityLivingBase) entityarrow.shootingEntity : null);
                WorldHelper.setBlockToAir(par1World, par2, par3, par4);
            }
        }
    }

    @Override
    public boolean canDropFromExplosion (Explosion par1Explosion)
    {
        return false;
    }

}
