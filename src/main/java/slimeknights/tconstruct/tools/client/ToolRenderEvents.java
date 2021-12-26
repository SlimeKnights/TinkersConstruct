package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic.AOEMatchType;
import slimeknights.tconstruct.library.tools.item.IModifiableHarvest;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Iterator;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = TConstruct.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ToolRenderEvents {
  /** Maximum number of blocks from the iterator to render */
  private static final int MAX_BLOCKS = 60;

  /**
   * Renders the outline on the extra blocks
   *
   * @param event the highlight event
   */
  @SubscribeEvent
  static void renderBlockHighlights(DrawHighlightEvent.HighlightBlock event) {
    World world = Minecraft.getInstance().level;
    PlayerEntity player = Minecraft.getInstance().player;
    if (world == null || player == null) {
      return;
    }
    // must have the right tags
    ItemStack stack = player.getMainHandItem();
    if (stack.isEmpty() || !TinkerTags.Items.HARVEST_PRIMARY.contains(stack.getItem())) {
      return;
    }
    // must be targeting a block
    RayTraceResult result = Minecraft.getInstance().hitResult;
    if (result == null || result.getType() != Type.BLOCK) {
      return;
    }
    // must not be broken, must be right interface
    ToolStack tool = ToolStack.from(stack);
    if (tool.isBroken() || !(stack.getItem() instanceof IModifiableHarvest)) {
      return;
    }
    BlockRayTraceResult blockTrace = event.getTarget();
    BlockPos origin = blockTrace.getBlockPos();
    ToolHarvestLogic harvestLogic = ((IModifiableHarvest) stack.getItem()).getToolHarvestLogic();
    BlockState state = world.getBlockState(origin);
    if (!harvestLogic.isEffective(tool, stack, state)) {
      return;
    }
    Iterator<BlockPos> extraBlocks = harvestLogic.getAOEBlocks(tool, stack, player, world.getBlockState(origin), world, origin, blockTrace.getDirection(), AOEMatchType.BREAKING).iterator();
    if (!extraBlocks.hasNext()) {
      return;
    }

    // set up renderer
    WorldRenderer worldRender = event.getContext();
    MatrixStack matrices = event.getMatrix();
    IRenderTypeBuffer.Impl buffers = worldRender.renderBuffers.bufferSource();
    IVertexBuilder vertexBuilder = buffers.getBuffer(RenderType.lines());
    matrices.pushPose();

    // start drawing
    ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
    Entity viewEntity = renderInfo.getEntity();
    Vector3d vector3d = renderInfo.getPosition();
    double x = vector3d.x();
    double y = vector3d.y();
    double z = vector3d.z();
    int rendered = 0;
    do {
      BlockPos pos = extraBlocks.next();
      if (world.getWorldBorder().isWithinBounds(pos)) {
        rendered++;
        worldRender.renderHitOutline(matrices, vertexBuilder, viewEntity, x, y, z, pos, world.getBlockState(pos));
      }
    } while(rendered < MAX_BLOCKS && extraBlocks.hasNext());
    matrices.popPose();
    buffers.endBatch();
  }

  /**
   * Renders the block damage process on the extra blocks
   *
   * @param event the RenderWorldLastEvent
   */
  @SubscribeEvent
  static void renderBlockDamageProgress(RenderWorldLastEvent event) {
    // validate required variables are set
    PlayerController controller = Minecraft.getInstance().gameMode;
    if (controller == null || !controller.isDestroying()) {
      return;
    }
    World world = Minecraft.getInstance().level;
    PlayerEntity player = Minecraft.getInstance().player;
    if (world == null || player == null || Minecraft.getInstance().getCameraEntity() == null) {
      return;
    }
    // must have the right tags
    ItemStack stack = player.getMainHandItem();
    if (stack.isEmpty() || !TinkerTags.Items.HARVEST_PRIMARY.contains(stack.getItem())) {
      return;
    }
    // must be targeting a block
    RayTraceResult result = Minecraft.getInstance().hitResult;
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
    BlockPos target = blockTrace.getBlockPos();
    DestroyBlockProgress progress = null;
    for (Int2ObjectMap.Entry<DestroyBlockProgress> entry : Minecraft.getInstance().levelRenderer.destroyingBlocks.int2ObjectEntrySet()) {
      if (entry.getValue().getPos().equals(target)) {
        progress = entry.getValue();
        break;
      }
    }
    if (progress == null) {
      return;
    }
    // determine extra blocks to highlight
    ToolHarvestLogic harvestLogic = ((IModifiableHarvest) stack.getItem()).getToolHarvestLogic();
    BlockState state = world.getBlockState(target);
    if (!harvestLogic.isEffective(tool, stack, state)) {
      return;
    }
    Iterator<BlockPos> extraBlocks = harvestLogic.getAOEBlocks(tool, stack, player, state, world, target, blockTrace.getDirection(), AOEMatchType.BREAKING).iterator();
    if (!extraBlocks.hasNext()) {
      return;
    }

    // set up buffers
    MatrixStack matrices = event.getMatrixStack();
    matrices.pushPose();
    IRenderTypeBuffer.Impl vertices = event.getContext().renderBuffers.crumblingBufferSource();
    IVertexBuilder vertexBuilder = vertices.getBuffer(ModelBakery.DESTROY_TYPES.get(progress.getProgress()));

    // finally, render the blocks
    ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
    double x = renderInfo.getPosition().x;
    double y = renderInfo.getPosition().y;
    double z = renderInfo.getPosition().z;
    BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
    int rendered = 0;
    do {
      BlockPos pos = extraBlocks.next();
      matrices.pushPose();
      matrices.translate(pos.getX() - x, pos.getY() - y, pos.getZ() - z);
      MatrixStack.Entry entry = matrices.last();
      IVertexBuilder blockBuilder = new MatrixApplyingVertexBuilder(vertexBuilder, entry.pose(), entry.normal());
      dispatcher.renderBreakingTexture(world.getBlockState(pos), pos, world, matrices, blockBuilder);
      matrices.popPose();
      rendered++;
    } while (rendered < MAX_BLOCKS && extraBlocks.hasNext());
    // finish rendering
    matrices.popPose();
    vertices.endBatch();
  }
}
