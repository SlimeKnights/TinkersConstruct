package slimeknights.tconstruct.tables.block.entity.chest;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import slimeknights.mantle.block.entity.NameableBlockEntity;
import slimeknights.tconstruct.tables.block.entity.inventory.IChestItemHandler;
import slimeknights.tconstruct.tables.menu.TinkerChestContainerMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Shared base logic for all Tinkers' chest tile entities */
public abstract class AbstractChestBlockEntity extends NameableBlockEntity {
  private static final String KEY_ITEMS = "Items";

  @Getter
  private final IChestItemHandler itemHandler;
  private final LazyOptional<IItemHandler> capability;
  protected AbstractChestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Component name, IChestItemHandler itemHandler) {
    super(type, pos, state, name);
    itemHandler.setParent(this);
    this.itemHandler = itemHandler;
    this.capability = LazyOptional.of(() -> itemHandler);
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
    if (cap == ForgeCapabilities.ITEM_HANDLER) {
      return capability.cast();
    }
    return super.getCapability(cap, side);
  }

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    capability.invalidate();
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int menuId, Inventory playerInventory, Player playerEntity) {
    return new TinkerChestContainerMenu(menuId, playerInventory, this);
  }

  /**
   * Checks if the given item should be inserted into the chest on interact
   * @param player    Player inserting
   * @param heldItem  Stack to insert
   * @return  Return true
   */
  public boolean canInsert(Player player, ItemStack heldItem) {
    return true;
  }

  @Override
  public void saveAdditional(CompoundTag tags) {
    super.saveAdditional(tags);
    // move the items from the serialized result
    // we don't care about the size and need it here for compat with old worlds
    CompoundTag handlerNBT = itemHandler.serializeNBT();
    tags.put(KEY_ITEMS, handlerNBT.getList(KEY_ITEMS, Tag.TAG_COMPOUND));
  }

  /** Reads the inventory from NBT */
  public void readInventory(CompoundTag tags) {
    // copy in just the items key for deserializing, don't want to change the size
    CompoundTag handlerNBT = new CompoundTag();
    handlerNBT.put(KEY_ITEMS, tags.getList(KEY_ITEMS, Tag.TAG_COMPOUND));
    itemHandler.deserializeNBT(handlerNBT);
  }

  @Override
  public void load(CompoundTag tags) {
    super.load(tags);
    readInventory(tags);
  }
}
