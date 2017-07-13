package slimeknights.tconstruct.library.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.library.client.texture.AnimatedColoredTexture;
import slimeknights.tconstruct.library.client.texture.InverseColoredTexture;
import slimeknights.tconstruct.library.client.texture.MetalColoredTexture;
import slimeknights.tconstruct.library.client.texture.MetalTextureTexture;
import slimeknights.tconstruct.library.client.texture.SimpleColoredTexture;
import slimeknights.tconstruct.library.client.texture.TextureColoredTexture;
import slimeknights.tconstruct.library.client.texture.TinkerTexture;

/**
 * Determines the type of texture used for rendering a specific material
 */
@SideOnly(Side.CLIENT)
public interface MaterialRenderInfo {

  TextureAtlasSprite getTexture(ResourceLocation baseTexture, String location);

  boolean isStitched();

  boolean useVertexColoring();

  int getVertexColor();

  // this actually would require its own thing, but we put it here for simplicity
  String getTextureSuffix();

  MaterialRenderInfo setTextureSuffix(String suffix);

  abstract class AbstractMaterialRenderInfo implements MaterialRenderInfo {

    private String suffix;

    @Override
    public boolean isStitched() {
      return true;
    }

    @Override
    public boolean useVertexColoring() {
      return false;
    }

    @Override
    public int getVertexColor() {
      return 0xffffffff; // white and opaque
    }

    @Override
    public String getTextureSuffix() {
      return suffix;
    }

    @Override
    public MaterialRenderInfo setTextureSuffix(String suffix) {
      this.suffix = suffix;
      return this;
    }
  }

  /**
   * Does not actually generate a new texture. Used for vertex-coloring in the model generation
   * Safes VRAM, so we use vertex colors instead of creating new data.
   */
  class Default extends AbstractMaterialRenderInfo {

    public final int color;

    public Default(int color) {
      this.color = color;
    }

    @Override
    public TextureAtlasSprite getTexture(ResourceLocation baseTexture, String location) {
      return TinkerTexture.loadManually(baseTexture);
    }

    @Override
    public boolean isStitched() {
      return false;
    }

    @Override
    public boolean useVertexColoring() {
      return true;
    }

    @Override
    public int getVertexColor() {
      return color;
    }
  }

  /**
   * Colors the texture of the tool with the material color
   */
  class MultiColor extends AbstractMaterialRenderInfo {

    // colors to be used
    protected final int low, mid, high;

    public MultiColor(int low, int mid, int high) {
      this.low = low;
      this.mid = mid;
      this.high = high;
    }

    @Override
    public TextureAtlasSprite getTexture(ResourceLocation baseTexture, String location) {
      return new SimpleColoredTexture(low, mid, high, baseTexture, location);
    }
  }

  class InverseMultiColor extends MultiColor {

    public InverseMultiColor(int low, int mid, int high) {
      super(low, mid, high);
    }

    @Override
    public TextureAtlasSprite getTexture(ResourceLocation baseTexture, String location) {
      return new InverseColoredTexture(low, mid, high, baseTexture, location);
    }
  }

  class Metal extends AbstractMaterialRenderInfo {

    protected float shinyness;
    protected float brightness;
    protected float hueshift;
    public int color;

    public Metal(int color, float shinyness, float brightness, float hueshift) {
      this.color = color;
      this.shinyness = shinyness;
      this.brightness = brightness;
      this.hueshift = hueshift;
    }

    public Metal(int color) {
      this(color, 0.4f, 0.4f, 0.1f);
    }


    @Override
    public TextureAtlasSprite getTexture(ResourceLocation baseTexture, String location) {
      return new MetalColoredTexture(baseTexture, location, color, shinyness, brightness, hueshift);
    }
  }

  class MetalTextured extends Metal {

    protected ResourceLocation extraTexture;

    public MetalTextured(ResourceLocation extraTexture, int color, float shinyness, float brightness, float hueshift) {
      super(color, shinyness, brightness, hueshift);
      this.extraTexture = extraTexture;
    }

    @Override
    public TextureAtlasSprite getTexture(ResourceLocation baseTexture, String location) {
      return new MetalTextureTexture(extraTexture, baseTexture, location, color, shinyness, brightness, hueshift);
    }
  }

  /**
   * Uses a (block) texture instead of a color to create the texture
   */
  class BlockTexture extends AbstractMaterialRenderInfo {

    protected ResourceLocation texturePath;

    public BlockTexture(ResourceLocation texturePath) {
      this.texturePath = texturePath;
    }

    @Override
    public TextureAtlasSprite getTexture(ResourceLocation baseTexture, String location) {
      TextureAtlasSprite blockTexture = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(texturePath.toString());

      if(blockTexture == null) {
        blockTexture = TinkerTexture.loadManually(texturePath);
      }

      TextureColoredTexture sprite = new TextureColoredTexture(new ResourceLocation(blockTexture.getIconName()), baseTexture, location);
      sprite.stencil = false;
      return sprite;
    }
  }


  /**
   * Creates an animated texture from an animated base texture. USE WITH CAUTION.
   * ACTUALLY ONLY USE THIS IF YOU KNOW EXACTLY WHAT YOU'RE DOING.
   */
  class AnimatedTexture extends AbstractMaterialRenderInfo {

    protected String texturePath;

    public AnimatedTexture(String texturePath) {
      this.texturePath = texturePath;
    }

    @Override
    public TextureAtlasSprite getTexture(ResourceLocation baseTexture, String location) {
      TextureAtlasSprite blockTexture = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(texturePath);

      if(blockTexture == null) {
        blockTexture = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
      }

      TextureColoredTexture sprite = new AnimatedColoredTexture(new ResourceLocation(blockTexture.getIconName()), baseTexture, location);
      return sprite;
    }
  }

}
