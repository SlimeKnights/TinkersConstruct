package slimeknights.tconstruct.smeltery.client.screen.module;

import lombok.AllArgsConstructor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModuleInventory;

import java.util.function.IntSupplier;
import java.util.function.Predicate;

@AllArgsConstructor
public class GuiMeltingModule {
  // progress bar tooltips
  private static final Component TOOLTIP_NO_HEAT = Component.translatable(TConstruct.makeTranslationKey("gui", "melting.no_heat"));
  private static final Component TOOLTIP_NO_SPACE = Component.translatable(TConstruct.makeTranslationKey("gui", "melting.no_space"));
  private static final Component TOOLTIP_UNMELTABLE = Component.translatable(TConstruct.makeTranslationKey("gui", "melting.no_recipe"));

  private final AbstractContainerScreen<?> screen;
  private final MeltingModuleInventory inventory;
  private final int indexOffset;
  private final IntSupplier temperature;
  private final Predicate<Slot> slotPredicate;
  private final ProgressBars progressBars;

  public GuiMeltingModule(AbstractContainerScreen<?> screen, MeltingModuleInventory inventory, int indexOffset, IntSupplier temperature, Predicate<Slot> slotPredicate, ResourceLocation background) {
    this(screen, inventory, indexOffset, temperature, slotPredicate, makeProgressBars(background));
  }

  /**
   * Draws the heat bars on each slot
   */
  public void drawHeatBars(GuiGraphics graphics) {
    int temperature = this.temperature.getAsInt();
    AbstractContainerMenu menu = screen.getMenu();
    for (int i = 0; i < inventory.getSlots(); i++) {
      Slot slot = menu.slots.get(i + indexOffset);
      if (slot.hasItem() && slotPredicate.test(slot)) {
        // determine the bar to draw and the progress
        ScalableElementScreen bar = progressBars.progress;

        int index = slot.getSlotIndex(); // note this is the inventory index, not the index in the slots list
        int currentTemp = inventory.getCurrentTime(index);
        int requiredTime = inventory.getRequiredTime(index);

        // no required time means unmeltable
        float progress = 1f;
        if (requiredTime == 0) {
          bar = progressBars.unmeltable;
        }
        else if (inventory.getRequiredTemp(index) > temperature) {
          bar = progressBars.noHeat;
        }
        // -1 error state if no space
        else if (currentTemp < 0) {
          bar = progressBars.noSpace;
        }
        // scale back normal progress if too large
        else if (currentTemp <= requiredTime) {
          progress = currentTemp / (float) requiredTime;
        }

        // draw the bar
        GuiUtil.drawProgressUp(graphics, bar, slot.x - 4, slot.y, progress);
      }
    }
  }

  /**
   * Draws the tooltip for the hovered hear slot
   * @param mouseX  Mouse X position
   * @param mouseY  Mouse Y position
   */
  public void drawHeatTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
    int checkX = mouseX - screen.leftPos;
    int checkY = mouseY - screen.topPos;
    int temperature = this.temperature.getAsInt();
    AbstractContainerMenu menu = screen.getMenu();
    for (int i = 0; i < inventory.getSlots(); i++) {
      Slot slot = menu.slots.get(i + indexOffset);
      // must have a stack
      if (slot.hasItem() && slotPredicate.test(slot)) {
        // mouse must be within the slot
        if (GuiUtil.isHovered(checkX, checkY, slot.x - 5, slot.y - 1, progressBars.width() + 1, progressBars.height() + 2)) {
          int index = slot.getSlotIndex(); // note this is the inventory index, not the index in the slots list
          Component tooltip = null;

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
            graphics.renderTooltip(screen.font, tooltip, mouseX, mouseY);
          }

          // cannot hover two slots, so done
          break;
        }
      }
    }
  }

  /** Contains all common progress bars */
  public record ProgressBars(ScalableElementScreen progress, ScalableElementScreen noHeat, ScalableElementScreen noSpace, ScalableElementScreen unmeltable) {
    public ProgressBars {
      assert sameSize(progress, noHeat);
      assert sameSize(progress, noSpace);
      assert sameSize(progress, unmeltable);
    }

    /** Checks that two elements are the same size */
    private static boolean sameSize(ElementScreen left, ElementScreen right) {
      return left.w == right.w && left.h == right.h && left.texW == right.texW && left.texH == right.texH;
    }

    /** Gets the progress bar width */
    public int width() {
      return progress.w;
    }

    /** Gets the progress bar height */
    public int height() {
      return progress.h;
    }
  }

  /** Creates all 4 progress bars at the common location */
  public static ProgressBars makeProgressBars(ResourceLocation background) {
    return new ProgressBars(
      new ScalableElementScreen(background, 176, 150, 3, 16, 256, 256),
      new ScalableElementScreen(background, 179, 150, 3, 16, 256, 256),
      new ScalableElementScreen(background, 182, 150, 3, 16, 256, 256),
      new ScalableElementScreen(background, 185, 150, 3, 16, 256, 256)
    );
  }
}
