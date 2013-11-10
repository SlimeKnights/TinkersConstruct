package tconstruct.items.blocks;

import java.util.List;

import tconstruct.blocks.logic.SignalTerminalLogic;
import tconstruct.library.TConstructRegistry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class SignalTerminalItem extends ItemBlock
{
    public static final String blockType[] = { "signalterminal" };

    public SignalTerminalItem(int id)
    {
        super(id);
        this.maxStackSize = 64;
        this.setHasSubtypes(false);
    }

    public int getMetadata (int meta)
    {
        return meta;
    }

    public String getUnlocalizedName (ItemStack itemstack)
    {
        int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockType.length - 1);
        return (new StringBuilder()).append("tile.").append(blockType[pos]).toString();
    }

    @Override
    public boolean placeBlockAt (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canPlaceItemBlockOnSide (World world, int x, int y, int z, int side, EntityPlayer entityPlayer, ItemStack itemStack)
    {
        if (super.canPlaceItemBlockOnSide(world, x, y, z, side, entityPlayer, itemStack) || _canPlaceItemBlockOnSide(world, x, y, z, side))
        {
            return true;
        }

        return false;
    }

    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        int tmpX = x;
        int tmpY = y;
        int tmpZ = z;

        switch (side)
        {
        case 0:
            tmpY += -1;
            break;
        case 1:
            tmpY += 1;
            break;
        case 2:
            tmpZ += -1;
            break;
        case 3:
            tmpZ += 1;
            break;
        case 4:
            tmpX += -1;
            break;
        case 5:
            tmpX += 1;
            break;
        default:
            break;
        }

        int tside = side;
        switch (side)
        {
        case 0: // DOWN
        case 1: // UP
        case 4: // EAST
        case 5: // WEST
            tside = ForgeDirection.OPPOSITES[side];
            break;
        default:
            tside = side;
            break;
        }
        NBTTagCompound data = new NBTTagCompound();
        stack.stackTagCompound = data;
        data.setInteger("connectedSide", tside);

        if (super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ))
        {
            return true;
        }

        if (!(_canPlaceItemBlockOnSide(world, x, y, z, side)))
        {
            return false;
        }

        TileEntity te = world.getBlockTileEntity(tmpX, tmpY, tmpZ);

        ((SignalTerminalLogic) te).addPendingSide(tside);
        ((SignalTerminalLogic) te).connectPending();

        stack.stackTagCompound = null;

        --stack.stackSize;

        world.markBlockForRenderUpdate(x, y, z);

        return true;

    }

    private boolean _canPlaceItemBlockOnSide (World world, int x, int y, int z, int side)
    {
        int tmpX = x;
        int tmpY = y;
        int tmpZ = z;

        switch (side)
        {
        case 0:
            tmpY += -1;
            break;
        case 1:
            tmpY += 1;
            break;
        case 2:
            tmpZ += -1;
            break;
        case 3:
            tmpZ += 1;
            break;
        case 4:
            tmpX += -1;
            break;
        case 5:
            tmpX += 1;
            break;
        default:
            break;
        }

        if (world.getBlockId(tmpX, tmpY, tmpZ) == this.getBlockID())
        {
            TileEntity te = world.getBlockTileEntity(tmpX, tmpY, tmpZ);
            if (te == null || !(te instanceof SignalTerminalLogic))
            {
                return false;
            }

            return true;
        }

        return false;
    }
}
