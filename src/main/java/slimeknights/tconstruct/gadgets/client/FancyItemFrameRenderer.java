package slimeknights.tconstruct.gadgets.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.gadgets.entity.FancyItemFrameEntity;
import slimeknights.tconstruct.gadgets.entity.FrameType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class FancyItemFrameRenderer extends EntityRenderer<FancyItemFrameEntity> {

  private static final ResourceLocation MAP_BACKGROUND_TEXTURES = new ResourceLocation("textures/map/map_background.png");

  private static final Map<FrameType, ModelResourceLocation> LOCATIONS_MODEL = new HashMap<>();
  private static final Map<FrameType, ModelResourceLocation> LOCATIONS_MODEL_MAP = new HashMap<>();

  private final Minecraft mc = Minecraft.getInstance();
  private final ItemRenderer itemRenderer;
  private final ItemFrameRenderer defaultRenderer;

  public FancyItemFrameRenderer(EntityRendererManager renderManagerIn, ItemRenderer itemRendererIn) {
    super(renderManagerIn);
    this.itemRenderer = itemRendererIn;
    this.defaultRenderer = (ItemFrameRenderer) renderManagerIn.renderers.get(EntityType.ITEM_FRAME);

    for (FrameType frameType : FrameType.values()) {
      // TODO: reinstate when Forge fixes itself
      // LOCATIONS_MODEL.put(color, new ModelResourceLocation(new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame"), "map=false"));
      // LOCATIONS_MODEL_MAP.put(color, new ModelResourceLocation(new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame"), "map=true"));

      LOCATIONS_MODEL.put(frameType, new ModelResourceLocation(new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame_empty"), "inventory"));
      LOCATIONS_MODEL_MAP.put(frameType, new ModelResourceLocation(new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame_map"), "inventory"));
    }
  }

  @Override
  public void render(FancyItemFrameEntity entity, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225623_6_) {
    super.render(entity, p_225623_2_, p_225623_3_, matrixStack, renderTypeBuffer, p_225623_6_);
    matrixStack.push();
    Direction direction = entity.getHorizontalFacing();
    Vec3d vec3d = this.func_225627_b_(entity, p_225623_3_);
    matrixStack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
    double d0 = 0.46875D;
    matrixStack.translate((double) direction.getXOffset() * 0.46875D, (double) direction.getYOffset() * 0.46875D, (double) direction.getZOffset() * 0.46875D);
    matrixStack.rotate(Vector3f.field_229179_b_.func_229187_a_(entity.rotationPitch));
    matrixStack.rotate(Vector3f.field_229181_d_.func_229187_a_(180.0F - entity.rotationYaw));
    BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
    ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
    FrameType frameType = entity.getFrameType();
    ModelResourceLocation modelresourcelocation = entity.getDisplayedItem().getItem() instanceof FilledMapItem ? LOCATIONS_MODEL_MAP.get(frameType) : LOCATIONS_MODEL.get(frameType);
    matrixStack.push();
    matrixStack. translate(-0.5D, -0.5D, -0.5D);
    blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(matrixStack.getLast(), renderTypeBuffer.getBuffer(Atlases.func_228782_g_()), null, modelmanager.getModel(modelresourcelocation), 1.0F, 1.0F, 1.0F, p_225623_6_, OverlayTexture.DEFAULT_LIGHT);
    matrixStack.pop();
    ItemStack itemstack = entity.getDisplayedItem();
    if (!itemstack.isEmpty()) {
      MapData mapdata = FilledMapItem.getMapData(itemstack, entity.world);
      matrixStack. translate(0.0D, 0.0D, 0.4375D);
      int i = mapdata != null ? entity.getRotation() % 4 * 2 : entity.getRotation();
      matrixStack.rotate(Vector3f.field_229183_f_.func_229187_a_((float) i * 360.0F / 8.0F));
      if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderItemInFrameEvent(entity, this.defaultRenderer))) {
        if (mapdata != null) {
          matrixStack.rotate(Vector3f.field_229183_f_.func_229187_a_(180.0F));
          float f = 0.0078125F;
          matrixStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
          matrixStack. translate(-64.0D, -64.0D, 0.0D);
          matrixStack. translate(0.0D, 0.0D, -1.0D);
          if (mapdata != null) {
            this.mc.gameRenderer.getMapItemRenderer().renderMap(matrixStack, renderTypeBuffer, mapdata, true, p_225623_6_);
          }
        }
        else {
          matrixStack.scale(0.5F, 0.5F, 0.5F);
          this.itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED, p_225623_6_, OverlayTexture.DEFAULT_LIGHT, matrixStack, renderTypeBuffer);
        }
      }
    }

    matrixStack.pop();
  }

  @Nullable
  @Override
  public ResourceLocation getEntityTexture(@Nonnull FancyItemFrameEntity entity) {
    return null;
  }

  public Vec3d getOffsetPosition(FancyItemFrameEntity p_225627_1_, float p_225627_2_) {
    return new Vec3d((float) p_225627_1_.getHorizontalFacing().getXOffset() * 0.3F, -0.25D, (float) p_225627_1_.getHorizontalFacing().getZOffset() * 0.3F);
  }

  @Override
  protected boolean canRenderName(FancyItemFrameEntity entity) {
    if (Minecraft.isGuiEnabled() && !entity.getDisplayedItem().isEmpty() && entity.getDisplayedItem().hasDisplayName() && this.renderManager.pointedEntity == entity) {
      double d0 = this.renderManager.func_229099_b_(entity);
      float f = entity.func_226273_bm_() ? 32.0F : 64.0F;
      return d0 < (double) (f * f);
    }
    else {
      return false;
    }
  }

  @Override
  protected void func_225629_a_(FancyItemFrameEntity itemFrameEntity, String name, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int lighting) {
    super.func_225629_a_(itemFrameEntity, itemFrameEntity.getDisplayedItem().getDisplayName().getFormattedText(), matrixStack, renderTypeBuffer, lighting);
  }
}
