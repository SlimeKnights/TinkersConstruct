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
      String name = type == FrameType.REVERSED_GOLD ? FrameType.GOLD.getString() : type.getString();
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
    if (renderNameplate.getResult() == Result.ALLOW || (renderNameplate.getResult() != Result.DENY && this.canRenderName(frame))) {
      this.renderName(frame, renderNameplate.getContent(), matrices, bufferIn, packedLight);
    }

    // orient the renderer
    matrices.push();
    Direction facing = frame.getHorizontalFacing();
    Vector3d offset = this.getRenderOffset(frame, partialTicks);
    matrices.translate(facing.getXOffset() * 0.46875D - offset.getX(), facing.getYOffset() * 0.46875D - offset.getY(), facing.getZOffset() * 0.46875D - offset.getZ());
    matrices.rotate(Vector3f.XP.rotationDegrees(frame.rotationPitch));
    matrices.rotate(Vector3f.YP.rotationDegrees(180.0F - frame.rotationYaw));

    // render the frame
    ItemStack stack = frame.getDisplayedItem();
    boolean isMap = !stack.isEmpty() && stack.getItem() instanceof FilledMapItem;
    // clear does not render the frame if filled
    boolean frameVisible = !frame.isInvisible() && (frameType != FrameType.CLEAR || stack.isEmpty());
    if (frameVisible) {
      matrices.push();
      matrices.translate(-0.5D, -0.5D, -0.5D);
      BlockRendererDispatcher blockRenderer = this.mc.getBlockRendererDispatcher();
      blockRenderer.getBlockModelRenderer().renderModel(
        matrices.getLast(), bufferIn.getBuffer(Atlases.getCutoutBlockType()), null,
        blockRenderer.getBlockModelShapes().getModelManager().getModel(isMap ? LOCATIONS_MODEL_MAP.get(frameType) : LOCATIONS_MODEL.get(frameType)),
        1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
      matrices.pop();
    }

    // render the item
    if (!stack.isEmpty()) {
      // if no frame, offset the item farther back
      matrices.translate(0.0D, 0.0D, 0.4375D);

      // determine rotation for the item inside
      MapData mapdata = null;
      if (isMap) {
        mapdata = FilledMapItem.getMapData(stack, frame.world);
      }
      int frameRotation = frame.getRotation();
      // for diamond, render the timer as a partial rotation
      if (frameType == FrameType.DIAMOND) {
        int rotation = mapdata != null ? (frameRotation + 2) % 4 * 4 : frameRotation;
        matrices.rotate(Vector3f.ZP.rotationDegrees(rotation * 360f / 16f));
      } else {
        int rotation = mapdata != null ? (frameRotation + 2) % 4 * 2 : frameRotation;
        matrices.rotate(Vector3f.ZP.rotationDegrees(rotation * 360f / 8f));
      }
      if (!MinecraftForge.EVENT_BUS.post(new RenderItemInFrameEvent(frame, this, matrices, bufferIn, packedLight))) {
        if (mapdata != null) {
          matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);
          matrices.translate(-64.0D, -64.0D, -1.0D);
          this.mc.gameRenderer.getMapItemRenderer().renderMap(matrices, bufferIn, mapdata, true, packedLight);
        } else {
          float scale = frameType == FrameType.CLEAR ? 0.75f : 0.5f;
          matrices.scale(scale, scale, scale);
          this.itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED, packedLight, OverlayTexture.NO_OVERLAY, matrices, bufferIn);
        }
      }
    }

    matrices.pop();
  }
}
