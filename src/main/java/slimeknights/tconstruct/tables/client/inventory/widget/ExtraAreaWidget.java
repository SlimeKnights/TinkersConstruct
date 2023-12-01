package slimeknights.tconstruct.tables.client.inventory.widget;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.Rect2i;

/**
 * An interface for widgets that might be placed outside the space of a screen,
 * and need to be accounted for when dropping items or when telling jei of extra gui areas.
 * This interface assumes that the widget occupies a rectangular shape.
 * This class could potentially be relocated to mantle.
 * @author kirderf1
 */
public interface ExtraAreaWidget {
  int getLeft();
  int getTop();
  int getWidth();
  int getHeight();

  default int getRight() {
    return this.getLeft() + this.getWidth();
  }

  default int getBottom() {
    return this.getTop() + this.getHeight();
  }

  default Rect2i getArea() {
    return new Rect2i(this.getLeft(), this.getTop(), this.getWidth(), this.getHeight());
  }

  default boolean isInArea(double x, double y) {
    return this.getLeft() <= x && x < this.getRight()
      && this.getTop() <= y && y < this.getBottom();
  }

  /**
   * A helper interface that combines ExtraAreaWidget and GuiEventListener
   * and implements GuiEventListener.isMouseOver() using ExtraAreaWidget.isInArea().
   */
  interface EventListener extends ExtraAreaWidget, GuiEventListener {
    @Override
    default boolean isMouseOver(double mouseX, double mouseY) {
      return this.isInArea(mouseX, mouseY);
    }
  }
}
