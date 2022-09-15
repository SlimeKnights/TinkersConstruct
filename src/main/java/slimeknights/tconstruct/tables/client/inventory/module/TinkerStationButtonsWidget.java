package slimeknights.tconstruct.tables.client.inventory.module;

import com.google.common.collect.Lists;
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
    this(parent, createLayoutsList(parent), style);
  }

  public TinkerStationButtonsWidget(TinkerStationScreen parent, List<StationSlotLayout> layouts, int style) {
    super(parent, TinkerStationScreen.COLUMN_COUNT, rowsForCount(TinkerStationScreen.COLUMN_COUNT, layouts.size()),
      SlotButtonItem.WIDTH, SlotButtonItem.HEIGHT, false);

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
  }

  public static List<StationSlotLayout> createLayoutsList(TinkerStationScreen parent) {
    List<StationSlotLayout> layouts = Lists.newArrayList();
    // repair layout
    layouts.add(parent.getDefaultLayout());
    // tool layouts
    layouts.addAll(StationSlotLayoutLoader.getInstance().getSortedSlots().stream()
      .filter(layout -> layout.getInputSlots().size() <= parent.getMaxInputs()).toList());
    return layouts;
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
