package slimeknights.tconstruct.library.client.book.elements;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.element.TextElement;

public class ListingCenteredElement extends TextElement {

  private final int originalX;

  public ListingCenteredElement(int x, int y, int width, int height, TextData... text) {
    super(x, y, width, height, text);

    this.originalX = this.x;
    this.text = Lists.asList(new TextData(), this.text).toArray(new TextData[this.text.length + 2]);
    this.text[this.text.length - 1] = new TextData();

    this.text[0].color = "dark red";
    this.text[this.text.length - 1].color = "dark red";
  }

  @Override
  public void draw(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Font fontRenderer) {
    if (this.isHovered(mouseX, mouseY)) {
      this.text[0].text = "> ";
      this.text[this.text.length - 1].text = " <";

      for (int i = 1; i < this.text.length - 1; i++) {
        this.text[i].color = "dark red";
      }

      this.x = this.originalX - fontRenderer.width(this.text[0].text);
    } else {
      this.text[0].text = "";
      this.text[this.text.length - 1].text = "";

      for (int i = 1; i < this.text.length - 1; i++) {
        this.text[i].color = "black";
      }

      this.x = this.originalX;
    }
    super.draw(graphics, mouseX, mouseY, partialTicks, fontRenderer);
  }
}
