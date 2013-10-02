package appeng.api;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import appeng.api.events.GridTileUnloadEvent;
import appeng.api.exceptions.AppEngTileMissingException;
import appeng.api.me.tiles.IGridTileEntity;

public class TileRef<T> extends WorldCoord {
	
	//private int dimension;
	private World w;
	boolean wasGrid;
	
	public TileRef( TileEntity gte ) {
		super( gte.xCoord, gte.yCoord, gte.zCoord );
		TileEntity te = gte;
		wasGrid = te instanceof IGridTileEntity;
		w = te.worldObj;
		if ( te.worldObj == null )
			throw new RuntimeException("Tile has no world.");
	}
	
	@SuppressWarnings("unchecked")
	public T getTile() throws AppEngTileMissingException
	{
		// there might be a possible tick where we have TileRefs for unloaded tiles?
		if ( w.getChunkProvider().chunkExists(x >> 4, z >> 4) )
		{
			TileEntity te = w.getBlockTileEntity( x, y, z );
			if ( te != null )
			{
				try
				{
					T ttt = (T) te; // I have no idea if this causes the exception or not...
					return ttt;
				}
				catch( ClassCastException err )
				{
					
				}
			}
		}
		
		/**
		 * was this a grid tile? if so inform the grid enum that something has derped.
		 */
		if ( wasGrid )
			MinecraftForge.EVENT_BUS.post( new GridTileUnloadEvent( null, w, this ) );
		
		throw new AppEngTileMissingException( w, x,y,z);
	}

	public DimentionalCoord getCoord() {
		return new DimentionalCoord( w, x, y, z );
	}
			
};
