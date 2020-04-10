package slimeknights.tconstruct.tables.client.inventory.module;

import com.google.common.collect.Lists;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.tables.client.inventory.ButtonItem;
import slimeknights.tconstruct.tables.client.inventory.table.PartBuilderScreen;
import slimeknights.tconstruct.tables.network.PartCrafterSelectionPacket;

import java.util.List;
import java.util.ListIterator;

public class PartCrafterButtonsScreen extends SideButtonsScreen {

  private final IInventory patternChest;

  public PartCrafterButtonsScreen(PartBuilderScreen parent, Container container, PlayerInventory playerInventory, ITextComponent title, IInventory patternChest) {
    super(parent, container, playerInventory, title, PartBuilderScreen.COLUMN_COUNT, false);

    this.patternChest = patternChest;
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    List<ItemStack> patterns = Lists.newArrayList(new ItemStack(Blocks.GLASS));//TinkerRegistry.getStencilTableCrafting());

    // remove all patterns that are not in the pattern chest
    // done this way to preserve order of buttons
    ListIterator<ItemStack> iter = patterns.listIterator();

    while (iter.hasNext()) {
      ItemStack pattern = iter.next();
      boolean found = false;

      for (int i = 0; i < patternChest.getSizeInventory(); i++) {
        if (ItemStack.areItemStacksEqual(pattern, patternChest.getStackInSlot(i))) {
          found = true;
          break;
        }
      }

      // also include the pattern slot of the gui
      if (!found) {
        found = ItemStack.areItemStacksEqual(pattern, parent.getContainer().getSlot(2).getStack());
      }

      if (!found) {
        iter.remove();
      }
    }

    this.buttons.clear();
    this.buttonCount = 0;

    for (ItemStack stencil : patterns) {
      /*
      TODO fix
      Item part = Pattern.getPartFromTag(stencil);
      if (part == null || !(part instanceof MaterialItem)) {
        continue;
      }

      ItemStack icon = ((MaterialItem) part).getItemstackWithMaterial(CustomTextureCreator.guiMaterial);*/
      ItemStack icon = new ItemStack(Blocks.DIRT);
      ButtonItem<ItemStack> button = new ButtonItem<>(-1, -1, icon, stencil, (buttonClicked) -> {
        if (buttonClicked instanceof ButtonItem) {
          ItemStack pattern = ((ButtonItem<ItemStack>) buttonClicked).data;
          TinkerNetwork.getInstance().sendToServer(new PartCrafterSelectionPacket(pattern));
        }
      });

      this.shiftButton(button, 0, 18);
      this.addSideButton(button);
    }

    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);
  }

  protected void shiftButton(ButtonItem<ItemStack> button, int xd, int yd) {
    button.setGraphics(Icons.ICON_Button.shift(xd, yd), Icons.ICON_ButtonHover.shift(xd, yd), Icons.ICON_ButtonPressed.shift(xd, yd), Icons.ICON);
  }
}
