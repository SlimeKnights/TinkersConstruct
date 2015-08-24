package slimeknights.tconstruct.tools.tileentity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.tileentity.TileTable;
import slimeknights.tconstruct.tools.inventory.ContainerToolStation;
import slimeknights.mantle.IInventoryGui;
import slimeknights.tconstruct.tools.client.GuiToolStation;

public class TileToolStation extends TileTable implements IInventoryGui {

  public TileToolStation() {
    super("gui.toolStation.name", 6);
  }

  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerToolStation(inventoryplayer, this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiToolStation(inventoryplayer, world, pos, this);
  }
}
