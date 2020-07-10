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
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.model.data.FluidCuboid;
import slimeknights.tconstruct.library.client.model.data.FluidCuboid.FluidFace;
import slimeknights.tconstruct.library.client.model.data.ModelItem;

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

  /* Fluid cuboids */

  /**
   * Adds a quad to the renderer
   * @param renderer    Renderer instnace
   * @param matrix      Render matrix
   * @param sprite      Sprite to render
   * @param from        Quad start
   * @param to          Quad end
   * @param face        Face to render
   * @param color       Color to use in rendering
   * @param brightness  Face brightness
   * @param flowing     If true, half texture coordinates
   */
  private static void putTexturedQuad(IVertexBuilder renderer, Matrix4f matrix, TextureAtlasSprite sprite, Vector3f from, Vector3f to, Direction face, int color, int brightness, int rotation, boolean flowing) {
    // start with texture coordinates
    float x1 = from.getX(), y1 = from.getY(), z1 = from.getZ();
    float x2 = to.getX(), y2 = to.getY(), z2 = to.getZ();
    // choose UV based on opposite two axis
    float u1, u2, v1, v2;
    switch (face.getAxis()) {
      case Y:
      default:
        u1 = x1; u2 = x2;
        v1 = z2; v2 = z1;
        break;
      case Z:
        u1 = x2; u2 = x1;
        v1 = y1; v2 = y2;
        break;
      case X:
        u1 = z2; u2 = z1;
        v1 = y1; v2 = y2;
        break;
    }
    // flip V when relevant
    if (rotation == 0 || rotation == 270) {
      float temp = v1;
      v1 = 1f - v2;
      v2 = 1f - temp;
    }
    // flip U when relevant
    if (rotation >= 180) {
      float temp = u1;
      u1 = 1f - u2;
      u2 = 1f - temp;
    }
    // if rotating by 90 or 270, swap U and V
    float minU, maxU, minV, maxV;
    double size = flowing ? 8 : 16;
    if ((rotation % 180) == 90) {
      minU = sprite.getInterpolatedU(v1 * size);
      maxU = sprite.getInterpolatedU(v2 * size);
      minV = sprite.getInterpolatedV(u1 * size);
      maxV = sprite.getInterpolatedV(u2 * size);
    } else {
      minU = sprite.getInterpolatedU(u1 * size);
      maxU = sprite.getInterpolatedU(u2 * size);
      minV = sprite.getInterpolatedV(v1 * size);
      maxV = sprite.getInterpolatedV(v2 * size);
    }
    // based on rotation, put coords into place
    float u3, u4, v3, v4;
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
        renderer.pos(matrix, x1, y1, z2).color(r, g, b, a).tex(u1, v1).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x1, y1, z1).color(r, g, b, a).tex(u2, v2).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x2, y1, z1).color(r, g, b, a).tex(u3, v3).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x2, y1, z2).color(r, g, b, a).tex(u4, v4).lightmap(light1, light2).endVertex();
        break;
      case UP:
        renderer.pos(matrix, x1, y2, z1).color(r, g, b, a).tex(u1, v1).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x1, y2, z2).color(r, g, b, a).tex(u2, v2).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x2, y2, z2).color(r, g, b, a).tex(u3, v3).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x2, y2, z1).color(r, g, b, a).tex(u4, v4).lightmap(light1, light2).endVertex();
        break;
      case NORTH:
        renderer.pos(matrix, x1, y1, z1).color(r, g, b, a).tex(u1, v1).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x1, y2, z1).color(r, g, b, a).tex(u2, v2).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x2, y2, z1).color(r, g, b, a).tex(u3, v3).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x2, y1, z1).color(r, g, b, a).tex(u4, v4).lightmap(light1, light2).endVertex();
        break;
      case SOUTH:
        renderer.pos(matrix, x2, y1, z2).color(r, g, b, a).tex(u1, v1).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x2, y2, z2).color(r, g, b, a).tex(u2, v2).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x1, y2, z2).color(r, g, b, a).tex(u3, v3).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x1, y1, z2).color(r, g, b, a).tex(u4, v4).lightmap(light1, light2).endVertex();
        break;
      case WEST:
        renderer.pos(matrix, x1, y1, z2).color(r, g, b, a).tex(u1, v1).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x1, y2, z2).color(r, g, b, a).tex(u2, v2).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x1, y2, z1).color(r, g, b, a).tex(u3, v3).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x1, y1, z1).color(r, g, b, a).tex(u4, v4).lightmap(light1, light2).endVertex();
        break;
      case EAST:
        renderer.pos(matrix, x2, y1, z1).color(r, g, b, a).tex(u1, v1).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x2, y2, z1).color(r, g, b, a).tex(u2, v2).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x2, y2, z2).color(r, g, b, a).tex(u3, v3).lightmap(light1, light2).endVertex();
        renderer.pos(matrix, x2, y1, z2).color(r, g, b, a).tex(u4, v4).lightmap(light1, light2).endVertex();
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
   * @param from      Fluid start
   * @param to        Fluid end
   * @param color     Fluid color
   * @param light     Quad lighting
   * @param isGas     If true, fluid is a gas
   */
  private static void putTexturedCuboid(MatrixStack matrices, IVertexBuilder buffer, FluidCuboid cube, TextureAtlasSprite still, TextureAtlasSprite flowing, Vector3f from, Vector3f to, int color, int light, boolean isGas) {
    Matrix4f matrix = matrices.getLast().getMatrix();
    int rotation = isGas ? 180 : 0;
    for (Direction dir : Direction.values()) {
      FluidFace face = cube.getFace(dir);
      if (face != null) {
        boolean isFlowing = face.isFlowing();
        int faceRot = (rotation + face.getRotation()) % 360;
        RenderUtil.putTexturedQuad(buffer, matrix, isFlowing ? flowing : still, from, to, dir, color, light, faceRot, isFlowing);
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
  public static void putTexturedCuboid(MatrixStack matrices, IVertexBuilder buffer, FluidCuboid cube, float yOffset, TextureAtlasSprite still, TextureAtlasSprite flowing, int color, int light, boolean isGas) {
    if (yOffset != 0) {
      matrices.push();
      matrices.translate(0, yOffset, 0);
    }
    putTexturedCuboid(matrices, buffer, cube, still, flowing, cube.getFromScaled(), cube.getToScaled(), color, light, isGas);
    if (yOffset != 0) {
      matrices.pop();
    }
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
    Vector3f from = cube.getFromScaled();
    Vector3f to = cube.getToScaled();
    // gas renders upside down
    float minY = from.getY();
    float maxY = to.getY();
    if (isGas && flipGas) {
      from = from.copy();
      from.setY(maxY + (height * (minY - maxY)));
    } else {
      to = to.copy();
      to.setY(minY + (height * (maxY - minY)));
    }

    // draw cuboid
    putTexturedCuboid(matrices, buffer.getBuffer(blockRenderType), cube, still, flowing, from, to, attributes.getColor(fluid), light, isGas);
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

  /* Items */

  /**
   * Renders a single item in a TESR
   * @param matrices    Matrix stack inst ance
   * @param buffer      Buffer instance
   * @param item        Item to render
   * @param modelItem   Model items for render information
   * @param light       Model light
   */
  public static void renderItem(MatrixStack matrices, IRenderTypeBuffer buffer, ItemStack item, ModelItem modelItem, int light) {
    // if the item says skip, skip
    if (modelItem.isEmpty()) return;
    // if no stack, skip
    if (item.isEmpty()) return;

    // start rendering
    matrices.push();
    Vector3f center = modelItem.getCenterScaled();
    matrices.translate(center.getX(), center.getY(), center.getZ());

    // scale
    float scale = modelItem.getSizeScaled();
    matrices.scale(scale, scale, scale);

    // rotate X, then Y
    float x = modelItem.getX();
    if (x != 0) {
      matrices.rotate(Vector3f.XP.rotationDegrees(x));
    }
    float y = modelItem.getY();
    if (y != 0) {
      matrices.rotate(Vector3f.YP.rotationDegrees(y));
    }

    // render the actual item
    Minecraft.getInstance().getItemRenderer().renderItem(item, TransformType.NONE, light, OverlayTexture.NO_OVERLAY, matrices, buffer);
    matrices.pop();
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
