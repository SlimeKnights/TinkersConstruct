package slimeknights.tconstruct.shared.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import slimeknights.mantle.tileentity.InventoryTileEntity;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.SoundUtils;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.network.InventorySlotSyncPacket;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tables.inventory.BaseStationContainer;
import slimeknights.tconstruct.tables.network.UpdateStationScreenPacket;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

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
  protected boolean shouldSyncOnUpdate() {
    return true;
  }

  @Override
  public CompoundNBT getUpdateTag() {
    CompoundNBT nbt = super.getUpdateTag();
    // inventory is already in main NBT, include it in update tag
    writeInventoryToNBT(nbt);
    return nbt;
  }

  /**
   * Sends a packet to all players with this container open
   */
  public void syncToRelevantPlayers(Consumer<PlayerEntity> action) {
    if (this.world == null || this.world.isRemote) {
      return;
    }

    this.world.getPlayers().stream()
      // sync if they are viewing this tile
      .filter(player -> {
        if (player.openContainer instanceof BaseStationContainer) {
          return ((BaseStationContainer<?>) player.openContainer).getTile() == this;
        }
        return false;
      })
      // send packets
      .forEach(action);
  }

  /**
   * Plays the crafting sound for all players around the given player
   *
   * @param player the player
   */
  protected void playCraftSound(PlayerEntity player) {
    SoundUtils.playSoundForAll(player, Sounds.SAW.getSound(), 0.8f, 0.8f + 0.4f * TConstruct.random.nextFloat());
  }

  /**
   * Update the screen to the given player
   * @param player  Player to send an update to
   */
  protected void syncScreen(PlayerEntity player) {
    if (this.world != null && !this.world.isRemote && player instanceof ServerPlayerEntity) {
      TinkerNetwork.getInstance().sendTo(new UpdateStationScreenPacket(), (ServerPlayerEntity) player);
    }
  }
}
