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
import tconstruct.common.TContent;
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
        ItemStack stack = player.getCurrentEquippedItem();
        CastingChannelLogic tile = (CastingChannelLogic) world.getBlockTileEntity(x, y, z);
        /* 
         * Debugging
        System.out.println("Side: "+side);
        System.out.println("hitting: "+hitX+", "+hitY+", "+hitZ);
        String s = "LiquidAmount "+(world.isRemote?"client side: ":"server side: ");
        int amount = 0;
        if (tile.getTankInfo(null)[0].fluid != null)
        	amount = tile.getTankInfo(null)[0].fluid.amount;
        if(world.isRemote)
        	player.addChatMessage("-----");
        player.addChatMessage(s + amount);
         */
        if (stack != null && stack.itemID == TContent.castingChannel.blockID)
            return false;
        else
        {
            tile.changeOutputs(player, side, hitX, hitY, hitZ);
            return true;
        }
    }

    @Override
    public void setBlockBoundsBasedOnState (IBlockAccess world, int x, int y, int z)
    {
        CastingChannelLogic tile = (CastingChannelLogic) world.getBlockTileEntity(x, y, z);
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

    @Override
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
