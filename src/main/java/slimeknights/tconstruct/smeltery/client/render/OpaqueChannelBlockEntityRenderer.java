package slimeknights.tconstruct.smeltery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.render.ChannelFluids;
import slimeknights.mantle.client.render.FluidCuboid;
import slimeknights.mantle.client.render.FluidRenderer;
import slimeknights.mantle.client.render.MantleRenderTypes;
import slimeknights.tconstruct.smeltery.block.channel.AbstractChannelBlock;
import slimeknights.tconstruct.smeltery.block.entity.ChannelBlockEntity;

import java.util.function.Consumer;

public class OpaqueChannelBlockEntityRenderer implements BlockEntityRenderer<ChannelBlockEntity> {
  public OpaqueChannelBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

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
    // parameter explosion does still happen somewhat due to using a mantle helper method
    Consumer<FluidCuboid> cuboidRenderer = cuboid -> FluidRenderer.renderCuboid(matrices, builder, cuboid, 0, still, flowing, color, light, false);

    // render flow downwards
    ChannelBlockEntityRenderer.renderVerticalFlow(Direction.DOWN, te, matrices, block, state, model, cuboidRenderer, world, pos);
    // render flow upwards
    ChannelBlockEntityRenderer.renderVerticalFlow(Direction.UP, te, matrices, block, state, model, cuboidRenderer, world, pos);
  }
}
