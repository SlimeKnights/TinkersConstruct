package slimeknights.tconstruct.shared.tileentity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.tileentity.InventoryTileEntity;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tables.inventory.BaseStationContainer;
import slimeknights.tconstruct.tools.common.network.InventorySlotSyncPacket;

import java.util.function.Consumer;

/**
 * Tile entity that displays items in world. TODO: better name?
 */
public abstract class TableTileEntity extends InventoryTileEntity {

  public TableTileEntity(BlockEntityType<?> tileEntityTypeIn, String name, int inventorySize) {
    super(tileEntityTypeIn, new TranslatableText(name), inventorySize);
  }

  public TableTileEntity(BlockEntityType<?> tileEntityTypeIn, String name, int inventorySize, int maxStackSize) {
    super(tileEntityTypeIn, new TranslatableText(name), inventorySize, maxStackSize);
  }

  /* Syncing */

  @Override
  public void setStack(int slot, @NotNull ItemStack itemstack) {
    // send a slot update to the client when items change, so we can update the TESR
    if (world != null && world instanceof ServerWorld && !world.isClient && !ItemStack.areEqual(itemstack, getStack(slot))) {
      TinkerNetwork.getInstance().sendToClientsAround(new InventorySlotSyncPacket(itemstack, slot, pos), (ServerWorld) world, this.pos);
    }
    super.setStack(slot, itemstack);
  }

  @Override
  public CompoundTag toInitialChunkDataTag() {
    // sync whole inventory on chunk load
    return this.toTag(new CompoundTag());
  }

  /**
   * Sends a packet to all players with this container open
   */
  public void syncToRelevantPlayers(Consumer<PlayerEntity> action) {
    if (this.world == null || this.world.isClient) {
      return;
    }

    this.world.getPlayers().stream()
      // sync if they are viewing this tile
      .filter(player -> {
        if (player.currentScreenHandler instanceof BaseStationContainer) {
          return ((BaseStationContainer) player.currentScreenHandler).getTile() == this;
        }
        return false;
      })
      // send packets
      .forEach(action);
  }
}
