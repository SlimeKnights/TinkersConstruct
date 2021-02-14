package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.item.IModifiableHarvest;

import java.util.List;

@Mod.EventBusSubscriber(modid = TConstruct.modID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ToolRenderEvents {

  /**
   * Renders the outline on the extra blocks
   *
   * @param event the highlight event
   */
  @SubscribeEvent
  static void renderBlockHighlights(DrawHighlightEvent.HighlightBlock event) {
    PlayerEntity player = Minecraft.getInstance().player;
    if (player == null) {
      return;
    }
    ItemStack tool = player.getHeldItemMainhand();
    if (tool.isEmpty()) {
      return;
    }

    // AOE preview
    if (tool.getItem() instanceof IModifiableHarvest) {
      World world = player.world;
      List<BlockPos> extraBlocks = ((IModifiableHarvest) tool.getItem()).getToolHarvestLogic().getAOEBlocks(tool, world, player, event.getTarget().getPos());
      if (extraBlocks.isEmpty()) {
        return;
      }

      WorldRenderer worldRender = event.getContext();
      MatrixStack matrix = event.getMatrix();
      IVertexBuilder vertexBuilder = worldRender.renderTypeTextures.getBufferSource().getBuffer(RenderType.getLines());

      ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
      Entity viewEntity = renderInfo.getRenderViewEntity();
      Vector3d vector3d = renderInfo.getProjectedView();

      double x = vector3d.getX();
      double y = vector3d.getY();
      double z = vector3d.getZ();

      matrix.push();
      for (BlockPos pos : extraBlocks) {
        if (world.getWorldBorder().contains(pos)) {
          worldRender.drawSelectionBox(matrix, vertexBuilder, viewEntity, x, y, z, pos, world.getBlockState(pos));
        }
      }
      matrix.pop();
    }
  }

  /**
   * Renders the block damage process on the extra blocks
   *
   * @param event the RenderWorldLastEvent
   */
  @SubscribeEvent
  static void renderBlockDamageProgress(RenderWorldLastEvent event) {
    // validate required variables are set
    PlayerController controller = Minecraft.getInstance().playerController;
    if (controller == null || !controller.isHittingBlock) {
      return;
    }
    PlayerEntity player = Minecraft.getInstance().player;
    if (player == null || Minecraft.getInstance().getRenderViewEntity() == null) {
      return;
    }
    ItemStack tool = player.getHeldItemMainhand();
    if (tool.isEmpty()) {
      return;
    }

    if (tool.getItem() instanceof IModifiableHarvest) {
      BlockRayTraceResult traceResult = RayTracer.retrace(player, RayTraceContext.FluidMode.NONE);
      if (traceResult.getType() != RayTraceResult.Type.BLOCK) {
        return;
      }

      List<BlockPos> extraBlocks = ((IModifiableHarvest) tool.getItem()).getToolHarvestLogic().getAOEBlocks(tool, player.world, player, traceResult.getPos());
      if (extraBlocks.isEmpty()) {
        return;
      }
      drawBlockDamageTexture(event.getContext(), event.getMatrixStack(), Minecraft.getInstance().gameRenderer.getActiveRenderInfo(), player.getEntityWorld(), extraBlocks);
    }
  }

  /**
   * Draws the damaged texture on the given blocks
   *
   * @param worldRender the current world renderer
   * @param matrixStackIn the matrix stack
   * @param renderInfo the current render info from the client
   * @param world the active world
   * @param extraBlocks the list of blocks
   */
  private static void drawBlockDamageTexture(WorldRenderer worldRender, MatrixStack matrixStackIn, ActiveRenderInfo renderInfo, World world, Iterable<BlockPos> extraBlocks) {
    double d0 = renderInfo.getProjectedView().x;
    double d1 = renderInfo.getProjectedView().y;
    double d2 = renderInfo.getProjectedView().z;

    assert Minecraft.getInstance().playerController != null;
    int progress = (int) (Minecraft.getInstance().playerController.curBlockDamageMP * 10.0F) - 1;

    if (progress < 0) {
      return;
    }

    progress = Math.min(progress, 10); // Ensure that for whatever reason the progress level doesn't go OOB.

    BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
    IVertexBuilder vertexBuilder = worldRender.renderTypeTextures.getCrumblingBufferSource().getBuffer(ModelBakery.DESTROY_RENDER_TYPES.get(progress));

    for (BlockPos pos : extraBlocks) {
      matrixStackIn.push();
      matrixStackIn.translate((double) pos.getX() - d0, (double) pos.getY() - d1, (double) pos.getZ() - d2);
      IVertexBuilder matrixBuilder = new MatrixApplyingVertexBuilder(vertexBuilder, matrixStackIn.getLast().getMatrix(), matrixStackIn.getLast().getNormal());
      dispatcher.renderBlockDamage(world.getBlockState(pos), pos, world, matrixStackIn, matrixBuilder);
      matrixStackIn.pop();
    }
  }
}
