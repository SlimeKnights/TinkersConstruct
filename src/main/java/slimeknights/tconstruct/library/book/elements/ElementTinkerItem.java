package slimeknights.tconstruct.library.book.elements;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;

import slimeknights.mantle.client.gui.book.element.ElementItem;

/** Same as ElementItem, but uses the vanilla fontrenderer if none other is given */
@SideOnly(Side.CLIENT)
public class ElementTinkerItem extends ElementItem {

  public boolean noTooltip = false;

  public ElementTinkerItem(ItemStack item) {
    this(0, 0, 1, item);
  }

  public ElementTinkerItem(int x, int y, float scale, Item item) {
    super(x, y, scale, item);
  }

  public ElementTinkerItem(int x, int y, float scale, Block item) {
    super(x, y, scale, item);
  }

  public ElementTinkerItem(int x, int y, float scale, ItemStack item) {
    super(x, y, scale, item);
  }

  public ElementTinkerItem(int x, int y, float scale, Collection<ItemStack> itemCycle) {
    super(x, y, scale, itemCycle);
  }

  public ElementTinkerItem(int x, int y, float scale, Collection<ItemStack> itemCycle, String action) {
    super(x, y, scale, itemCycle, action);
  }

  public ElementTinkerItem(int x, int y, float scale, ItemStack... itemCycle) {
    super(x, y, scale, itemCycle);
  }

  public ElementTinkerItem(int x, int y, float scale, ItemStack[] itemCycle, String action) {
    super(x, y, scale, itemCycle, action);
  }

  @Override
  public void drawOverlay(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
    if(noTooltip) {
      return;
    }
    if(tooltip == null) {
      fontRenderer = mc.fontRenderer;
    }
    super.drawOverlay(mouseX, mouseY, partialTicks, fontRenderer);
  }
}
