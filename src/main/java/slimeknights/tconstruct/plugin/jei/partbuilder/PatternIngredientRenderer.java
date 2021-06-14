package slimeknights.tconstruct.plugin.jei.partbuilder;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.NoArgsConstructor;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class PatternIngredientRenderer implements IIngredientRenderer<Pattern> {
  public static final PatternIngredientRenderer INSTANCE = new PatternIngredientRenderer();

  @Override
  public void render(MatrixStack matrices, int x, int y, @Nullable Pattern pattern) {
    if (pattern != null) {
      TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlasTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE).getSprite(pattern.getTexture());
      Minecraft.getInstance().getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
      Screen.blit(matrices, x, y, 100, 16, 16, sprite);
    }
  }

  @Override
  public List<ITextComponent> getTooltip(Pattern pattern, ITooltipFlag iTooltipFlag) {
    return Collections.singletonList(pattern.getDisplayName());
  }
}
