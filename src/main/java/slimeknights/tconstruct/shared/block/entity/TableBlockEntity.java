package slimeknights.tconstruct.shared.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.block.entity.InventoryBlockEntity;
import slimeknights.tconstruct.common.SoundUtils;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.network.InventorySlotSyncPacket;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.tables.menu.TabbedContainerMenu;
import slimeknights.tconstruct.tables.network.UpdateStationScreenPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Tile entity that displays items in world. TODO: better name?
 */
public abstract class TableBlockEntity extends InventoryBlockEntity {
  /** tick sound was last played for each player */
  private final Map<UUID, Integer> lastSoundTick = new HashMap<>();

  public TableBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, Component name, int inventorySize) {
    super(tileEntityTypeIn, pos, state, name, false, inventorySize);
  }

  public TableBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, Component name, int inventorySize, int maxStackSize) {
    super(tileEntityTypeIn, pos, state, name, false, inventorySize, maxStackSize);
  }

  /* Syncing */

  @Override
  public void setItem(int slot, ItemStack itemstack) {
    // send a slot update to the client when items change, so we can update the TESR
    if (level != null && level instanceof ServerLevel && !level.isClientSide && !ItemStack.matches(itemstack, getItem(slot))) {
      TinkerNetwork.getInstance().sendToClientsAround(new InventorySlotSyncPacket(itemstack, slot, worldPosition), (ServerLevel) level, this.worldPosition);
    }
    super.setItem(slot, itemstack);
  }

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }

  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag nbt = super.getUpdateTag();
    // inventory is already in main NBT, include it in update tag
    writeInventoryToNBT(nbt);
    return nbt;
  }

  /**
   * Sends a packet to all players with this container open
   */
  public void syncToRelevantPlayers(Consumer<Player> action) {
    if (this.level == null || this.level.isClientSide) {
      return;
    }

    this.level.players().stream()
      // sync if they are viewing this tile
      .filter(player -> {
        if (player.containerMenu instanceof TabbedContainerMenu) {
          return ((TabbedContainerMenu<?>) player.containerMenu).getTile() == this;
        }
        return false;
      })
      // send packets
      .forEach(action);
  }

  /** Checks if we can play the sound right now */
  protected boolean isSoundReady(Player player) {
    int lastSound = lastSoundTick.getOrDefault(player.getUUID(), 0);
    if (lastSound < player.tickCount) {
      lastSoundTick.put(player.getUUID(), player.tickCount);
      return true;
    }
    return false;
  }

  /**
   * Plays the crafting sound for all players around the given player
   *
   * @param player the player
   */
  protected void playCraftSound(Player player) {
    if (isSoundReady(player)) {
      SoundUtils.playSoundForAll(player, Sounds.SAW.getSound(), 0.8f, 0.8f + 0.4f * player.level().random.nextFloat());
    }
  }

  /**
   * Update the screen to the given player
   * @param player  Player to send an update to
   */
  protected void syncScreen(Player player) {
    if (this.level != null && !this.level.isClientSide && player instanceof ServerPlayer serverPlayer) {
      TinkerNetwork.getInstance().sendTo(UpdateStationScreenPacket.INSTANCE, serverPlayer);
    }
  }

  /**
   * Update the screen for all players using this UI
   */
  protected void syncScreenToRelevantPlayers() {
    if (this.level != null && !this.level.isClientSide) {
      syncToRelevantPlayers(this::syncScreen);
    }
  }
}
