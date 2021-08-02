package slimeknights.tconstruct.tables.tileentity.chest;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import slimeknights.mantle.tileentity.NamableTileEntity;
import slimeknights.tconstruct.tables.inventory.TinkerChestContainer;

import javax.annotation.Nullable;

/** Shared base logic for all Tinkers' chest tile entities */
public abstract class ChestTileEntity extends NamableTileEntity {
  private static final String KEY_ITEMS = "Items";

  @Getter
  private final ItemStackHandler itemHandler;
  private final LazyOptional<IItemHandler> capability;
  protected ChestTileEntity(TileEntityType<?> tileEntityTypeIn, String name, ItemStackHandler itemHandler) {
    super(tileEntityTypeIn, new TranslationTextComponent(name));
    this.itemHandler = itemHandler;
    this.capability = LazyOptional.of(() -> itemHandler);
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
    if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return capability.cast();
    }
    return super.getCapability(cap, side);
  }

  @Override
  protected void invalidateCaps() {
    super.invalidateCaps();
    capability.invalidate();
  }

  @Nullable
  @Override
  public Container createMenu(int menuId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new TinkerChestContainer(menuId, playerInventory, this);
  }

  /**
   * Checks if the given item should be inserted into the chest on interact
   * @param player    Player inserting
   * @param heldItem  Stack to insert
   * @return  Return true
   */
  public boolean canInsert(PlayerEntity player, ItemStack heldItem) {
    return true;
  }

  @Override
  public CompoundNBT write(CompoundNBT tags) {
    tags = super.write(tags);
    // move the items from the serialized result
    // we don't care about the size and need it here for compat with old worlds
    CompoundNBT handlerNBT = itemHandler.serializeNBT();
    tags.put(KEY_ITEMS, handlerNBT.getList(KEY_ITEMS, NBT.TAG_COMPOUND));
    return tags;
  }

  /** Reads the inventory from NBT */
  public void readInventory(CompoundNBT tags) {
    // copy in just the items key for deserializing, don't want to change the size
    CompoundNBT handlerNBT = new CompoundNBT();
    handlerNBT.put(KEY_ITEMS, tags.getList(KEY_ITEMS, NBT.TAG_COMPOUND));
    itemHandler.deserializeNBT(handlerNBT);
  }

  @Override
  public void read(BlockState blockState, CompoundNBT tags) {
    super.read(blockState, tags);
    readInventory(tags);
  }
}
