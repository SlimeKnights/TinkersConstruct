package slimeknights.tconstruct.smeltery.inventory;

import lombok.Getter;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import slimeknights.mantle.inventory.ItemHandlerSlot;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.utils.ValidZeroIntReference;
import slimeknights.tconstruct.shared.inventory.TriggeringBaseContainer;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.AlloyerTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.module.alloying.MixerAlloyTank;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class AlloyerContainer extends TriggeringBaseContainer<AlloyerTileEntity> {
  @Getter
  private boolean hasFuelSlot = false;
  public AlloyerContainer(int id, @Nullable PlayerInventory inv, @Nullable AlloyerTileEntity alloyer) {
    super(TinkerSmeltery.alloyerContainer.get(), id, inv, alloyer);

    // create slots
    if (alloyer != null) {
      // refresh cache of neighboring tanks
      World world = alloyer.getWorld();
      if (world != null && world.isRemote) {
        MixerAlloyTank alloyTank = alloyer.getAlloyTank();
        for (Direction direction : Direction.values()) {
          if (direction != Direction.DOWN) {
            alloyTank.refresh(direction, true);
          }
        }
      }

      // add fuel slot if present
      BlockPos down = alloyer.getPos().down();
      if (world != null && world.getBlockState(down).isIn(TinkerTags.Blocks.FUEL_TANKS)) {
        TileEntity te = world.getTileEntity(down);
        if (te != null) {
          hasFuelSlot = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).filter(handler -> {
            this.addSlot(new ItemHandlerSlot(handler, 0, 151, 32));
            return true;
          }).isPresent();
        }
      }

      this.addInventorySlots();

      // syncing
      Consumer<IntReferenceHolder> referenceConsumer = this::trackInt;
      ValidZeroIntReference.trackIntArray(referenceConsumer, alloyer.getFuelModule());
    }
  }

  public AlloyerContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, AlloyerTileEntity.class));
  }
}
