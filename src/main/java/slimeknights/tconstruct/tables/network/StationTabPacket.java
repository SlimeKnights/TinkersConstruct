package slimeknights.tconstruct.tables.network;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import org.apache.logging.log4j.LogManager;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tables.block.ITinkerStationBlock;
import slimeknights.tconstruct.tables.block.TinkerTableBlock;
import java.util.function.Consumer;

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
  public void handleThreadsafe(PlayerEntity player, PacketSender context) {
    LogManager.getLogger().info("DUHHH, its always crab");
    ServerPlayerEntity sender = (ServerPlayerEntity) player;
    if (sender != null) {
      ItemStack heldStack = sender.inventory.getCursorStack();
      if (!heldStack.isEmpty()) {
        // set it to empty, so it's doesn't get dropped
        sender.inventory.setCursorStack(ItemStack.EMPTY);
      }

      BlockState state = sender.getEntityWorld().getBlockState(pos);
      if (state.getBlock() instanceof ITinkerStationBlock) {
        //FIXME: This does not work for whatever reason
        ((ITinkerStationBlock) state.getBlock()).openGui(sender, sender.getEntityWorld(), pos);
      } else {

        NamedScreenHandlerFactory provider = state.createScreenHandlerFactory(sender.getEntityWorld(), pos);
        if (provider != null) {

          ((ITinkerStationBlock) state.getBlock()).openGui(sender, sender.getEntityWorld(), pos);
          //throw new RuntimeException("CRAB!");
          //TODO: PORT
          //NetworkHooks.openGui(sender, provider, pos);
        }
      }

      if (!heldStack.isEmpty()) {
        sender.inventory.setCursorStack(heldStack);
        TinkerNetwork.getInstance().sendVanillaPacket(sender, new ScreenHandlerSlotUpdateS2CPacket(-1, -1, heldStack));
      }
    }
  }
}
