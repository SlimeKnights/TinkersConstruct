package slimeknights.tconstruct.library.client.crosshair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class CrosshairRenderEvents {

  public static final CrosshairRenderEvents INSTANCE = new CrosshairRenderEvents();

  private static final Minecraft mc = Minecraft.getMinecraft();

  private CrosshairRenderEvents() {}

  @SubscribeEvent
  public void onCrosshairRender(RenderGameOverlayEvent event) {
    if(event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
      return;
    }

    EntityPlayer entityPlayer = mc.thePlayer;
    ItemStack itemStack = getItemstack(entityPlayer);

    if(itemStack == null) {
      return;
    }

    ICustomCrosshairUser customCrosshairUser = (ICustomCrosshairUser) itemStack.getItem();
    ICrosshair crosshair = customCrosshairUser.getCrosshair(itemStack, entityPlayer);

    if(crosshair == ICrosshair.DEFAULT) {
      return;
    }

    float width = event.getResolution().getScaledWidth();
    float height = event.getResolution().getScaledHeight();

    crosshair.render(customCrosshairUser.getCrosshairState(itemStack, entityPlayer), width, height, event.getPartialTicks());

    event.setCanceled(true);

    // restore gui texture for following draw calls
    mc.getTextureManager().bindTexture(Gui.ICONS);
  }

  private ItemStack getItemstack(EntityPlayer entityPlayer) {
    ItemStack itemStack = null;
    if(entityPlayer.isHandActive() && isValidItem(entityPlayer.getActiveItemStack())) {
      itemStack = entityPlayer.getActiveItemStack();
    }
    if(itemStack == null && isValidItem(entityPlayer.getHeldItemMainhand())) {
      itemStack = entityPlayer.getHeldItemMainhand();
    }
    if(itemStack == null && isValidItem(entityPlayer.getHeldItemOffhand())) {
      itemStack = entityPlayer.getHeldItemOffhand();
    }

    return itemStack;
  }

  private boolean isValidItem(ItemStack itemStack) {
    return itemStack != null && itemStack.getItem() instanceof ICustomCrosshairUser;
  }
}
