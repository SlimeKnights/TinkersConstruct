package tconstruct.blocks.logic;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import tconstruct.blocks.EnderFluidBlock;
import tconstruct.library.util.CoordTuple;
import tconstruct.util.EnderWarpCoords;

public class EnderWarpLogic extends TileEntity
{
    protected int frequency = -1;

    public boolean canUpdate ()
    {
        return false;
    }

    @Override
    public void invalidate ()
    {
        super.invalidate();
        EnderWarpCoords.unregisterEnderporter(this, frequency);
    }

    @Override
    public void validate ()
    {
        super.validate();
        if (frequency == -1)
            frequency = getNewFrequency();
        EnderWarpCoords.registerEnderporter(this, frequency);
    }

    public void recalculateFrequency ()
    {
        int newFrequency = getNewFrequency();
        if (newFrequency != frequency)
        {
            EnderWarpCoords.changeFrequency(this, newFrequency, frequency);
            frequency = newFrequency;
        }
    }

    int getNewFrequency ()
    {
        int north = worldObj.getBlockId(xCoord, yCoord, zCoord + 1);
        int south = worldObj.getBlockId(xCoord, yCoord, zCoord - 1);
        int east = worldObj.getBlockId(xCoord + 1, yCoord, zCoord);
        int west = worldObj.getBlockId(xCoord - 1, yCoord, zCoord);

        int addition = 0;
        if (north != 0)
            addition += 1;
        if (south != 0)
            addition += 1;
        if (east != 0)
            addition += 1;
        if (west != 0)
            addition += 1;

        int newFrequency = addition != 0 ? north + south + east + west + (addition - 1) * 4096 : 0;
        return newFrequency;
    }

    public void teleport ()
    {
        CoordTuple coord = EnderWarpCoords.getClosestPortal(this, frequency, 4192);
        if (coord != null)
        {
            AxisAlignedBB axisalignedbb = AxisAlignedBB.getAABBPool().getAABB(this.xCoord, this.yCoord, this.zCoord, (this.xCoord + 1), (this.yCoord + 3), (this.zCoord + 1));
            List list = this.worldObj.getEntitiesWithinAABB(Entity.class, axisalignedbb);
            for (Object o : list)
            {
                Entity entity = (Entity) o;
                if (entity instanceof EntityLivingBase)
                {
                    EnderFluidBlock.teleportEntityTo((EntityLivingBase) entity, coord.x, coord.y + 1, coord.z);
                }
                else
                {
                    entity.setPosition(coord.x, coord.y, coord.z);
                    entity.worldObj.playSoundEffect(coord.x, coord.y + 1, coord.z, "mob.endermen.portal", 1.0F, 1.0F);
                    entity.playSound("mob.endermen.portal", 1.0F, 1.0F);
                }
            }

            for (int i = 0; i < 12; i++)
            {
                worldObj.spawnParticle("portal", xCoord + worldObj.rand.nextFloat(), yCoord + worldObj.rand.nextFloat() * 2, zCoord + worldObj.rand.nextFloat(), 0, 0, 0);
                worldObj.spawnParticle("portal", coord.x + worldObj.rand.nextFloat(), coord.y + worldObj.rand.nextFloat() * 2, coord.z + worldObj.rand.nextFloat(), 0, 0, 0);
            }
        }
    }

    @Override
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
        frequency = tags.getInteger("Frequency");
    }

    @Override
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
        tags.setInteger("Frequency", frequency);
    }
}
