package slimeknights.tconstruct.plugin.jei.modifiers;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.RequiredArgsConstructor;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ModifierIngredientRenderer implements IIngredientRenderer<ModifierEntry> {
  private final int width;

  @Override
  public void render(MatrixStack matrices, int x, int y, @Nullable ModifierEntry entry) {
    if (entry != null) {
      ITextComponent name = entry.getModifier().getDisplayName(entry.getLevel());
      FontRenderer fontRenderer = getFontRenderer(Minecraft.getInstance(), entry);
      x += (width - fontRenderer.getStringPropertyWidth(name)) / 2;
      fontRenderer.drawTextWithShadow(matrices, name, x, y + 1, -1);
    }
  }

  @Override
  public List<ITextComponent> getTooltip(ModifierEntry entry, ITooltipFlag flag) {
    List<ITextComponent> tooltip = entry.getModifier().getDescriptionList(entry.getLevel());
    if (flag.isAdvanced()) {
      tooltip = new ArrayList<>(tooltip);
      tooltip.add((new StringTextComponent(entry.getModifier().getId().toString())).mergeStyle(TextFormatting.DARK_GRAY));
    }
    return tooltip;
  }
}
