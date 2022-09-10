package slimeknights.tconstruct.tables.client.inventory.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.library.client.RenderUtils;

/**
 * A simple widget that draws the given element at the given position, and gives its dimensions through ExtraAreaWidget.
 * The main benefit of this widget is the implementation of ExtraAreaWidget,
 * which defines the extra area as the area covered by the drawn element.
 * As such, it is recommended to not use this widget for elements that are entirely within the space of the screen.
 * For these cases, it is recommended to instead draw the element directly.
 * @author kirderf1
 */
public class SimpleElementWidget implements Widget, ExtraAreaWidget {

  private final int x, y;
  private final ElementScreen element;
  private final ResourceLocation texture;

  public SimpleElementWidget(int x, int y, ElementScreen element, ResourceLocation texture) {
    this.x = x;
    this.y = y;
    this.element = element;
    this.texture = texture;
  }

  @Override
  public int getLeft() {
    return this.x;
  }

  @Override
  public int getTop() {
    return this.y;
  }

  @Override
  public int getWidth() {
    return this.element.w;
  }

  @Override
  public int getHeight() {
    return this.element.h;
  }

  @Override
  public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
    RenderUtils.setup(texture);
    this.element.draw(poseStack, this.x, this.y);
  }
}
