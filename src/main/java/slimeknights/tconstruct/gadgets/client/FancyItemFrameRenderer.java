package slimeknights.tconstruct.gadgets.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event.Result;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.gadgets.entity.FancyItemFrameEntity;
import slimeknights.tconstruct.gadgets.entity.FrameType;

import java.util.EnumMap;
import java.util.Map;

public class FancyItemFrameRenderer<T extends FancyItemFrameEntity> extends ItemFrameRenderer<T> {
  public static final Map<FrameType, ResourceLocation> LOCATIONS_MODEL = new EnumMap<>(FrameType.class);
  public static final Map<FrameType, ResourceLocation> LOCATIONS_MODEL_MAP = new EnumMap<>(FrameType.class);
  static {
    for (FrameType type : FrameType.values()) {
      String name = type == FrameType.REVERSED_GOLD ? FrameType.GOLD.getSerializedName() : type.getSerializedName();
      LOCATIONS_MODEL.put(type, TConstruct.getResource("block/frame/" + name));
      LOCATIONS_MODEL_MAP.put(type, TConstruct.getResource("block/frame/" + name + "_map"));
    }
  }

  public FancyItemFrameRenderer(EntityRendererProvider.Context context) {
    super(context);
  }

  @Override
  protected int getBlockLightLevel(T frame, BlockPos pPos) {
    int baseLight = super.getBlockLightLevel(frame, pPos);
    return frame.getFrameType() == FrameType.MANYULLYN ? Math.max(7, baseLight) : baseLight;
  }

  @SuppressWarnings({"UnstableApiUsage", "deprecation"})  // no thanks forge, I'm matching vanilla behavior so I'll call the methods there
  // seriously forge, how am I supposed to implement something like vanilla if I cannot create events?
  @Override
  public void render(T frame, float entityYaw, float partialTicks, PoseStack matrices, MultiBufferSource bufferIn, int packedLight) {
    FrameType frameType = frame.getFrameType();

    // base entity rendering logic, since calling super gives us the item frame renderer that we are replacing
    RenderNameTagEvent renderNameplate = new RenderNameTagEvent(frame, frame.getDisplayName(), this, matrices, bufferIn, packedLight, partialTicks);
    MinecraftForge.EVENT_BUS.post(renderNameplate);
    if (renderNameplate.getResult() == Result.ALLOW || (renderNameplate.getResult() != Result.DENY && this.shouldShowName(frame))) {
      this.renderNameTag(frame, renderNameplate.getContent(), matrices, bufferIn, packedLight);
    }

    // orient the renderer
    matrices.pushPose();
    Direction facing = frame.getDirection();
    Vec3 offset = this.getRenderOffset(frame, partialTicks);
    matrices.translate(facing.getStepX() * 0.46875D - offset.x(), facing.getStepY() * 0.46875D - offset.y(), facing.getStepZ() * 0.46875D - offset.z());
    matrices.mulPose(Axis.XP.rotationDegrees(frame.getXRot()));
    matrices.mulPose(Axis.YP.rotationDegrees(180.0F - frame.getYRot()));

    // render the frame
    ItemStack stack = frame.getItem();
    boolean isMap = !stack.isEmpty() && stack.getItem() instanceof MapItem;
    // clear does not render the frame if filled
    boolean frameVisible = !frame.isInvisible() && (frameType != FrameType.CLEAR || stack.isEmpty());
    if (frameVisible) {
      matrices.pushPose();
      matrices.translate(-0.5D, -0.5D, -0.5D);
      blockRenderer.getModelRenderer().renderModel(
        matrices.last(), bufferIn.getBuffer(Sheets.cutoutBlockSheet()), null,
        blockRenderer.getBlockModelShaper().getModelManager().getModel(isMap ? LOCATIONS_MODEL_MAP.get(frameType) : LOCATIONS_MODEL.get(frameType)),
        1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.NO_OVERLAY);
      matrices.popPose();
    }

    // render the item
    if (!stack.isEmpty()) {
      // if no frame, offset the item farther back
      matrices.translate(0.0D, 0.0D, 0.4375D);

      // determine rotation for the item inside
      MapItemSavedData mapdata = null;
      if (isMap) {
        mapdata = MapItem.getSavedData(stack, frame.level());
      }
      int frameRotation = frame.getRotation();
      // for diamond, render the timer as a partial rotation
      if (frameType == FrameType.DIAMOND) {
        int rotation = mapdata != null ? (frameRotation + 2) % 4 * 4 : frameRotation;
        matrices.mulPose(Axis.ZP.rotationDegrees(rotation * 360f / 16f));
      } else {
        int rotation = mapdata != null ? (frameRotation + 2) % 4 * 2 : frameRotation;
        matrices.mulPose(Axis.ZP.rotationDegrees(rotation * 360f / 8f));
      }
      if (!MinecraftForge.EVENT_BUS.post(new RenderItemInFrameEvent(frame, this, matrices, bufferIn, packedLight))) {
        if (mapdata != null) {
          matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);
          matrices.translate(-64.0D, -64.0D, -1.0D);
          int light = frameType == FrameType.MANYULLYN ? 0x00F000F0 : packedLight;
          Integer mapId = MapItem.getMapId(stack);
          assert mapId != null;
          Minecraft.getInstance().gameRenderer.getMapRenderer().render(matrices, bufferIn, mapId, mapdata, true, light);
        } else {
          float scale = frameType == FrameType.CLEAR ? 0.75f : 0.5f;
          matrices.scale(scale, scale, scale);
          int light = frameType == FrameType.MANYULLYN ? 0x00F000F0 : packedLight;
          this.itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, matrices, bufferIn, frame.level(), frame.getId());
        }
      }
    }

    matrices.popPose();
  }
}
