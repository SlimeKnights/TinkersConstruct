package slimeknights.tconstruct.library.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.model.data.FluidCuboid;
import slimeknights.tconstruct.library.client.model.data.FluidCuboid.FluidFace;

import javax.annotation.Nullable;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RenderUtil {
  @Getter
  private static final RenderType blockRenderType = RenderType.makeType(TConstruct.modID + ":block_render_type",
    DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP, 7, 256, true, false,
    RenderType.State.getBuilder().texture(new RenderState.TextureState(PlayerContainer.LOCATION_BLOCKS_TEXTURE, false, false))
      .shadeModel(RenderType.SHADE_ENABLED)
      .lightmap(RenderType.LIGHTMAP_ENABLED)
      .texture(RenderType.BLOCK_SHEET_MIPPED)
      .transparency(RenderType.TRANSLUCENT_TRANSPARENCY)
      .build(false));

  private static Minecraft mc = Minecraft.getInstance();

  /**
   * Renders a fluid block with offset from the matrices and from x1/y1/z1 to x2/y2/z2 inside the block local coordinates, so from 0-1
   */
  public static void renderFluidCuboid(FluidStack fluid, MatrixStack matrices, IVertexBuilder renderer, int combinedLight, float x1, float y1, float z1, float x2, float y2, float z2) {
    mc.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
    boolean upsideDown = fluid.getFluid().getAttributes().isGaseous(fluid);

    Function<ResourceLocation, TextureAtlasSprite> spriteGetter = mc.getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
    FluidAttributes attributes = fluid.getFluid().getAttributes();
    TextureAtlasSprite still = spriteGetter.apply(attributes.getStillTexture(fluid));
    TextureAtlasSprite flowing = spriteGetter.apply(attributes.getFlowingTexture(fluid));

    matrices.push();
    matrices.translate(x1, y1, z1);
    Matrix4f matrix = matrices.getLast().getMatrix();

    // x/y/z2 - x/y/z1 is because we need the width/height/depth
    int color = attributes.getColor(fluid);
    int rotation = upsideDown ? 180 : 0;
    putTexturedQuad(renderer, matrix, still,   x2 - x1, y2 - y1, z2 - z1, Direction.DOWN,  color, combinedLight, rotation, false);
    putTexturedQuad(renderer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.NORTH, color, combinedLight, rotation, true);
    putTexturedQuad(renderer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.EAST,  color, combinedLight, rotation, true);
    putTexturedQuad(renderer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.SOUTH, color, combinedLight, rotation, true);
    putTexturedQuad(renderer, matrix, flowing, x2 - x1, y2 - y1, z2 - z1, Direction.WEST,  color, combinedLight, rotation, true);
    putTexturedQuad(renderer, matrix, still,   x2 - x1, y2 - y1, z2 - z1, Direction.UP,    color, combinedLight, rotation, false);

    matrices.pop();
  }


  /* Fluid cuboids */

  /**
   * Adds a quad to the renderer
   * @param renderer          Renderer instnace
   * @param matrix            Render matrix
   * @param sprite            Sprite to render
   * @param w                 Sprite width
   * @param h                 Sprite height
   * @param d                 Sprite depth
   * @param face              Face to render
   * @param color             Color to use in rendering
   * @param brightness        Face brightness
   * @param flowing           If true, half texture coordinates
   */
  public static void putTexturedQuad(IVertexBuilder renderer, Matrix4f matrix, TextureAtlasSprite sprite, float w, float h, float d, Direction face,
                                     int color, int brightness, int rotation, boolean flowing) {
    // start with texture coordinates
    // TODO: is starting at 0 the best idea?
    double xt1 = 0, xt2 = w % 1d;
    double zt1 = 0, zt2 = d % 1d;
    // fluid expands upwards, so unless flipped start flowing at the bottom
    double yt1, yt2;
    if (rotation != 90 && rotation != 180) {
      yt1 = 1d - (h % 1d);
      yt2 = 1d;
    } else {
      yt1 = 0;
      yt2 = h % 1f;
    }
    // choose UV based on opposite two axis
    double ut1, ut2, vt1, vt2;
    switch (face.getAxis()) {
      case Y:
      default:
        ut1 = xt1; ut2 = xt2;
        vt1 = zt2; vt2 = zt1;
        break;
      case Z:
        ut1 = xt2; ut2 = xt1;
        vt1 = yt1; vt2 = yt2;
        break;
      case X:
        ut1 = zt2; ut2 = zt1;
        vt1 = yt1; vt2 = yt2;
        break;
    }
    // if rotating by 90 or 270, swap U and V
    float minU, maxU, minV, maxV;
    double size = flowing ? 8f : 16f;
    if ((rotation % 180) == 90) {
      minU = sprite.getInterpolatedU(vt1 * size);
      maxU = sprite.getInterpolatedU(vt2 * size);
      minV = sprite.getInterpolatedV(ut1 * size);
      maxV = sprite.getInterpolatedV(ut2 * size);
    } else {
      minU = sprite.getInterpolatedU(ut1 * size);
      maxU = sprite.getInterpolatedU(ut2 * size);
      minV = sprite.getInterpolatedV(vt1 * size);
      maxV = sprite.getInterpolatedV(vt2 * size);
    }
    // based on rotation, put coords into place
    float u1, u2, u3, u4, v1, v2, v3, v4;
    switch(rotation) {
      case 0:
      default:
        u1 = minU; v1 = maxV;
        u2 = minU; v2 = minV;
        u3 = maxU; v3 = minV;
        u4 = maxU; v4 = maxV;
        break;
      case 90:
        u1 = minU; v1 = minV;
        u2 = maxU; v2 = minV;
        u3 = maxU; v3 = maxV;
        u4 = minU; v4 = maxV;
        break;
      case 180:
        u1 = maxU; v1 = minV;
        u2 = maxU; v2 = maxV;
        u3 = minU; v3 = maxV;
        u4 = minU; v4 = minV;
        break;
      case 270:
        u1 = maxU; v1 = maxV;
        u2 = minU; v2 = maxV;
        u3 = minU; v3 = minV;
        u4 = maxU; v4 = minV;
        break;
    }
    // add quads
    int light1 = brightness >> 0x10 & 0xFFFF;
    int light2 = brightness & 0xFFFF;
    int a = color >> 24 & 0xFF;
    int r = color >> 16 & 0xFF;
    int g = color >> 8 & 0xFF;
    int b = color & 0xFF;
    switch (face) {
      case DOWN:
        renderer.pos(matrix, 0, 0, d).color(r, g, b, a).tex(u1, v1).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, 0, 0, 0).color(r, g, b, a).tex(u2, v2).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, w, 0, 0).color(r, g, b, a).tex(u3, v3).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, w, 0, d).color(r, g, b, a).tex(u4, v4).lightmap(light1, light2).endVertex();
        break;
      case UP:
        renderer.pos(matrix, 0, h, 0).color(r, g, b, a).tex(u1, v1).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, 0, h, d).color(r, g, b, a).tex(u2, v2).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, w, h, d).color(r, g, b, a).tex(u3, v3).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, w, h, 0).color(r, g, b, a).tex(u4, v4).lightmap(light1, light2).endVertex();
        break;
      case NORTH:
        renderer.pos(matrix, 0, 0, 0).color(r, g, b, a).tex(u1, v1).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, 0, h, 0).color(r, g, b, a).tex(u2, v2).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, w, h, 0).color(r, g, b, a).tex(u3, v3).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, w, 0, 0).color(r, g, b, a).tex(u4, v4).lightmap(light1, light2).endVertex();
        break;
      case SOUTH:
        renderer.pos(matrix, w, 0, d).color(r, g, b, a).tex(u1, v1).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, w, h, d).color(r, g, b, a).tex(u2, v2).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, 0, h, d).color(r, g, b, a).tex(u3, v3).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, 0, 0, d).color(r, g, b, a).tex(u4, v4).lightmap(light1, light2).endVertex();
        break;
      case WEST:
        renderer.pos(matrix, 0, 0, d).color(r, g, b, a).tex(u1, v1).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, 0, h, d).color(r, g, b, a).tex(u2, v2).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, 0, h, 0).color(r, g, b, a).tex(u3, v3).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, 0, 0, 0).color(r, g, b, a).tex(u4, v4).lightmap(light1, light2).endVertex();
        break;
      case EAST:
        renderer.pos(matrix, w, 0, 0).color(r, g, b, a).tex(u1, v1).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, w, h, 0).color(r, g, b, a).tex(u2, v2).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, w, h, d).color(r, g, b, a).tex(u3, v3).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, w, 0, d).color(r, g, b, a).tex(u4, v4).lightmap(light1, light2).endVertex();
        break;
    }
  }

  /**
   * Adds textured quads for a textured cuboid
   * @param matrices  Matrix stack instance
   * @param buffer    Buffer type
   * @param still     Still sprite
   * @param flowing   Flowing sprite
   * @param cube      Fluid cuboid
   * @param xSize     X size for cube
   * @param ySize     Y size for cube
   * @param zSize     Z size for cube
   * @param color     Fluid color
   * @param light     Quad lighting
   * @param isGas     If true, fluid is a gas
   */
  private static void putTexturedCuboid(MatrixStack matrices, IVertexBuilder buffer, FluidCuboid cube, TextureAtlasSprite still, TextureAtlasSprite flowing, float xSize, float ySize, float zSize, int color, int light, boolean isGas) {
    Matrix4f matrix = matrices.getLast().getMatrix();
    int rotation = isGas ? 180 : 0;
    for (Direction dir : Direction.values()) {
      FluidFace face = cube.getFace(dir);
      if (face != null) {
        boolean isFlowing = face.isFlowing();
        int faceRot = (rotation + face.getRotation()) % 360;
        RenderUtil.putTexturedQuad(buffer, matrix, isFlowing ? flowing : still, xSize, ySize, zSize, dir, color, light, faceRot, isFlowing);
      }
    }
  }

  /**
   * Adds textured quads for a textured cuboid
   * @param matrices  Matrix stack instance
   * @param buffer    Buffer type
   * @param still     Still sprite
   * @param flowing   Flowing sprite
   * @param cube      Fluid cuboid
   * @param color     Fluid color
   * @param light     Quad lighting
   * @param isGas     If true, fluid is a gas
   */
  public static void renderCuboid(MatrixStack matrices, IVertexBuilder buffer, FluidCuboid cube, float yOffset, TextureAtlasSprite still, TextureAtlasSprite flowing, int color, int light, boolean isGas) {
    matrices.push();
    // determine coords
    Vector3f from = cube.getFrom();
    Vector3f to = cube.getTo();
    float x1 = from.getX() / 16;
    float y1 = from.getY() / 16;
    float z1 = from.getZ() / 16;
    float xSize = to.getX() / 16 - x1;
    float ySize = to.getY() / 16 - y1;
    float zSize = to.getZ() / 16 - z1;
    matrices.translate(x1, yOffset + y1, z1);
    putTexturedCuboid(matrices, buffer, cube, still, flowing, xSize, ySize, zSize, color, light, isGas);
    matrices.pop();
  }

  /**
   * Adds textured quads for a textured cuboid
   * @param matrices  Matrix stack instance
   * @param buffer    Render type buffer instance
   * @param fluid     Fluid to render
   * @param height    Fluid height percentage, should be between 0 and 1
   * @param light     Quad lighting
   * @param cube      Fluid cuboid instance
   * @param flipGas   If true, flips gas cubes
   */
  public static void renderScaledCuboid(MatrixStack matrices, IRenderTypeBuffer buffer, FluidCuboid cube, FluidStack fluid, float height, int light, boolean flipGas) {
    // fluid attributes
    FluidAttributes attributes = fluid.getFluid().getAttributes();
    Function<ResourceLocation, TextureAtlasSprite> spriteGetter = mc.getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
    TextureAtlasSprite still = spriteGetter.apply(attributes.getStillTexture(fluid));
    TextureAtlasSprite flowing = spriteGetter.apply(attributes.getFlowingTexture(fluid));
    boolean isGas = attributes.isGaseous(fluid);
    // determine height based on fluid amount
    matrices.push();
    Vector3f from = cube.getFrom();
    Vector3f to = cube.getTo();
    float yMin = from.getY() / 16;
    float yMax = to.getY() / 16;
    float y1;
    float ySize = height * (yMax - yMin);
    if (flipGas && isGas) {
      y1 = yMax - ySize;
    } else {
      y1 = yMin;
    }
    // other coords
    float x1 = from.getX() / 16;
    float z1 = from.getZ() / 16;
    matrices.translate(x1, y1, z1);
    putTexturedCuboid(matrices, buffer.getBuffer(blockRenderType), cube, still, flowing,to.getX() / 16 - x1, ySize, to.getZ() / 16 - z1, attributes.getColor(fluid), light, isGas);
    matrices.pop();
  }

  public static void setColorRGB(int color) {
    setColorRGBA(color | 0xff000000);
  }

  public static void setColorRGBA(int color) {
    float a = alpha(color) / 255.0F;
    float r = red(color) / 255.0F;
    float g = green(color) / 255.0F;
    float b = blue(color) / 255.0F;

    RenderSystem.color4f(r, g, b, a);
  }

  public static int compose(int r, int g, int b, int a) {
    int rgb = a;
    rgb = (rgb << 8) + r;
    rgb = (rgb << 8) + g;
    rgb = (rgb << 8) + b;
    return rgb;
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


  /* Models */

  /**
   * Gets the model for the given block
   * @param state  Block state
   * @param clazz  Class type to cast result into
   * @param <T>    Class type
   * @return  Block model, or null if its missing or the wrong class type
   */
  @Nullable
  public static <T extends IBakedModel> T getBakedModel(BlockState state, Class<T> clazz) {
    IBakedModel baked = mc.getModelManager().getBlockModelShapes().getModel(state);
    if (clazz.isInstance(baked)) {
      return clazz.cast(baked);
    }
    return null;
  }

  /**
   * Gets the model for the given item
   * @param item   Item provider
   * @param clazz  Class type to cast result into
   * @param <T>    Class type
   * @return  Item model, or null if its missing or the wrong class type
   */
  @Nullable
  public static <T extends IBakedModel> T getBakedModel(IItemProvider item, Class<T> clazz) {
    IBakedModel baked = mc.getItemRenderer().getItemModelMesher().getItemModel(item.asItem());
    if (clazz.isInstance(baked)) {
      return clazz.cast(baked);
    }
    return null;
  }
}
