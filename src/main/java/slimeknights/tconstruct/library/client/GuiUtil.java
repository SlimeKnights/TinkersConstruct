package slimeknights.tconstruct.library.client;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GuiUtil {
  /**
   * Draws the background of a container
   * @param screen      Parent screen
   * @param background  Background location
   */
  public static void drawBackground(ContainerScreen<?> screen, ResourceLocation background) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    screen.getMinecraft().getTextureManager().bindTexture(background);
    screen.blit(screen.guiLeft, screen.guiTop, 0, 0, screen.xSize, screen.ySize);
  }

  /**
   * Draws the container names
   * @param screen  Screen name
   * @param font    Screen font TODO: can remove?
   * @param inv     Player inventory TODO: can remove?
   */
  public static void drawContainerNames(ContainerScreen<?> screen, FontRenderer font, PlayerInventory inv) {
    String name = screen.getTitle().getFormattedText();
    font.drawString(name, (screen.xSize / 2f - font.getStringWidth(name) / 2f), 6.0F, 0x404040);
    font.drawString(inv.getDisplayName().getFormattedText(), 8.0F, (screen.ySize - 96 + 2), 0x404040);
  }

  /**
   * Checks if the given area is hovered
   * @param mouseX    Mouse X position
   * @param mouseY    Mouse Y position
   * @param x         Tank X position
   * @param y         Tank Y position
   * @param width     Tank width
   * @param height    Tank height
   * @return  True if the area is hovered
   */
  public static boolean isHovered(int mouseX, int mouseY, int x, int y, int width, int height) {
    return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
  }

  /**
   * Checks if the given tank area is hovered
   * @param mouseX    Mouse X position
   * @param mouseY    Mouse Y position
   * @param amount    Current tank amount
   * @param capacity  Tank capacity
   * @param x         Tank X position
   * @param y         Tank Y position
   * @param width     Tank width
   * @param height    Tank height
   * @return  True if the tank is hovered, false otherwise
   */
  public static boolean isTankHovered(int mouseX, int mouseY, int amount, int capacity, int x, int y, int width, int height) {
    // check X position first, its easier
    if (mouseX < x || mouseX > x + width || mouseY > y + height) {
      return false;
    }
    // next, try height
    int topHeight = height - (height * amount / capacity);
    return mouseY > y + topHeight;
  }

  /**
   * Renders a fluid tank with a partial fluid level
   * @param screen    Parent screen
   * @param stack     Fluid stack
   * @param capacity  Tank capacity, determines height
   * @param x         Tank X position
   * @param y         Tank Y position
   * @param width     Tank width
   * @param height    Tank height
   * @param depth     Tank depth
   */
  public static void renderFluidTank(ContainerScreen<?> screen, FluidStack stack, int capacity, int x, int y, int width, int height, int depth) {
    if(!stack.isEmpty()) {
      int maxY = y + height;
      int fluidHeight = Math.min(height * stack.getAmount() / capacity, height);
      renderTiledFluid(screen, stack, x, maxY - fluidHeight, width, fluidHeight, depth);
    }
  }

  /**
   * Colors and renders a fluid sprite
   * @param screen  Parent screen
   * @param stack   Fluid stack
   * @param x       Fluid X
   * @param y       Fluid Y
   * @param width   Fluid width
   * @param height  Fluid height
   * @param depth   Fluid depth
   */
  public static void renderTiledFluid(ContainerScreen<?> screen, FluidStack stack, int x, int y, int width, int height, int depth) {
    if (!stack.isEmpty()) {
      TextureAtlasSprite fluidSprite = screen.getMinecraft().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(stack.getFluid().getAttributes().getStillTexture(stack));
      RenderUtil.setColorRGBA(stack.getFluid().getAttributes().getColor(stack));
      renderTiledTextureAtlas(screen, fluidSprite, x, y, width, height, depth, stack.getFluid().getAttributes().isGaseous(stack));
    }
  }

  /**
   * Renders a texture atlas sprite tiled over the given area
   * @param screen  Parent screen
   * @param sprite      Sprite to render
   * @param x           X position to render
   * @param y           Y position to render
   * @param width       Render width
   * @param height      Render height
   * @param depth       Render depth
   * @param upsideDown  If true, flips the sprite
   */
  public static void renderTiledTextureAtlas(ContainerScreen<?> screen, TextureAtlasSprite sprite, int x, int y, int width, int height, int depth, boolean upsideDown) {
    // start drawing sprites
    screen.getMinecraft().getTextureManager().bindTexture(sprite.getAtlasTexture().getTextureLocation());
    BufferBuilder builder = Tessellator.getInstance().getBuffer();
    builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

    // tile vertically
    float u1 = sprite.getMinU();
    float v1 = sprite.getMinV();
    int spriteHeight = sprite.getHeight();
    int spriteWidth = sprite.getWidth();
    int startX = x + screen.guiLeft;
    int startY = y + screen.guiTop;
    do {
      int renderHeight = Math.min(spriteHeight, height);
      height -= renderHeight;
      float v2 = sprite.getInterpolatedV((16f * renderHeight) / spriteHeight);

      // we need to draw the quads per width too
      int x2 = startX;
      int widthLeft = width;
      // tile horizontally
      do {
        int renderWidth = Math.min(spriteWidth, widthLeft);
        widthLeft -= renderWidth;

        float u2 = sprite.getInterpolatedU((16f * renderWidth) / spriteWidth);
        if(upsideDown) {
          // FIXME: I think this causes tiling errors, look into it
          buildSquare(builder, x2, x2 + renderWidth, startY, startY + renderHeight, depth, u1, u2, v2, v1);
        } else {
          buildSquare(builder, x2, x2 + renderWidth, startY, startY + renderHeight, depth, u1, u2, v1, v2);
        }
        x2 += renderWidth;
      } while(widthLeft > 0);

      startY += renderHeight;
    } while(height > 0);

    // finish drawing sprites
    builder.finishDrawing();
    RenderSystem.enableAlphaTest();
    WorldVertexBufferUploader.draw(builder);
  }

  /**
   * Draws a single sprite at the given locations
   * @param x1  X start
   * @param x2  X end
   * @param y1  Y start
   * @param y2  Y end
   * @param z   Depth
   * @param u1  Texture U start
   * @param u2  Texture U end
   * @param v1  Texture V start
   * @param v2  Texture V end
   */
  private static void blit(int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2) {
    BufferBuilder builder = Tessellator.getInstance().getBuffer();
    builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
    buildSquare(builder, x1, x2, y1, y2, z, u1, u2, v1, v2);
    builder.finishDrawing();
    RenderSystem.enableAlphaTest();
    WorldVertexBufferUploader.draw(builder);
  }

  /**
   * Adds a square of texture to a buffer builder
   * @param builder  Builder instance
   * @param x1       X start
   * @param x2       X end
   * @param y1       Y start
   * @param y2       Y end
   * @param z        Depth
   * @param u1       Texture U start
   * @param u2       Texture U end
   * @param v1       Texture V start
   * @param v2       Texture V end
   */
  private static void buildSquare(BufferBuilder builder, int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2) {
    builder.pos((double)x1, (double)y2, (double)z).tex(u1, v2).endVertex();
    builder.pos((double)x2, (double)y2, (double)z).tex(u2, v2).endVertex();
    builder.pos((double)x2, (double)y1, (double)z).tex(u2, v1).endVertex();
    builder.pos((double)x1, (double)y1, (double)z).tex(u1, v1).endVertex();
  }


  /*
   * Fluid amount displays
   */
}
