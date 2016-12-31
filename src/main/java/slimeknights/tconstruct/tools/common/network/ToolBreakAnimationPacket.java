package slimeknights.tconstruct.tools.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import io.netty.buffer.ByteBuf;
import slimeknights.mantle.network.AbstractPacketThreadsafe;

public class ToolBreakAnimationPacket extends AbstractPacketThreadsafe {

  public ItemStack breakingTool;

  public ToolBreakAnimationPacket() {
  }

  public ToolBreakAnimationPacket(ItemStack breakingTool) {
    this.breakingTool = breakingTool;
  }

  @Override
  public void handleClientSafe(NetHandlerPlayClient netHandler) {
    // play the animation
    Minecraft.getMinecraft().player.renderBrokenItemStack(breakingTool);
  }

  @Override
  public void handleServerSafe(NetHandlerPlayServer netHandler) {
    // clientside only
    throw new UnsupportedOperationException("Clientside only");
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    breakingTool = ByteBufUtils.readItemStack(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    ByteBufUtils.writeItemStack(buf, breakingTool);
  }
}
