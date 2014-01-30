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
        super(Material.field_151576_e);
        this.func_149711_c(1F);
        this.func_149752_b(10);
        this.field_149762_H = field_149769_e;
        func_149647_a(TConstructRegistry.blockTab);
    }

    @Override
    public boolean func_149727_a (World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            CastingChannelLogic tile = (CastingChannelLogic) world.func_147438_o(x, y, z);
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
        if (stack != null && ComparisonHelper.areEquivalent(stack.getItem(), this))
            return false;
        return true;
    }

    @Override
    public void func_149719_a (IBlockAccess world, int x, int y, int z)
    {
        CastingChannelLogic tile = (CastingChannelLogic) world.func_147438_o(x, y, z);
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

        this.func_149676_a(minX, 0.375F, minZ, maxX, 0.625F, maxZ);
    }

    @Override
    public boolean func_149686_d ()
    {
        return false;
    }

    @Override
    public boolean func_149662_c ()
    {
        return false;
    }

    @Override
    public int func_149645_b ()
    {
        return BlockRenderCastingChannel.renderID;
    }

    @Override
    public void func_149651_a (IIconRegister iconRegister)
    {
        this.field_149761_L = iconRegister.registerIcon("tinker:searedstone");
    }

    @Override
    public TileEntity func_149915_a (World var1, int var2)
    {
        return new CastingChannelLogic();
    }

}