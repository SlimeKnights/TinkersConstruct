package slimeknights.tconstruct.library.client.book.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import slimeknights.mantle.client.screen.book.element.ItemElement;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Same as ElementItem, but uses the vanilla fontrenderer if none other is given
 */
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

  public TinkerItemElement(int x, int y, float scale, ItemStack[] itemCycle, @Nullable String action) {
    super(x, y, scale, itemCycle, action);
  }

  @Override
  public void drawOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, Font fontRenderer) {
    if (this.noTooltip) {
      return;
    }

    if (this.tooltip == null) {
      fontRenderer = mc.font;
    }

    super.drawOverlay(matrixStack, mouseX, mouseY, partialTicks, fontRenderer);
  }
}
