package slimeknights.tconstruct.library.client.crosshair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public final class CrosshairRenderEvents {

  public static final CrosshairRenderEvents INSTANCE = new CrosshairRenderEvents();

  private static final Minecraft mc = Minecraft.getMinecraft();

  private CrosshairRenderEvents() {}

  @SubscribeEvent
  public void onCrosshairRender(RenderGameOverlayEvent.Pre event) {
    if(event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
      return;
    }

    EntityPlayer entityPlayer = mc.player;
    ItemStack itemStack = getItemstack(entityPlayer);

    if(itemStack.isEmpty()) {
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


    // damage cooldown indicator
    if(mc.gameSettings.attackIndicator == 1) {
      int resW = event.getResolution().getScaledWidth();
      int resH = event.getResolution().getScaledHeight();

      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.enableAlpha();
      float f = mc.player.getCooledAttackStrength(0.0F);

      if(f < 1.0F) {
        int i = resH / 2 - 7 + 16;
        int j = resW / 2 - 7;
        int k = (int) (f * 17.0F);
        mc.ingameGUI.drawTexturedModalRect(j, i, 36, 94, 16, 4);
        mc.ingameGUI.drawTexturedModalRect(j, i, 52, 94, k, 4);
      }
    }


    OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
    GlStateManager.disableBlend();
  }

  @Nonnull
  private ItemStack getItemstack(EntityPlayer entityPlayer) {
    ItemStack itemStack = ItemStack.EMPTY;
    if(entityPlayer.isHandActive() && isValidItem(entityPlayer.getActiveItemStack())) {
      itemStack = entityPlayer.getActiveItemStack();
    }
    if(itemStack.isEmpty() && isValidItem(entityPlayer.getHeldItemMainhand())) {
      itemStack = entityPlayer.getHeldItemMainhand();
    }
    if(itemStack.isEmpty() && isValidItem(entityPlayer.getHeldItemOffhand())) {
      itemStack = entityPlayer.getHeldItemOffhand();
    }

    return itemStack;
  }

  private boolean isValidItem(ItemStack itemStack) {
    return itemStack.getItem() instanceof ICustomCrosshairUser;
  }
}
