package slimeknights.tconstruct.smeltery.tileentity;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import slimeknights.mantle.client.model.data.SinglePropertyData;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.inventory.SingleItemContainer;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryInputOutputTileEntity.SmelteryFluidIO;
import slimeknights.tconstruct.smeltery.tileentity.inventory.DuctItemHandler;
import slimeknights.tconstruct.smeltery.tileentity.inventory.DuctTankWrapper;
import slimeknights.tconstruct.smeltery.tileentity.tank.IDisplayFluidListener;

import org.jetbrains.annotations.Nullable;

/**
 * Filtered drain tile entity
 */
public class DuctTileEntity extends SmelteryFluidIO implements NamedScreenHandlerFactory {
  private static final String TAG_ITEM = "item";
  private static final Text TITLE = new TranslatableText(Util.makeTranslationKey("gui", "duct"));

  @Getter
  private final DuctItemHandler itemHandler = new DuctItemHandler(this);
  private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> itemHandler);
  @Getter
  private final IModelData modelData = new SinglePropertyData<>(IDisplayFluidListener.PROPERTY);

  public DuctTileEntity() {
    this(TinkerSmeltery.duct.get());
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

  @Override
  protected LazyOptional<IFluidHandler> makeWrapper(LazyOptional<IFluidHandler> capability) {
    return LazyOptional.of(() -> new DuctTankWrapper(capability.orElse(emptyInstance), itemHandler));
  }

  /** Updates the fluid in model data */
  public void updateFluid() {
    Fluid fluid = itemHandler.getFluid();
    modelData.setData(IDisplayFluidListener.PROPERTY, fluid);
    requestModelDataUpdate();
    assert world != null;
    BlockState state = getCachedState();
    world.updateListeners(pos, state, state, 48);
  }


  /* NBT */

  @Override
  public void fromTag(BlockState state, CompoundTag tags) {
    super.fromTag(state, tags);
    if (tags.contains(TAG_ITEM, NBT.TAG_COMPOUND)) {
      itemHandler.readFromNBT(tags.getCompound(TAG_ITEM));
    }
  }

  @Override
  public void handleUpdateTag(BlockState state, CompoundTag tag) {
    super.handleUpdateTag(state, tag);
    if (world != null && world.isClient) {
      updateFluid();
    }
  }

  @Override
  public void writeSynced(CompoundTag tags) {
    super.writeSynced(tags);
    tags.put(TAG_ITEM, itemHandler.writeToNBT());
  }
}
