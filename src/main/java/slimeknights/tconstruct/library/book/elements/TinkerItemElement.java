package slimeknights.tconstruct.library.book.elements;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Same as ElementItem, but uses the vanilla fontrenderer if none other is given
 */
@Environment(EnvType.CLIENT)
public class TinkerItemElement extends ItemElement {

  public boolean noTooltip = false;

  public TinkerItemElement(ItemStack item) {
    this(0, 0, 1, item);
  }

  public TinkerItemElement(int x, int y, float scale, Item item) {
    super(x, y, scale, item);
  }

  public TinkerItemElement(int x, int y, float scale, Block item) {
    super(x, y, scale, item);
  }

  public TinkerItemElement(int x, int y, float scale, ItemStack item) {
    super(x, y, scale, item);
  }

  public TinkerItemElement(int x, int y, float scale, Collection<ItemStack> itemCycle) {
    super(x, y, scale, itemCycle);
  }

  public TinkerItemElement(int x, int y, float scale, Collection<ItemStack> itemCycle, String action) {
    super(x, y, scale, itemCycle, action);
  }

  public TinkerItemElement(int x, int y, float scale, ItemStack... itemCycle) {
    super(x, y, scale, itemCycle);
  }

  public TinkerItemElement(int x, int y, float scale, ItemStack[] itemCycle, String action) {
    super(x, y, scale, itemCycle, action);
  }

  @Override
  public void drawOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, TextRenderer textRenderer) {
    if (this.noTooltip) {
      return;
    }

    if (this.tooltip == null) {
      textRenderer = mc.textRenderer;
    }

    super.drawOverlay(matrixStack, mouseX, mouseY, partialTicks, textRenderer);
  }
}
