package slimeknights.tconstruct.tables.client.inventory.module;

import net.minecraft.client.gui.components.Button;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayout;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.tables.client.inventory.TinkerStationScreen;
import slimeknights.tconstruct.tables.client.inventory.widget.SlotButtonItem;

import java.util.List;

public class TinkerStationButtonsWidget extends SideButtonsWidget<SlotButtonItem> {

  public static final int WOOD_STYLE = 2;
  public static final int METAL_STYLE = 1;

  public TinkerStationButtonsWidget(TinkerStationScreen parent, int style) {
    super(parent, TinkerStationScreen.COLUMN_COUNT, false);

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

    // repair button
    SlotButtonItem slotButtonItem = new SlotButtonItem(0, -1, -1, parent.getDefaultLayout(), onButtonPressed);
    this.addInfoButton(slotButtonItem, style);
    if (parent.getDefaultLayout() == parent.getCurrentLayout()) {
      slotButtonItem.pressed = true;
    }

    // tool buttons
    int index = 1;
    for (StationSlotLayout layout : StationSlotLayoutLoader.getInstance().getSortedSlots()) {
      if (layout.getInputSlots().size() <= parent.getMaxInputs()) {
        slotButtonItem = new SlotButtonItem(index, -1, -1, layout, onButtonPressed);
        this.addInfoButton(slotButtonItem, style);
        if (layout == parent.getCurrentLayout()) {
          slotButtonItem.pressed = true;
        }
        index++;
      }
    }
  }

  private void addInfoButton(SlotButtonItem slotButtonItem, int style) {
    slotButtonItem.setGraphics(Icons.BUTTON.shift(0, -18 * style),
      Icons.BUTTON_HOVERED.shift(0, -18 * style),
      Icons.BUTTON_PRESSED.shift(0, -18 * style),
      Icons.ICONS);
    this.buttons.add(slotButtonItem);
  }

  public List<SlotButtonItem> getButtons() {
    return this.buttons;
  }
}
