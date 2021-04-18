package slimeknights.tconstruct.library.book.elements;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.element.TextElement;

@Environment(EnvType.CLIENT)
public class ListingLeftElement extends TextElement {

  public ListingLeftElement(int x, int y, int width, int height, TextData... text) {
    super(x, y, width, height, text);

    this.text = Lists.asList(new TextData(), this.text).toArray(new TextData[this.text.length + 1]);
    this.text[0].color = "dark red";
  }

  @Override
  public void draw(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, TextRenderer fontRenderer) {
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

    super.draw(matrices, mouseX, mouseY, partialTicks, fontRenderer);
  }
}
