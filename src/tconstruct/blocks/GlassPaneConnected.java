package tconstruct.blocks;

import java.util.List;

import tconstruct.client.block.PaneConnectedRender;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class GlassPaneConnected extends GlassBlockConnected
{

    private Icon theIcon;

    public GlassPaneConnected(int par1, String location, boolean hasAlpha)
    {
        super(par1, location, hasAlpha);
    }

    @Override
    public int getRenderType ()
    {
        return PaneConnectedRender.model;
    }

    @Override
    public void addCollisionBoxesToList (World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
    {
        Block.thinGlass.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
    }

    @Override
    public void setBlockBoundsForItemRender ()
    {
        Block.thinGlass.setBlockBoundsForItemRender();
    }

    @Override
    public void setBlockBoundsBasedOnState (IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        Block.thinGlass.setBlockBoundsBasedOnState(par1IBlockAccess, par2, par3, par4);
    }

    public Icon getSideTextureIndex ()
    {
        return this.theIcon;
    }

    public final boolean canThisPaneConnectToThisBlockID (int par1)
    {
        return Block.opaqueCubeLookup[par1] || par1 == this.blockID || par1 == Block.glass.blockID;
    }

    public void registerIcons (IconRegister par1IconRegister)
    {
        super.registerIcons(par1IconRegister);
        this.theIcon = par1IconRegister.registerIcon("tinkersconstruct:glass/" + folder + "/glass_side");
    }

    public boolean canPaneConnectTo (IBlockAccess access, int x, int y, int z, ForgeDirection dir)
    {
        return canThisPaneConnectToThisBlockID(access.getBlockId(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ))
                || access.isBlockSolidOnSide(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, dir.getOpposite(), false);
    }

}
