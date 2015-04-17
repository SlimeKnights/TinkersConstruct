package tconstruct.library.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import tconstruct.library.client.texture.SimpleColoredTexture;
import tconstruct.library.client.texture.TextureColoredTexture;

/**
 * Determines the type of texture used for rendering a specific material
 */
@SideOnly(Side.CLIENT)
public interface MaterialRenderInfo {
  TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location);

  /** Colors the texture of the tool with the material color */
  class Default implements MaterialRenderInfo {
    // colors to be used
    protected final int low, mid, high;

    public Default(int low, int mid, int high) {
      this.low = low;
      this.mid = mid;
      this.high = high;
    }

    public Default(int color) {
      this(color, color, color);
    }

    @Override
    public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
      return new SimpleColoredTexture(low, mid, high, baseTexture, location);
    }
  }

  class CustomDefault extends Default {
    protected final String customSuffix;

    public CustomDefault(int low, int mid, int high, String customSuffix) {
      super(low, mid, high);
      this.customSuffix = customSuffix;
    }

    public CustomDefault(int color, String customSuffix) {
      super(color);
      this.customSuffix = customSuffix;
    }

    @Override
    public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
      // use the base texture with the suffix if it exists
      if(CustomTextureCreator.exists(baseTexture.toString() + "_" + customSuffix))
        return new SimpleColoredTexture(low, mid, high, baseTexture.getIconName(), customSuffix, location);
      // otherwise default texture
      return super.getTexture(baseTexture, location);
    }
  }

  /** Uses a block texture instead of a color to create the texture */
  class BlockTexture implements MaterialRenderInfo {
    protected final Block block;

    public BlockTexture(Block block) {
      this.block = block;
    }

    @Override
    public TextureAtlasSprite getTexture(TextureAtlasSprite baseTexture, String location) {
      ResourceLocation blockloc = new ResourceLocation(block.getDefaultState().toString());
      blockloc = new ResourceLocation(blockloc.getResourceDomain(), "blocks/" + blockloc.getResourcePath());
      TextureAtlasSprite blockTexture = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(blockloc.toString());

      TextureColoredTexture sprite = new TextureColoredTexture(blockTexture, baseTexture, location);
      sprite.stencil = false;
      return sprite;
    }
  }
}
