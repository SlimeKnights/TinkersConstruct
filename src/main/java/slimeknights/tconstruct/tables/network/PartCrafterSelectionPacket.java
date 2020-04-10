package slimeknights.tconstruct.tables.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import slimeknights.mantle.network.AbstractPacket;
import slimeknights.tconstruct.tables.inventory.table.PartBuilderContainer;

import java.util.function.Supplier;

public class PartCrafterSelectionPacket extends AbstractPacket {

  public ItemStack pattern;

  public PartCrafterSelectionPacket(ItemStack pattern) {
    this.pattern = pattern;
  }

  public PartCrafterSelectionPacket(PacketBuffer packetBuffer) {
    this.pattern = packetBuffer.readItemStack();
  }

  @Override
  public void encode(PacketBuffer packetBuffer) {
    packetBuffer.writeItemStack(this.pattern);
  }

  @Override
  public void handle(Supplier<NetworkEvent.Context> supplier) {
    supplier.get().enqueueWork(() -> {
      if (supplier.get().getDirection().getReceptionSide() == LogicalSide.SERVER) {
        if (supplier.get().getSender() != null) {
          ServerPlayerEntity playerEntity = supplier.get().getSender();
          Container container = playerEntity.openContainer;

          if (container instanceof PartBuilderContainer) {
            ((PartBuilderContainer) container).setPattern(this.pattern);
          }
        }
      }
    });

    supplier.get().setPacketHandled(true);
  }
}
