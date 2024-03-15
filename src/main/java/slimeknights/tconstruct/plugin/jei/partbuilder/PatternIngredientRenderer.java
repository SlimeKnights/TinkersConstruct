package slimeknights.tconstruct.plugin.jei.partbuilder;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.NoArgsConstructor;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.client.RenderUtils;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class PatternIngredientRenderer implements IIngredientRenderer<Pattern> {
  public static final PatternIngredientRenderer INSTANCE = new PatternIngredientRenderer();

  @Override
  public void render(PoseStack matrices, @Nullable Pattern pattern) {
    if (pattern != null) {
      TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(pattern.getTexture());
      RenderUtils.setup(InventoryMenu.BLOCK_ATLAS);
      Screen.blit(matrices, 0, 0, 100, 16, 16, sprite);
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
