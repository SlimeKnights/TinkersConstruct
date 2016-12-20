package slimeknights.tconstruct.tools.common.client;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.tools.common.client.module.GuiButtonsStencilTable;
import slimeknights.tconstruct.tools.common.client.module.GuiSideInventory;
import slimeknights.tconstruct.tools.common.inventory.ContainerPatternChest;
import slimeknights.tconstruct.tools.common.inventory.ContainerStencilTable;
import slimeknights.tconstruct.tools.common.inventory.ContainerTinkerStation;
import slimeknights.tconstruct.tools.common.network.StencilTableSelectionPacket;
import slimeknights.tconstruct.tools.common.tileentity.TileStencilTable;

@SideOnly(Side.CLIENT)
public class GuiStencilTable extends GuiTinkerStation {

  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/stenciltable.png");

  public static final int Column_Count = 4;

  protected GuiButtonsStencilTable buttons;
  protected GuiSideInventory sideInventory;
  protected ContainerPatternChest.DynamicChestInventory chestContainer;

  public GuiStencilTable(InventoryPlayer playerInv, World world, BlockPos pos, TileStencilTable tile) {
    super(world, pos, (ContainerTinkerStation) tile.createContainer(playerInv, world, pos));

    buttons = new GuiButtonsStencilTable(this, inventorySlots, false);
    this.addModule(buttons);

    if(inventorySlots instanceof ContainerStencilTable) {
      ContainerStencilTable container = (ContainerStencilTable) inventorySlots;
      chestContainer = container.getSubContainer(ContainerPatternChest.DynamicChestInventory.class);
      if(chestContainer != null) {
        sideInventory = new GuiSideInventory(this, chestContainer, chestContainer.getSizeInventory(), chestContainer.columns, true, false);
        this.addModule(sideInventory);
      }
    }
  }

  public void onSelectionPacket(StencilTableSelectionPacket packet) {
    buttons.setSelectedbuttonByItem(packet.output);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    if(sideInventory != null) {
      sideInventory.updateSlotCount(chestContainer.getSizeInventory());
    }

    drawIcon(inventorySlots.getSlot(0), Icons.ICON_Pattern);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }
}
