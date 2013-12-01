package tconstruct.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import tconstruct.blocks.logic.CastingChannelLogic;
import tconstruct.client.block.BlockRenderCastingChannel;
import tconstruct.library.TConstructRegistry;

/**
 * @author BluSunrize
 */

public class CastingChannelBlock extends BlockContainer
{

    public CastingChannelBlock(int par1)
    {
        super(par1, Material.rock);
        this.setHardness(1F);
        this.setResistance(10);
        this.setStepSound(soundStoneFootstep);
        setCreativeTab(TConstructRegistry.blockTab);
    }

    @Override
    public boolean onBlockActivated (World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            CastingChannelLogic tile = (CastingChannelLogic) world.getBlockTileEntity(x, y, z);
            if (player.isSneaking())
            {
            }
            else
            {
                //int amount = 0;
                //if (tile.getFluid() != null)
                //amount = tile.getLiquidAmount();
                //player.addChatMessage("LiquidAmount: " + amount);
                tile.setActive(true);
            }
        }

        ItemStack stack = player.getCurrentEquippedItem();
        if (stack != null && stack.itemID == this.blockID)
            return false;
        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState (IBlockAccess world, int x, int y, int z)
    {
        CastingChannelLogic tile = (CastingChannelLogic) world.getBlockTileEntity(x, y, z);
        float minX = 0.3125F;
        float maxX = 0.6875F;
        float minZ = 0.3125F;
        float maxZ = 0.6875F;
        if (tile.hasTankConnected(ForgeDirection.NORTH))
            minZ = 0F;
        if (tile.hasTankConnected(ForgeDirection.SOUTH))
            maxZ = 1F;
        if (tile.hasTankConnected(ForgeDirection.WEST))
            minX = 0F;
        if (tile.hasTankConnected(ForgeDirection.EAST))
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

    @Override
    public int getRenderType ()
    {
        return BlockRenderCastingChannel.renderID;
    }

    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        this.blockIcon = iconRegister.registerIcon("tinker:searedstone");
    }

    @Override
    public TileEntity createNewTileEntity (World var1)
    {
        return new CastingChannelLogic();
    }

}