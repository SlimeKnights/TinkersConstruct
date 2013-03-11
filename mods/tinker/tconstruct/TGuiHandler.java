package mods.tinker.tconstruct;

import java.util.WeakHashMap;

import mods.tinker.common.InventoryLogic;
import mods.tinker.tconstruct.client.TProxyClient;
import mods.tinker.tconstruct.client.gui.FrypanGui;
import mods.tinker.tconstruct.client.gui.GuiManual;
import mods.tinker.tconstruct.client.gui.PartCrafterGui;
import mods.tinker.tconstruct.client.gui.PatternChestGui;
import mods.tinker.tconstruct.client.gui.PatternShaperGui;
import mods.tinker.tconstruct.client.gui.SmelteryGui;
import mods.tinker.tconstruct.client.gui.ToolStationGui;
import mods.tinker.tconstruct.container.SmelteryContainer;
import mods.tinker.tconstruct.logic.FrypanLogic;
import mods.tinker.tconstruct.logic.PartCrafterLogic;
import mods.tinker.tconstruct.logic.PatternChestLogic;
import mods.tinker.tconstruct.logic.PatternShaperLogic;
import mods.tinker.tconstruct.logic.SmelteryLogic;
import mods.tinker.tconstruct.logic.ToolStationLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class TGuiHandler implements IGuiHandler
{
	public static int stationID = 0;
	public static int partID = 1;
	public static int pchestID = 2;
	public static int pshaperID = 3;
	public static int frypanID = 4;

	public static int smeltery = 7;
	public static int manualGui = -1;
	
	public static WeakHashMap<String, Container> openContainers = new WeakHashMap<String, Container>();

	@Override
	public Object getServerGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if (ID < 0)
			return null;
		
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile != null && tile instanceof InventoryLogic)
		{
			Object o = ((InventoryLogic) tile).getGuiContainer(player.inventory, world, x, y, z);
			if (o instanceof SmelteryContainer)
			{
				openContainers.put(player.username, (Container) o);
			}
			return o;
		}
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
		{
			ItemStack stack = player.getCurrentEquippedItem();
			return new GuiManual(stack, TProxyClient.getManualFromStack(stack));
		}
		return null;
	}

}
