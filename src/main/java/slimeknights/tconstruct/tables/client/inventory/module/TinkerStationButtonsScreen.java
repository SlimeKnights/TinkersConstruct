package slimeknights.tconstruct.tables.client.inventory.module;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayout;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.tables.client.inventory.SlotButtonItem;
import slimeknights.tconstruct.tables.client.inventory.table.TinkerStationScreen;

import java.util.List;

public class TinkerStationButtonsScreen extends SideButtonsScreen {

  protected final TinkerStationScreen parent;
  protected int selected = 0;
  private int style = 0;

  /** Logic to run when a button is pressed */
  private final Button.IPressable ON_BUTTON_PRESSED = self -> {
    for (Widget widget : TinkerStationButtonsScreen.this.buttons) {
      if (widget instanceof SlotButtonItem) {
        ((SlotButtonItem) widget).pressed = false;
      }
    }
    if (self instanceof SlotButtonItem) {
      SlotButtonItem slotInformationButton = (SlotButtonItem) self;
      slotInformationButton.pressed = true;
      TinkerStationButtonsScreen.this.selected = slotInformationButton.buttonId;
      TinkerStationButtonsScreen.this.parent.onToolSelection(slotInformationButton.getLayout());
    }
  };

  public static final int WOOD_STYLE = 2;
  public static final int METAL_STYLE = 1;

  public TinkerStationButtonsScreen(TinkerStationScreen parent, Container container, PlayerInventory playerInventory, ITextComponent title) {
    super(parent, container, playerInventory, title, TinkerStationScreen.COLUMN_COUNT, false);

    this.parent = parent;
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);
    this.buttonCount = 0;

    // repair button
    SlotButtonItem slotButtonItem = new SlotButtonItem(0, -1, -1, parent.getDefaultLayout(), ON_BUTTON_PRESSED);
    this.addInfoButton(slotButtonItem);
    if (0 == selected) {
      slotButtonItem.pressed = true;
    }

    // tool buttons
    int index = 1;
    for (StationSlotLayout layout : StationSlotLayoutLoader.getInstance().getSortedSlots()) {
      if (layout.getInputSlots().size() <= parent.getMaxInputs()) {
        slotButtonItem = new SlotButtonItem(index, -1, -1, layout, ON_BUTTON_PRESSED);
        this.addInfoButton(slotButtonItem);
        if (index == selected) {
          slotButtonItem.pressed = true;
        }
        index++;
      }
    }

    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);
  }

  public void addInfoButton(SlotButtonItem slotButtonItem) {
    this.shiftButton(slotButtonItem, 0, -18 * this.style);
    this.addSideButton(slotButtonItem);
  }

  public void shiftStyle(int style) {
    for (Widget widget : this.buttons) {
      if (widget instanceof SlotButtonItem) {
        this.shiftButton((SlotButtonItem) widget, 0, -18);
      }
    }

    this.style = style;
  }

  protected void shiftButton(SlotButtonItem button, int xd, int yd) {
    button.setGraphics(Icons.BUTTON.shift(xd, yd),
      Icons.BUTTON_HOVERED.shift(xd, yd),
      Icons.BUTTON_PRESSED.shift(xd, yd),
      Icons.ICONS);
  }

  public List<Widget> getButtons() {
    return this.buttons;
  }
}
