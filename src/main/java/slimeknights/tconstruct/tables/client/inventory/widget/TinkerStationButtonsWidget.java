package slimeknights.tconstruct.tables.client.inventory.widget;

import net.minecraft.client.gui.components.Button;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayout;
import slimeknights.tconstruct.tables.client.inventory.TinkerStationScreen;

import java.util.List;

public class TinkerStationButtonsWidget extends SideButtonsWidgetPaged<SlotButtonItem> {

  public static final int WOOD_STYLE = 2;
  public static final int METAL_STYLE = 1;

  public TinkerStationButtonsWidget(TinkerStationScreen parent, int leftPos, int topPos, List<StationSlotLayout> layouts, int style) {
    super(parent, leftPos, topPos, TinkerStationScreen.COLUMN_COUNT, rowsForCount(TinkerStationScreen.COLUMN_COUNT, layouts.size()),
      SlotButtonItem.WIDTH, SlotButtonItem.HEIGHT);

    // Logic to run when a button is pressed
    Button.OnPress onButtonPressed = self -> {
      for (SlotButtonItem button : TinkerStationButtonsWidget.this.buttons) {
        button.pressed = false;
      }
      if (self instanceof SlotButtonItem slotInformationButton) {
        slotInformationButton.pressed = true;
        parent.onToolSelection(slotInformationButton.getLayout());
      }
    };

    // create buttons for layouts
    for (int index = 0; index < layouts.size(); index++) {
      StationSlotLayout layout = layouts.get(index);

      SlotButtonItem slotButtonItem = new SlotButtonItem(index, -1, -1, layout, onButtonPressed);
      this.addInfoButton(slotButtonItem, style);
      if (layout == parent.getCurrentLayout()) {
        slotButtonItem.pressed = true;
      }
    }

    this.setButtonPositions();
  }

  private void addInfoButton(SlotButtonItem slotButtonItem, int style) {
    slotButtonItem.setGraphics(Icons.BUTTON.shift(0, -18 * style),
      Icons.BUTTON_HOVERED.shift(0, -18 * style),
      Icons.BUTTON_PRESSED.shift(0, -18 * style));
    this.buttons.add(slotButtonItem);
  }

  public List<SlotButtonItem> getButtons() {
    return this.buttons;
  }

  /**
   * Calculates the width of this widget given the number of columns.
   */
  public static int width(int columns) {
    return size(columns, SlotButtonItem.WIDTH);
  }
}
