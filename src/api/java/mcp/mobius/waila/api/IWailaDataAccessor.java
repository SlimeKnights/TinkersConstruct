package mcp.mobius.waila.api;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/* The Accessor is used to get some basic data out of the game without having to request
 * direct access to the game engine.
 * It will also return things that are unmodified by the overriding systems (like getWailaStack).
 */

public interface IWailaDataAccessor {
		
	World        		 getWorld();
	EntityPlayer 		 getPlayer();
	Block        		 getBlock();
	int          		 getBlockID();
	int          		 getMetadata();
	TileEntity           getTileEntity();
	MovingObjectPosition getPosition();
	Vec3                 getRenderingPosition();
	NBTTagCompound       getNBTData();
	int                  getNBTInteger(NBTTagCompound tag, String keyname);
	double               getPartialFrame();
	ForgeDirection       getSide();
	ItemStack            getStack();
}
