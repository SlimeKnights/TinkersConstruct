package tconstruct.blocks.logic;

import tconstruct.TConstruct;
import tconstruct.library.util.IActiveLogic;
import mantle.blocks.iface.IFacingLogic;;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class FaucetLogic extends TileEntity implements IFacingLogic, IActiveLogic, IFluidHandler
{
    byte direction;
    boolean active;
    public FluidStack liquid;

    public boolean activateFaucet ()
    {
        if (liquid == null && active)
        {
            int x = xCoord, z = zCoord;
            switch (getRenderDirection())
            {
            case 2:
                z++;
                break;
            case 3:
                z--;
                break;
            case 4:
                x++;
                break;
            case 5:
                x--;
                break;
            }

            TileEntity drainte = worldObj.getBlockTileEntity(x, yCoord, z);
            TileEntity tankte = worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord);

            if (drainte != null && drainte instanceof IFluidHandler && tankte != null && tankte instanceof IFluidHandler)
            {
                FluidStack templiquid = ((IFluidHandler) drainte).drain(getForgeDirection(), TConstruct.ingotLiquidValue, false);
                if (templiquid != null)
                {
                    int drained = ((IFluidHandler) tankte).fill(ForgeDirection.UP, templiquid, false);
                    if (drained > 0)
                    {
                        liquid = ((IFluidHandler) drainte).drain(getForgeDirection(), drained, true);
                        ((IFluidHandler) tankte).fill(ForgeDirection.UP, liquid, true);
                        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                        return true;
                    }
                    else
                    {
                        return false;
                    }

                }
            }
        }
        return false;
    }

    @Override
    public void updateEntity ()
    {
        if (liquid != null)
        {
            liquid.amount -= TConstruct.liquidUpdateAmount;
            if (liquid.amount <= 0)
            {
                liquid = null;
                if (!activateFaucet())
                {
                    active = false;
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                }
            }
        }
    }

    @Override
    public byte getRenderDirection ()
    {
        return direction;
    }

    @Override
    public ForgeDirection getForgeDirection ()
    {
        return ForgeDirection.VALID_DIRECTIONS[direction];
    }

    @Override
    public void setDirection (int side)
    {
        //direction = (byte) side;
    }

    @Override
    public void setDirection (float yaw, float pitch, EntityLivingBase player)
    {
        int facing = MathHelper.floor_double((double) (yaw / 360) + 0.5D) & 3;
        switch (facing)
        {
        case 0:
            direction = 2;
            break;

        case 1:
            direction = 5;
            break;

        case 2:
            direction = 3;
            break;

        case 3:
            direction = 4;
            break;
        }
    }

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        readCustomNBT(tags);
    }

    public void readCustomNBT (NBTTagCompound tags)
    {
        direction = tags.getByte("Direction");
        if (tags.getBoolean("hasLiquid"))
        {
            this.liquid = FluidStack.loadFluidStackFromNBT(tags.getCompoundTag("Fluid"));
        }
        else
            this.liquid = null;
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        writeCustomNBT(tags);
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        tags.setByte("Direction", direction);
        tags.setBoolean("hasLiquid", liquid != null);
        if (liquid != null)
        {
            NBTTagCompound nbt = new NBTTagCompound();
            liquid.writeToNBT(nbt);
            tags.setCompoundTag("Fluid", nbt);
        }
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readCustomNBT(packet.data);
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public boolean getActive ()
    {
        return active;
    }

    @Override
    public void setActive (boolean flag)
    {
        if (!active)
        {
            active = true;
            activateFaucet();
        }
        else
        {
            active = false;
        }
    }

    @Override
    public int fill (ForgeDirection from, FluidStack resource, boolean doFill)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public FluidStack drain (ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FluidStack drain (ForgeDirection from, int maxDrain, boolean doDrain)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean canFill (ForgeDirection from, Fluid fluid)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canDrain (ForgeDirection from, Fluid fluid)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo (ForgeDirection from)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
