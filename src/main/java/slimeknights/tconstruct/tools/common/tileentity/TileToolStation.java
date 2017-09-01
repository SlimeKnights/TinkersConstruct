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
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;
import slimeknights.tconstruct.shared.inventory.ConfigurableInvWrapperCapability;
import slimeknights.tconstruct.shared.tileentity.TileTable;
import slimeknights.tconstruct.tools.common.client.GuiButtonRepair;
import slimeknights.tconstruct.tools.common.client.GuiToolStation;
import slimeknights.tconstruct.tools.common.inventory.ContainerToolStation;

public class TileToolStation extends TileTable implements IInventoryGui {

  public TileToolStation() {
    super("gui.toolstation.name", 6);
    this.itemHandler = new ConfigurableInvWrapperCapability(this, false, false);
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

  @Override
  protected IExtendedBlockState setInventoryDisplay(IExtendedBlockState state) {
    PropertyTableItem.TableItems toDisplay = new PropertyTableItem.TableItems();

    ToolBuildGuiInfo info = GuiButtonRepair.info;
    /* Disabled for now
    // todo: evaluate this again
    if(Minecraft.getMinecraft().currentScreen instanceof GuiToolStation) {
      info = ((GuiToolStation) Minecraft.getMinecraft().currentScreen).currentInfo;
    }*/
    float s = 0.46875f;

    for(int i = 0; i < info.positions.size(); i++) {
      ItemStack stackInSlot = getStackInSlot(i);
      PropertyTableItem.TableItem item = getTableItem(stackInSlot, this.getWorld(), null);
      if(item != null) {
        item.x = (33 - info.positions.get(i).getX()) / 61f;
        item.z = (42 - info.positions.get(i).getY()) / 61f;
        item.s *= s;

        if(i == 0 || info != GuiButtonRepair.info) {
          item.s *= 1.3f;
        }

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
