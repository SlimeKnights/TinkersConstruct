package slimeknights.tconstruct.plugin.jei.modifiers;

import lombok.RequiredArgsConstructor;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import org.jetbrains.annotations.Nullable;
import java.util.List;

@RequiredArgsConstructor
public class ModifierIngredientRenderer implements IIngredientRenderer<ModifierEntry> {
  private final int width;

  @Override
  public void render(MatrixStack matrices, int x, int y, @Nullable ModifierEntry entry) {
    if (entry != null) {
      Text name = entry.getModifier().getDisplayName(entry.getLevel());
      TextRenderer fontRenderer = getFontRenderer(MinecraftClient.getInstance(), entry);
      x += (width - fontRenderer.getWidth(name)) / 2;
      fontRenderer.drawWithShadow(matrices, name, x, y + 1, -1);
    }
  }

  @Override
  public List<Text> getTooltip(ModifierEntry entry, TooltipContext iTooltipFlag) {
    return entry.getModifier().getDescriptionList();
  }
}
