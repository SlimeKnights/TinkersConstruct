package slimeknights.tconstruct.tables.client.inventory.module;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.tables.client.inventory.ButtonItem;
import slimeknights.tconstruct.tables.client.inventory.RepairButton;
import slimeknights.tconstruct.tables.client.inventory.library.ToolBuildScreenInfo;
import slimeknights.tconstruct.tables.client.inventory.table.ToolStationScreen;
import slimeknights.tconstruct.tools.ToolRegistry;

import java.util.List;

public class ToolStationButtonsScreen extends SideButtonsScreen {

  protected final ToolStationScreen parent;
  protected int selected = 0;
  private int style = 0;

  public ToolStationButtonsScreen(ToolStationScreen parent, Container container, PlayerInventory playerInventory, ITextComponent title) {
    super(parent, container, playerInventory, title, ToolStationScreen.COLUMN_COUNT, false);

    this.parent = parent;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    int index = 0;
    buttonCount = 0;

    Button.IPressable onPressed = button -> {
      for (Object o : ToolStationButtonsScreen.this.buttons) {
        if (o instanceof ButtonItem) {
          ((ButtonItem<ToolBuildScreenInfo>) o).pressed = false;
        }
      }

      if (button instanceof ButtonItem) {
        ButtonItem<ToolBuildScreenInfo> buildScreenInfoButtonItem = (ButtonItem<ToolBuildScreenInfo>) button;

        buildScreenInfoButtonItem.pressed = true;

        ToolStationButtonsScreen.this.selected = buildScreenInfoButtonItem.buttonId;

        ToolStationButtonsScreen.this.parent.onToolSelection(buildScreenInfoButtonItem.data);
      }
    };

    ButtonItem<ToolBuildScreenInfo> repairButton = new RepairButton(index++, -1, -1, onPressed);

    this.addInfoButton(repairButton);

    for (Item item : this.parent.getBuildableItems()) {
      ToolBuildScreenInfo toolInfo = ToolRegistry.getToolBuildInfoForTool(item);
      if (toolInfo != null) {
        ButtonItem<ToolBuildScreenInfo> buttonItem = new ButtonItem<>(index++, -1, -1, toolInfo.getToolForRendering(), toolInfo, onPressed);

        this.addInfoButton(buttonItem);

        if (index - 1 == selected) {
          buttonItem.pressed = true;
        }
      }
    }

    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    this.parent.updateGUI();
  }

  public void addInfoButton(ButtonItem<ToolBuildScreenInfo> buttonItem) {
    this.shiftButton(buttonItem, 0, -18 * this.style);
    this.addSideButton(buttonItem);
  }

  @SuppressWarnings("unchecked")
  public void wood() {
    for (Object o : this.buttons) {
      this.shiftButton((ButtonItem<ToolBuildScreenInfo>) o, 0, -18);
    }

    this.style = 2;
  }

  @SuppressWarnings("unchecked")
  public void metal() {
    for (Object o : this.buttons) {
      this.shiftButton((ButtonItem<ToolBuildScreenInfo>) o, 0, -18);
    }

    this.style = 1;
  }

  @SuppressWarnings("unchecked")
  public void setSelectedButtonByTool(ItemStack tool) {
    for (Object o : this.buttons) {
      if (o instanceof ButtonItem) {
        ButtonItem<ToolBuildScreenInfo> btn = (ButtonItem<ToolBuildScreenInfo>) o;
        btn.pressed = ItemStack.areItemStacksEqual(btn.data.tool, tool);
      }
    }
  }

  protected void shiftButton(ButtonItem<ToolBuildScreenInfo> button, int xd, int yd) {
    button.setGraphics(Icons.BUTTON.shift(xd, yd),
      Icons.BUTTON_HOVERED.shift(xd, yd),
      Icons.BUTTON_PRESSED.shift(xd, yd),
      Icons.ICONS);
  }

  public List<Widget> getButtons() {
    return this.buttons;
  }
}
