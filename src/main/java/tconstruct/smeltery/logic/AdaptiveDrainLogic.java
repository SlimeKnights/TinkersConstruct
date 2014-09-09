package tconstruct.smeltery.logic;

import mantle.blocks.abstracts.MultiServantLogic;
import mantle.blocks.iface.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

public class AdaptiveDrainLogic extends MultiServantLogic implements IFluidHandler, IFacingLogic// , ISwitchableMaster
{
    byte direction;

    public boolean canUpdate ()
    {
        return false;
    }

    @Override
    public boolean setPotentialMaster (IMasterLogic master, World world, int x, int y, int z)
    {
        // TConstruct.logger.info("Master: "+master);
        return (master instanceof AdaptiveSmelteryLogic || master instanceof AdaptiveDrainLogic) && !getHasMaster();
    }

    @Override
    public int fill (ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (hasValidMaster() && canDrain(from, null))
        {
            AdaptiveSmelteryLogic smeltery = (AdaptiveSmelteryLogic) worldObj.getTileEntity(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            return smeltery.fill(from, resource, doFill);
        }
        return 0;
    }

    @Override
    public FluidStack drain (ForgeDirection from, int maxDrain, boolean doDrain)
    {
        // TConstruct.logger.info("Attempting drain " + hasValidMaster());
        if (hasValidMaster() && canDrain(from, null))
        {
            AdaptiveSmelteryLogic smeltery = (AdaptiveSmelteryLogic) worldObj.getTileEntity(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            //  TConstruct.logger.info("Found master");
            return smeltery.drain(from, maxDrain, doDrain);
        }
        return null;
    }

    @Override
    public FluidStack drain (ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        if (hasValidMaster() && canDrain(from, null))
        {
            AdaptiveSmelteryLogic smeltery = (AdaptiveSmelteryLogic) worldObj.getTileEntity(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            return smeltery.drain(from, resource, doDrain);
        }
        return null;
    }

    @Override
    public boolean canFill (ForgeDirection from, Fluid fluid)
    {
        if (hasValidMaster())
        {
            AdaptiveSmelteryLogic smeltery = (AdaptiveSmelteryLogic) worldObj.getTileEntity(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            return smeltery.getFillState() < 2;
        }
        return false;
    }

    @Override
    public boolean canDrain (ForgeDirection from, Fluid fluid)
    {
        if (hasValidMaster())
        {
            AdaptiveSmelteryLogic smeltery = (AdaptiveSmelteryLogic) worldObj.getTileEntity(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            return smeltery.getFillState() > 0;
        }
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo (ForgeDirection from)
    {
        if (hasValidMaster() && (from == getForgeDirection() || from == getForgeDirection().getOpposite() || from == ForgeDirection.UNKNOWN))
        {
            AdaptiveSmelteryLogic smeltery = (AdaptiveSmelteryLogic) worldObj.getTileEntity(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            return smeltery.getTankInfo(ForgeDirection.UNKNOWN);
        }
        return null;
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

    }

    @Override
    public void setDirection (float yaw, float pitch, EntityLivingBase player)
    {
        if (pitch > 45)
            direction = 1;
        else if (pitch < -45)
            direction = 0;
        else
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
    }

    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        direction = tags.getByte("Direction");
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        tags.setByte("Direction", direction);
    }

    /* Packets */
    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity packet)
    {
        readFromNBT(packet.func_148857_g());
        worldObj.func_147479_m(xCoord, yCoord, zCoord);
    }
}