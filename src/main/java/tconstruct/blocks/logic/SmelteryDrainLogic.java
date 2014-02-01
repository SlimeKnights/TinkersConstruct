package tconstruct.blocks.logic;

import mantle.blocks.abstracts.MultiServantLogic;
import mantle.blocks.iface.IFacingLogic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

public class SmelteryDrainLogic extends MultiServantLogic implements IFluidHandler, IFacingLogic
{
    byte direction;

    public boolean canUpdate ()
    {
        return false;
    }

    @Override
    public int fill (ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (hasValidMaster() && resource != null && canFill(from, resource.getFluid()))
        {
            if (doFill)
            {
                SmelteryLogic smeltery = (SmelteryLogic) field_145850_b.func_147438_o(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
                return smeltery.fill(resource, doFill);
            }
            else
            {
                return resource.amount;
            }
        }
        else
        {
            return 0;
        }
    }

    @Override
    public FluidStack drain (ForgeDirection from, int maxDrain, boolean doDrain)
    {
        if (hasValidMaster() && canDrain(from, null))
        {
            SmelteryLogic smeltery = (SmelteryLogic) field_145850_b.func_147438_o(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            return smeltery.drain(maxDrain, doDrain);
        }
        else
        {
            return null;
        }
    }

    @Override
    public FluidStack drain (ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean canFill (ForgeDirection from, Fluid fluid)
    {
        return true;
        //return from == getForgeDirection();//.getOpposite();
    }

    @Override
    public boolean canDrain (ForgeDirection from, Fluid fluid)
    {
        // Check that the drain is coming from the from the front of the block 
        // and that the fluid to be drained is in the smeltery.
        boolean containsFluid = fluid == null;
        if (fluid != null)
        {
            SmelteryLogic smeltery = (SmelteryLogic) field_145850_b.func_147438_o(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            for (FluidStack fstack : smeltery.moltenMetal)
            {
                if (fstack.fluidID == fluid.getID())
                {
                    containsFluid = true;
                    break;
                }
            }
        }
        //return from == getForgeDirection().getOpposite() && containsFluid;
        return containsFluid;
    }

    @Override
    public FluidTankInfo[] getTankInfo (ForgeDirection from)
    {
        if (hasValidMaster() && (from == getForgeDirection() || from == getForgeDirection().getOpposite() || from == ForgeDirection.UNKNOWN))
        {
            SmelteryLogic smeltery = (SmelteryLogic) field_145850_b.func_147438_o(getMasterPosition().x, getMasterPosition().y, getMasterPosition().z);
            return smeltery.getMultiTankInfo();
            //return new FluidTankInfo[] { smeltery.getInfo() };
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

    public void func_145839_a (NBTTagCompound tags)
    {
        super.func_145839_a(tags);
        direction = tags.getByte("Direction");
    }

    @Override
    public void func_145841_b (NBTTagCompound tags)
    {
        super.func_145841_b(tags);
        tags.setByte("Direction", direction);
    }

    /* Packets */
    @Override
    public Packet func_145844_m ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        func_145841_b(tag);
        return new S35PacketUpdateTileEntity(field_145851_c, field_145848_d, field_145849_e, 1, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity packet)
    {
        func_145839_a(packet.func_148857_g());
        field_145850_b.func_147479_m(field_145851_c, field_145848_d, field_145849_e);
    }
}
