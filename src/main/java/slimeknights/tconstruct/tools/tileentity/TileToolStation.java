package slimeknights.tconstruct.tools.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;
import slimeknights.tconstruct.shared.tileentity.TileTable;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;
import slimeknights.tconstruct.tools.client.GuiButtonRepair;
import slimeknights.tconstruct.tools.inventory.ContainerToolStation;
import slimeknights.mantle.common.IInventoryGui;
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

  protected IExtendedBlockState setInventoryDisplay(IExtendedBlockState state) {
    PropertyTableItem.TableItems toDisplay = new PropertyTableItem.TableItems();

    ToolBuildGuiInfo info = GuiButtonRepair.info;
    if(Minecraft.getMinecraft().currentScreen instanceof GuiToolStation) {
      info = ((GuiToolStation) Minecraft.getMinecraft().currentScreen).currentInfo;
    }
    float s = 0.46875f;

    for(int i = 0; i < info.positions.size(); i++) {
      PropertyTableItem.TableItem item = getTableItem(getStackInSlot(i));
      if(item != null) {
        item.x = (33 - info.positions.get(i).getX())/61f;
        item.z = (42 - info.positions.get(i).getY())/61f;
        item.s *= s;

        if(i == 0 || info != GuiButtonRepair.info) {
          item.s *= 1.3f;
        }

        // correct itemblock because scaling
        if(getStackInSlot(i).getItem() instanceof ItemBlock) {
          item.y = -(1f - item.s)/2f;
        }

        //item.s *= 2/5f;
        toDisplay.items.add(item);
      }
    }

    // add inventory if needed
    return state.withProperty(BlockTable.INVENTORY, toDisplay);
  }
}
