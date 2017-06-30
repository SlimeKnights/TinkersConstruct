package slimeknights.tconstruct.tools.common.client.module;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.IOException;

import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.tools.common.client.GuiButtonItem;
import slimeknights.tconstruct.tools.common.client.GuiStencilTable;
import slimeknights.tconstruct.tools.common.inventory.ContainerStencilTable;
import slimeknights.tconstruct.tools.common.network.StencilTableSelectionPacket;

public class GuiButtonsStencilTable extends GuiSideButtons {

  public int selected = -1;

  public GuiButtonsStencilTable(GuiStencilTable parent, Container container, boolean right) {
    super(parent, container, GuiStencilTable.Column_Count, right);
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    int index = 0;

    buttonList.clear();
    buttonCount = 0;
    for(ItemStack stencil : TinkerRegistry.getStencilTableCrafting()) {
      Item part = Pattern.getPartFromTag(stencil);
      if(part == null || !(part instanceof MaterialItem)) {
        continue;
      }

      ItemStack icon = ((MaterialItem) part).getItemstackWithMaterial(CustomTextureCreator.guiMaterial);
      GuiButtonItem<ItemStack> button = new GuiButtonItem<>(index++, -1, -1, icon, stencil);
      shiftButton(button, 0, 18);
      addSideButton(button);

      if(index - 1 == selected) {
        button.pressed = true;
      }
    }

    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);
  }

  public void setSelectedbuttonByItem(ItemStack stack) {
    for(Object o : buttonList) {
      if(o instanceof GuiButtonItem) {
        GuiButtonItem<ItemStack> button = (GuiButtonItem<ItemStack>) o;
        button.pressed = ItemStack.areItemStacksEqual(button.data, stack);
      }
    }
  }


  @Override
  @SuppressWarnings("unchecked")
  protected void actionPerformed(GuiButton button) throws IOException {
    for(Object o : buttonList) {
      if(o instanceof GuiButtonItem) {
        ((GuiButtonItem<ItemStack>) o).pressed = false;
      }
    }

    if(button instanceof GuiButtonItem) {
      GuiButtonItem<ItemStack> buttonItem = (GuiButtonItem<ItemStack>) button;
      buttonItem.pressed = true;
      selected = button.id;

      ContainerStencilTable container = ((ContainerStencilTable) parent.inventorySlots);
      ItemStack output = buttonItem.data;
      container.setOutput(output.copy());

      TinkerNetwork.sendToServer(new StencilTableSelectionPacket(output));
    }
  }

  protected void shiftButton(GuiButtonItem<ItemStack> button, int xd, int yd) {
    button.setGraphics(Icons.ICON_Button.shift(xd, yd),
                       Icons.ICON_ButtonHover.shift(xd, yd),
                       Icons.ICON_ButtonPressed.shift(xd, yd),
                       Icons.ICON);
  }
}
