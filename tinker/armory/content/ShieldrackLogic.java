package tinker.armory.content;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

public class ShieldrackLogic extends DisplayLogic
{

	public ShieldrackLogic()
	{
		super(3);
	}

	@Override
	public String getInvName ()
	{
		return "armory.shieldrack";
	}

	@Override
	public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
	{
		return null;
	}
	
}
