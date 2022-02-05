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
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.model.FaucetFluidLoader;
import slimeknights.mantle.client.model.fluid.FluidCuboid;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.client.render.FluidRenderer;
import slimeknights.mantle.client.render.MantleRenderTypes;
import slimeknights.mantle.client.render.RenderingHelper;
import slimeknights.tconstruct.library.client.model.block.ChannelModel;
import slimeknights.tconstruct.smeltery.block.ChannelBlock;
import slimeknights.tconstruct.smeltery.block.ChannelBlock.ChannelConnection;
import slimeknights.tconstruct.smeltery.block.entity.ChannelBlockEntity;

public class ChannelBlockEntityRenderer implements BlockEntityRenderer<ChannelBlockEntity> {
  public ChannelBlockEntityRenderer(Context context) {}

	@Override
	public void render(ChannelBlockEntity te, float partialTicks, PoseStack matrices, MultiBufferSource buffer, int light, int combinedOverlayIn)  {
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
		ChannelModel.Baked model = ModelHelper.getBakedModel(state, ChannelModel.Baked.class);
		if (model == null) {
			return;
		}

		// fluid attributes
		FluidAttributes attributes = fluid.getFluid().getAttributes();
		TextureAtlasSprite still = FluidRenderer.getBlockSprite(attributes.getStillTexture(fluid));
		TextureAtlasSprite flowing = FluidRenderer.getBlockSprite(attributes.getFlowingTexture(fluid));
		VertexConsumer builder = buffer.getBuffer(MantleRenderTypes.FLUID);
		int color = attributes.getColor(fluid);
		light = FluidRenderer.withBlockLight(light, attributes.getLuminosity(fluid));

		// render sides first, while doing so we will determine center "flow"
		FluidCuboid cube;
		boolean isRotated;
		Direction centerFlow = Direction.UP;
		for (Direction direction : Plane.HORIZONTAL) {
			// check if we have that side on the block
			ChannelConnection connection = state.getValue(ChannelBlock.DIRECTION_MAP.get(direction));
			if (connection.canFlow()) {
				// apply rotation for the side
				isRotated = RenderingHelper.applyRotation(matrices, direction);
				// get the relevant fluid model, render it
				if (te.isFlowing(direction)) {
					cube = model.getSideFlow(connection == ChannelConnection.OUT);

					// add to center direction
					if (connection == ChannelConnection.OUT) {
						// if unset (up), use this direction
						if (centerFlow == Direction.UP) {
							centerFlow = direction;
							// if set and it disagrees, set the fail state (down)
						} else if (centerFlow != direction) {
							centerFlow = Direction.DOWN;
						}
					}
					// render the extra edge against other blocks
					if (!world.getBlockState(pos.relative(direction)).is(state.getBlock())) {
						FluidRenderer.renderCuboid(matrices, builder, model.getSideEdge(), 0, still, flowing, color, light, false);
					}
				} else {
					cube = model.getSideStill();
				}
				FluidRenderer.renderCuboid(matrices, builder, cube, 0, still, flowing, color, light, false);
				// undo rotation
				if (isRotated) {
					matrices.popPose();
				}
			}
		}

		// render center
		isRotated = false;
		if (centerFlow.getAxis().isVertical()) {
			cube = model.getCenterFluid(false);
		} else {
			cube = model.getCenterFluid(true);
			isRotated = RenderingHelper.applyRotation(matrices, centerFlow);
		}
		// render the cube and pop back
		FluidRenderer.renderCuboid(matrices, builder, cube, 0, still, flowing, color, light, false);
		if (isRotated) {
			matrices.popPose();
		}

		// render flow downwards
		if (state.getValue(ChannelBlock.DOWN) && te.isFlowing(Direction.DOWN)) {
			cube = model.getDownFluid();
			FluidRenderer.renderCuboid(matrices, builder, cube, 0, still, flowing, color, light, false);

			// render into the block(s) below
			FaucetFluidLoader.renderFaucetFluids(world, pos, Direction.DOWN, matrices, builder, still, flowing, color, light);
		}
	}
}
