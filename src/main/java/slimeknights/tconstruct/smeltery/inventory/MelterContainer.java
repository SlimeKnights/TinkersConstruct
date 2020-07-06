package slimeknights.tconstruct.smeltery.inventory;

import lombok.Getter;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tconstruct.library.utils.IntArrayWrapper;
import slimeknights.tconstruct.library.utils.LambdaIntReference;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.MelterTileEntity;

import javax.annotation.Nullable;

public class MelterContainer extends BaseContainer<MelterTileEntity> {
  @Getter
  private final Slot[] inputs;
  public MelterContainer(int id, @Nullable PlayerInventory inv, MelterTileEntity melter) {
    super(TinkerSmeltery.melterContainer.get(), id, inv, melter);

    IIntArray array;

    // create slots
    int size = melter.getSizeInventory();
    inputs = new Slot[size];
    for (int i = 0; i < inputs.length; i++) {
      inputs[i] = this.addSlot(new Slot(melter, i, 22, 16 + (i * 18)));
    }
    this.addInventorySlots();

    // syncing
    this.trackInt(new LambdaIntReference(melter::getFuel, melter::setFuel));
    this.trackInt(new LambdaIntReference(melter::getTemperature, melter::setTemperature));
    this.trackIntArray(new IntArrayWrapper(melter::getItemTemperatures));
    this.trackIntArray(new IntArrayWrapper(melter::getItemTempRequired));
  }

  public MelterContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, MelterTileEntity.class));
  }
}
