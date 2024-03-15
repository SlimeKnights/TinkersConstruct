package slimeknights.tconstruct.library.client.book.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.screen.book.ArrowButton;
import slimeknights.mantle.client.screen.book.element.ArrowElement;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.tconstruct.library.client.book.content.ContentModifier;

import java.util.ArrayList;
import java.util.Collections;

public class CycleRecipeElement extends ArrowElement {

  public CycleRecipeElement(int x, int y, ArrowButton.ArrowType arrowType, int arrowColor, int arrowColorHover, ContentModifier modifier, BookData book, ArrayList<BookElement> list) {
    super(x, y, arrowType, arrowColor, arrowColorHover, (button) -> modifier.nextRecipe(book, list));
  }

  @Override
  public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (this.button != null && this.isHovered(mouseX, mouseY)) {
      this.playDownSound(Minecraft.getInstance().getSoundManager());
      this.button.onPress();
    }
  }

  public void playDownSound(SoundManager handler) {
    handler.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
  }

  @Override
  public void drawOverlay(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, Font fontRenderer) {
    if (this.isHovered(mouseX, mouseY)) {
      this.drawTooltip(matrixStack, Collections.singletonList(Component.translatable("gui.tconstruct.manual.cycle.recipes")), mouseX, mouseY, fontRenderer);
    }
  }
}
