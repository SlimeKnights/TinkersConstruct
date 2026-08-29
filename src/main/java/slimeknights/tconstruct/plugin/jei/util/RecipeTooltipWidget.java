package slimeknights.tconstruct.plugin.jei.util;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.List;

/** Adds a tooltip to a rectangular area of a recipe, optionally drawing content in the same location. */
public class RecipeTooltipWidget implements IRecipeWidget {
  @Nullable
  private final IDrawable drawable;
  private final ScreenPosition position;
  private final int width;
  private final int height;
  private final List<Component> tooltip;

  public RecipeTooltipWidget(IDrawable drawable, int x, int y, Component tooltip) {
    this(drawable, x, y, List.of(tooltip));
  }

  public RecipeTooltipWidget(IDrawable drawable, int x, int y, List<Component> tooltip) {
    this(drawable, x, y, drawable.getWidth(), drawable.getHeight(), tooltip);
  }

  public RecipeTooltipWidget(IDrawable drawable, int x, int y, int width, int height, Component tooltip) {
    this(drawable, x, y, width, height, List.of(tooltip));
  }

  public RecipeTooltipWidget(int x, int y, int width, int height, Component tooltip) {
    this(x, y, width, height, List.of(tooltip));
  }

  public RecipeTooltipWidget(int x, int y, int width, int height, List<Component> tooltip) {
    this(null, x, y, width, height, tooltip);
  }

  private RecipeTooltipWidget(@Nullable IDrawable drawable, int x, int y, int width, int height, List<Component> tooltip) {
    this.drawable = drawable;
    this.position = new ScreenPosition(x, y);
    this.width = width;
    this.height = height;
    this.tooltip = List.copyOf(tooltip);
  }

  @Override
  public ScreenPosition getPosition() {
    return position;
  }

  @Override
  public void drawWidget(GuiGraphics graphics, double mouseX, double mouseY) {
    if (drawable != null) {
      drawable.draw(graphics);
    }
  }

  @Override
  public void getTooltip(ITooltipBuilder builder, double mouseX, double mouseY) {
    if (mouseX >= 0 && mouseY >= 0 && mouseX < width && mouseY < height) {
      builder.addAll(tooltip);
    }
  }
}
