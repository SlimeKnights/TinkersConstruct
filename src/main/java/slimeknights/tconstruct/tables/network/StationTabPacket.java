package slimeknights.tconstruct.tables.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.impl.screenhandler.Networking;
import org.apache.logging.log4j.LogManager;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.shared.tileentity.TableTileEntity;
import slimeknights.tconstruct.world.WorldExtension;
import slimeknights.tconstruct.tables.block.ITinkerStationBlock;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class StationTabPacket implements IThreadsafePacket {

  private BlockPos pos;

  public StationTabPacket(BlockPos blockPos) {
    this.pos = blockPos;
  }

  public StationTabPacket(PacketByteBuf buffer) {
    this.pos = buffer.readBlockPos();
  }

  @Override
  public void encode(PacketByteBuf buffer) {
    buffer.writeBlockPos(pos);
  }

  @Override
  public void handle(PlayerEntity playerEntity, PacketSender sender) {
//    try {
//      Thread.currentThread().join();
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
//    ((WorldExtension)playerEntity.world).queuePacket(() -> handle(playerEntity, sender));
    handleThreadsafe(playerEntity, sender);
  }

  @Override
  public void handleThreadsafe(PlayerEntity player, PacketSender context) {
    ServerPlayerEntity sender = (ServerPlayerEntity) player;
    LogManager.getLogger().info("SIDE: "+player);
    LogManager.getLogger().info("SIDE: "+Thread.currentThread());
    if (sender != null) {
      ItemStack heldStack = sender.inventory.getCursorStack();
      if (!heldStack.isEmpty()) {
        // set it to empty, so it's doesn't get dropped
        sender.inventory.setCursorStack(ItemStack.EMPTY);
      }

      BlockState state = sender.getEntityWorld().getBlockState(pos);
      if (state.getBlock() instanceof ITinkerStationBlock) {
          ((ITinkerStationBlock) state.getBlock()).openGui(sender, sender.getEntityWorld(), pos);
      }

      if (!heldStack.isEmpty()) {
        sender.inventory.setCursorStack(heldStack);
        TinkerNetwork.getInstance().sendVanillaPacket(sender, new ScreenHandlerSlotUpdateS2CPacket(-1, -1, heldStack));
      }
    }
  }
}
