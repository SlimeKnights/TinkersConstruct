package powercrystals.minefactoryreloaded.api.rednet;

import net.minecraft.nbt.NBTTagCompound;

public interface IRedNetLogicCircuit
{
	public int getInputCount();
	
	public int getOutputCount();
	
	public int[] recalculateOutputValues(long worldTime, int[] inputValues);
	
	public String getUnlocalizedName();
	public String getInputPinLabel(int pin);
	public String getOutputPinLabel(int pin);
	
	public void readFromNBT(NBTTagCompound tag);
	public void writeToNBT(NBTTagCompound tag);
}
