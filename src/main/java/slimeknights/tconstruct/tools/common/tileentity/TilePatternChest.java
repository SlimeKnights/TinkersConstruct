package slimeknights.tconstruct.tools.common.tileentity;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import slimeknights.mantle.common.IInventoryGui;
import slimeknights.tconstruct.library.smeltery.ICast;
import slimeknights.tconstruct.library.tools.IPattern;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.tools.common.client.GuiPatternChest;
import slimeknights.tconstruct.tools.common.inventory.ContainerPatternChest;

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
  public boolean isItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {
    if(itemstack.isEmpty() || !(itemstack.getItem() instanceof IPattern || itemstack.getItem() instanceof ICast)) {
      return false;
    }
    Item part = Pattern.getPartFromTag(itemstack);
    boolean hasContents = false;
    for(int i = 0; i < getSizeInventory(); i++) {
      if(isStackInSlot(i)) {
        hasContents = true;
        break;
      }
    }

    // empty chest accepts everything
    if(!hasContents) {
      return true;
    }
    // otherwise check that only same goes into the same chest
    else {
      boolean castChest = isCastChest();

      // if cast chest only accept casts.
      if(castChest && !(itemstack.getItem() instanceof ICast)) {
        return false;
      }
      // and only patterns go into pattern chests
      else if(!castChest && (!(itemstack.getItem() instanceof IPattern) || itemstack.getItem() instanceof ICast)) {
        return false;
      }
    }

    // now it's ensured that only patterns go in pattern chests, and only casts in cast chests
    // next find out if the cast already is present in the chest

    // not a part cast? go by nbt
    if(part == null) {
      for(int i = 0; i < getSizeInventory(); i++) {
        ItemStack inv = getStackInSlot(i);
        if(inv.isEmpty()) {
          continue;
        }

        // is it exactly the same item?
        if(ItemStack.areItemsEqual(itemstack, inv) && ItemStack.areItemStackTagsEqual(itemstack, inv)) {
          return false;
        }
      }
      return true;
    }

    // part cast, go by item returned
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

  @Nonnull
  @Override
  public String getName() {
    // do we hold casts instead of patterns?
    if(isCastChest()) {
      return "gui.castchest.name";
    }
    return super.getName();
  }

  public boolean isCastChest() {
    // do we hold casts instead of patterns?
    for(int i = 0; i < getSizeInventory(); i++) {
      if(getStackInSlot(i).getItem() instanceof ICast) {
        return true;
      }
    }
    return false;
  }
}
