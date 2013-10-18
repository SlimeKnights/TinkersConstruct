package appeng.api.me.tiles;

/**
 * Allows you to drain energy via ME Cables, if you only care if the grid is powered you only need IGridTileEntity.
 */
public abstract interface IGridMachine extends IGridTileEntity
{
    /**
     *  how much power this entity drains to run constantly.
     */
    public abstract float getPowerDrainPerTick();
	
}
