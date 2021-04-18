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
    World world = Minecraft.getInstance().world;
    PlayerEntity player = Minecraft.getInstance().player;
    if (world == null || player == null) {
      return;
    }
    // must have the right tags
    ItemStack stack = player.getHeldItemMainhand();
    if (stack.isEmpty() || !TinkerTags.Items.HARVEST.contains(stack.getItem())) {
      return;
    }
    // must be targeting a block
    RayTraceResult result = Minecraft.getInstance().objectMouseOver;
    if (result == null || result.getType() != Type.BLOCK) {
      return;
    }
    // must not be broken, must be right interface
    ToolStack tool = ToolStack.from(stack);
    if (tool.isBroken() || !(stack.getItem() instanceof IModifiableHarvest)) {
      return;
    }
    BlockRayTraceResult blockTrace = event.getTarget();
    List<BlockPos> extraBlocks = ((IModifiableHarvest) stack.getItem()).getToolHarvestLogic().getAOEBlocks(tool, stack, world, player, blockTrace.getPos(), blockTrace);
    if (extraBlocks.isEmpty()) {
      return;
    }

    // set up renderer
    WorldRenderer worldRender = event.getContext();
    MatrixStack matrices = event.getMatrix();
    IRenderTypeBuffer.Impl buffers = worldRender.renderTypeTextures.getBufferSource();
    IVertexBuilder vertexBuilder = buffers.getBuffer(RenderType.getLines());
    matrices.push();

    // start drawing
    ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
    Entity viewEntity = renderInfo.getRenderViewEntity();
    Vector3d vector3d = renderInfo.getProjectedView();
    double x = vector3d.getX();
    double y = vector3d.getY();
    double z = vector3d.getZ();
    for (BlockPos pos : extraBlocks) {
      if (world.getWorldBorder().contains(pos)) {
        worldRender.drawSelectionBox(matrices, vertexBuilder, viewEntity, x, y, z, pos, world.getBlockState(pos));
      }
    }
    matrices.pop();
    buffers.finish();
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
    if (controller == null || !controller.getIsHittingBlock()) {
      return;
    }
    World world = Minecraft.getInstance().world;
    PlayerEntity player = Minecraft.getInstance().player;
    if (world == null || player == null || Minecraft.getInstance().getRenderViewEntity() == null) {
      return;
    }
    // must have the right tags
    ItemStack stack = player.getHeldItemMainhand();
    if (stack.isEmpty() || !TinkerTags.Items.HARVEST.contains(stack.getItem())) {
      return;
    }
    // must be targeting a block
    RayTraceResult result = Minecraft.getInstance().objectMouseOver;
    if (result == null || result.getType() != Type.BLOCK) {
      return;
    }
    // must not be broken, must be right interface
    ToolStack tool = ToolStack.from(stack);
    if (tool.isBroken() || !(stack.getItem() instanceof IModifiableHarvest)) {
      return;
    }
    // find breaking progress
    BlockRayTraceResult blockTrace = (BlockRayTraceResult)result;
    BlockPos target = blockTrace.getPos();
    DestroyBlockProgress progress = null;
    for (Int2ObjectMap.Entry<DestroyBlockProgress> entry : Minecraft.getInstance().worldRenderer.damagedBlocks.int2ObjectEntrySet()) {
      if (entry.getValue().getPosition().equals(target)) {
        progress = entry.getValue();
        break;
      }
    }
    if (progress == null) {
      return;
    }
    // determine extra blocks to highlight
    List<BlockPos> extraBlocks = ((IModifiableHarvest) stack.getItem()).getToolHarvestLogic().getAOEBlocks(tool, stack, world, player, target, blockTrace);
    if (extraBlocks.isEmpty()) {
      return;
    }

    // set up buffers
    MatrixStack matrices = event.getMatrixStack();
    matrices.push();
    IRenderTypeBuffer.Impl vertices = event.getContext().renderTypeTextures.getCrumblingBufferSource();
    IVertexBuilder vertexBuilder = vertices.getBuffer(ModelBakery.DESTROY_RENDER_TYPES.get(progress.getPartialBlockDamage()));

    // finally, render the blocks
    ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
    double x = renderInfo.getProjectedView().x;
    double y = renderInfo.getProjectedView().y;
    double z = renderInfo.getProjectedView().z;
    BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
    for (BlockPos pos : extraBlocks) {
      matrices.push();
      matrices.translate(pos.getX() - x, pos.getY() - y, pos.getZ() - z);
      MatrixStack.Entry entry = matrices.getLast();
      IVertexBuilder blockBuilder = new MatrixApplyingVertexBuilder(vertexBuilder, entry.getMatrix(), entry.getNormal());
      dispatcher.renderBlockDamage(world.getBlockState(pos), pos, world, matrices, blockBuilder);
      matrices.pop();
    }
    // finish rendering
    matrices.pop();
    vertices.finish();
  }

  @Override
  public void onInitializeClient() {

  }
}
