package tconstruct.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class FakeBlockEntity extends Entity implements IEntityAdditionalSpawnData
{

    public FakeBlockEntity(World world)
    {
        super(world);
        preventEntitySpawning = true;
    }

    @Override
    protected void entityInit ()
    {

    }

    @Override
    protected void readEntityFromNBT (NBTTagCompound var1)
    {

    }

    @Override
    protected void writeEntityToNBT (NBTTagCompound var1)
    {

    }

    @Override
    public void writeSpawnData (ByteBuf buffer)
    {
        
    }

    @Override
    public void readSpawnData (ByteBuf additionalData)
    {
        
    }

}
