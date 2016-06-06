package slimeknights.tconstruct.tools.tileentity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import slimeknights.mantle.common.IInventoryGui;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.shared.tileentity.TileTable;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.client.GuiStencilTable;
import slimeknights.tconstruct.tools.inventory.ContainerStencilTable;

public class TileStencilTable extends TileTable implements IInventoryGui {

  public TileStencilTable() {
    super("gui.stenciltable.name", 1);
  }

  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerStencilTable(inventoryplayer, this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiStencilTable(inventoryplayer, world, pos, this);
  }

  @Override
  public boolean isItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {
    return itemstack != null && itemstack.getItem() == TinkerTools.pattern && (Config.reuseStencil || !itemstack.hasTagCompound());
  }
}
