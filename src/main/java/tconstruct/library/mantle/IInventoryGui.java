package tconstruct.library.mantle;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface IInventoryGui {
  Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos);
  GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos);
}
