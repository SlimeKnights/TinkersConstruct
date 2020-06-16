package slimeknights.tconstruct.library.client.renderer.font;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.EmptyGlyph;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class CustomFontRenderer extends FontRenderer implements ISelectiveResourceReloadListener {

  private int state = 0;
  private int redFromColor;
  private int greenFromColor;
  private int blueFromColor;

  public CustomFontRenderer(TextureManager textureManagerIn, Font fontIn) {
    super(textureManagerIn, fontIn);
  }

  public CustomFontRenderer(FontRenderer fontRenderer) {
    super(fontRenderer.textureManager, fontRenderer.font);
  }

  @Nonnull
  @Override
  public List<String> listFormattedStringToWidth(@Nonnull String str, int wrapWidth) {
    return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
  }

  @Override
  public String wrapFormattedStringToWidth(String str, int wrapWidth) {
    int i = this.sizeStringToWidth(str, wrapWidth);

    if (str.length() <= i) {
      return str;
    } else {
      String s = str.substring(0, i);
      char character = str.charAt(i);
      boolean obfuscated = character == 32 || character == 10;
      String s1 = getCustomFormatFromString(s) + str.substring(i + (obfuscated ? 1 : 0));
      return s + "\n" + this.wrapFormattedStringToWidth(s1, wrapWidth);
    }
  }

  public static String getCustomFormatFromString(String text) {
    String s = "";
    int i = 0;
    int j = text.length();

    while ((i < j - 1)) {
      char c = text.charAt(i);
      // vanilla formatting
      if (c == 167) {

        char character = text.charAt(i + 1);

        if (character >= 48 && character <= 57 || character >= 97 && character <= 102 || character >= 65 && character <= 70) {
          s = "\u00a7" + character;
          i++;
        } else if (character >= 107 && character <= 111 || character >= 75 && character <= 79 || character == 114 || character == 82) {
          s = s + "\u00a7" + character;
          i++;
        }
      }
      // custom formatting
      else if ((int) c >= CustomFontColor.MARKER && (int) c <= CustomFontColor.MARKER + 0xFF) {
        s = String.format("%s%s%s", c, text.charAt(i + 1), text.charAt(i + 2));
        i += 2;
      }
      i++;
    }

    return s;
  }

  @Override
  public int renderString(String text, float x, float y, int color, Matrix4f matrix, boolean dropShadow) {
    return super.renderString(text, x, y, color, matrix, dropShadow);
  }

  @Override
  public float renderStringAtPos(String text, float x, float y, int colorIn, boolean isShadow, Matrix4f matrix, IRenderTypeBuffer buffer, boolean isTransparent, int colorBackgroundIn, int packedLight) {
    float offset = isShadow ? 0.25F : 1.0F;
    float defaultRed = (float) (colorIn >> 16 & 255) / 255.0F * offset;
    float defaultGreen = (float) (colorIn >> 8 & 255) / 255.0F * offset;
    float defaultBlue = (float) (colorIn & 255) / 255.0F * offset;
    float xOut = x;
    float red = defaultRed;
    float green = defaultGreen;
    float blue = defaultBlue;
    float alpha = (float) (colorIn >> 24 & 255) / 255.0F;
    boolean obfuscated = false;
    boolean bold = false;
    boolean italic = false;
    boolean underline = false;
    boolean strikethough = false;
    List<TexturedGlyph.Effect> effects = Lists.newArrayList();

    for (int i = 0; i < text.length(); ++i) {
      char character = text.charAt(i);

      if (character == 167 && i + 1 < text.length()) {
        TextFormatting textformatting = TextFormatting.fromFormattingCode(text.charAt(i + 1));

        if (textformatting != null) {
          if (textformatting.isNormalStyle()) {
            obfuscated = false;
            bold = false;
            strikethough = false;
            underline = false;
            italic = false;
            red = defaultRed;
            green = defaultGreen;
            blue = defaultBlue;
          }

          if (textformatting.getColor() != null) {
            int color = textformatting.getColor();

            red = (float) (color >> 16 & 255) / 255.0F * offset;
            green = (float) (color >> 8 & 255) / 255.0F * offset;
            blue = (float) (color & 255) / 255.0F * offset;
          } else if (textformatting == TextFormatting.OBFUSCATED) {
            obfuscated = true;
          } else if (textformatting == TextFormatting.BOLD) {
            bold = true;
          } else if (textformatting == TextFormatting.STRIKETHROUGH) {
            strikethough = true;
          } else if (textformatting == TextFormatting.UNDERLINE) {
            underline = true;
          } else if (textformatting == TextFormatting.ITALIC) {
            italic = true;
          }
        }

        ++i;
      } else {
        boolean shouldDrawCharacter = true;

        if ((int) character >= CustomFontColor.MARKER && (int) character <= CustomFontColor.MARKER + 0xFF) {
          int value = character & 0xFF;

          switch (this.state) {
            case 0:
              this.redFromColor = value;
              break;
            case 1:
              this.greenFromColor = value;
              break;
            case 2:
              this.blueFromColor = value;
              break;

          }

          this.state = ++this.state % 3;

          int colorFromFont = (this.redFromColor << 16) | (this.greenFromColor << 8) | this.blueFromColor | (0xff << 24);

          if ((colorFromFont & -67108864) == 0) {
            colorFromFont |= -16777216;
          }

          if (isShadow) {
            colorFromFont = (colorFromFont & 16579836) >> 2 | colorFromFont & -16777216;
          }

          red = (float) ((colorFromFont >> 16) & 255) / 255.0F * offset;
          green = (float) ((colorFromFont >> 8) & 255) / 255.0F * offset;
          blue = (float) (colorFromFont & 255) / 255.0F * offset;
          alpha = (float) ((colorFromFont >> 24) & 255) / 255.0F;

          shouldDrawCharacter = false;
        }

        if(shouldDrawCharacter) {
          if (this.state != 0) {
            this.state = 0;
          }
        }

        IGlyph glyph = this.font.findGlyph(character);
        TexturedGlyph texturedGlyph = obfuscated && character != ' ' ? this.font.obfuscate(glyph) : this.font.getGlyph(character);

        if (shouldDrawCharacter) {
          if (!(texturedGlyph instanceof EmptyGlyph)) {
            float boldOffset = bold ? glyph.getBoldOffset() : 0.0F;
            float shadowOffset = isShadow ? glyph.getShadowOffset() : 0.0F;
            IVertexBuilder ivertexbuilder = buffer.getBuffer(texturedGlyph.getRenderType(isTransparent));

            this.drawGlyph(texturedGlyph, bold, italic, boldOffset, xOut + shadowOffset, y + shadowOffset, matrix, ivertexbuilder, red, green, blue, alpha, packedLight);
          }

          float boldOffset = glyph.getAdvance(bold);
          float shadowOffset = isShadow ? 1.0F : 0.0F;

          if (strikethough) {
            effects.add(new TexturedGlyph.Effect(xOut + shadowOffset - 1.0F, y + shadowOffset + 4.5F, xOut + shadowOffset + boldOffset, y + shadowOffset + 4.5F - 1.0F, 0.0F, red, green, blue, alpha));
          }

          if (underline) {
            effects.add(new TexturedGlyph.Effect(xOut + shadowOffset - 1.0F, y + shadowOffset + 9.0F, xOut + shadowOffset + boldOffset, y + shadowOffset + 9.0F - 1.0F, 0.0F, red, green, blue, alpha));
          }

          xOut += boldOffset;
        } else {
          xOut += 1;
        }
      }
    }

    if (colorBackgroundIn != 0) {
      float backgroundAlpha = (float) (colorBackgroundIn >> 24 & 255) / 255.0F;
      float backgroundRed = (float) (colorBackgroundIn >> 16 & 255) / 255.0F;
      float backgroundGreen = (float) (colorBackgroundIn >> 8 & 255) / 255.0F;
      float backgroundBlue = (float) (colorBackgroundIn & 255) / 255.0F;

      effects.add(new TexturedGlyph.Effect(x - 1.0F, y + 9.0F, xOut + 1.0F, y - 1.0F, 0.01F, backgroundRed, backgroundGreen, backgroundBlue, backgroundAlpha));
    }

    if (!effects.isEmpty()) {
      TexturedGlyph whiteGlyph = this.font.getWhiteGlyph();
      IVertexBuilder vertexBuilder = buffer.getBuffer(whiteGlyph.getRenderType(isTransparent));

      for (TexturedGlyph.Effect effect : effects) {
        whiteGlyph.renderEffect(effect, matrix, vertexBuilder, packedLight);
      }
    }

    return xOut;
  }

  @Override
  public void onResourceManagerReload(IResourceManager resourceManager) {
    this.setBidiFlag(Minecraft.getInstance().getLanguageManager().isCurrentLanguageBidirectional());
  }

  @Override
  public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
    if (resourcePredicate.test(VanillaResourceType.LANGUAGES)) {
      this.onResourceManagerReload(resourceManager);
    }
  }
}
