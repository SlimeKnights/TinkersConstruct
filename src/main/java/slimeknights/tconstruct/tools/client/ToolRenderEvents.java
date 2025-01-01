package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Iterator;

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
  static void renderBlockHighlights(RenderHighlightEvent.Block event) {
    Level world = Minecraft.getInstance().level;
    Player player = Minecraft.getInstance().player;
    if (world == null || player == null) {
      return;
    }
    // must have the right tags
    ItemStack stack = player.getMainHandItem();
    if (stack.isEmpty() || !stack.is(TinkerTags.Items.HARVEST)) {
      return;
    }
    // must be targeting a block
    HitResult result = Minecraft.getInstance().hitResult;
    if (result == null || result.getType() != Type.BLOCK) {
      return;
    }
    // must not be broken, must be right interface
    ToolStack tool = ToolStack.from(stack);
    if (tool.isBroken()) {
      return;
    }
    BlockHitResult blockTrace = event.getTarget();
    BlockPos origin = blockTrace.getBlockPos();
    BlockState state = world.getBlockState(origin);
    // must not be broken, and the tool definition must be effective
    if (!IsEffectiveToolHook.isEffective(tool, state)) {
      return;
    }
    Iterator<BlockPos> extraBlocks = tool.getHook(ToolHooks.AOE_ITERATOR).getBlocks(tool, stack, player, world.getBlockState(origin), world, origin, blockTrace.getDirection(), AreaOfEffectIterator.AOEMatchType.BREAKING).iterator();
    if (!extraBlocks.hasNext()) {
      return;
    }

    // set up renderer
    LevelRenderer worldRender = event.getLevelRenderer();
    PoseStack matrices = event.getPoseStack();
    MultiBufferSource.BufferSource buffers = worldRender.renderBuffers.bufferSource();
    VertexConsumer vertexBuilder = buffers.getBuffer(RenderType.lines());
    matrices.pushPose();

    // start drawing
    Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
    Entity viewEntity = renderInfo.getEntity();
    Vec3 vector3d = renderInfo.getPosition();
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

  /** Renders the block damage process on the extra blocks */
  @SubscribeEvent
  static void renderBlockDamageProgress(RenderLevelStageEvent event) {
    // TODO: validate this is the right stage for block breaking particles, maybe I want a bit earlier
    if (event.getStage() != Stage.AFTER_TRIPWIRE_BLOCKS) {
      return;
    }

    // validate required variables are set
    MultiPlayerGameMode controller = Minecraft.getInstance().gameMode;
    if (controller == null || !controller.isDestroying()) {
      return;
    }
    Level world = Minecraft.getInstance().level;
    Player player = Minecraft.getInstance().player;
    if (world == null || player == null || Minecraft.getInstance().getCameraEntity() == null) {
      return;
    }
    // must have the right tags
    ItemStack stack = player.getMainHandItem();
    if (stack.isEmpty() || !stack.is(TinkerTags.Items.HARVEST)) {
      return;
    }
    // must be targeting a block
    HitResult result = Minecraft.getInstance().hitResult;
    if (result == null || result.getType() != Type.BLOCK) {
      return;
    }
    // must not be broken, must be right interface
    ToolStack tool = ToolStack.from(stack);
    if (tool.isBroken()) {
      return;
    }
    // find breaking progress
    BlockHitResult blockTrace = (BlockHitResult)result;
    BlockPos target = blockTrace.getBlockPos();
    BlockDestructionProgress progress = null;
    for (Int2ObjectMap.Entry<BlockDestructionProgress> entry : Minecraft.getInstance().levelRenderer.destroyingBlocks.int2ObjectEntrySet()) {
      if (entry.getValue().getPos().equals(target)) {
        progress = entry.getValue();
        break;
      }
    }
    if (progress == null) {
      return;
    }
    // determine extra blocks to highlight
    BlockState state = world.getBlockState(target);
    // must not be broken, and the tool definition must be effective
    if (!IsEffectiveToolHook.isEffective(tool, state)) {
      return;
    }
    Iterator<BlockPos> extraBlocks = tool.getHook(ToolHooks.AOE_ITERATOR).getBlocks(tool, stack, player, state, world, target, blockTrace.getDirection(), AreaOfEffectIterator.AOEMatchType.BREAKING).iterator();
    if (!extraBlocks.hasNext()) {
      return;
    }

    // set up buffers
    PoseStack matrices = event.getPoseStack();
    matrices.pushPose();
    MultiBufferSource.BufferSource vertices = event.getLevelRenderer().renderBuffers.crumblingBufferSource();
    VertexConsumer vertexBuilder = vertices.getBuffer(ModelBakery.DESTROY_TYPES.get(progress.getProgress()));

    // finally, render the blocks
    Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
    double x = renderInfo.getPosition().x;
    double y = renderInfo.getPosition().y;
    double z = renderInfo.getPosition().z;
    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
    int rendered = 0;
    do {
      BlockPos pos = extraBlocks.next();
      matrices.pushPose();
      matrices.translate(pos.getX() - x, pos.getY() - y, pos.getZ() - z);
      PoseStack.Pose entry = matrices.last();
      VertexConsumer blockBuilder = new SheetedDecalTextureGenerator(vertexBuilder, entry.pose(), entry.normal(), 1);
      // TODO: is it practical to fetch model data here?
      dispatcher.renderBreakingTexture(world.getBlockState(pos), pos, world, matrices, blockBuilder);
      matrices.popPose();
      rendered++;
    } while (rendered < MAX_BLOCKS && extraBlocks.hasNext());
    // finish rendering
    matrices.popPose();
    vertices.endBatch();
  }
}
