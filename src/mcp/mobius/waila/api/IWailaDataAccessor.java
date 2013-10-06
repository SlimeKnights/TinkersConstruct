package mcp.mobius.waila.api;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public interface IWailaDataAccessor {
	World        getWorld();
	EntityPlayer getPlayer();
	Block        getBlock();
	int          getBlockID();
	int          getMetadata();
	TileEntity           getTileEntity();
	MovingObjectPosition getPosition();
	NBTTagCompound       getNBTData();
	int          getNBTInteger(NBTTagCompound tag, String keyname);
}
