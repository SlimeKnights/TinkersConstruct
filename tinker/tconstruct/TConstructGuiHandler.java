package tinker.tconstruct;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tinker.common.InventoryLogic;
import tinker.tconstruct.client.gui.FrypanGui;
import tinker.tconstruct.client.gui.GuiManual;
import tinker.tconstruct.client.gui.PartCrafterGui;
import tinker.tconstruct.client.gui.PatternChestGui;
import tinker.tconstruct.client.gui.PatternShaperGui;
import tinker.tconstruct.client.gui.SmelteryGui;
import tinker.tconstruct.client.gui.ToolStationGui;
import tinker.tconstruct.logic.FrypanLogic;
import tinker.tconstruct.logic.PartCrafterLogic;
import tinker.tconstruct.logic.PatternChestLogic;
import tinker.tconstruct.logic.PatternShaperLogic;
import tinker.tconstruct.logic.SmelteryLogic;
import tinker.tconstruct.logic.ToolStationLogic;
import cpw.mods.fml.common.network.IGuiHandler;

public class TConstructGuiHandler implements IGuiHandler
{
	public static int stationID = 0;
	public static int partID = 1;
	public static int pchestID = 2;
	public static int pshaperID = 3;
	public static int frypanID = 4;

	public static int smeltery = 7;
	public static int manualGui = -1;

	@Override
	public Object getServerGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if (ID < 0)
			return null;
		
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
		if (ID == pshaperID)
			return new PatternShaperGui(player.inventory, (PatternShaperLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
		if (ID == manualGui)
			return new GuiManual(player.getCurrentEquippedItem());
		return null;
	}

}
