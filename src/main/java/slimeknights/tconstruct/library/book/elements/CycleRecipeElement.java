package slimeknights.tconstruct.library.book.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.screen.book.ArrowButton;
import slimeknights.mantle.client.screen.book.element.ArrowElement;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.tconstruct.library.book.content.ContentModifier;

import java.util.ArrayList;
import java.util.Collections;

public class CycleRecipeElement extends ArrowElement {

  public CycleRecipeElement(int x, int y, ArrowButton.ArrowType arrowType, int arrowColor, int arrowColorHover, ContentModifier modifier, BookData book, ArrayList<BookElement> list) {
    super(x, y, arrowType, arrowColor, arrowColorHover, (button) -> modifier.nextRecipe(book, list));
  }

  @Override
  public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (this.button != null && this.isHovered(mouseX, mouseY)) {
      this.playDownSound(Minecraft.getInstance().getSoundHandler());
      this.button.onPress();
    }
  }

  public void playDownSound(SoundHandler handler) {
    handler.play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
  }

  @Override
  public void drawOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
    if (this.isHovered(mouseX, mouseY)) {
      this.drawHoveringText(matrixStack, Collections.singletonList(new TranslationTextComponent("gui.tconstruct.manual.cycle.recipes")), mouseX, mouseY, fontRenderer);
    }
  }
}
