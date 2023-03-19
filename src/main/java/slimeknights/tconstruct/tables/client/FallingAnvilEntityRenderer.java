package slimeknights.tconstruct.tables.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import slimeknights.tconstruct.tables.entity.FallingAnvilEntity;

import java.util.Random;

public class FallingAnvilEntityRenderer extends EntityRenderer<FallingAnvilEntity> {

  public FallingAnvilEntityRenderer(EntityRendererProvider.Context p_174008_) {
    super(p_174008_);
  }

  @Override
  public void render(FallingAnvilEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
    BlockState blockstate = pEntity.getBlockState();
    Level level = pEntity.getLevel();
    pMatrixStack.pushPose();
    BlockPos blockpos = new BlockPos(pEntity.getX(), pEntity.getBoundingBox().maxY, pEntity.getZ());
    pMatrixStack.translate(-0.5D, 0.0D, -0.5D);
    IModelData modelData = pEntity.getModelData();
    BlockRenderDispatcher blockrenderdispatcher = Minecraft.getInstance().getBlockRenderer();
    for (net.minecraft.client.renderer.RenderType type : net.minecraft.client.renderer.RenderType.chunkBufferLayers()) {
      if (ItemBlockRenderTypes.canRenderInLayer(blockstate, type)) {
        net.minecraftforge.client.ForgeHooksClient.setRenderType(type);
        blockrenderdispatcher.getModelRenderer().tesselateBlock(level, blockrenderdispatcher.getBlockModel(blockstate), blockstate, blockpos, pMatrixStack, pBuffer.getBuffer(type), false, new Random(), blockstate.getSeed(pEntity.getStartPos()), OverlayTexture.NO_OVERLAY, modelData);
      }
    }
    net.minecraftforge.client.ForgeHooksClient.setRenderType(null);
    pMatrixStack.popPose();
    super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
  }

  @Override
  public ResourceLocation getTextureLocation(FallingAnvilEntity pEntity) {
    return TextureAtlas.LOCATION_BLOCKS;
  }
}
