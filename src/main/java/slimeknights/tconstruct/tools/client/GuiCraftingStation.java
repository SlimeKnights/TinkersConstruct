package slimeknights.tconstruct.tools.client;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.tconstruct.common.client.gui.GuiModule;
import slimeknights.tconstruct.tools.client.module.GuiSideInventory;
import slimeknights.tconstruct.tools.inventory.ContainerCraftingStation;
import slimeknights.tconstruct.tools.inventory.ContainerSideInventory;
import slimeknights.tconstruct.tools.inventory.ContainerTinkerStation;
import slimeknights.tconstruct.tools.tileentity.TileCraftingStation;

@SideOnly(Side.CLIENT)
public class GuiCraftingStation extends GuiTinkerStation {

  private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/container/crafting_table.png");
  protected final TileCraftingStation tile;

  public GuiCraftingStation(InventoryPlayer playerInv, World world, BlockPos pos, TileCraftingStation tile) {
    super(world, pos, (ContainerTinkerStation)tile.createContainer(playerInv, world, pos));

    this.tile = tile;

    if(inventorySlots instanceof ContainerCraftingStation) {
      ContainerCraftingStation container = (ContainerCraftingStation) inventorySlots;
      ContainerSideInventory chestContainer = container.getSubContainer(ContainerSideInventory.class);
      if(chestContainer != null) {
        this.addModule(new GuiSideInventory(this, chestContainer, chestContainer.getSlotCount(), chestContainer.columns));
      }
    }
  }

  public boolean isSlotInChestInventory(Slot slot) {
    GuiModule module = getModuleForSlot(slot.slotNumber);
    return module instanceof GuiSideInventory;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }
}
