package slimeknights.tconstruct.library.book.elements;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.element.ElementText;

@OnlyIn(Dist.CLIENT)
public class ElementListingLeft extends ElementText {

  public ElementListingLeft(int x, int y, int width, int height, TextData... text) {
    super(x, y, width, height, text);

    this.text = Lists.asList(new TextData(), this.text).toArray(new TextData[this.text.length + 1]);
    this.text[0].color = "dark red";
  }

  @Override
  public void draw(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
    if (this.isHovered(mouseX, mouseY)) {
      this.text[0].text = " > ";
      for (int i = 1; i < this.text.length; i++) {
        this.text[i].color = "dark red";
      }
    } else {
      this.text[0].text = "- ";
      for (int i = 1; i < this.text.length; i++) {
        this.text[i].color = "black";
      }
    }
    super.draw(mouseX, mouseY, partialTicks, fontRenderer);
  }
}
