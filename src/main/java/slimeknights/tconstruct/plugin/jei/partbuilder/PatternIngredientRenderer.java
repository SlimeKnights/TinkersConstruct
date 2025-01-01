package slimeknights.tconstruct.plugin.jei.partbuilder;

import lombok.NoArgsConstructor;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class PatternIngredientRenderer implements IIngredientRenderer<Pattern> {
  public static final PatternIngredientRenderer INSTANCE = new PatternIngredientRenderer();

  @Override
  public void render(GuiGraphics graphics, @Nullable Pattern pattern) {
    if (pattern != null) {
      GuiUtil.renderPattern(graphics, pattern, 0, 0);
    }
  }

  @Override
  public List<Component> getTooltip(Pattern pattern, TooltipFlag flag) {
    if (flag.isAdvanced()) {
      return Arrays.asList(pattern.getDisplayName(), Component.literal(pattern.toString()).withStyle(ChatFormatting.DARK_GRAY));
    } else {
      return Collections.singletonList(pattern.getDisplayName());
    }
  }
}
