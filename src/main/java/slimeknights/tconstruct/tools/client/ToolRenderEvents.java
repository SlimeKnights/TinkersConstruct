package slimeknights.tconstruct.tools.client;

import net.fabricmc.api.ClientModInitializer;

public class ToolRenderEvents implements ClientModInitializer {

  @Override
  public void onInitializeClient() {}

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
    BlockPos origin = blockTrace.getPos();
    Iterator<BlockPos> extraBlocks = ((IModifiableHarvest) stack.getItem()).getToolHarvestLogic().getAOEBlocks(tool, stack, player, world.getBlockState(origin), world, origin, blockTrace.getFace(), AOEMatchType.BREAKING).iterator();
    if (!extraBlocks.hasNext()) {
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
    int rendered = 0;
    do {
      BlockPos pos = extraBlocks.next();
      if (world.getWorldBorder().contains(pos)) {
        rendered++;
        worldRender.drawSelectionBox(matrices, vertexBuilder, viewEntity, x, y, z, pos, world.getBlockState(pos));
      }
    } while(rendered < MAX_BLOCKS && extraBlocks.hasNext());
    matrices.pop();
    buffers.finish();
  }

  *//**
     * Renders the block damage process on the extra blocks
     *
     * @param event the RenderWorldLastEvent
     *//*
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
    Iterator<BlockPos> extraBlocks = ((IModifiableHarvest) stack.getItem()).getToolHarvestLogic().getAOEBlocks(tool, stack, player, world.getBlockState(target), world, target, blockTrace.getFace(), AOEMatchType.BREAKING).iterator();
    if (!extraBlocks.hasNext()) {
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
    int rendered = 0;
    do {
      BlockPos pos = extraBlocks.next();
      matrices.push();
      matrices.translate(pos.getX() - x, pos.getY() - y, pos.getZ() - z);
      MatrixStack.Entry entry = matrices.getLast();
      IVertexBuilder blockBuilder = new MatrixApplyingVertexBuilder(vertexBuilder, entry.getMatrix(), entry.getNormal());
      dispatcher.renderBlockDamage(world.getBlockState(pos), pos, world, matrices, blockBuilder);
      matrices.pop();
      rendered++;
    } while (rendered < MAX_BLOCKS && extraBlocks.hasNext());
    // finish rendering
    matrices.pop();
    vertices.finish();
  }*/
}
