package slimeknights.tconstruct.tools.common.tileentity;

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
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.tools.common.client.GuiPartChest;
import slimeknights.tconstruct.tools.common.inventory.ContainerPartChest;

public class TilePartChest extends TileTinkerChest implements IInventoryGui {

  public TilePartChest() {
    super("gui.partchest.name");
  }

  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerPartChest(inventoryplayer, this);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiPartChest(inventoryplayer, world, pos, this);
  }

  // toolparts only
  @Override
  public boolean isItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {
    // check if there is no other slot containing that item
    for(int i = 0; i < getSizeInventory(); i++) {
      // don't compare count
      if(ItemStack.areItemsEqual(itemstack, getStackInSlot(i))
         && ItemStack.areItemStackTagsEqual(itemstack, getStackInSlot(i))) {
        return i == slot; // only allowed in the same slot
      }
    }

    return itemstack.getItem() instanceof IToolPart;
  }

}
