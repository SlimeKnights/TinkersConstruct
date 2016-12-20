package slimeknights.tconstruct.library.book.elements;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.element.ElementText;

@SideOnly(Side.CLIENT)
public class ElementListingCentered extends ElementText {

  private final int originalX;

  public ElementListingCentered(int x, int y, int width, int height, TextData... text) {
    super(x, y, width, height, text);

    this.originalX = this.x;
    this.text = Lists.asList(new TextData(), this.text).toArray(new TextData[this.text.length + 2]);
    this.text[this.text.length - 1] = new TextData();

    this.text[0].color = "dark red";
    this.text[this.text.length - 1].color = "dark red";
  }

  @Override
  public void draw(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
    if(isHovered(mouseX, mouseY)) {
      text[0].text = "> ";
      text[text.length - 1].text = " <";
      for(int i = 1; i < text.length - 1; i++) {
        text[i].color = "dark red";
      }

      x = originalX - fontRenderer.getStringWidth(text[0].text);
    }
    else {
      text[0].text = "";
      text[text.length - 1].text = "";
      for(int i = 1; i < text.length - 1; i++) {
        text[i].color = "black";
      }
      x = originalX;
    }
    super.draw(mouseX, mouseY, partialTicks, fontRenderer);
  }
}
