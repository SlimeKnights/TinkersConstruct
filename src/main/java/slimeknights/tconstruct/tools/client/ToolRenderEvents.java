package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
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
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic.AOEMatchType;
import slimeknights.tconstruct.library.tools.item.IModifiableHarvest;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Iterator;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = TConstruct.modID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ToolRenderEvents {
  /** Maximum number of blocks from the iterator to render */
  private static final int MAX_BLOCKS = 30;

  /**
   * Renders the outline on the extra blocks
   *
   * @param event the highlight event
   */
  @SubscribeEvent
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
    Iterator<BlockPos> extraBlocks = ((IModifiableHarvest) stack.getItem()).getToolHarvestLogic().getAOEBlocks(tool, stack, world, player, blockTrace.getPos(), blockTrace.getFace(), AOEMatchType.BREAKING).iterator();
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
    Iterator<BlockPos> extraBlocks = ((IModifiableHarvest) stack.getItem()).getToolHarvestLogic().getAOEBlocks(tool, stack, world, player, target, blockTrace.getFace(), AOEMatchType.BREAKING).iterator();
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
  }
}
