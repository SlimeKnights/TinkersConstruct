package slimeknights.tconstruct.tools.common.client.module;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.tools.common.client.GuiButtonItem;
import slimeknights.tconstruct.tools.common.client.GuiPartBuilder;
import slimeknights.tconstruct.tools.common.network.PartCrafterSelectionPacket;

public class GuiButtonsPartCrafter extends GuiSideButtons {

  private final IInventory patternChest;

  public GuiButtonsPartCrafter(GuiPartBuilder parent, Container container, IInventory patternChest) {
    super(parent, container, GuiPartBuilder.Column_Count, false);

    this.patternChest = patternChest;
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    int index = 0;

    List<ItemStack> patterns = Lists.newArrayList(TinkerRegistry.getStencilTableCrafting());

    // remove all patterns that are not in the pattern chest
    // done this way to preserve order of buttons
    ListIterator<ItemStack> iter = patterns.listIterator();
    while(iter.hasNext()) {
      ItemStack pattern = iter.next();
      boolean found = false;
      for(int i = 0; i < patternChest.getSizeInventory(); i++) {
        if(ItemStack.areItemStacksEqual(pattern, patternChest.getStackInSlot(i))) {
          found = true;
          break;
        }
      }

      // also include the pattern slot of the gui
      if(!found) {
        found = ItemStack.areItemStacksEqual(pattern, parent.inventorySlots.getSlot(2).getStack());
      }

      if(!found) {
        iter.remove();
      }
    }

    buttonList.clear();
    buttonCount = 0;
    for(ItemStack stencil : patterns) {
      Item part = Pattern.getPartFromTag(stencil);
      if(part == null || !(part instanceof MaterialItem)) {
        continue;
      }

      ItemStack icon = ((MaterialItem) part).getItemstackWithMaterial(CustomTextureCreator.guiMaterial);
      GuiButtonItem<ItemStack> button = new GuiButtonItem<>(index++, -1, -1, icon, stencil);
      shiftButton(button, 0, 18);
      addSideButton(button);
    }

    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);
  }

  @Override
  protected void actionPerformed(GuiButton button) throws IOException {
    if(button instanceof GuiButtonItem) {
      ItemStack pattern = ((GuiButtonItem<ItemStack>) button).data;

      TinkerNetwork.sendToServer(new PartCrafterSelectionPacket(pattern));
    }
  }

  protected void shiftButton(GuiButtonItem<ItemStack> button, int xd, int yd) {
    button.setGraphics(Icons.ICON_Button.shift(xd, yd),
                       Icons.ICON_ButtonHover.shift(xd, yd),
                       Icons.ICON_ButtonPressed.shift(xd, yd),
                       Icons.ICON);
  }
}
