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
import slimeknights.tconstruct.shared.inventory.ConfigurableInvWrapperCapability;
import slimeknights.tconstruct.shared.tileentity.TileTable;
import slimeknights.tconstruct.tools.common.client.GuiPartBuilder;
import slimeknights.tconstruct.tools.common.inventory.ContainerPartBuilder;

public class TilePartBuilder extends TileTable implements IInventoryGui {

  public TilePartBuilder() {
    super("gui.partbuilder.name", 4);
    this.itemHandler = new ConfigurableInvWrapperCapability(this, false, false);
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

  @Override
  protected IExtendedBlockState setInventoryDisplay(IExtendedBlockState state) {
    PropertyTableItem.TableItems toDisplay = new PropertyTableItem.TableItems();
    float c = 0.2125f;
    float[] x = new float[]{c, -c, c, -c};
    float[] y = new float[]{-c, -c, c, c};
    for(int i = 0; i < 4; i++) {
      ItemStack stackInSlot = getStackInSlot(i);
      PropertyTableItem.TableItem item = getTableItem(stackInSlot, this.getWorld(), null);
      if(item != null) {
        item.x += x[i];
        item.z += y[i];
        item.s *= 0.46875f;

        // correct itemblock because scaling
        if(stackInSlot.getItem() instanceof ItemBlock && !(Block.getBlockFromItem(stackInSlot.getItem())  instanceof BlockPane)) {
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
