package tinker.tconstruct.entity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class FakeBlockEntity extends Entity
	implements IEntityAdditionalSpawnData
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
	public void writeSpawnData (ByteArrayDataOutput data)
	{
		
	}

	@Override
	public void readSpawnData (ByteArrayDataInput data)
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

}
