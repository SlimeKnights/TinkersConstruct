package slimeknights.tconstruct.library.book;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.FontRenderer;

import java.util.Arrays;
import java.util.Collection;

import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.element.ElementText;

public class ElementTextBoldHover extends ElementText {

  public ElementTextBoldHover(int x, int y, int width, int height, String text) {
    super(x, y, width, height, text);
  }

  public ElementTextBoldHover(int x, int y, int width, int height, Collection<TextData> text) {
    super(x, y, width, height, text);
  }

  public ElementTextBoldHover(int x, int y, int width, int height, TextData... text) {
    super(x, y, width, height, text);

    this.text = Lists.asList(new TextData(), this.text).toArray(new TextData[this.text.length+1]);
  }

  @Override
  public void draw(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
    if(isHovered(mouseX, mouseY))
    {
      text[0].text = "> ";
      for(TextData data : text) {
        data.bold = true;
      }
    }
    else {
      text[0].text = "- ";
      for(TextData data : text) {
        data.bold = false;
      }
    }
    super.draw(mouseX, mouseY, partialTicks, fontRenderer);
  }
}
