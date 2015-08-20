package slimeknights.tconstruct.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import slimeknights.tconstruct.library.mantle.IInventoryGui;

public class GuiHandler implements IGuiHandler {

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof IInventoryGui) {
      return ((IInventoryGui) te).createContainer(player.inventory, world, new BlockPos(x, y, z));
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof IInventoryGui) {
      return ((IInventoryGui) te).createGui(player.inventory, world, new BlockPos(x, y, z));
    }
    return null;
  }
}
