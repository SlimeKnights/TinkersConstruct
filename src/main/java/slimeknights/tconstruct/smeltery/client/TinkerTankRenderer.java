package slimeknights.tconstruct.smeltery.client;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;
import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockTinkerTankController;
import slimeknights.tconstruct.smeltery.tileentity.TileTinkerTank;

public class TinkerTankRenderer extends SmelteryTankRenderer<TileTinkerTank> {
  public static final String TEXTURE_TINKER_TANK_BACKGROUND = "tconstruct:blocks/smeltery/tinker_tank_background";

  protected static Minecraft mc = Minecraft.getMinecraft();

  @Override
  public void renderTileEntityAt(@Nonnull TileTinkerTank tinkerTank, double x, double y, double z, float partialTicks, int destroyStage) {
    if(!tinkerTank.isActive()) {
      return;
    }

    // safety first!
    if(tinkerTank.minPos == null || tinkerTank.maxPos == null) {
      return;
    }

    // generic data
    SmelteryTank tank = tinkerTank.getTank();
    BlockPos pos = tinkerTank.getPos();

    // draw the bottom most fluid on the controller itself
    World world = tinkerTank.getWorld();
    IBlockState state = world.getBlockState(pos);
    if(state.getBlock() == TinkerSmeltery.tinkerTankController) {
      Tessellator tessellator = Tessellator.getInstance();
      VertexBuffer renderer = tessellator.getBuffer();

      renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
      mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

      EnumFacing face = world.getBlockState(pos).getValue(BlockTinkerTankController.FACING);

      // set up data for the sprite rendering
      FluidStack fluidStack = tank.getFluid();
      TextureAtlasSprite sprite;
      int brightness, color;
      if(fluidStack != null) {
        Fluid fluid = fluidStack.getFluid();
        sprite = mc.getTextureMapBlocks().getTextureExtry(fluid.getStill().toString());
        brightness = world.getCombinedLight(pos.offset(face), fluid.getLuminosity(fluidStack));
        color = fluid.getColor(fluidStack);
      }
      else {
        // if the fluid is missing (aka empty tank) use the default texture
        sprite = mc.getTextureMapBlocks().getTextureExtry(TEXTURE_TINKER_TANK_BACKGROUND);
        brightness = world.getCombinedLight(pos.offset(face), 0);
        color = 0xFFFFFFFF;
      }

      RenderUtil.pre(x, y, z);

      // fluid is missing texture/default got lost
      if(sprite == null) {
        sprite = mc.getTextureMapBlocks().getMissingSprite();
      }

      float d = RenderUtil.FLUID_OFFSET;
      float d2 = 1 - (d * 2);
      GlStateManager.disableBlend(); // transparent liquids should render solid textured
      RenderUtil.putTexturedQuad(renderer, sprite, d, d, d, d2, d2, d2, face, color, brightness, false);
      tessellator.draw();
      RenderUtil.post();
    }

    // draw the fluids inside
    renderFluids(tank, pos, tinkerTank.minPos, tinkerTank.maxPos, x, y, z);
  }
}
