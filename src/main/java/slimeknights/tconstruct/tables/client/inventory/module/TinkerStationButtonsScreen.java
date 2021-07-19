package slimeknights.tconstruct.tables.client.inventory.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import org.apache.logging.log4j.LogManager;
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

  public TinkerStationButtonsScreen(TinkerStationScreen parent, ScreenHandler container, PlayerInventory playerInventory, Text title) {
    super(parent, container, playerInventory, title, TinkerStationScreen.COLUMN_COUNT, false);

    this.parent = parent;
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    int index = 0;
    this.buttonCount = 0;

    ButtonWidget.PressAction onPressed = button -> {
      for (AbstractButtonWidget widget : TinkerStationButtonsScreen.this.buttons) {
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
      LogManager.getLogger().info(slotInformation);
      SlotButtonItem slotButtonItem = null;
      if (slotInformation.isRepair()) {
        // there are multiple repair slots, one for each relevant size
        if (slotInformation.getPoints().size() == parent.getMaxInputs()) {
          slotButtonItem = new SlotButtonItem(index++, -1, -1, new TranslatableText("gui.tconstruct.repair"), slotInformation, onPressed) {
            @Override
            protected void drawIcon(MatrixStack matrices, MinecraftClient minecraft) {
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
    for (AbstractButtonWidget widget : this.buttons) {
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

  public List<AbstractButtonWidget> getButtons() {
    return this.buttons;
  }
}
