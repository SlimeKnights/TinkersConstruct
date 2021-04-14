package slimeknights.tconstruct.smeltery.tileentity;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.tileentity.NamableTileEntity;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.inventory.SingleItemContainer;
import slimeknights.tconstruct.smeltery.tileentity.inventory.HeaterItemHandler;

/** Tile entity for the heater block below the melter */
public class HeaterTileEntity extends NamableTileEntity {
  private static final String TAG_ITEM = "item";
  private static final Text TITLE = new TranslatableText(Util.makeTranslationKey("gui", "heater"));

  private final HeaterItemHandler itemHandler = new HeaterItemHandler(this);

  protected HeaterTileEntity(BlockEntityType<?> type) {
    super(type, TITLE);
  }

  public HeaterTileEntity() {
    this(TinkerSmeltery.heater);
  }

  @Nullable
  @Override
  public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity playerEntity) {
    return new SingleItemContainer(id, inventory, this);
  }


/*
  */
/* Capability *//*


  @Override
  public <C> Optional<C> getCapability(Capability<C> capability, @Nullable Direction facing) {
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

*/

  /* NBT */

  @Override
  public void fromTag(BlockState state, CompoundTag tags) {
    super.fromTag(state, tags);
    if (tags.contains(TAG_ITEM, NbtType.COMPOUND)) {
      itemHandler.readFromNBT(tags.getCompound(TAG_ITEM));
    }
  }

  @Override
  public CompoundTag toTag(CompoundTag tags) {
    super.writeSynced(tags);
    tags.put(TAG_ITEM, itemHandler.writeToNBT());
    return tags;
  }
}
