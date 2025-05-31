package slimeknights.tconstruct.smeltery.menu;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.mantle.fluid.FluidTransferHelper;
import slimeknights.mantle.fluid.transfer.FluidContainerTransferManager;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer.TransferDirection;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer.TransferResult;
import slimeknights.mantle.util.sync.ValidZeroDataSlot;
import slimeknights.tconstruct.shared.inventory.TriggeringMultiModuleContainerMenu;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.block.entity.module.MultitankFuelModule;
import slimeknights.tconstruct.smeltery.block.entity.tank.SmelteryTank;
import slimeknights.tconstruct.tables.menu.module.SideInventoryContainer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class HeatingStructureContainerMenu extends TriggeringMultiModuleContainerMenu<HeatingStructureBlockEntity> implements TransferDirectionSupplier {
  /** List of directions to consider. Not using values() as we want to ensure future potential reverse direction is not used. */
  private static final TransferDirection[] TRANSFER_DIRECTIONS = { TransferDirection.AUTO, TransferDirection.EMPTY_ITEM, TransferDirection.FILL_ITEM };
  @Getter
  private final SideInventoryContainer<HeatingStructureBlockEntity> sideInventory;
  @Getter
  private final Container bucketContainer;
  @Getter
  private TransferDirection transferDirection = TransferDirection.AUTO;
  private final Slot bucketResultSlot;
  public HeatingStructureContainerMenu(int id, @Nullable Inventory inv, @Nullable HeatingStructureBlockEntity structure) {
    super(TinkerSmeltery.smelteryContainer.get(), id, inv, structure);
    bucketContainer = new SimpleContainer(2);
    if (inv != null && structure != null) {
      // slots for emptying/filling buckets - do first but filtered
      if (!inv.player.level().isClientSide) {
        IFluidHandler tank = structure.getTank();
        addSlot(new BucketInputSlot(bucketContainer, 125, 46, tank, this, inv.player));
        bucketResultSlot = addSlot(new BucketResultSlot(bucketContainer, 125, 104, tank, this, inv.player));
      } else {
        addSlot(new BucketSlot(bucketContainer, 0, 125, 46));
        bucketResultSlot = addSlot(new ResultSlot(bucketContainer, 1, 125, 104));
      }

      // can hold 7 in a column, so try to fill the first column first
      // cap to 4 columns
      MeltingModuleInventory inventory = structure.getMeltingInventory();
      sideInventory = new SideInventoryContainer<>(TinkerSmeltery.smelteryContainer.get(), id, inv, structure, 0, 0, calcColumns(inventory.getSlots()));
      addSubContainer(sideInventory, true);

      Consumer<DataSlot> referenceConsumer = this::addDataSlot;
      ValidZeroDataSlot.trackIntArray(referenceConsumer, structure.getFuelModule());
      inventory.trackInts(array -> ValidZeroDataSlot.trackIntArray(referenceConsumer, array));
    } else {
      sideInventory = null;
      bucketResultSlot = null;
    }
    addInventorySlots();
  }

  public HeatingStructureContainerMenu(int id, Inventory inv, FriendlyByteBuf buf) {
    this(id, inv, getTileEntityFromBuf(buf, HeatingStructureBlockEntity.class));
  }

  @Override
  protected int getInventoryYOffset() {
    return 138;
  }

  /** Updates the bucket inventory */
  private void updateBucket(Player player) {
    // changing mode means we might have an update
    if (!player.level().isClientSide && tile != null) {
      ItemStack bucket = bucketContainer.getItem(0);
      if (!bucket.isEmpty() && bucketContainer.getItem(1).isEmpty()) {
        TransferResult result = FluidTransferHelper.interactWithStack(tile.getTank(), bucket, transferDirection);
        if (result != null) {
          FluidTransferHelper.playUISound(player, result.getSound());
          bucketContainer.setItem(0, bucket);
          bucketContainer.setItem(1, result.stack());
          bucketContainer.setChanged();
        }
      }
    }
  }

  @Override
  public boolean clickMenuButton(Player player, int id) {
    if (player.isSpectator()) {
      return false;
    }
    // 0 means update transfer direction
    if (id == 0) {
      transferDirection = TRANSFER_DIRECTIONS[(transferDirection.ordinal() + 1) % TRANSFER_DIRECTIONS.length];
      updateBucket(player);
      return true;
    }
    // id 1 means drain fuel, id 2 means fill fuel
    // id 3 means we clicked the empty spot on the tank, just try dumping our item in
    if (1 <= id && id <= 3) {
      // if we have a held item, must not be empty
      ItemStack held = getCarried();
      if (held.isEmpty()) {
        return false;
      }
      // transfer the fluid
      if (!player.level().isClientSide && tile != null) {
        TransferResult result;
        if (id == 1) {
          // drain fuel into item
          MultitankFuelModule fuelModule = tile.getFuelModule();
          result = FluidTransferHelper.fillStack(fuelModule, held, fuelModule.getLastFluid());
        } else {
          // drain item into fuel/tank
          result = FluidTransferHelper.interactWithStack(id == 2 ? tile.getFuelModule() : tile.getTank(), held, TransferDirection.EMPTY_ITEM);
        }
        setCarried(FluidTransferHelper.handleUIResult(player, held, result));
      }
      return true;
    }
    // id 4+ means we clicked a fluid. Try filling our item with the fluid, otherwise change order
    if (id >= 4 && tile != null) {
      // ensure we have a valid index, if not ignore
      int index = id - 4;
      SmelteryTank<?> tank = tile.getTank();
      FluidStack fluid = tank.getFluidInTank(index);
      if (!fluid.isEmpty()) {
        if (!player.level().isClientSide) {
          ItemStack held = getCarried();
          if (!held.isEmpty()) {
            // if holding an item, fill it from the fluid
            TransferResult result = FluidTransferHelper.fillStack(tank, held, fluid);
            setCarried(FluidTransferHelper.handleUIResult(player, held, result));
          } else {
            // switch fluid order if not holding anything
            tank.moveFluidToBottom(index);
            // fluids changed, so try the slot again
            updateBucket(player);
          }
        }
        return true;
      }
    }

    return false;
  }

  @Override
  public void removed(Player player) {
    super.removed(player);
    if (!player.level().isClientSide) {
      this.clearContainer(player, bucketContainer);
    }
  }

  @Override
  public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
    return pSlot != bucketResultSlot && super.canTakeItemForPickAll(pStack, pSlot);
  }

  /**
   * Calculates the number of columns to use for the screen
   * @param slots  Number of slots
   * @return  Number of columns
   */
  public static int calcColumns(int slots) {
    // every 7 slots gives us a new column, up to a maximum of 4 columns
    return Math.min(4, (slots + 6) / 7);
  }

  /** Filtered slot instance, used on both sides to prevent placing non-fluid containers in the bucket slot */
  public static class BucketSlot extends Slot {
    public BucketSlot(Container pContainer, int pSlot, int pX, int pY) {
      super(pContainer, pSlot, pX, pY);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
      return FluidContainerTransferManager.INSTANCE.mayHaveTransfer(stack) || stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
    }
  }

  /** Bucket input slot - used serverside only to handle draining the bucket when placed in the slot */
  public static class BucketInputSlot extends BucketSlot {
    private final IFluidHandler tank;
    private final TransferDirectionSupplier directionSupplier;
    private final Player player;
    public BucketInputSlot(Container pContainer, int pX, int pY, IFluidHandler tank, TransferDirectionSupplier directionSupplier, Player player) {
      super(pContainer, 0, pX, pY);
      this.tank = tank;
      this.directionSupplier = directionSupplier;
      this.player = player;
    }

    @Override
    public void set(ItemStack stack) {
      if (!stack.isEmpty() && container.getItem(1).isEmpty()) {
        TransferResult result = FluidTransferHelper.interactWithStack(tank, stack, directionSupplier.getTransferDirection());
        if (result != null) {
          FluidTransferHelper.playUISound(player, result.getSound());
          container.setItem(1, result.stack());
        }
      }
      super.set(stack);
    }
  }

  /** Result slot - used both sides to disallow placing items in the result */
  public static class ResultSlot extends Slot {
    public ResultSlot(Container pContainer, int pSlot, int pX, int pY) {
      super(pContainer, pSlot, pX, pY);
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
      return false;
    }
  }

  /** Bucket result slot - used serverside to trigger fluid transfer when the slot is emptied */
  public static class BucketResultSlot extends ResultSlot {
    private final IFluidHandler tank;
    private final TransferDirectionSupplier directionSupplier;
    private final Player player;
    public BucketResultSlot(Container pContainer, int pX, int pY, IFluidHandler tank, TransferDirectionSupplier directionSupplier, Player player) {
      super(pContainer, 1, pX, pY);
      this.tank = tank;
      this.directionSupplier = directionSupplier;
      this.player = player;
    }

    @Override
    public void set(ItemStack stack) {
      if (stack.isEmpty()) {
        ItemStack bucket = container.getItem(0);
        if (!bucket.isEmpty()) {
          TransferResult result = FluidTransferHelper.interactWithStack(tank, bucket, directionSupplier.getTransferDirection());
          if (result != null) {
            FluidTransferHelper.playUISound(player, result.getSound());
            container.setItem(0, bucket);
            stack = result.stack();
          }
        }
      }
      super.set(stack);
    }
  }
}
