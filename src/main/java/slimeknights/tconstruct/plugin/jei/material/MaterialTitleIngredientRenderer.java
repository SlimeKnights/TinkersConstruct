package slimeknights.tconstruct.plugin.jei.material;

import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;

import java.util.List;

/** Renders the material as a title. */
public record MaterialTitleIngredientRenderer(int width, int height) implements IIngredientRenderer<MaterialVariant> {
  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public void render(GuiGraphics graphics, MaterialVariant ingredient) {
    render(graphics, ingredient, 0, 0);
  }

  @Override
  public void render(GuiGraphics graphics, MaterialVariant material, int posX, int posY) {
    Component name = MaterialTooltipCache.getColoredDisplayName(material.getVariant());
    Font font = getFontRenderer(Minecraft.getInstance(), material);
    graphics.drawString(font, name, posX, posY + 1, -1, true);
  }

  @SuppressWarnings("removal")
  @Override
  public List<Component> getTooltip(MaterialVariant material, TooltipFlag flag) {
    Component name = MaterialTooltipCache.getDisplayName(material.getVariant());
    if (flag.isAdvanced()) {
      return List.of(name ,Component.literal(material.getId().toString()).withStyle(ChatFormatting.DARK_GRAY));
    }
    return List.of(name);
  }

  @SuppressWarnings("removal")
  @Override
  public void getTooltip(ITooltipBuilder tooltip, MaterialVariant material, TooltipFlag flag) {
    tooltip.add(MaterialTooltipCache.getDisplayName(material.getVariant()));
    if (flag.isAdvanced()) {
      tooltip.add(Component.literal(material.getId().toString()).withStyle(ChatFormatting.DARK_GRAY));
    }
  }
}
