package slimeknights.tconstruct.library.book.elements;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.element.ElementText;

@SideOnly(Side.CLIENT)
public class ElementListingLeft extends ElementText {

  public ElementListingLeft(int x, int y, int width, int height, TextData... text) {
    super(x, y, width, height, text);

    this.text = Lists.asList(new TextData(), this.text).toArray(new TextData[this.text.length + 1]);
    this.text[0].color = "dark red";
  }

  @Override
  public void draw(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
    if(isHovered(mouseX, mouseY)) {
      text[0].text = " > ";
      for(int i = 1; i < text.length; i++) {
        text[i].color = "dark red";
      }
    }
    else {
      text[0].text = "- ";
      for(int i = 1; i < text.length; i++) {
        text[i].color = "black";
      }
    }
    super.draw(mouseX, mouseY, partialTicks, fontRenderer);
  }
}
