package slimeknights.tconstruct.gadgets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItemFrame;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.gadgets.entity.EntityFancyItemFrame;
import slimeknights.tconstruct.library.Util;

@SideOnly(Side.CLIENT)
public class RenderFancyItemFrame extends RenderItemFrame {

  public static final IRenderFactory<EntityFancyItemFrame> FACTORY = new Factory();

  private final Minecraft mc = Minecraft.getMinecraft();

  public RenderFancyItemFrame(RenderManager renderManagerIn, RenderItem itemRendererIn) {
    super(renderManagerIn, itemRendererIn);
  }

  public static String getVariant(EntityFancyItemFrame.FrameType type, boolean withMap) {
    return String.format("map=%s,type=%s", withMap, type.toString());
  }

  @Override
  public void doRender(@Nonnull EntityItemFrame entity, double x, double y, double z, float entityYaw, float partialTicks) {
    EntityFancyItemFrame.FrameType type = ((EntityFancyItemFrame) entity).getType();

    GlStateManager.pushMatrix();
    BlockPos blockpos = entity.getHangingPosition();
    double d0 = blockpos.getX() - entity.posX + x;
    double d1 = blockpos.getY() - entity.posY + y;
    double d2 = blockpos.getZ() - entity.posZ + z;
    GlStateManager.translate(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D);
    GlStateManager.rotate(180.0F - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
    this.renderManager.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

    // we don't render the clear variant if it has an item
    if(entity.getDisplayedItem().isEmpty() || type != EntityFancyItemFrame.FrameType.CLEAR) {
      BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
      ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
      IBakedModel ibakedmodel;

      boolean withMap = entity.getDisplayedItem().getItem() == Items.FILLED_MAP;
      String variant = getVariant(((EntityFancyItemFrame) entity).getType(), withMap);

      ibakedmodel = modelmanager.getModel(Util.getModelResource("fancy_frame", variant));

      GlStateManager.pushMatrix();
      GlStateManager.translate(-0.5F, -0.5F, -0.5F);
      blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
    }
    GlStateManager.translate(0.0F, 0.0F, 0.4375F);
    if(type == EntityFancyItemFrame.FrameType.CLEAR) {
      GlStateManager.translate(0.0F, 0.0F, 0.03125F);
    }
    this.renderItem(entity);
    GlStateManager.popMatrix();
    assert entity.facingDirection != null;
    this.renderName(entity, x + entity.facingDirection.getFrontOffsetX() * 0.3F, y - 0.25D, z + entity.facingDirection.getFrontOffsetZ() * 0.3F);
  }

  private static class Factory implements IRenderFactory<EntityFancyItemFrame> {

    @Override
    public Render<? super EntityFancyItemFrame> createRenderFor(RenderManager manager) {
      return new RenderFancyItemFrame(manager, Minecraft.getMinecraft().getRenderItem());
    }
  }
}
