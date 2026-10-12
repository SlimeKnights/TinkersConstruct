package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.render.ChannelFluids;
import slimeknights.mantle.client.render.FluidCuboid;
import slimeknights.mantle.client.render.FluidRenderer;
import slimeknights.mantle.client.render.MantleRenderTypes;
import slimeknights.mantle.client.render.RenderingHelper;
import slimeknights.tconstruct.smeltery.block.channel.AbstractChannelBlock;
import slimeknights.tconstruct.smeltery.block.channel.ChannelConnection.TwoWay;
import slimeknights.tconstruct.smeltery.block.entity.ChannelBlockEntity;

import javax.annotation.Nullable;

public class ChannelBlockEntityRenderer implements BlockEntityRenderer<ChannelBlockEntity> {
  public ChannelBlockEntityRenderer(Context context) {}

  @Override
  public void render(ChannelBlockEntity te, float partialTicks, PoseStack matrices, MultiBufferSource buffer, int initialLight, int combinedOverlayIn) {
    FluidStack fluid = te.getFluid();
    if (fluid.isEmpty()) {
      return;
    }

    // fetch model properties
    Level world = te.getLevel();
    if (world == null) {
      return;
    }
    BlockPos pos = te.getBlockPos();
    BlockState state = te.getBlockState();
    AbstractChannelBlock block = ((AbstractChannelBlock) state.getBlock());
    ChannelFluids model = ChannelFluids.REGISTRY.get(block);
    if (model == null) {
      return;
    }

    // fluid attributes
    IClientFluidTypeExtensions attributes = IClientFluidTypeExtensions.of(fluid.getFluid());
    TextureAtlasSprite still = FluidRenderer.getBlockSprite(attributes.getStillTexture(fluid));
    TextureAtlasSprite flowing = FluidRenderer.getBlockSprite(attributes.getFlowingTexture(fluid));
    VertexConsumer builder = buffer.getBuffer(MantleRenderTypes.FLUID);
    int color = attributes.getTintColor(fluid);
    int light = FluidRenderer.withBlockLight(initialLight, fluid.getFluid().getFluidType().getLightLevel(fluid));

    // render sides first, while doing so we will determine center "flow"
    Direction centerFlow = null;
    for (Direction direction : Plane.HORIZONTAL) {
      centerFlow = renderSide(te, matrices, direction, centerFlow, block, state, model, builder, still, flowing, color, light);
    }

    // render center
    renderCenter(matrices, centerFlow, model, builder, still, flowing, color, light);
    // render flow downwards
    renderVerticalFlow(Direction.DOWN, te, matrices, block, state, model, builder, still, flowing, color, light, world, pos);
    // render flow upwards
    renderVerticalFlow(Direction.UP, te, matrices, block, state, model, builder, still, flowing, color, light, world, pos);
  }

  protected static void renderVerticalFlow(Direction dir, ChannelBlockEntity te, PoseStack matrices, AbstractChannelBlock block, BlockState state, ChannelFluids model, VertexConsumer builder, TextureAtlasSprite still, TextureAtlasSprite flowing, int color, int light, Level world, BlockPos pos) {
    if (block.getCurrentFlowOnSide(dir, state) == TwoWay.OUT && te.isFlowing(dir)) {
      // render the part in the block
      FluidCuboid cube = dir == Direction.DOWN ? model.down() : model.up();
      if (cube != null) {
        FluidRenderer.renderCuboid(matrices, builder, cube, 0, still, flowing, color, light, false);
      }
      // render into the block(s) below or above
      if (dir == Direction.DOWN)
        RenderingHelper.renderFaucetFluids(world, pos, dir, matrices, builder, still, flowing, color, light);
      else
        ReverseFaucetFluidLoader.renderReverseFaucetFluids(world, pos, dir, matrices, builder, still, flowing, color, light);
    }
  }

  protected static void renderCenter(PoseStack matrices, @Nullable Direction centerFlow, ChannelFluids model, VertexConsumer builder, TextureAtlasSprite still, TextureAtlasSprite flowing, int color, int light) {
    FluidCuboid cube;
    boolean isRotated = false;
    // if its flowing multiple incompatible directions, render still
    if (centerFlow == Direction.UP) {
      cube = model.center(false);
    } else {
      cube = model.center(true);
      if (centerFlow != null) isRotated = RenderingHelper.applyRotation(matrices, centerFlow);
    }
    // render the cube and pop back
    FluidRenderer.renderCuboid(matrices, builder, cube, 0, still, flowing, color, light, false);
    if (isRotated) {
      matrices.popPose();
    }
  }

  protected static @Nullable Direction renderSide(ChannelBlockEntity te, PoseStack matrices, Direction direction, @Nullable Direction centerFlow, AbstractChannelBlock block, BlockState state, ChannelFluids model, VertexConsumer builder, TextureAtlasSprite still, TextureAtlasSprite flowing, int color, int light) {
    // render sides and calculate center flow
    // check if we have that side on the block
    TwoWay connection = block.getCurrentFlowOnSide(direction, state);
    if (connection.canFlow()) {
      FluidCuboid cube;
      // apply rotation for the side
      boolean isRotated = RenderingHelper.applyRotation(matrices, direction);
      // get the relevant fluid model, render it
      if (te.isFlowing(direction)) {
        cube = model.side().flow(connection == TwoWay.OUT);

        // add to center direction
        if (connection == TwoWay.OUT) {
          // if unset (null), use this direction
          if (centerFlow == null) {
            centerFlow = direction;
            // if set and it disagrees, set the fail state (UP)
          } else if (centerFlow != direction) {
            centerFlow = Direction.UP;
          }
        }
        // render the extra edge against other blocks
        //noinspection DataFlowIssue // we check this in the parent method
        if (!te.getLevel().getBlockState(te.getBlockPos().relative(direction)).is(state.getBlock())) {
          FluidRenderer.renderCuboid(matrices, builder, model.side().edge(), 0, still, flowing, color, light, false);
        }
      } else {
        cube = model.side().still();
      }
      FluidRenderer.renderCuboid(matrices, builder, cube, 0, still, flowing, color, light, false);
      // undo rotation
      if (isRotated) {
        matrices.popPose();
      }
    }
    return centerFlow;
  }
}
