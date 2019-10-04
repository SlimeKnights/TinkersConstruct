package slimeknights.tconstruct.gadgets.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.common.MinecraftForge;
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
    this.defaultRenderer = renderManagerIn.getRenderer(ItemFrameEntity.class);

    for (FrameType frameType : FrameType.values()) {
      // TODO: reinstate when Forge fixes itself
      // LOCATIONS_MODEL.put(color, new ModelResourceLocation(new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame"), "map=false"));
      // LOCATIONS_MODEL_MAP.put(color, new ModelResourceLocation(new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame"), "map=true"));

      LOCATIONS_MODEL.put(frameType, new ModelResourceLocation(new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame_empty"), "inventory"));
      LOCATIONS_MODEL_MAP.put(frameType, new ModelResourceLocation(new ResourceLocation(TConstruct.modID, frameType.getName() + "_frame_map"), "inventory"));
    }
  }

  @Override
  public void doRender(FancyItemFrameEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
    GlStateManager.pushMatrix();
    BlockPos blockpos = entity.getHangingPosition();
    double d0 = (double) blockpos.getX() - entity.posX + x;
    double d1 = (double) blockpos.getY() - entity.posY + y;
    double d2 = (double) blockpos.getZ() - entity.posZ + z;
    GlStateManager.translated(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D);
    GlStateManager.rotatef(entity.rotationPitch, 1.0F, 0.0F, 0.0F);
    GlStateManager.rotatef(180.0F - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
    this.renderManager.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
    BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
    ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();

    FrameType frameType = entity.getFrameType();
    ModelResourceLocation modelresourcelocation = entity.getDisplayedItem().getItem() instanceof FilledMapItem ? LOCATIONS_MODEL_MAP.get(frameType) : LOCATIONS_MODEL.get(frameType);
    GlStateManager.pushMatrix();
    GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
    if (this.renderOutlines) {
      GlStateManager.enableColorMaterial();
      GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(entity));
    }

    blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(modelmanager.getModel(modelresourcelocation), 1.0F, 1.0F, 1.0F, 1.0F);
    if (this.renderOutlines) {
      GlStateManager.tearDownSolidRenderingTextureCombine();
      GlStateManager.disableColorMaterial();
    }

    GlStateManager.popMatrix();
    GlStateManager.enableLighting();
    if (entity.getDisplayedItem().getItem() == Items.FILLED_MAP) {
      GlStateManager.pushLightingAttributes();
      RenderHelper.enableStandardItemLighting();
    }

    GlStateManager.translatef(0.0F, 0.0F, 0.4375F);
    this.renderItem(entity);
    if (entity.getDisplayedItem().getItem() == Items.FILLED_MAP) {
      RenderHelper.disableStandardItemLighting();
      GlStateManager.popAttributes();
    }

    GlStateManager.enableLighting();
    GlStateManager.popMatrix();
    this.renderName(entity, x + (double) ((float) entity.getHorizontalFacing().getXOffset() * 0.3F), y - 0.25D, z + (double) ((float) entity.getHorizontalFacing().getZOffset() * 0.3F));
  }

  @Nullable
  @Override
  protected ResourceLocation getEntityTexture(@Nonnull FancyItemFrameEntity entity) {
    return null;
  }

  private void renderItem(FancyItemFrameEntity itemFrame) {
    ItemStack stack = itemFrame.getDisplayedItem();
    if (!stack.isEmpty()) {
      GlStateManager.pushMatrix();
      MapData mapdata = FilledMapItem.getMapData(stack, itemFrame.world);
      int rotation = (mapdata != null) ? ((itemFrame.getRotation() % 4) * 2) : itemFrame.getRotation();
      GlStateManager.rotatef((float) rotation * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
      if (!MinecraftForge.EVENT_BUS.post(new RenderItemInFrameEvent(itemFrame, this.defaultRenderer))) {
        if (mapdata != null) {
          GlStateManager.disableLighting();
          this.renderManager.textureManager.bindTexture(MAP_BACKGROUND_TEXTURES);
          GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
          GlStateManager.scalef(0.0078125F, 0.0078125F, 0.0078125F);
          GlStateManager.translatef(-64.0F, -64.0F, 0.0F);
          GlStateManager.translatef(0.0F, 0.0F, -1.0F);
          this.mc.gameRenderer.getMapItemRenderer().renderMap(mapdata, true);
        }
        else {
          GlStateManager.scalef(0.5F, 0.5F, 0.5F);
          this.itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
        }
      }

      GlStateManager.popMatrix();
    }
  }

  @Override
  protected void renderName(@Nonnull FancyItemFrameEntity entity, double x, double y, double z) {
    if (Minecraft.isGuiEnabled() && !entity.getDisplayedItem().isEmpty() && entity.getDisplayedItem().hasDisplayName() && this.renderManager.pointedEntity == entity) {
      double d0 = entity.getDistanceSq(this.renderManager.info.getProjectedView());
      float f = entity.shouldRenderSneaking() ? 32.0F : 64.0F;
      if (!(d0 >= (double) (f * f))) {
        String s = entity.getDisplayedItem().getDisplayName().getFormattedText();
        this.renderLivingLabel(entity, s, x, y, z, 64);
      }
    }
  }
}
