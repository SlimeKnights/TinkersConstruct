package slimeknights.tconstruct.library.client;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GuiUtil {
  /**
   * Draws the background of a container
   * @param graphics    Graphics context
   * @param screen      Parent screen
   * @param background  Background location
   */
  public static void drawBackground(GuiGraphics graphics, AbstractContainerScreen<?> screen, ResourceLocation background) {
    graphics.blit(background, screen.getGuiLeft(), screen.getGuiTop(), 0, 0, screen.getXSize(), screen.getYSize());
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
    return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
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
  public static void renderFluidTank(GuiGraphics graphics, AbstractContainerScreen<?> screen, FluidStack stack, int capacity, int x, int y, int width, int height, int depth) {
    renderFluidTank(graphics, screen, stack, stack.getAmount(), capacity, x, y, width, height, depth);
  }

  /**
   * Renders a fluid tank with a partial fluid level and an amount override
   * @param screen    Parent screen
   * @param stack     Fluid stack
   * @param capacity  Tank capacity, determines height
   * @param x         Tank X position
   * @param y         Tank Y position
   * @param width     Tank width
   * @param height    Tank height
   * @param depth     Tank depth
   */
  public static void renderFluidTank(GuiGraphics graphics, AbstractContainerScreen<?> screen, FluidStack stack, int amount, int capacity, int x, int y, int width, int height, int depth) {
    if(!stack.isEmpty() && capacity > 0) {
      int maxY = y + height;
      int fluidHeight = Math.min(height * amount / capacity, height);
      renderTiledFluid(graphics, screen, stack, x, maxY - fluidHeight, width, fluidHeight, depth);
    }
  }

  /**
   * Colors and renders a fluid sprite
   * @param matrices    Matrix instance
   * @param screen  Parent screen
   * @param stack   Fluid stack
   * @param x       Fluid X
   * @param y       Fluid Y
   * @param width   Fluid width
   * @param height  Fluid height
   * @param depth   Fluid depth
   */
  public static void renderTiledFluid(GuiGraphics graphics, AbstractContainerScreen<?> screen, FluidStack stack, int x, int y, int width, int height, int depth) {
    if (!stack.isEmpty()) {
      IClientFluidTypeExtensions clientFluid = IClientFluidTypeExtensions.of(stack.getFluid());
      TextureAtlasSprite fluidSprite = screen.getMinecraft().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(clientFluid.getStillTexture(stack));
      RenderUtils.setColorRGBA(clientFluid.getTintColor(stack));
      renderTiledTextureAtlas(graphics, screen, fluidSprite, x, y, width, height, depth, stack.getFluid().getFluidType().isLighterThanAir());
      RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
  }

  /**
   * Renders a texture atlas sprite tiled over the given area
   * @param matrices    Matrix instance
   * @param screen      Parent screen
   * @param sprite      Sprite to render
   * @param x           X position to render
   * @param y           Y position to render
   * @param width       Render width
   * @param height      Render height
   * @param depth       Render depth
   * @param upsideDown  If true, flips the sprite
   */
  public static void renderTiledTextureAtlas(GuiGraphics graphics, AbstractContainerScreen<?> screen, TextureAtlasSprite sprite, int x, int y, int width, int height, int depth, boolean upsideDown) {
    float u1 = sprite.getU0();
    float v1 = sprite.getV0();
    int spriteWidth = sprite.contents().width();
    int spriteHeight = sprite.contents().height();
    int startX = x + screen.getGuiLeft();
    int startY = y + screen.getGuiTop();
    do {
      int renderHeight = Math.min(spriteHeight, height);
      height -= renderHeight;
      float v2 = sprite.getV((float)renderHeight / spriteHeight);

      // we need to draw the quads per width too
      int x2 = startX;
      int widthLeft = width;
      // tile horizontally
      do {
        int renderWidth = Math.min(spriteWidth, widthLeft);
        widthLeft -= renderWidth;

        float u2 = sprite.getU((float)renderWidth / spriteWidth);
        if(upsideDown) {
          // FIXME: I think this causes tiling errors, look into it
          graphics.innerBlit(sprite.atlasLocation(), x2, x2 + renderWidth, startY, startY + renderHeight, depth, u1, u2, v2, v1);
        } else {
          graphics.innerBlit(sprite.atlasLocation(), x2, x2 + renderWidth, startY, startY + renderHeight, depth, u1, u2, v1, v2);
        }
        x2 += renderWidth;
      } while(widthLeft > 0);

      startY += renderHeight;
    } while(height > 0);
  }

  /**
   * Draws an upwards progress bar. TODO: is this just {@link slimeknights.mantle.client.screen.ScalableElementScreen}?
   * @param element   Element to draw
   * @param x         X position to start
   * @param y         Y position to start
   * @param progress  Progress between 0 and 1
   */
  public static void drawProgressUp(GuiGraphics graphics, ElementScreen element, int x, int y, float progress) {
    int height;
    if (progress > 1) {
      height = element.h;
    } else if (progress < 0) {
      height = 0;
    } else {
      // add an extra 0.5 so it rounds instead of flooring
      height = (int)(progress * element.h + 0.5);
    }
    // amount to offset element by for the height
    int deltaY = element.h - height;
    graphics.blit(element.texture, x, y + deltaY, element.x, element.y + deltaY, element.w, height, element.texW, element.texH);
  }

  /**
   * Renders a highlight overlay for the given area
   * @param graphics  Graphics instance
   * @param x         Element X position
   * @param y         Element Y position
   * @param width     Element width
   * @param height    Element height
   */
  public static void renderHighlight(GuiGraphics graphics, int x, int y, int width, int height) {
      RenderSystem.disableDepthTest();
      RenderSystem.colorMask(true, true, true, false);
      graphics.fill(x, y, x + width, y + height, 100, 0x80FFFFFF);
      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.enableDepthTest();
  }

  /** Renders a pattern at the given location */
  public static void renderPattern(GuiGraphics graphics, Pattern pattern, int x, int y) {
    TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(pattern.getTexture());
    graphics.blit(x, y, 100, 16, 16, sprite);
  }
}
