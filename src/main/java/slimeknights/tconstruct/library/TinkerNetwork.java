package slimeknights.tconstruct.library;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import slimeknights.mantle.network.NetworkWrapper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.network.UpdateMaterialsPacket;
import slimeknights.tconstruct.tools.common.network.BouncedPacket;
import slimeknights.tconstruct.tools.common.network.EntityMovementChangePacket;
import slimeknights.tconstruct.tools.common.network.InventorySlotSyncPacket;

public class TinkerNetwork extends NetworkWrapper {

  public static TinkerNetwork instance;

  private TinkerNetwork() {
    super(TConstruct.modID);
  }

  public SimpleChannel getChannel() {
    return network;
  }

  public void setup() {
    instance = new TinkerNetwork();
    this.registerPacket(EntityMovementChangePacket.class, EntityMovementChangePacket::encode, EntityMovementChangePacket::new, EntityMovementChangePacket::handle);
    this.registerPacket(BouncedPacket.class, BouncedPacket::encode, BouncedPacket::new, BouncedPacket::handle);
    this.registerPacket(InventorySlotSyncPacket.class, InventorySlotSyncPacket::encode, InventorySlotSyncPacket::new, InventorySlotSyncPacket::handle);

    this.registerPacket(UpdateMaterialsPacket.class, UpdateMaterialsPacket::encode, UpdateMaterialsPacket::new, UpdateMaterialsPacket::handle);
/*
    // OK this breaks login. Guess we need the event.
    this.network.messageBuilder(UpdateMaterialsPacketVanillaStyle.class, 55)
      .encoder((updateMaterialsPacket, buf) -> {
        updateMaterialsPacket.writePacketData(buf);
        Collection<IMaterial> materials = MaterialRegistry.getMaterials();
        buf.writeInt(materials.size());
        materials.forEach(material -> {
          buf.writeString(material.getIdentifier().toString());
          buf.writeBoolean(material.isCraftable());
          buf.writeString(material.getFluid().getRegistryName().toString());
          buf.writeItemStack(material.getShard());
        });
      })
      .decoder(packetBuffer -> {
        UpdateMaterialsPacketVanillaStyle updateMaterialsPacket = new UpdateMaterialsPacketVanillaStyle();
        updateMaterialsPacket.readPacketData(packetBuffer);
        return updateMaterialsPacket;
      })
      .consumer((updateMaterialsPacket, contextSupplier) -> {
        updateMaterialsPacket.processPacket(null);
        return true;
      })
      .markAsLoginPacket()
      .add();*/
  }

  public void sendVanillaPacket(Entity player, IPacket<?> packet) {
    if (player instanceof ServerPlayerEntity && ((ServerPlayerEntity) player).connection != null) {
      ((ServerPlayerEntity) player).connection.sendPacket(packet);
    }
  }

  public void sendToClientsAround(Object msg, ServerWorld serverWorld, BlockPos position) {
    Chunk chunk = serverWorld.getChunkAt(position);

    this.network.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), msg);
  }
}
