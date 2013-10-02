package tconstruct.library.component;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class LogicComponent
{
    protected World world;
    
    public void setWorld(World world)
    {
        this.world = world;
    }
    
    public void readFromNBT (NBTTagCompound tags)
    {
        
    }
    
    public void writeToNBT (NBTTagCompound tags)
    {
        
    }

    public void readNetworkNBT (NBTTagCompound tags)
    {
        
    }
    
    public void writeNetworkNBT (NBTTagCompound tags)
    {
        
    }    
}