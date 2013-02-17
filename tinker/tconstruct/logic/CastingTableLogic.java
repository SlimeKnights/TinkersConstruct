package tinker.tconstruct.logic;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import tinker.common.InventoryLogic;

public class CastingTableLogic extends InventoryLogic
{

	public CastingTableLogic()
	{
		super(2);
	}

	@Override
	public String getInvName () //Not a gui block
	{
		return null;
	}

	@Override
	public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z) //Not a gui block
	{
		return null;
	}

}
