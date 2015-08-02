package tconstruct.tools.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import tconstruct.common.client.gui.GuiElement;
import tconstruct.library.Util;
import tconstruct.common.inventory.ContainerMultiModule;
import tconstruct.tools.client.module.GuiSideInventory;
import tconstruct.tools.inventory.ContainerCraftingStation;
import tconstruct.tools.inventory.ContainerPatternChest;
import tconstruct.tools.inventory.ContainerSideInventory;
import tconstruct.tools.inventory.ContainerStencilTable;
import tconstruct.tools.tileentity.TileStencilTable;

@SideOnly(Side.CLIENT)
public class GuiStencilTable extends GuiTinkerStation {
  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/stenciltable.png");

  private static final GuiElement PatternBackground = new GuiElement(18,0,18,18,256,256);

  public GuiStencilTable(InventoryPlayer playerInv, World world, BlockPos pos, TileStencilTable tile) {
    super(world, pos, (ContainerMultiModule) tile.createContainer(playerInv, world, pos));

    if(inventorySlots instanceof ContainerStencilTable) {
      ContainerStencilTable container = (ContainerStencilTable) inventorySlots;
      ContainerSideInventory chestContainer = container.getSubContainer(ContainerPatternChest.SideInventory.class);
      if(chestContainer != null) {
        this.addModule(new GuiSideInventory(this, chestContainer, chestContainer.getSlotCount(), chestContainer.columns, true));
      }
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    drawSlotBackground(inventorySlots.getSlot(0), PatternBackground);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }
}
