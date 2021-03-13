package slimeknights.tconstruct.tables.client.inventory.module;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.tables.client.SlotInformationLoader;
import slimeknights.tconstruct.tables.client.inventory.SlotButtonItem;
import slimeknights.tconstruct.tables.client.inventory.library.slots.SlotInformation;
import slimeknights.tconstruct.tables.client.inventory.table.TinkerStationScreen;

import java.util.List;

public class TinkerStationButtonsScreen extends SideButtonsScreen {

  protected final TinkerStationScreen parent;
  protected int selected = 0;
  private int style = 0;

  public static final int WOOD_STYLE = 2;
  public static final int METAL_STYLE = 1;

  public TinkerStationButtonsScreen(TinkerStationScreen parent, Container container, PlayerInventory playerInventory, ITextComponent title) {
    super(parent, container, playerInventory, title, TinkerStationScreen.COLUMN_COUNT, false);

    this.parent = parent;
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    int index = 0;
    this.buttonCount = 0;

    Button.IPressable onPressed = button -> {
      for (Widget widget : TinkerStationButtonsScreen.this.buttons) {
        if (widget instanceof SlotButtonItem) {
          ((SlotButtonItem) widget).pressed = false;
        }
      }

      if (button instanceof SlotButtonItem) {
        SlotButtonItem slotInformationButton = (SlotButtonItem) button;

        slotInformationButton.pressed = true;

        TinkerStationButtonsScreen.this.selected = slotInformationButton.buttonId;

        TinkerStationButtonsScreen.this.parent.onToolSelection(slotInformationButton.data);
      }
    };

    for (SlotInformation slotInformation : SlotInformationLoader.getSlotInformationList()) {
      SlotButtonItem slotButtonItem = null;
      if (slotInformation.isRepair()) {
        // there are multiple repair slots, one for each relevant size
        if (slotInformation.getPoints().size() == parent.getMaxInputs()) {
          slotButtonItem = new SlotButtonItem(index++, -1, -1, new TranslationTextComponent("gui.tconstruct.repair"), slotInformation, onPressed) {
            @Override
            protected void drawIcon(MatrixStack matrices, Minecraft minecraft) {
              minecraft.getTextureManager().bindTexture(Icons.ICONS);
              Icons.ANVIL.draw(matrices, this.x, this.y);
            }
          };
        }
      }
      // only slow tools if few enough inputs
      else if (slotInformation.getPoints().size() <= parent.getMaxInputs()) {
        slotButtonItem = new SlotButtonItem(index++, -1, -1, slotInformation.getToolForRendering(), slotInformation, onPressed);
      }

      // may skip some tools
      if (slotButtonItem != null) {
        this.addInfoButton(slotButtonItem);
        if (index - 1 == selected) {
          slotButtonItem.pressed = true;
        }
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
