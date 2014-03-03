package common.darkknight.jewelrycraft.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import common.darkknight.jewelrycraft.tileentity.TileEntityBlockShadow;

public class BlockShadow extends BlockContainer
{
    private Icon[] iconArray;

    public BlockShadow(int par1)
    {
        super(par1, Material.iron);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public int getRenderBlockPass()
    {
        return 1;
    }
    
    public boolean isBeaconBase(World worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ)
    {
        return true;
    }
    
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
    {
        return true;
    }
    
    public static boolean isNormalCube(int par0)
    {
        return true;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntityBlockShadow();
    }
    
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.iconArray = new Icon[16];

        for (int i = 0; i < this.iconArray.length; ++i)
        {
            this.iconArray[i] = par1IconRegister.registerIcon(this.getTextureName() + (15 - i));
        }
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        if(world.getBlockMetadata(x, y, z) == 15) return null;
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }
    
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }
    
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return true;
    }
    
    public boolean hasComparatorInputOverride()
    {
        return true;
    }
    
    public int getComparatorInputOverride(World world, int x, int y, int z, int meta)
    {
        return world.getBlockMetadata(x, y, z);
    }
    
    public Icon getIcon(int par1, int par2)
    {
        return this.iconArray[par2];
    }
}
