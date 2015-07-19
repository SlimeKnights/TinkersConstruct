package tconstruct.tools.tileentity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import org.apache.commons.lang3.ObjectUtils;

import tconstruct.common.tileentity.TileTable;
import tconstruct.library.mantle.IInventoryGui;
import tconstruct.tools.client.GuiCraftingStation;
import tconstruct.tools.inventory.ContainerCraftingStation;

public class TileCraftingStation extends TileTable implements IInventoryGui {

  public TileCraftingStation() {
    super("container.craftingStation", 9);
  }

  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerCraftingStation(inventoryplayer, this);
  }

  @Override
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiCraftingStation(inventoryplayer, world, pos, this);
  }

  /** Searches for a sidechest to display in the UI */
  public TileEntityChest detectChest() {
    return ObjectUtils.firstNonNull(detectChest(pos.north()),
                                    detectChest(pos.east()),
                                    detectChest(pos.south()),
                                    detectChest(pos.west()));
  }

  private TileEntityChest detectChest(BlockPos pos) {
    TileEntity te = this.worldObj.getTileEntity(pos);

    if(te != null && te instanceof TileEntityChest) {
      return (TileEntityChest) te;
    }
    return null;
  }
}
