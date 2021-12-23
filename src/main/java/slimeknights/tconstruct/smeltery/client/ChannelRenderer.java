package slimeknights.tconstruct.smeltery.client;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.smeltery.IFaucetDepth;
import slimeknights.tconstruct.smeltery.block.BlockChannel;
import slimeknights.tconstruct.smeltery.tileentity.TileChannel;
import slimeknights.tconstruct.smeltery.tileentity.TileChannel.ChannelConnection;

public class ChannelRenderer extends FastTESR<TileChannel> {
  private static Minecraft mc = Minecraft.getMinecraft();

  @Override
  public void renderTileEntityFast(@Nonnull TileChannel te, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder renderer) {
    FluidStack fluidStack = te.getTank().getFluid();
    if(fluidStack == null) {
      return;
    }

    Fluid fluid = fluidStack.getFluid();
    if(fluid == null) {
      return;
    }

    World world = te.getWorld();
    BlockPos pos = te.getPos();

    // start with the center fluid
    renderer.setTranslation(x, y, z);

    int color = fluid.getColor(fluidStack);
    int brightness = te.getWorld().getCombinedLight(te.getPos(), fluid.getLuminosity());
    TextureMap map = mc.getTextureMapBlocks();
    TextureAtlasSprite still = map.getTextureExtry(fluid.getStill(fluidStack).toString());
    TextureAtlasSprite flowing = map.getTextureExtry(fluid.getFlowing(fluidStack).toString());

    // sides
    double x1 = 0, z1 = 0, x2 = 0, z2 = 0;
    EnumFacing rotation = null, oneOutput = null;
    ChannelConnection connection;
    BlockPos offsetPos;
    int outputs = 0;
    for(EnumFacing side : EnumFacing.HORIZONTALS) {
      connection = te.getConnection(side);
      if(!ChannelConnection.canFlow(connection)) {
        continue;
      }

      if(te.isFlowing(side)) {
        offsetPos = pos.offset(side);

        // first, get location for side
        // these are the coords for flows going into the channel
        switch(side) {
          case NORTH:
            x1 = 0.375;
            z1 = 0;
            x2 = 0.625;
            z2 = 0.375;
            break;
          case SOUTH:
            x1 = 0.375;
            z1 = 0.625;
            x2 = 0.625;
            z2 = 1;
            break;
          case WEST:
            x1 = 0;
            z1 = 0.375;
            x2 = 0.375;
            z2 = 0.625;
            break;
          case EAST:
            x1 = 0.625;
            z1 = 0.375;
            x2 = 1;
            z2 = 0.625;
            break;
        }

        // only render the extra piece if no channel on this side
        if(!(world.getBlockState(offsetPos).getBlock() instanceof BlockChannel)) {
          RenderUtil.putATexturedQuad(renderer, flowing, x1, 0.375, z1, x2-x1, 0.09375, z2-z1, side, color, brightness, true);
        }

        // next, direction of flow
        // in means we are going the opposite direction
        if(connection == ChannelConnection.IN) {
          rotation = side;
        } else {
          rotation = side.getOpposite();
          outputs++;
          oneOutput = rotation;
        }

        RenderUtil.putARotatedQuad(renderer, flowing, x1, 0.46875, z1, x2-x1, z2-z1, rotation, color, brightness, true);
      } else {
        // sides of main sliver
        RenderUtil.putATexturedQuad(renderer, flowing, 0.375, 0.375, 0.375, 0.25, 0.09375, 0.25, side, color, brightness, true);
      }
    }

    // the stuff in the center
    // if we have just one output, have the center flow towards that
    if(outputs == 1) {
      RenderUtil.putARotatedQuad(renderer, flowing, 0.375, 0.46875, 0.375, 0.25, 0.25, oneOutput, color, brightness, true);
    } else {
      RenderUtil.putATexturedQuad(renderer, still, 0.375, 0.46875, 0.375, 0.25, 0, 0.25, EnumFacing.UP, color, brightness, false);
    }

    // downwards flow
    if(te.isConnectedDown()) {
      double xz1 = 0.375;
      double y1;
      double wd = 0.25;
      double h;
      if(te.isFlowingDown()) {
        // check how far into the 2nd block we want to render
        BlockPos below = pos.down();
        IBlockState state = world.getBlockState(below);
        Block block = state.getBlock();
        float yMin = -15f / 16f;
        if(block instanceof IFaucetDepth) {
          // negated so the interface is easier to understand
          yMin = -((IFaucetDepth) block).getFlowDepth(world, below, state);
        }

        y1 = 0;
        h = 0.125;
        RenderUtil.putATexturedQuad(renderer, flowing, xz1, y1, xz1, wd, h, wd, EnumFacing.NORTH, color, brightness, true);
        RenderUtil.putATexturedQuad(renderer, flowing, xz1, y1, xz1, wd, h, wd, EnumFacing.EAST,  color, brightness, true);
        RenderUtil.putATexturedQuad(renderer, flowing, xz1, y1, xz1, wd, h, wd, EnumFacing.SOUTH, color, brightness, true);
        RenderUtil.putATexturedQuad(renderer, flowing, xz1, y1, xz1, wd, h, wd, EnumFacing.WEST,  color, brightness, true);

        if(yMin < 0) {
          y1 = yMin;
          h = -yMin;
          RenderUtil.putATexturedQuad(renderer, flowing, xz1, y1, xz1, wd, h, wd, EnumFacing.NORTH, color, brightness, true);
          RenderUtil.putATexturedQuad(renderer, flowing, xz1, y1, xz1, wd, h, wd, EnumFacing.EAST,  color, brightness, true);
          RenderUtil.putATexturedQuad(renderer, flowing, xz1, y1, xz1, wd, h, wd, EnumFacing.SOUTH, color, brightness, true);
          RenderUtil.putATexturedQuad(renderer, flowing, xz1, y1, xz1, wd, h, wd, EnumFacing.WEST,  color, brightness, true);
        }

      } else {
        y1 = 0.375;
        h = 0;
      }
      // draw at current bottom
      RenderUtil.putATexturedQuad(renderer, still, xz1, y1, xz1, wd, h, wd, EnumFacing.DOWN,  color, brightness, false);
    }

    // Reset
    renderer.setTranslation(0.0D, 0.0D, 0.0D);
  }
}
