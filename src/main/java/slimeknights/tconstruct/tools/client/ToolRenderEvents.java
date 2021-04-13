package slimeknights.tconstruct.tools.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ToolRenderEvents implements ClientModInitializer {

  //TODO: rendering port because events with forg are cursed
  /**
   * Renders the outline on the extra blocks
   *
   * @param event the highlight event
   */
/*  @SubscribeEvent
  static void renderBlockHighlights(DrawHighlightEvent.HighlightBlock event) {
    PlayerEntity player = MinecraftClient.getInstance().player;
    if (player == null) {
      return;
    }
    ItemStack tool = player.getMainHandStack();
    if (tool.isEmpty()) {
      return;
    }

    // AOE preview
    if (tool.getItem() instanceof IModifiableHarvest) {
      World world = player.world;
      List<BlockPos> extraBlocks = ((IModifiableHarvest) tool.getItem()).getToolHarvestLogic().getAOEBlocks(tool, world, player, event.getTarget().getBlockPos());
      if (extraBlocks.isEmpty()) {
        return;
      }

      WorldRenderer worldRender = event.getContext();
      MatrixStack matrix = event.getMatrix();
      VertexConsumer vertexBuilder = worldRender.bufferBuilders.getEntityVertexConsumers().getBuffer(RenderLayer.getLines());

      Camera renderInfo = MinecraftClient.getInstance().gameRenderer.getCamera();
      Entity viewEntity = renderInfo.getFocusedEntity();
      Vec3d vector3d = renderInfo.getPos();

      double x = vector3d.getX();
      double y = vector3d.getY();
      double z = vector3d.getZ();

      matrix.push();
      for (BlockPos pos : extraBlocks) {
        if (world.getWorldBorder().contains(pos)) {
          worldRender.drawBlockOutline(matrix, vertexBuilder, viewEntity, x, y, z, pos, world.getBlockState(pos));
        }
      }
      matrix.pop();
    }
  }*/

/**
   * Renders the block damage process on the extra blocks
   *
   * @param event the RenderWorldLastEvent
   */
/*
  @SubscribeEvent
  static void renderBlockDamageProgress(RenderWorldLastEvent event) {
    // validate required variables are set
    ClientPlayerInteractionManager controller = MinecraftClient.getInstance().interactionManager;
    if (controller == null || !controller.breakingBlock) {
      return;
    }
    PlayerEntity player = MinecraftClient.getInstance().player;
    if (player == null || MinecraftClient.getInstance().getCameraEntity() == null) {
      return;
    }
    ItemStack tool = player.getMainHandStack();
    if (tool.isEmpty()) {
      return;
    }

    if (tool.getItem() instanceof IModifiableHarvest) {
      BlockHitResult traceResult = RayTracer.retrace(player, RaycastContext.FluidHandling.NONE);
      if (traceResult.getType() != HitResult.Type.BLOCK) {
        return;
      }

      List<BlockPos> extraBlocks = ((IModifiableHarvest) tool.getItem()).getToolHarvestLogic().getAOEBlocks(tool, player.world, player, traceResult.getBlockPos());
      if (extraBlocks.isEmpty()) {
        return;
      }
      drawBlockDamageTexture(event.getContext(), event.getMatrixStack(), MinecraftClient.getInstance().gameRenderer.getCamera(), player.getEntityWorld(), extraBlocks);
    }
  }
*/

  /**
   * Draws the damaged texture on the given blocks
   *
   * @param worldRender the current world renderer
   * @param matrixStackIn the matrix stack
   * @param renderInfo the current render info from the client
   * @param world the active world
   * @param extraBlocks the list of blocks
   */
  private static void drawBlockDamageTexture(WorldRenderer worldRender, MatrixStack matrixStackIn, Camera renderInfo, World world, Iterable<BlockPos> extraBlocks) {
    double d0 = renderInfo.getPos().x;
    double d1 = renderInfo.getPos().y;
    double d2 = renderInfo.getPos().z;

    assert MinecraftClient.getInstance().interactionManager != null;
    int progress = (int) (MinecraftClient.getInstance().interactionManager.currentBreakingProgress * 10.0F) - 1;

    if (progress < 0) {
      return;
    }

    progress = Math.min(progress, 10); // Ensure that for whatever reason the progress level doesn't go OOB.

    BlockRenderManager dispatcher = MinecraftClient.getInstance().getBlockRenderManager();
    VertexConsumer vertexBuilder = worldRender.bufferBuilders.getEffectVertexConsumers().getBuffer(ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.get(progress));

    for (BlockPos pos : extraBlocks) {
      matrixStackIn.push();
      matrixStackIn.translate((double) pos.getX() - d0, (double) pos.getY() - d1, (double) pos.getZ() - d2);
      VertexConsumer matrixBuilder = new OverlayVertexConsumer(vertexBuilder, matrixStackIn.peek().getModel(), matrixStackIn.peek().getNormal());
      dispatcher.renderDamage(world.getBlockState(pos), pos, world, matrixStackIn, matrixBuilder);
      matrixStackIn.pop();
    }
  }

  @Override
  public void onInitializeClient() {

  }
}
