package slimeknights.tconstruct.tools.tileentity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.common.IInventoryGui;
import slimeknights.tconstruct.library.smeltery.ICast;
import slimeknights.tconstruct.library.tools.IPattern;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.client.GuiPatternChest;
import slimeknights.tconstruct.tools.inventory.ContainerPatternChest;

public class TilePatternChest extends TileTinkerChest implements IInventoryGui {

  public TilePatternChest() {
    super("gui.patternchest.name", MAX_INVENTORY, 1);
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
    if(itemstack == null || !(itemstack.getItem() instanceof IPattern || itemstack.getItem() instanceof ICast)) {
      return false;
    }
    Item part = Pattern.getPartFromTag(itemstack);

    if(part == null) {
      // not a part cast, go by nbt
      for(int i = 0; i < getSizeInventory(); i++) {
        ItemStack inv = getStackInSlot(i);
        // ensure that the type (pattern/cast) is the same
        if(inv != null && (
           (inv.getItem() instanceof IPattern && !(itemstack.getItem() instanceof IPattern)) ||
           (inv.getItem() instanceof ICast && !(itemstack.getItem() instanceof ICast)))) {
          return false;
        }
        if(ItemStack.areItemsEqual(itemstack, inv) && ItemStack.areItemStackTagsEqual(itemstack, inv)) {
          return false;
        }
      }
      return true;
    }

    for(int i = 0; i < getSizeInventory(); i++) {
      Item slotPart = Pattern.getPartFromTag(getStackInSlot(i));
      // duplicate, already present
      if(slotPart != null) {
        // only the same item (== cast or pattern)
        if(getStackInSlot(i).getItem() != itemstack.getItem()) {
          return false;
        }
        // no duplicate parts
        if(slotPart == part) {
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
      if(getStackInSlot(i) != null && getStackInSlot(i).getItem() instanceof ICast) {
        return "gui.castchest.name";
      }
    }
    return super.getName();
  }
}
