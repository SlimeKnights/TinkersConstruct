package slimeknights.tconstruct.plugin.jei.modifiers;

import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.client.RenderUtils;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Special modifier ingredient renderer used for ingredients in the bookmark menu */
public enum ModifierBookmarkIngredientRenderer implements IIngredientRenderer<ModifierEntry> {
  INSTANCE;

  private static final String WRAPPER_KEY = "jei.tconstruct.modifier_ingredient";

  @Override
  public void render(MatrixStack matrixStack, int x, int y, @Nullable ModifierEntry entry) {
    if (entry != null) {
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.getTextureManager().bindTexture(ModifierRecipeCategory.BACKGROUND_LOC);
      Screen.blit(matrixStack, x, y, 224f, 0f, 16, 16, 256, 256);
      RenderUtils.setColorRGBA(0xFF000000 | entry.getModifier().getColor());
      Screen.blit(matrixStack, x, y, 240f, 0f, 16, 16, 256, 256);
      RenderUtils.setColorRGBA(-1);
    }
  }

  @Override
  public List<ITextComponent> getTooltip(ModifierEntry entry, ITooltipFlag flag) {
    List<ITextComponent> list = new ArrayList<>();
    list.add(new TranslationTextComponent(WRAPPER_KEY, new TranslationTextComponent(entry.getModifier().getTranslationKey())));
    if (flag.isAdvanced()) {
      list.add((new StringTextComponent(entry.getModifier().getId().toString())).mergeStyle(TextFormatting.DARK_GRAY));
    }
    return list;
  }
}
