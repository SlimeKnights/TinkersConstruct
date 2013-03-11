package mods.tinker.tconstruct.logic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public class LiquidTextureLogic extends TileEntity
{
	int texturePos;

	public boolean canUpdate()
    {
        return false;
    }
	
	public void setTexturePos (int tex)
	{
		texturePos = tex;
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

	public int getTexturePos ()
	{
		int tex = (texturePos % 5) * 3;
		tex += (texturePos / 5) * 32;
		return tex;
	}

	public int getLiquidType ()
	{
		return texturePos;
	}

	public void readFromNBT (NBTTagCompound tags)
	{
		super.readFromNBT(tags);
		texturePos = tags.getInteger("Texture");
	}

	public void writeToNBT (NBTTagCompound tags)
	{
		super.writeToNBT(tags);
		tags.setInteger("Texture", texturePos);
	}

	/*public boolean shouldRefresh (int oldID, int newID, int oldMeta, int newMeta, World world, int x, int y, int z)
	{
		if (newID == TContent.metalFlowing.blockID || newID == TContent.metalStill.blockID)
		{
			System.out.println("Not refreshing");
			return false;
		}
		else
			return true;
	}*/

	@Override
	public Packet getDescriptionPacket ()
	{
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public void onDataPacket (INetworkManager net, Packet132TileEntityData packet)
	{
		readFromNBT(packet.customParam1);
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}
}
