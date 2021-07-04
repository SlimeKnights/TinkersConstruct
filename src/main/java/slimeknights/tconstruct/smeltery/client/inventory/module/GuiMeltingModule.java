package slimeknights.tconstruct.smeltery.client.inventory.module;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.AllArgsConstructor;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.tileentity.module.MeltingModuleInventory;

import java.util.function.IntSupplier;
import java.util.function.Predicate;

@AllArgsConstructor
public class GuiMeltingModule {
  // progress bars
  private static final ScalableElementScreen PROGRESS_BAR = new ScalableElementScreen(176, 150, 3, 16, 256, 256);
  private static final ScalableElementScreen NO_HEAT_BAR = new ScalableElementScreen(179, 150, 3, 16, 256, 256);
  private static final ScalableElementScreen NO_SPACE_BAR = new ScalableElementScreen(182, 150, 3, 16, 256, 256);
  private static final ScalableElementScreen UNMELTABLE_BAR = new ScalableElementScreen(185, 150, 3, 16, 256, 256);

  // progress bar tooltips
  private static final ITextComponent TOOLTIP_NO_HEAT = new TranslationTextComponent(TConstruct.makeTranslationKey("gui", "melting.no_heat"));
  private static final ITextComponent TOOLTIP_NO_SPACE = new TranslationTextComponent(TConstruct.makeTranslationKey("gui", "melting.no_space"));
  private static final ITextComponent TOOLTIP_UNMELTABLE = new TranslationTextComponent(TConstruct.makeTranslationKey("gui", "melting.no_recipe"));

  private final ContainerScreen<?> screen;
  private final MeltingModuleInventory inventory;
  private final IntSupplier temperature;
  private final Predicate<Slot> slotPredicate;


  /**
   * Draws the heat bars on each slot
   */
  public void drawHeatBars(MatrixStack matrices) {
    int temperature = this.temperature.getAsInt();
    for (int i = 0; i < inventory.getSlots(); i++) {
      Slot slot = screen.getContainer().inventorySlots.get(i);
      if (slot.getHasStack() && slotPredicate.test(slot)) {
        // determine the bar to draw and the progress
        ScalableElementScreen bar = PROGRESS_BAR;

        int index = slot.getSlotIndex();
        int currentTemp = inventory.getCurrentTime(index);
        int requiredTime = inventory.getRequiredTime(index);

        // no required time means unmeltable
        float progress = 1f;
        if (requiredTime == 0) {
          bar = UNMELTABLE_BAR;
        }
        else if (inventory.getRequiredTemp(index) > temperature) {
          bar = NO_HEAT_BAR;
        }
        // -1 error state if no space
        else if (currentTemp < 0) {
          bar = NO_SPACE_BAR;
          progress = 1f;
        }
        // scale back normal progress if too large
        else if (currentTemp <= requiredTime) {
          progress = currentTemp / (float) requiredTime;
        }

        // draw the bar
        GuiUtil.drawProgressUp(matrices, bar, slot.xPos - 4, slot.yPos, progress);
      }
    }
  }

  /**
   * Draws the tooltip for the hovered hear slot
   * @param mouseX  Mouse X position
   * @param mouseY  Mouse Y position
   */
  public void drawHeatTooltips(MatrixStack matrices, int mouseX, int mouseY) {
    int checkX = mouseX - screen.guiLeft;
    int checkY = mouseY - screen.guiTop;
    int temperature = this.temperature.getAsInt();
    for (int i = 0; i < inventory.getSlots(); i++) {
      Slot slot = screen.getContainer().inventorySlots.get(i);
      // must have a stack
      if (slot.getHasStack() && slotPredicate.test(slot)) {
        // mouse must be within the slot
        if (GuiUtil.isHovered(checkX, checkY, slot.xPos - 5, slot.yPos - 1, PROGRESS_BAR.w + 1, PROGRESS_BAR.h + 2)) {
          int index = slot.getSlotIndex();
          ITextComponent tooltip = null;

          // NaN means 0 progress for 0 need, unmeltable
          if (inventory.getRequiredTime(index) == 0) {
            tooltip = TOOLTIP_UNMELTABLE;
          }
          // -1 error state if temperature is too low
          else if (inventory.getRequiredTemp(slot.getSlotIndex()) > temperature) {
            tooltip = TOOLTIP_NO_HEAT;
          }
          // 2x error state if no space
          else if (inventory.getCurrentTime(index) < 0) {
            tooltip = TOOLTIP_NO_SPACE;
          }

          // draw tooltip if relevant
          if (tooltip != null) {
            screen.renderTooltip(matrices, tooltip, mouseX, mouseY);
          }

          // cannot hover two slots, so done
          break;
        }
      }
    }
  }
}
