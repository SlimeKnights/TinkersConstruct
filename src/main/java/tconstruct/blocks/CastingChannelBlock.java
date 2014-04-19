package tconstruct.blocks;

import mantle.common.ComparisonHelper;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import tconstruct.blocks.logic.CastingChannelLogic;
import tconstruct.client.block.BlockRenderCastingChannel;
import tconstruct.library.TConstructRegistry;

/**
 * @author BluSunrize
 */

public class CastingChannelBlock extends BlockContainer
{

    public CastingChannelBlock()
    {
        super(Material.rock);
        this.setHardness(1F);
        this.setResistance(10);
        this.stepSound = soundTypeStone;
        setCreativeTab(TConstructRegistry.blockTab);
    }

    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getCurrentEquippedItem();
        CastingChannelLogic tile = (CastingChannelLogic) world.getTileEntity(x, y, z);

        if (stack == null)
            tile.changeOutputs(player, side, hitX, hitY, hitZ);
        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState (IBlockAccess world, int x, int y, int z)
    {
        CastingChannelLogic tile = (CastingChannelLogic) world.getTileEntity(x, y, z);
        float minX = 0.3125F;
        float maxX = 0.6875F;
        float minZ = 0.3125F;
        float maxZ = 0.6875F;
        minZ = 0F;
        maxZ = 1F;
        minX = 0F;
        maxX = 1F;
        this.setBlockBounds(minX, 0.375F, minZ, maxX, 0.625F, maxZ);
    }

    @Override
    public boolean renderAsNormalBlock ()
    {
        return false;
    }
    
    @Override
    public boolean isOpaqueCube ()
    {
        return false;
    }

    public boolean shouldSideBeRendered (IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return true;
    }

    @Override
    public int getRenderType ()
    {
        return BlockRenderCastingChannel.renderID;
    }

    @Override
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        this.blockIcon = iconRegister.registerIcon("tinker:searedstone");
    }

    @Override
    public TileEntity createNewTileEntity (World var1, int var2)
    {
        return new CastingChannelLogic();
    }

}