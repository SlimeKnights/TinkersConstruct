package slimeknights.tconstruct.library.client;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraftforge.fluids.FluidAttributes;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import slimeknights.mantle.client.model.fluid.FluidCuboid;
import slimeknights.mantle.client.render.FluidRenderer;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RenderUtils {
  /**
   * Adds a fluid cuboid with transparency
   * @param matrices  Matrix stack instance
   * @param buffer    Render type buffer instance
   * @param fluid     Fluid to render
   * @param opacity   Fluid opacity to blend in
   * @param light     Quad lighting
   * @param cube      Fluid cuboid instance
   */
  public static void renderTransparentCuboid(MatrixStack matrices, VertexConsumerProvider buffer, FluidCuboid cube, FluidVolume fluid, int opacity, int light) {
    // nothing to render? skip
    if (opacity < 0 || fluid.isEmpty()) {
      return;
    }

    FluidAttributes attributes = fluid.getFluid().getAttributes();
    Sprite still = FluidRenderer.getBlockSprite(attributes.getStillTexture(fluid));
    Sprite flowing = FluidRenderer.getBlockSprite(attributes.getFlowingTexture(fluid));
    boolean isGas = attributes.isGaseous(fluid);
    light = FluidRenderer.withBlockLight(light, attributes.getLuminosity(fluid));

    // add in fluid opacity if given
    int color = attributes.getColor(fluid);
    if (opacity < 0xFF) {
      // alpha is top 8 bits, multiply by opacity and divide out remainder
      int alpha = ((color >> 24) & 0xFF) * opacity / 0xFF;
      // clear bits in color and or in the new alpha
      color = (color & 0xFFFFFF) | (alpha << 24);
    }
    FluidRenderer.renderCuboid(matrices, buffer.getBuffer(FluidRenderer.RENDER_TYPE), cube, still, flowing, cube.getFromScaled(), cube.getToScaled(), color, light, isGas);
  }

  /**
   * Add textured quads for a fluid tank
   * @param matrices      Matrix stack instance
   * @param buffer        Render type buffer instance
   * @param tank          Fluid tank animated to render=
   * @param light         Quad lighting
   * @param cube          Fluid cuboid instance
   * @param partialTicks  Partial ticks
   * @param flipGas       If true, flips gas cubes
   */
  public static void renderFluidTank(MatrixStack matrices, VertexConsumerProvider buffer, FluidCuboid cube, FluidTankAnimated tank, int light, float partialTicks, boolean flipGas) {
    // render liquid if present
    FluidVolume liquid = tank.getFluid();
    int capacity = tank.getCapacity();
    if (!liquid.isEmpty() && capacity > 0) {
      // update render offset
      float offset = tank.getRenderOffset();
      if (offset > 1.2f || offset < -1.2f) {
        offset = offset - ((offset / 12f + 0.1f) * partialTicks);
        tank.setRenderOffset(offset);
      } else {
        tank.setRenderOffset(0);
      }

      // fetch fluid information from the model
      FluidRenderer.renderScaledCuboid(matrices, buffer, cube, liquid, offset, capacity, light, flipGas);
    } else {
      // clear render offet if no liquid
      tank.setRenderOffset(0);
    }
  }

  public static void setColorRGBA(int color) {
    float a = alpha(color) / 255.0F;
    float r = red(color) / 255.0F;
    float g = green(color) / 255.0F;
    float b = blue(color) / 255.0F;

    RenderSystem.color4f(r, g, b, a);
  }

  public static int alpha(int c) {
    return (c >> 24) & 0xFF;
  }

  public static int red(int c) {
    return (c >> 16) & 0xFF;
  }

  public static int green(int c) {
    return (c >> 8) & 0xFF;
  }

  public static int blue(int c) {
    return (c) & 0xFF;
  }
}
