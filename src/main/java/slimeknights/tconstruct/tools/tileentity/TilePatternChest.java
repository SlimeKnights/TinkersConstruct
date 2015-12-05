package slimeknights.tconstruct.tools.tileentity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.common.IInventoryGui;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.shared.tileentity.TileTable;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.client.GuiPatternChest;
import slimeknights.tconstruct.tools.inventory.ContainerPatternChest;

public class TilePatternChest extends TileTable implements IInventoryGui {

  public TilePatternChest() {
    super("gui.patternchest.name", 27, 1);
  }

  @Override
  public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new ContainerPatternChest(inventoryplayer, this);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
    return new GuiPatternChest(inventoryplayer, world, pos, this);
  }

  // we only allow one type (cast/pattern) and only one of each toolpart
  @Override
  public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
    IToolPart part = Pattern.getPartFromTag(itemstack);

    if(part == null) {
      return false;
    }

    for(int i = 0; i < getSizeInventory(); i++) {
      IToolPart slotPart = Pattern.getPartFromTag(getStackInSlot(i));
      // duplicate, already present
      if(slotPart != null) {
        // only the same item
        if(getStackInSlot(i).getItem() != itemstack.getItem()) {
          return false;
        }
        // no duplicate parts
        if(slotPart.getIdentifier().equals(part.getIdentifier())) {
          return false;
        }
      }
    }

    return true;
  }

  @Override
  public String getName() {
    // do we hold casts instead of patterns?
    for(int i = 0; i < getSizeInventory(); i++) {
      if(getStackInSlot(i) != null && getStackInSlot(i).getItem() == TinkerSmeltery.cast) {
        return "gui.castchest.name";
      }
    }
    return super.getName();
  }
}
