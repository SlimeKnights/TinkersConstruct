package slimeknights.tconstruct.tools.tileentity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.common.tileentity.TileTable;
import slimeknights.tconstruct.tools.client.GuiPartBuilder;
import slimeknights.tconstruct.library.mantle.IInventoryGui;
import slimeknights.tconstruct.tools.inventory.ContainerPartBuilder;

public class TilePartBuilder extends TileTable implements IInventoryGui {

  public TilePartBuilder() {
    super("gui.partBuilder.name", 4);
  }

  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerPartBuilder(inventoryplayer, this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiPartBuilder(inventoryplayer, world, pos, this);
  }
}
