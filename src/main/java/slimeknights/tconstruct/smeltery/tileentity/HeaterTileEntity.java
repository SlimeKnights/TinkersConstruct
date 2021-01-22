package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import slimeknights.mantle.tileentity.NamableTileEntity;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.inventory.SingleItemContainer;
import slimeknights.tconstruct.smeltery.tileentity.inventory.HeaterItemHandler;

import javax.annotation.Nullable;

/** Tile entity for the heater block below the melter */
public class HeaterTileEntity extends NamableTileEntity {
  private static final String TAG_ITEM = "item";
  private static final ITextComponent TITLE = new TranslationTextComponent(Util.makeTranslationKey("gui", "heater"));

  private final HeaterItemHandler itemHandler = new HeaterItemHandler(this);
  private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> itemHandler);

  protected HeaterTileEntity(TileEntityType<?> type) {
    super(type, TITLE);
  }

  public HeaterTileEntity() {
    this(TinkerSmeltery.heater.get());
  }

  @Nullable
  @Override
  public Container createMenu(int id, PlayerInventory inventory, PlayerEntity playerEntity) {
    return new SingleItemContainer(id, inventory, this);
  }


  /* Capability */

  @Override
  public <C> LazyOptional<C> getCapability(Capability<C> capability, @Nullable Direction facing) {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return itemCapability.cast();
    }
    return super.getCapability(capability, facing);
  }

  @Override
  protected void invalidateCaps() {
    super.invalidateCaps();
    itemCapability.invalidate();
  }


  /* NBT */

  @Override
  public void read(BlockState state, CompoundNBT tags) {
    super.read(state, tags);
    if (tags.contains(TAG_ITEM, NBT.TAG_COMPOUND)) {
      itemHandler.readFromNBT(tags.getCompound(TAG_ITEM));
    }
  }

  @Override
  public CompoundNBT write(CompoundNBT tags) {
    super.writeSynced(tags);
    tags.put(TAG_ITEM, itemHandler.writeToNBT());
    return tags;
  }
}
