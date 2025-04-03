package slimeknights.tconstruct.smeltery.block.entity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import slimeknights.mantle.fluid.FluidTransferHelper;
import slimeknights.mantle.fluid.transfer.FluidContainerTransferManager;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.shared.block.entity.TableBlockEntity;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.item.TankItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CastingTankBlockEntity extends TableBlockEntity implements ITankBlockEntity, WorldlyContainer {
  /** Max capacity for the tank */
  public static final int DEFAULT_CAPACITY = FluidType.BUCKET_VOLUME * 2;
  // slots
  public static final int INPUT = 0;
  public static final int OUTPUT = 1;
  private static final Component NAME = TConstruct.makeTranslation("gui", "casting");

  /** Internal fluid tank instance */
  @Getter
  protected final FluidTankAnimated tank;
  /** Capability holder for the tank */
  private final LazyOptional<IFluidHandler> fluidHolder;
  /** Last comparator strength to reduce block updates */
  @Getter @Setter
  private int lastStrength = -1;

  /**
   * Gets the capacity for the given block
   * @param block  block
   * @return  Capacity
   */
  public static int getCapacity(Block block) {
    if (block instanceof ITankBlock) {
      return ((ITankBlock) block).getCapacity();
    }
    return DEFAULT_CAPACITY;
  }

  /**
   * Gets the capacity for the given item
   * @param item  item
   * @return  Capacity
   */
  public static int getCapacity(Item item) {
    if (item instanceof BlockItem) {
      return getCapacity(((BlockItem) item).getBlock());
    }
    return DEFAULT_CAPACITY;
  }

  public CastingTankBlockEntity(BlockPos pos, BlockState state) {
    this(pos, state, (ITankBlock) state.getBlock());
  }

  /** Main constructor */
  public CastingTankBlockEntity(BlockPos pos, BlockState state, ITankBlock block) {
    this(TinkerSmeltery.castingTankBE.get(), pos, state, block);
  }

  /** Extendable constructor */
  @SuppressWarnings("WeakerAccess")
  protected CastingTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ITankBlock block) {
    super(type, pos, state, NAME, 2, 1);
    tank = new FluidTankAnimated(block.getCapacity(), this);
    fluidHolder = LazyOptional.of(() -> tank);
    itemHandler = new SidedInvWrapper(this, Direction.DOWN);
  }

  /**
   * Called from {@link slimeknights.tconstruct.smeltery.block.CastingTankBlock#use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)}
   * @param player Player activating the block.
   */
  public void interact(Player player, InteractionHand hand, BlockHitResult hit) {
    // skip client side
    if (level == null || level.isClientSide) {
      return;
    }

    ItemStack input = getItem(INPUT);
    ItemStack output = getItem(OUTPUT);
    ItemStack held = player.getItemInHand(hand);
    // if the held item can be placed inside, do so
    if (canReceiveItemStack(held)) {
      setItem(INPUT, held.split(1));
      tryToProcessItem(); // also need to try when we get an item from automation
    // otherwise, interact with the tank
    } else if (!FluidTransferHelper.interactWithTank(level, worldPosition, player, hand, hit)
      // if the tank wasn't interacted with and the player has an empty hand, the player may take the item from the casting tank
      && held.isEmpty() // TODO this is wrong, the player can take the item at this point if it would fit in the player's inventory, not just if empty-handed
    ) {
      if (!output.isEmpty()) {
        setItem(OUTPUT, ItemStack.EMPTY);
        player.setItemInHand(hand, output);
      } else if (!input.isEmpty()) {
        setItem(INPUT, ItemStack.EMPTY);
        player.setItemInHand(hand, input);
      }
    }
  }

  /**
   * Checks whether a given stack can be received into the casting tank's inventory
   * @param stack Item stack to be received
   * @return True if the stack can be received
   */
  protected boolean canReceiveItemStack(ItemStack stack) {
      return getItem(INPUT).isEmpty() && getItem(OUTPUT).isEmpty() && !stack.isEmpty() && (
        // check the various options for some sort of fluid-containing stack
        FluidContainerTransferManager.INSTANCE.mayHaveTransfer(stack)
          || stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent()
          || stack.getItem() instanceof BucketItem
      );
  }

  @Override
  public boolean canPlaceItem(int pIndex, ItemStack pStack) {
    if (pIndex == INPUT) {
      return canReceiveItemStack(pStack);
    }
    return false;
  }

  /**
   * Tries to empty or fill an item in the input spot. If either happens, the resulting item is placed in the output slot.
   */
  private void tryToProcessItem() {
    ItemStack input = getItem(INPUT);
    if (input.isEmpty() || !getItem(OUTPUT).isEmpty()) {
      return;
    }

    // need to take the item out of the input slot to prevent a nested call from getting too far if we do modify the tank
    setItem(INPUT, ItemStack.EMPTY);
    ItemStack result = FluidTransferHelper.interactWithTankSlot(tank, input, IFluidContainerTransfer.TransferDirection.AUTO);

    // if the item got processed
    if (!result.isEmpty()) {
      setItem(OUTPUT, result);
      // TODO, play sound

    // if the item wasn't emptied/filled, put it back in the input slot
    } else {
      setItem(INPUT, input);
    }
  }

  @Override
  public void setChanged() {
    super.setChanged();
    tryToProcessItem();
  }

  @Override
  @Nonnull
  public int[] getSlotsForFace(Direction side) {
    return new int[]{INPUT, OUTPUT};
  }

  @Override
  public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
    return index == INPUT && !isStackInSlot(OUTPUT);
  }

  @Override
  public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
    return index == OUTPUT;
  }

  /*
   * Tank methods
   */

  @Override
  @Nonnull
  public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
    if (capability == ForgeCapabilities.FLUID_HANDLER) {
      return fluidHolder.cast();
    }
    return super.getCapability(capability, facing);
  }

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    fluidHolder.invalidate();
  }

  @Nonnull
  @Override
  public ModelData getModelData() {
    return ModelData.builder()
      .with(ModelProperties.FLUID_STACK, tank.getFluid())
      .with(ModelProperties.TANK_CAPACITY, tank.getCapacity()).build();
  }

  @Override
  public void onTankContentsChanged() {
    ITankBlockEntity.super.onTankContentsChanged();
    if (this.level != null) {
      level.getLightEngine().checkBlock(this.worldPosition);
      this.requestModelDataUpdate();
    }

    tryToProcessItem();
  }

  @Override
  public void updateFluidTo(FluidStack fluid) {
    ITankBlockEntity.super.updateFluidTo(fluid);
    // update light if the fluid changes
    if (this.level != null) {
      level.getLightEngine().checkBlock(this.worldPosition);
    }
  }


  /*
   * NBT
   */

  /**
   * Sets the tag on the stack based on the contained tank
   * @param stack  Stack
   */
  public void setTankTag(ItemStack stack) {
    TankItem.setTank(stack, tank);
  }

  /**
   * Updates the tank from an NBT tag, used in the block
   * @param nbt  tank NBT
   */
  public void updateTank(CompoundTag nbt) {
    if (nbt.isEmpty()) {
      tank.setFluid(FluidStack.EMPTY);
    } else {
      tank.readFromNBT(nbt);
      if (level != null) {
        level.getLightEngine().checkBlock(worldPosition);
      }
    }
  }

  @Override
  public void load(CompoundTag tag) {
    tank.setCapacity(getCapacity(getBlockState().getBlock()));
    updateTank(tag.getCompound(NBTTags.TANK));
    super.load(tag);
  }

  @Override
  public void saveSynced(CompoundTag tag) {
    super.saveSynced(tag);
    // want tank on the client on world load
    if (!tank.isEmpty()) {
      tag.put(NBTTags.TANK, tank.writeToNBT(new CompoundTag()));
    }
  }

  @Override
  public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
    return null;
  }

  /** Interface for blocks to return their capacity */
  public interface ITankBlock {
    /** Gets the capacity for this tank */
    int getCapacity();
  }
}
