package slimeknights.tconstruct.tables.client.inventory.module;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayout;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.tables.client.inventory.TinkerStationScreen;
import slimeknights.tconstruct.tables.client.inventory.widget.SlotButtonItem;

import java.util.List;

public class TinkerStationButtonsScreen extends SideButtonsScreen {

  protected final TinkerStationScreen parent;
  protected int selected = 0;
  private int style = 0;

  /** Logic to run when a button is pressed */
  private final Button.OnPress ON_BUTTON_PRESSED = self -> {
    for (Widget widget : TinkerStationButtonsScreen.this.renderables) {
      if (widget instanceof SlotButtonItem) {
        ((SlotButtonItem) widget).pressed = false;
      }
    }
    if (self instanceof SlotButtonItem slotInformationButton) {
      slotInformationButton.pressed = true;
      TinkerStationButtonsScreen.this.selected = slotInformationButton.buttonId;
      TinkerStationButtonsScreen.this.parent.onToolSelection(slotInformationButton.getLayout());
    }
  };

  public static final int WOOD_STYLE = 2;
  public static final int METAL_STYLE = 1;

  public TinkerStationButtonsScreen(TinkerStationScreen parent, AbstractContainerMenu container, Inventory playerInventory, Component title) {
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
    for (Widget widget : this.renderables) {
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
    return this.renderables;
  }
}
