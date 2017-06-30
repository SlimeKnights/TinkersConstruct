package slimeknights.tconstruct.library.book.elements;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.client.book.action.StringActionProcessor;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.element.SizedBookElement;

@SideOnly(Side.CLIENT)
public class ElementPageIconLink extends SizedBookElement {

  public PageData pageData;
  public SizedBookElement displayElement;
  public TextData link;
  public String action;
  public String name;

  public ElementPageIconLink(int x, int y, SizedBookElement displayElement, String name, PageData pageData) {
    this(x, y, displayElement.width, displayElement.height, displayElement, name, pageData);
  }

  public ElementPageIconLink(int x, int y, int w, int h, SizedBookElement displayElement, String name, PageData pageData) {
    super(x, y, w, h);
    this.displayElement = displayElement;
    this.pageData = pageData;

    this.action = "go-to-page-rtn:" + pageData.parent.name + "." + pageData.name;
    this.name = name;
  }

  @Override
  public void draw(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
    boolean hover = isHovered(mouseX, mouseY);
    GlStateManager.color(1F, 1F, 1F, hover ? 1F : 0.5F);
    //GlStateManager.scale(scale, scale, 1f);
    if(isHovered(mouseX, mouseY)) {
      drawRect(x, y, x + width, y + height, parent.book.appearance.hoverColor | (0x77 << 24));
    }


    displayElement.draw(mouseX, mouseY, partialTicks, fontRenderer);
    //GlStateManager.scale(1/scale, 1/scale, 1f);
  }

  @Override
  public void drawOverlay(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
    if(name != null && !name.isEmpty() && isHovered(mouseX, mouseY)) {
      drawHoveringText(ImmutableList.of(name), mouseX, mouseY, fontRenderer);
    }
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if(isHovered(mouseX, mouseY)) {
      StringActionProcessor.process(action, parent);
    }
  }
}
