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
import java.util.function.Consumer;

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
    final TextureAtlasSprite still = FluidRenderer.getBlockSprite(attributes.getStillTexture(fluid));
    final TextureAtlasSprite flowing = FluidRenderer.getBlockSprite(attributes.getFlowingTexture(fluid));
    final VertexConsumer builder = buffer.getBuffer(MantleRenderTypes.FLUID);
    final int color = attributes.getTintColor(fluid);
    final int light = FluidRenderer.withBlockLight(initialLight, fluid.getFluid().getFluidType().getLightLevel(fluid));
    // capture all the variables into a lambda to pass around, to avoid parameter explosion
    Consumer<FluidCuboid> cuboidRenderer = cuboid -> FluidRenderer.renderCuboid(matrices, builder, cuboid, 0, still, flowing, color, light, false);

    // render sides first, while doing so we will determine center "flow"
    Direction centerFlow = null;
    for (Direction direction : Plane.HORIZONTAL) {
      // render sides and calculate center flow
      centerFlow = renderSide(cuboidRenderer, te, matrices, direction, block, state, model, centerFlow);
    }

    // render center
    renderCenter(matrices, centerFlow, model, cuboidRenderer);
    // render flow downwards
    renderVerticalFlow(Direction.DOWN, te, matrices, block, state, model, cuboidRenderer, world, pos);
    // render flow upwards
    renderVerticalFlow(Direction.UP, te, matrices, block, state, model, cuboidRenderer, world, pos);
  }

  protected static void renderVerticalFlow(Direction dir, ChannelBlockEntity te, PoseStack matrices, AbstractChannelBlock block, BlockState state, ChannelFluids model, Consumer<FluidCuboid> cuboidRenderer, Level world, BlockPos pos) {
    if (block.getCurrentFlowOnSide(dir, state) == TwoWay.OUT && te.isFlowing(dir)) {
      FluidCuboid cube = dir == Direction.DOWN ? model.down() : model.up();
      if (cube != null) {
        cuboidRenderer.accept(cube);

        // render into the block(s) below
        RenderingHelper.renderFaucetFluids(cuboidRenderer, world, pos, dir, matrices, dir == Direction.DOWN);
      }
    }
  }

  protected static void renderCenter(PoseStack matrices, @Nullable Direction centerFlow, ChannelFluids model, Consumer<FluidCuboid> cuboidRenderer) {
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
    cuboidRenderer.accept(cube);
    if (isRotated) {
      matrices.popPose();
    }
  }

  protected static @Nullable Direction renderSide(Consumer<FluidCuboid> cuboidRenderer, ChannelBlockEntity te, PoseStack matrices, Direction direction, AbstractChannelBlock block, BlockState state, ChannelFluids model, @Nullable Direction centerFlow) {
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
          cuboidRenderer.accept(model.side().edge());
        }
      } else {
        cube = model.side().still();
      }
      cuboidRenderer.accept(cube);
      // undo rotation
      if (isRotated) {
        matrices.popPose();
      }
    }
    return centerFlow;
  }
}
