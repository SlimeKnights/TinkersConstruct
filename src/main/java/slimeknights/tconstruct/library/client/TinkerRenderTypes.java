package slimeknights.tconstruct.library.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderType;
import slimeknights.mantle.client.render.MantleRenderTypes;
import slimeknights.tconstruct.TConstruct;

import java.util.OptionalDouble;

public class TinkerRenderTypes extends RenderType {
  public TinkerRenderTypes(String name, VertexFormat format, Mode mode, int bufferSize, boolean affectsCrumbling, boolean sort, Runnable setupState, Runnable clearState) {
    super(name, format, mode, bufferSize, affectsCrumbling, sort, setupState, clearState);
  }

  /** Render type for the error block that is seen through everything, mostly based on {@link RenderType#LINES} */
  public static final RenderType ERROR_BLOCK = RenderType.create(
    TConstruct.resourceString("lines"), DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 256, false, false,
    RenderType.CompositeState.builder()
                             .setShaderState(RENDERTYPE_LINES_SHADER)
                             .setLineState(new LineStateShard(OptionalDouble.empty()))
                             .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                             .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                             .setOutputState(ITEM_ENTITY_TARGET)
                             .setWriteMaskState(COLOR_DEPTH_WRITE)
                             .setCullState(NO_CULL)
                             .setDepthTestState(NO_DEPTH_TEST)
                             .createCompositeState(false));

  /** Render type for fluids, like {@link slimeknights.mantle.client.render.MantleRenderTypes#FLUID}, but disables cull so both sides show */
  public static final RenderType SMELTERY_FLUID = RenderType.create(
    TConstruct.resourceString("smeltery_fluid"), DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true,
    CompositeState.builder()
                  .setLightmapState(LIGHTMAP)
                  .setShaderState(MantleRenderTypes.FLUID_SHADER)
                  .setTextureState(BLOCK_SHEET_MIPPED)
                  .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                  .setCullState(NO_CULL)
                  .createCompositeState(false));
}
