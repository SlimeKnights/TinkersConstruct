package slimeknights.tconstruct.gadgets.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EntityType;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.gadgets.entity.FancyItemFrameEntity;
import slimeknights.tconstruct.gadgets.entity.FrameType;

import org.jetbrains.annotations.Nonnull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

// TODO: needs so much cleanup
public class FancyItemFrameRenderer extends EntityRenderer<FancyItemFrameEntity> {

  private static final Identifier MAP_BACKGROUND_TEXTURES = new Identifier("textures/map/map_background.png");

  private static final Map<FrameType, ModelIdentifier> LOCATIONS_MODEL = new HashMap<>();
  private static final Map<FrameType, ModelIdentifier> LOCATIONS_MODEL_MAP = new HashMap<>();

  private final MinecraftClient mc = MinecraftClient.getInstance();
  private final ItemRenderer itemRenderer;
  private final ItemFrameEntityRenderer defaultRenderer;

  public FancyItemFrameRenderer(EntityRenderDispatcher renderManagerIn, ItemRenderer itemRendererIn) {
    super(renderManagerIn);
    this.itemRenderer = itemRendererIn;
    this.defaultRenderer = (ItemFrameEntityRenderer) renderManagerIn.renderers.get(EntityType.ITEM_FRAME);

    for (FrameType frameType : FrameType.values()) {
      // TODO: reinstate when Forge fixes itself
      // LOCATIONS_MODEL.put(color, new ModelResourceLocation(new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame"), "map=false"));
      // LOCATIONS_MODEL_MAP.put(color, new ModelResourceLocation(new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame"), "map=true"));

      LOCATIONS_MODEL.put(frameType, new ModelIdentifier(new Identifier(TConstruct.modID, frameType.asString() + "_frame_empty"), "inventory"));
      LOCATIONS_MODEL_MAP.put(frameType, new ModelIdentifier(new Identifier(TConstruct.modID, frameType.asString() + "_frame_map"), "inventory"));
    }
  }

  @Override
  public void render(FancyItemFrameEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn) {
    super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    matrixStackIn.push();
    Direction direction = entityIn.getHorizontalFacing();
    Vec3d vec3d = this.getRenderOffset(entityIn, partialTicks);
    matrixStackIn.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
    matrixStackIn.translate((double) direction.getOffsetX() * 0.46875D, (double) direction.getOffsetY() * 0.46875D, (double) direction.getOffsetZ() * 0.46875D);
    matrixStackIn.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(entityIn.pitch));
    matrixStackIn.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F - entityIn.yaw));
    BlockRenderManager blockrendererdispatcher = this.mc.getBlockRenderManager();
    BakedModelManager modelmanager = blockrendererdispatcher.getModels().getModelManager();
    FrameType frameType = entityIn.getFrameType();
    ModelIdentifier modelresourcelocation = entityIn.getHeldItemStack().getItem() instanceof FilledMapItem ? LOCATIONS_MODEL_MAP.get(frameType) : LOCATIONS_MODEL.get(frameType);
    matrixStackIn.push();
    matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
    blockrendererdispatcher.getModelRenderer().render(matrixStackIn.peek(), bufferIn.getBuffer(TexturedRenderLayers.getEntitySolid()), null, modelmanager.getModel(modelresourcelocation), 1.0F, 1.0F, 1.0F, packedLightIn, OverlayTexture.DEFAULT_UV);
    matrixStackIn.pop();
    ItemStack itemstack = entityIn.getHeldItemStack();
    if (!itemstack.isEmpty()) {
      MapState mapdata = FilledMapItem.getOrCreateMapState(itemstack, entityIn.world);
      matrixStackIn.translate(0.0D, 0.0D, 0.4375D);
      int i = mapdata != null ? entityIn.getRotation() % 4 * 2 : entityIn.getRotation();
      matrixStackIn.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float) i * 360.0F / 8.0F));
      if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderItemInFrameEvent(entityIn, this.defaultRenderer, matrixStackIn, bufferIn, packedLightIn))) {
        if (mapdata != null) {
          matrixStackIn.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
          float f = 0.0078125F;
          matrixStackIn.scale(0.0078125F, 0.0078125F, 0.0078125F);
          matrixStackIn.translate(-64.0D, -64.0D, 0.0D);
          matrixStackIn.translate(0.0D, 0.0D, -1.0D);
          this.mc.gameRenderer.getMapRenderer().draw(matrixStackIn, bufferIn, mapdata, true, packedLightIn);
        } else {
          matrixStackIn.scale(0.5F, 0.5F, 0.5F);
          this.itemRenderer.renderItem(itemstack, ModelTransformation.Mode.FIXED, packedLightIn, OverlayTexture.DEFAULT_UV, matrixStackIn, bufferIn);
        }
      }
    }

    matrixStackIn.pop();
  }

  @Nullable
  @Override
  public Identifier getEntityTexture(@Nonnull FancyItemFrameEntity entity) {
    return null;
  }

  @Override
  public Vec3d getRenderOffset(FancyItemFrameEntity entityIn, float partialTicks) {
    return new Vec3d((float) entityIn.getHorizontalFacing().getOffsetX() * 0.3F, -0.25D, (float) entityIn.getHorizontalFacing().getOffsetZ() * 0.3F);
  }

  @Override
  protected boolean canRenderName(FancyItemFrameEntity entity) {
    if (MinecraftClient.isHudEnabled() && !entity.getHeldItemStack().isEmpty() && entity.getHeldItemStack().hasCustomName() && this.dispatcher.targetedEntity == entity) {
      double d0 = this.dispatcher.getSquaredDistanceToCamera(entity);
      float f = entity.isSneaky() ? 32.0F : 64.0F;
      return d0 < (double) (f * f);
    } else {
      return false;
    }
  }

  @Override
  protected void renderName(FancyItemFrameEntity entityIn, Text displayNameIn, MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn) {
    super.renderLabelIfPresent(entityIn, entityIn.getHeldItemStack().getName(), matrixStackIn, bufferIn, packedLightIn);
  }
}
