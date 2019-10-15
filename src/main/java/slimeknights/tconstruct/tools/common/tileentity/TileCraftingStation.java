package slimeknights.tconstruct.tools.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.common.IInventoryGui;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;
import slimeknights.tconstruct.shared.tileentity.TileTable;
import slimeknights.tconstruct.tools.common.client.GuiCraftingStation;
import slimeknights.tconstruct.tools.common.inventory.ContainerCraftingStation;
import slimeknights.tconstruct.tools.common.inventory.CraftingStationItemHandler;

public class TileCraftingStation extends TileTable implements IInventoryGui {

  public TileCraftingStation() {
    super("gui.craftingstation.name", 9);
    this.itemHandler = new CraftingStationItemHandler(this, true, false);
  }

  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerCraftingStation(inventoryplayer, this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiCraftingStation(inventoryplayer, world, pos, this);
  }

  @Override
  protected IExtendedBlockState setInventoryDisplay(IExtendedBlockState state) {
    PropertyTableItem.TableItems toDisplay = new PropertyTableItem.TableItems();

    float s = 0.125f;
    float o = 3f / 16f; // we want to move it 3 pixel in a 16 width texture
    for(int i = 0; i < 9; i++) {
      ItemStack itemStack = getStackInSlot(i);
      PropertyTableItem.TableItem item = getTableItem(itemStack, this.getWorld(), null);
      if(item != null) {
        item.x = +o - (i % 3) * o;
        item.z = +o - (i / 3) * o;
        item.y = -0.5f + s / 32f; // half itemmodel height + move it down to the bottom from the center
        //item.s *= 0.46875f;
        item.s = s;

        // correct itemblock because scaling
        if(itemStack.getItem() instanceof ItemBlock && !(Block.getBlockFromItem(itemStack.getItem())  instanceof BlockPane)) {
          item.y = -(1f - item.s) / 2f;
        }

        //item.s *= 2/5f;
        toDisplay.items.add(item);
      }
    }

    // add inventory if needed
    return state.withProperty(BlockTable.INVENTORY, toDisplay);
  }
}
