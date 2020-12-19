package slimeknights.tconstruct.smeltery.client;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.smeltery.block.BlockTinkerTankController;
import slimeknights.tconstruct.smeltery.tileentity.TileTinkerTank;

public class TinkerTankRenderer extends SmelteryTankRenderer<TileTinkerTank> {
  protected static Minecraft mc = Minecraft.getMinecraft();

  @Override
  public void render(@Nonnull TileTinkerTank tinkerTank, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
    if(!tinkerTank.isActive()) {
      return;
    }

    BlockPos tilePos = tinkerTank.getPos();
    BlockPos minPos = tinkerTank.getMinPos();
    BlockPos maxPos = tinkerTank.getMaxPos();

    // safety first!
    if(minPos == null || maxPos == null) {
      return;
    }

    // generic data
    SmelteryTank tank = tinkerTank.getTank();

    // draw the bottom most fluid on the controller itself
    World world = tinkerTank.getWorld();
    IBlockState state = world.getBlockState(tilePos);
    // on the odd chance something outside of us changes the blockstate and uses this TE
    if(state.getBlock() instanceof BlockTinkerTankController) {

      // make sure we have a fluid before continuing
      FluidStack fluidStack = tank.getFluid();
      // we draw water for an active tank so people know it's active
      if(fluidStack == null) {
        fluidStack = new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
      }

      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder renderer = tessellator.getBuffer();

      renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
      mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

      EnumFacing face = world.getBlockState(tilePos).getValue(BlockTinkerTankController.FACING);

      // set up data for the sprite rendering
      Fluid fluid = fluidStack.getFluid();
      TextureAtlasSprite sprite = mc.getTextureMapBlocks().getTextureExtry(fluid.getStill().toString());
      int brightness = world.getCombinedLight(tilePos.offset(face), fluid.getLuminosity(fluidStack));
      int color = fluid.getColor(fluidStack);

      RenderUtil.pre(x, y, z);

      // fluid is missing texture
      if(sprite == null) {
        sprite = mc.getTextureMapBlocks().getMissingSprite();
      }

      float d = RenderUtil.FLUID_OFFSET;
      float d2 = 1 - (d * 2);

      // transparent liquids should render solid textured
      // otherwise it would look bad as our backface is so dark
      GlStateManager.disableBlend();
      RenderUtil.putATexturedQuad(renderer, sprite, d, d, d, d2, d2 - 0.3f, d2, face, color, brightness, false);
      tessellator.draw();
      RenderUtil.post();
    }

    // draw the fluids inside
    // we don't offset the minPos for lighting since its possible to have a solid block (always light 0) where the liquid starts
    renderFluids(tank, tilePos, minPos.add(-1, 0, -1), maxPos.add(1, 0, 1), x, y, z, 0.0625f, minPos);
  }
}
