package slimeknights.tconstruct.smeltery.menu;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;
import slimeknights.mantle.fluid.FluidTransferHelper;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer.TransferDirection;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer.TransferResult;
import slimeknights.mantle.inventory.SmartItemHandlerSlot;
import slimeknights.mantle.util.sync.ValidZeroDataSlot;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.shared.inventory.TriggeringBaseContainerMenu;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.controller.AlloyerBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.alloying.MixerAlloyTank;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class AlloyerContainerMenu extends TriggeringBaseContainerMenu<AlloyerBlockEntity> {
  public static final ResourceLocation TOOLTIP_FORMAT = TConstruct.getResource("alloyer");

  @Getter
  private boolean hasFuelSlot = false;
  public AlloyerContainerMenu(int id, @Nullable Inventory inv, @Nullable AlloyerBlockEntity alloyer) {
    super(TinkerSmeltery.alloyerContainer.get(), id, inv, alloyer);

    // create slots
    if (alloyer != null) {
      // refresh cache of neighboring tanks
      Level world = alloyer.getLevel();
      if (world != null && world.isClientSide) {
        MixerAlloyTank alloyTank = alloyer.getAlloyTank();
        for (Direction direction : Direction.values()) {
          if (direction != Direction.DOWN) {
            alloyTank.refresh(direction, true);
          }
        }
      }

      // add fuel slot if present
      BlockPos down = alloyer.getBlockPos().below();
      if (world != null && world.getBlockState(down).is(TinkerTags.Blocks.FUEL_TANKS)) {
        BlockEntity te = world.getBlockEntity(down);
        if (te != null) {
          hasFuelSlot = te.getCapability(ForgeCapabilities.ITEM_HANDLER).filter(handler -> {
            this.addSlot(new SmartItemHandlerSlot(handler, 0, 151, 32));
            return true;
          }).isPresent();
        }
      }

      this.addInventorySlots();

      // syncing
      Consumer<DataSlot> referenceConsumer = this::addDataSlot;
      ValidZeroDataSlot.trackIntArray(referenceConsumer, alloyer.getFuelModule());
    }
  }

  public AlloyerContainerMenu(int id, Inventory inv, FriendlyByteBuf buf) {
    this(id, inv, getTileEntityFromBuf(buf, AlloyerBlockEntity.class));
  }

  @Override
  public boolean clickMenuButton(Player player, int id) {
    ItemStack held = getCarried();
    if (id >= 0 && !held.isEmpty() && !player.isSpectator()) {
      if (!player.level().isClientSide && tile != null) {
        int index = id / 2;
        IFluidHandler handler;
        // first index is the internal tank
        if (index == 0) {
          handler = tile.getTank();
        } else if (index == 1) {
          handler = tile.getFuelModule().getTank();
        } else {
          // index 2 and onwards is a handler tank
          handler = tile.getAlloyTank().getFluidHandler(index - 2);
        }
        // invalid index would make the handler empty through the alloy tank
        if (handler != EmptyFluidHandler.INSTANCE) {
          // even numbers are fill, odd are drain
          TransferResult result = FluidTransferHelper.interactWithStack(handler, held, (id & 1) == 0 ? TransferDirection.FILL_ITEM : TransferDirection.EMPTY_ITEM);
          setCarried(FluidTransferHelper.handleUIResult(player, held, result));
        }
      }
      return true;
    }
    return false;
  }
}
