package slimeknights.tconstruct.gadgets.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event.Result;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.gadgets.entity.FancyItemFrameEntity;
import slimeknights.tconstruct.gadgets.entity.FrameType;

import java.util.EnumMap;
import java.util.Map;

public class FancyItemFrameRenderer extends ItemFrameRenderer {
  public static final Map<FrameType, ResourceLocation> LOCATIONS_MODEL = new EnumMap<>(FrameType.class);
  public static final Map<FrameType, ResourceLocation> LOCATIONS_MODEL_MAP = new EnumMap<>(FrameType.class);
  static {
    for (FrameType type : FrameType.values()) {
      String name = type == FrameType.REVERSED_GOLD ? FrameType.GOLD.getSerializedName() : type.getSerializedName();
      LOCATIONS_MODEL.put(type, TConstruct.getResource("block/frame/" + name));
      LOCATIONS_MODEL_MAP.put(type, TConstruct.getResource("block/frame/" + name + "_map"));
    }
  }

  public FancyItemFrameRenderer(EntityRendererManager rendererManager, ItemRenderer itemRenderer) {
    super(rendererManager, itemRenderer);
  }

  @Override
  public void render(ItemFrameEntity entity, float entityYaw, float partialTicks, MatrixStack matrices, IRenderTypeBuffer bufferIn, int packedLight) {
    // we know its our entity, so cast is safe
    FancyItemFrameEntity frame = (FancyItemFrameEntity) entity;
    FrameType frameType = frame.getFrameType();
    if (frameType == FrameType.MANYULLYN) {
      packedLight = 0x00F000F0;
    }

    // base entity rendering logic, since calling super gives us the item frame renderer
    RenderNameplateEvent renderNameplate = new RenderNameplateEvent(frame, frame.getDisplayName(), this, matrices, bufferIn, packedLight, partialTicks);
    MinecraftForge.EVENT_BUS.post(renderNameplate);
    if (renderNameplate.getResult() == Result.ALLOW || (renderNameplate.getResult() != Result.DENY && this.shouldShowName(frame))) {
      this.renderNameTag(frame, renderNameplate.getContent(), matrices, bufferIn, packedLight);
    }

    // orient the renderer
    matrices.pushPose();
    Direction facing = frame.getDirection();
    Vector3d offset = this.getRenderOffset(frame, partialTicks);
    matrices.translate(facing.getStepX() * 0.46875D - offset.x(), facing.getStepY() * 0.46875D - offset.y(), facing.getStepZ() * 0.46875D - offset.z());
    matrices.mulPose(Vector3f.XP.rotationDegrees(frame.xRot));
    matrices.mulPose(Vector3f.YP.rotationDegrees(180.0F - frame.yRot));

    // render the frame
    ItemStack stack = frame.getItem();
    boolean isMap = !stack.isEmpty() && stack.getItem() instanceof FilledMapItem;
    // clear does not render the frame if filled
    boolean frameVisible = !frame.isInvisible() && (frameType != FrameType.CLEAR || stack.isEmpty());
    if (frameVisible) {
      matrices.pushPose();
      matrices.translate(-0.5D, -0.5D, -0.5D);
      BlockRendererDispatcher blockRenderer = this.minecraft.getBlockRenderer();
      blockRenderer.getModelRenderer().renderModel(
        matrices.last(), bufferIn.getBuffer(Atlases.cutoutBlockSheet()), null,
        blockRenderer.getBlockModelShaper().getModelManager().getModel(isMap ? LOCATIONS_MODEL_MAP.get(frameType) : LOCATIONS_MODEL.get(frameType)),
        1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
      matrices.popPose();
    }

    // render the item
    if (!stack.isEmpty()) {
      // if no frame, offset the item farther back
      matrices.translate(0.0D, 0.0D, 0.4375D);

      // determine rotation for the item inside
      MapData mapdata = null;
      if (isMap) {
        mapdata = FilledMapItem.getOrCreateSavedData(stack, frame.level);
      }
      int frameRotation = frame.getRotation();
      // for diamond, render the timer as a partial rotation
      if (frameType == FrameType.DIAMOND) {
        int rotation = mapdata != null ? (frameRotation + 2) % 4 * 4 : frameRotation;
        matrices.mulPose(Vector3f.ZP.rotationDegrees(rotation * 360f / 16f));
      } else {
        int rotation = mapdata != null ? (frameRotation + 2) % 4 * 2 : frameRotation;
        matrices.mulPose(Vector3f.ZP.rotationDegrees(rotation * 360f / 8f));
      }
      if (!MinecraftForge.EVENT_BUS.post(new RenderItemInFrameEvent(frame, this, matrices, bufferIn, packedLight))) {
        if (mapdata != null) {
          matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);
          matrices.translate(-64.0D, -64.0D, -1.0D);
          this.minecraft.gameRenderer.getMapRenderer().render(matrices, bufferIn, mapdata, true, packedLight);
        } else {
          float scale = frameType == FrameType.CLEAR ? 0.75f : 0.5f;
          matrices.scale(scale, scale, scale);
          this.itemRenderer.renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, packedLight, OverlayTexture.NO_OVERLAY, matrices, bufferIn);
        }
      }
    }

    matrices.popPose();
  }
}
