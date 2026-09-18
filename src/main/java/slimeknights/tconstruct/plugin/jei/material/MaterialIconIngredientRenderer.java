package slimeknights.tconstruct.plugin.jei.material;

import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.tools.TinkerToolParts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Special modifier ingredient renderer used for ingredients in the bookmark menu */
public enum MaterialIconIngredientRenderer implements IIngredientRenderer<MaterialVariant> {
  INSTANCE;

  private static final String WRAPPER_KEY = "jei.tconstruct.material_ingredient";
  private final Map<MaterialVariantId, ItemStack> stackCache = new HashMap<>();
  private final Function<MaterialVariantId, ItemStack> stackGetter = id -> TinkerToolParts.repairKit.get().withMaterialForDisplay(id);

  @Override
  public void render(GuiGraphics guiGraphics, MaterialVariant material) {
    render(guiGraphics, material, 0, 0);
  }

  @Override
  public void render(GuiGraphics graphics, MaterialVariant material, int posX, int posY) {
    RenderSystem.enableDepthTest();
    graphics.renderFakeItem(stackCache.computeIfAbsent(material.getVariant(), stackGetter), posX, posY);
    RenderSystem.disableBlend();
  }

  @SuppressWarnings("removal")
  @Override
  public List<Component> getTooltip(MaterialVariant material, TooltipFlag flag) {
    List<Component> list = new ArrayList<>();
    // not using the main method as that applies color
    list.add(Component.translatable(WRAPPER_KEY, MaterialTooltipCache.getDisplayName(material.getVariant())));
    if (flag.isAdvanced()) {
      list.add((Component.literal(material.getId().toString())).withStyle(ChatFormatting.DARK_GRAY));
    }
    return list;
  }

  @SuppressWarnings("removal")
  @Override
  public void getTooltip(ITooltipBuilder tooltip, MaterialVariant material, TooltipFlag flag) {
    tooltip.add(Component.translatable(WRAPPER_KEY, MaterialTooltipCache.getDisplayName(material.getVariant())));
    if (flag.isAdvanced()) {
      tooltip.add((Component.literal(material.getId().toString())).withStyle(ChatFormatting.DARK_GRAY));
    }
  }
}
