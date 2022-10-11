package slimeknights.tconstruct.plugin.jei.modifiers;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.client.modifiers.ModifierIconManager;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Special modifier ingredient renderer used for ingredients in the bookmark menu */
public enum ModifierBookmarkIngredientRenderer implements IIngredientRenderer<ModifierEntry> {
  INSTANCE;

  private static final String WRAPPER_KEY = "jei.tconstruct.modifier_ingredient";

  @Override
  public void render(PoseStack matrixStack, @Nullable ModifierEntry entry) {
    if (entry != null) {
      ModifierIconManager.renderIcon(matrixStack, entry.getModifier(), 0, 0, 100, 16);
    }
  }

  @Override
  public List<Component> getTooltip(ModifierEntry entry, TooltipFlag flag) {
    List<Component> list = new ArrayList<>();
    // not using the main method as that applies color
    list.add(new TranslatableComponent(WRAPPER_KEY, new TranslatableComponent(entry.getModifier().getTranslationKey())));
    if (flag.isAdvanced()) {
      list.add((new TextComponent(entry.getId().toString())).withStyle(ChatFormatting.DARK_GRAY));
    }
    return list;
  }
}
