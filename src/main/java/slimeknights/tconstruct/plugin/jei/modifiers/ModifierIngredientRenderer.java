package slimeknights.tconstruct.plugin.jei.modifiers;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public record ModifierIngredientRenderer(int width, int height) implements IIngredientRenderer<ModifierEntry> {
  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public void render(PoseStack matrices, @Nullable ModifierEntry entry) {
    if (entry != null) {
      Component name = entry.getModifier().getDisplayName(entry.getLevel());
      Font fontRenderer = getFontRenderer(Minecraft.getInstance(), entry);
      int x = (width - fontRenderer.width(name)) / 2;
      fontRenderer.drawShadow(matrices, name, x, 1, -1);
    }
  }

  @Override
  public List<Component> getTooltip(ModifierEntry entry, TooltipFlag flag) {
    List<Component> tooltip = entry.getModifier().getDescriptionList(entry.getLevel());
    if (flag.isAdvanced()) {
      tooltip = new ArrayList<>(tooltip);
      tooltip.add((Component.literal(entry.getId().toString())).withStyle(ChatFormatting.DARK_GRAY));
    }
    return tooltip;
  }
}
