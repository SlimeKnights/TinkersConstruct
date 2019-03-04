package slimeknights.tconstruct.tools.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.inventory.Container;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import io.netty.buffer.ByteBuf;
import slimeknights.mantle.network.AbstractPacket;
import slimeknights.tconstruct.tools.common.inventory.ContainerCraftingStation;

// not threadsafe!
public class LastRecipeMessage extends AbstractPacket {

  private IRecipe recipe;

  public LastRecipeMessage() {
  }

  public LastRecipeMessage(IRecipe recipe) {
    this.recipe = recipe;
  }

  @Override
  public IMessage handleClient(NetHandlerPlayClient netHandler) {
    Container container = Minecraft.getMinecraft().player.openContainer;
    if(container instanceof ContainerCraftingStation) {
      ((ContainerCraftingStation) container).updateLastRecipeFromServer(recipe);
    }
    return null;
  }

  @Override
  public IMessage handleServer(NetHandlerPlayServer netHandler) {
    // only sent to server
    throw new UnsupportedOperationException("Clientside only");
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    recipe = CraftingManager.REGISTRY.getObjectById(buf.readInt());
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(CraftingManager.REGISTRY.getIDForObject(recipe));
  }
}
