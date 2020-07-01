package slimeknights.tconstruct.smeltery.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.MelterTileEntity;

import javax.annotation.Nullable;

public class MelterContainer extends BaseContainer<MelterTileEntity> {
  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/partbuilder.png");

  private final Slot[] inputs;
  public MelterContainer(int id, @Nullable PlayerInventory inv, MelterTileEntity melter) {
    super(TinkerSmeltery.melterContainer.get(), id, inv, melter);

    // create slots
    inputs = new Slot[melter.getSizeInventory()];
    for (int i = 0; i < inputs.length; i++) {
      inputs[i] = this.addSlot(new Slot(melter, i, 22, 16 + (i * 18)));
    }

    this.addInventorySlots();
  }

  public MelterContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, MelterTileEntity.class));
  }
}
