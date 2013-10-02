package tconstruct.blocks.logic;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import tconstruct.TConstruct;
import tconstruct.blocks.component.SmelteryComponent;
import tconstruct.blocks.component.SmelteryScan;
import tconstruct.common.TContent;
import tconstruct.library.blocks.AdaptiveInventoryLogic;
import tconstruct.library.component.IComponentHolder;
import tconstruct.library.component.LogicComponent;
import tconstruct.library.component.MultiFluidTank;
import tconstruct.library.util.IActiveLogic;
import tconstruct.library.util.IMasterLogic;
import tconstruct.library.util.IServantLogic;

public class AdaptiveSmelteryLogic extends AdaptiveInventoryLogic implements IActiveLogic, IMasterLogic, IComponentHolder
{
    byte direction;
    SmelteryScan structure = new SmelteryScan(this, TContent.smeltery, TContent.lavaTank);
    MultiFluidTank multitank = new MultiFluidTank();
    SmelteryComponent smeltery = new SmelteryComponent(this, structure, multitank, 800);

    int tick = 0;

    public void updateEntity ()
    {
        tick++;
        if (tick % 4 == 0)
            smeltery.heatItems();

        if (tick % 20 == 0)
        {
            if (!structure.isComplete())
            {
                structure.checkValidStructure();
                if (structure.isComplete())
                {
                    smeltery.adjustSize(structure.getAirSize(), true);
                    multitank.setCapacity(structure.getAirSize() * (TConstruct.ingotLiquidValue * 18));
                }
            }
            else
            {
                smeltery.update();
            }
        }

        if (tick == 60)
        {
            tick = 0;
        }
    }

    @Override
    public void setWorldObj (World world)
    {
        super.setWorldObj(world);
        structure.setWorld(world);
        smeltery.setWorld(world);
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
    public boolean getActive ()
    {
        return structure.isComplete();
    }

    @Override
    public void setActive (boolean flag)
    {

    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public String getDefaultName ()
    {
        return "crafters.Smeltery";
    }

    @Override
    public List<LogicComponent> getComponents ()
    {
        ArrayList<LogicComponent> ret = new ArrayList<LogicComponent>(3);
        ret.add(structure);
        ret.add(multitank);
        ret.add(smeltery);
        return ret;
    }

    @Override
    public void notifyChange (IServantLogic servant, int x, int y, int z)
    {

    }

    @Override
    public void placeBlock (EntityLivingBase entity, ItemStack itemstack)
    {
        structure.checkValidStructure();
        if (structure.isComplete())
        {
            smeltery.adjustSize(structure.getAirSize(), true);
            multitank.setCapacity(structure.getAirSize() * (TConstruct.ingotLiquidValue * 18));
        }
    }

    @Override
    public void removeBlock ()
    {
        structure.cleanup();
    }

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);

        structure.readFromNBT(tags);
        multitank.readFromNBT(tags);
        smeltery.readFromNBT(tags);
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);

        structure.writeToNBT(tags);
        multitank.writeToNBT(tags);
        smeltery.writeToNBT(tags);
    }

    public void readNetworkNBT (NBTTagCompound tags)
    {
        direction = tags.getByte("Direction");

        structure.readNetworkNBT(tags);
        multitank.readNetworkNBT(tags);
        smeltery.readNetworkNBT(tags);
    }

    public void writeNetworkNBT (NBTTagCompound tags)
    {
        tags.setByte("Direction", direction);

        structure.writeNetworkNBT(tags);
        multitank.writeNetworkNBT(tags);
        smeltery.writeNetworkNBT(tags);
    }

    @Override
    public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
    {
        readNetworkNBT(packet.data);
        worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public Packet getDescriptionPacket ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeNetworkNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }
}
