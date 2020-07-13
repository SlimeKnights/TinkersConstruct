package slimeknights.tconstruct.shared.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import slimeknights.mantle.tileentity.InventoryTileEntity;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tools.common.network.InventorySlotSyncPacket;

import javax.annotation.Nonnull;

/**
 * Tile entity that displays items in world. TODO: better name?
 */
public abstract class TableTileEntity extends InventoryTileEntity {

  public TableTileEntity(TileEntityType<?> tileEntityTypeIn, String name, int inventorySize) {
    super(tileEntityTypeIn, new TranslationTextComponent(name), inventorySize);
  }

  public TableTileEntity(TileEntityType<?> tileEntityTypeIn, String name, int inventorySize, int maxStackSize) {
    super(tileEntityTypeIn, new TranslationTextComponent(name), inventorySize, maxStackSize);
  }

  /* Syncing */

  @Override
  public void setInventorySlotContents(int slot, @Nonnull ItemStack itemstack) {
    // send a slot update to the client when items change, so we can update the TESR
    if (world != null && world instanceof ServerWorld && !world.isRemote && !ItemStack.areItemStacksEqual(itemstack, getStackInSlot(slot))) {
      TinkerNetwork.getInstance().sendToClientsAround(new InventorySlotSyncPacket(itemstack, slot, pos), (ServerWorld) world, this.pos);
    }
    super.setInventorySlotContents(slot, itemstack);
  }

  @Override
  public CompoundNBT getUpdateTag() {
    // sync whole inventory on chunk load
    return this.write(new CompoundNBT());
  }
}
