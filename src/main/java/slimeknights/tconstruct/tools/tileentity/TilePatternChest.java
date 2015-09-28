package slimeknights.tconstruct.tools.tileentity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.shared.tileentity.TileTable;
import slimeknights.tconstruct.tools.client.GuiPatternChest;
import slimeknights.tconstruct.tools.inventory.ContainerPatternChest;
import slimeknights.mantle.common.IInventoryGui;

public class TilePatternChest extends TileTable implements IInventoryGui {

  public TilePatternChest() {
    super("gui.patternChest.name", 30, 1);
  }

  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerPatternChest(3, 10, inventoryplayer, this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiPatternChest(inventoryplayer, world, pos, this);
  }
}
