package slimeknights.tconstruct.smeltery.inventory;

import lombok.Getter;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.Property;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.mantle.inventory.ItemHandlerSlot;
import slimeknights.tconstruct.library.utils.ValidZeroIntReference;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.MelterTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.module.MeltingModuleInventory;

import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

public class MelterContainer extends BaseContainer<MelterTileEntity> {
  @SuppressWarnings("MismatchedReadAndWriteOfArray")
  @Getter
  private final Slot[] inputs;
  @Getter
  private boolean hasFuelSlot = false;
  public MelterContainer(int id, @Nullable PlayerInventory inv, @Nullable MelterTileEntity melter) {
    super(TinkerSmeltery.melterContainer.get(), id, inv, melter);

    // create slots
    if (melter != null) {
      MeltingModuleInventory inventory = melter.getMeltingInventory();
      inputs = new Slot[inventory.getSlots()];
      for (int i = 0; i < inputs.length; i++) {
        inputs[i] = this.addSlot(new ItemHandlerSlot(inventory, i, 22, 16 + (i * 18)));
      }

      // add fuel slot if present, we only add for the melter though
      World world = melter.getWorld();
      BlockPos down = melter.getPos().down();
      if (world != null && world.getBlockState(down).isOf(TinkerSmeltery.searedHeater.get())) {
        BlockEntity te = world.getBlockEntity(down);
        if (te != null) {
          hasFuelSlot = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).filter(handler -> {
            this.addSlot(new ItemHandlerSlot(handler, 0, 151, 32));
            return true;
          }).isPresent();
        }
      }

      this.addInventorySlots();

      // syncing
      Consumer<Property> referenceConsumer = this::addProperty;
      ValidZeroIntReference.trackIntArray(referenceConsumer, melter.getFuelModule());
      inventory.trackInts(array -> ValidZeroIntReference.trackIntArray(referenceConsumer, array));
    } else {
      inputs = new Slot[0];
    }
  }

  public MelterContainer(int id, PlayerInventory inv, PacketByteBuf buf) {
    this(id, inv, getTileEntityFromBuf(buf, MelterTileEntity.class));
  }
}
