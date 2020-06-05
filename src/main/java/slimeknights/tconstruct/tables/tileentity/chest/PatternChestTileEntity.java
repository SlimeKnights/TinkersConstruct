package slimeknights.tconstruct.tables.tileentity.chest;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.tables.inventory.chest.PatternChestContainer;
import slimeknights.tconstruct.tileentities.TablesTileEntities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PatternChestTileEntity extends TinkerChestTileEntity {

  public PatternChestTileEntity() {
    super(TablesTileEntities.pattern_chest.get(), "gui.tconstruct.pattern_chest", TinkerChestTileEntity.MAX_INVENTORY, 1);
  }

  @Nullable
  @Override
  public Container createMenu(int menuId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new PatternChestContainer(menuId, playerInventory, this);
  }

  @Override
  public boolean isItemValidForSlot(int slot, @Nonnull ItemStack itemstack) {
    return true;
    //TODO FIX
    /*if(itemstack.isEmpty() || !(itemstack.getItem() IPattern || itemstack.getItem() instanceof ICast)) {
      return false;
    }

    Item part = Pattern.getPartFromTag(itemstack);
    boolean hasContents = false;

    for(int i = 0; i < this.getSizeInventory(); i++) {
      if(this.isStackInSlot(i)) {
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
      boolean castChest = this.isCastChest();

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
      for(int i = 0; i < this.getSizeInventory(); i++) {
        ItemStack inv = this.getStackInSlot(i);
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
    for(int i = 0; i < this.getSizeInventory(); i++) {
      Item slotPart = Pattern.getPartFromTag(this.getStackInSlot(i));
      // duplicate, already present
      if(slotPart != null) {
        // only the same item (== cast or pattern)
        if(this.getStackInSlot(i).getItem() != itemstack.getItem()) {
          return false;
        }
        // no duplicate parts
        if(slotPart == part) {
          return false;
        }
      }
    }

    return true;*/
  }

  @Nonnull
  @Override
  public ITextComponent getName() {
    // do we hold casts instead of patterns?
    if (this.isCastChest()) {
      return new TranslationTextComponent("gui.tconstruct.cast_chest");
    }
    return super.getName();
  }

  public boolean isCastChest() {
    // do we hold casts instead of patterns?
    for (int i = 0; i < this.getSizeInventory(); i++) {
      if (this.getStackInSlot(i).getItem() instanceof MaterialItem) {
        return true;
      }
    }
    return false;
  }
}
