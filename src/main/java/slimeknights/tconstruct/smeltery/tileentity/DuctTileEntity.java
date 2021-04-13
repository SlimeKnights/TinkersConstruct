package slimeknights.tconstruct.smeltery.tileentity;

import lombok.Getter;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.model.IModelData;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.inventory.SingleItemContainer;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryInputOutputTileEntity.SmelteryFluidIO;
import slimeknights.tconstruct.smeltery.tileentity.inventory.DuctItemHandler;
import slimeknights.tconstruct.smeltery.tileentity.tank.IDisplayFluidListener;

/**
 * Filtered drain tile entity
 */
public class DuctTileEntity extends SmelteryFluidIO implements NamedScreenHandlerFactory {
  private static final String TAG_ITEM = "item";
  private static final Text TITLE = new TranslatableText(Util.makeTranslationKey("gui", "duct"));

  @Getter
  private final DuctItemHandler itemHandler = new DuctItemHandler(this);

  public DuctTileEntity() {
    this(TinkerSmeltery.duct);
  }

  protected DuctTileEntity(BlockEntityType<?> type) {
    super(type);
  }


  /* Container */

  @Override
  public Text getDisplayName() {
    return TITLE;
  }

  @Nullable
  @Override
  public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity playerEntity) {
    return new SingleItemContainer(id, inventory, this);
  }


  /* Capability */
/*
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

  @Override
  protected Optional<IFluidHandler> makeWrapper(Optional<IFluidHandler> capability) {
    return Optional.of(() -> new DuctTankWrapper(capability.orElse(emptyInstance), itemHandler));
  }

  *//** Updates the fluid in model data *//*
  public void updateFluid() {
    Fluid fluid = itemHandler.getFluid();
    modelData.setData(IDisplayFluidListener.PROPERTY, fluid);
    requestModelDataUpdate();
    assert world != null;
    BlockState state = getCachedState();
    world.updateListeners(pos, state, state, 48);
  }*/


  /* NBT */

  @Override
  public void fromTag(BlockState state, CompoundTag tags) {
    super.fromTag(state, tags);
    if (tags.contains(TAG_ITEM, NbtType.COMPOUND)) {
      throw new RuntimeException("Crab");
//      itemHandler.readFromNBT(tags.getCompound(TAG_ITEM));
    }
  }

//  @Override
//  public void handleUpdateTag(BlockState state, CompoundTag tag) {
//    super.handleUpdateTag(state, tag);
//    if (world != null && world.isClient) {
//      updateFluid();
//    }
//  }

  @Override
  public void writeSynced(CompoundTag tags) {
    super.writeSynced(tags);
    throw new RuntimeException("Crab");
//    tags.put(TAG_ITEM, itemHandler.writeToNBT());
  }
}
