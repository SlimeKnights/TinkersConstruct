package slimeknights.tconstruct.library.book.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;
import slimeknights.mantle.client.render.RenderingHelper;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.ItemElement;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Same as ElementItem, but uses the vanilla fontrenderer if none other is given
 */
@OnlyIn(Dist.CLIENT)
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
  public void drawOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
    if (this.noTooltip) {
      return;
    }

    if (this.tooltip == null) {
      fontRenderer = mc.fontRenderer;
    }

    super.drawOverlay(matrixStack, mouseX, mouseY, partialTicks, fontRenderer);
  }
  
  //Fix odd tooltip rendering that makes the tooltip go off the screen.
  @Override
  public void drawHoveringText(MatrixStack matrixStack, List<ITextComponent> textLines, int x, int y, FontRenderer font) {
    GuiUtils.drawHoveringText(matrixStack, textLines, x, y, BookScreen.PAGE_WIDTH, BookScreen.PAGE_HEIGHT, BookScreen.PAGE_WIDTH, font);
    RenderHelper.disableStandardItemLighting();
  }
}
