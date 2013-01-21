package tinker.tconstruct;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tinker.common.InventoryLogic;
import tinker.tconstruct.client.gui.*;
import tinker.tconstruct.logic.*;
import cpw.mods.fml.common.network.IGuiHandler;

public class TConstructGuiHandler implements IGuiHandler
{
	public static int stationID = 0;
	public static int partID = 1;
	public static int pchestID = 2;
	public static int pshaperID = 3;
	public static int frypanID = 4;

	public static int smeltery = 7;

	@Override
	public Object getServerGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile != null && tile instanceof InventoryLogic)
			return ((InventoryLogic) tile).getGuiContainer(player.inventory, world, x, y, z);
		return null;
	}

	@Override
	public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if (ID == stationID)
			return new ToolStationGui(player.inventory, (ToolStationLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
		if (ID == partID)
			return new PartCrafterGui(player.inventory, (PartCrafterLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
		if (ID == pchestID)
			return new PatternChestGui(player.inventory, (PatternChestLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
		if (ID == frypanID)
			return new FrypanGui(player.inventory, (FrypanLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
		if (ID == smeltery)
			return new SmelteryGui(player.inventory, (SmelteryLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
		return null;
	}

}
