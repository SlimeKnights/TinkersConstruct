package slimeknights.tconstruct.tables.client.inventory.module;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.tables.client.inventory.ButtonItem;
import slimeknights.tconstruct.tables.client.inventory.table.PartBuilderScreen;

public class PartCrafterButtonsScreen extends SideButtonsScreen {

  private final IInventory patternChest;

  public PartCrafterButtonsScreen(PartBuilderScreen parent, Container container, PlayerInventory playerInventory, ITextComponent title, IInventory patternChest) {
    super(parent, container, playerInventory, title, PartBuilderScreen.COLUMN_COUNT, false);

    this.patternChest = patternChest;
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

  }

  protected void shiftButton(ButtonItem<ItemStack> button, int xd, int yd) {
    button.setGraphics(Icons.ICON_Button.shift(xd, yd),
      Icons.ICON_ButtonHover.shift(xd, yd),
      Icons.ICON_ButtonPressed.shift(xd, yd),
      Icons.ICON);
  }
}
