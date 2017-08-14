package slimeknights.tconstruct.tools.common.client;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.client.gui.GuiModule;
import slimeknights.tconstruct.tools.common.client.module.GuiSideInventory;
import slimeknights.tconstruct.tools.common.inventory.ContainerCraftingStation;
import slimeknights.tconstruct.tools.common.inventory.ContainerSideInventory;
import slimeknights.tconstruct.tools.common.inventory.ContainerTinkerStation;
import slimeknights.tconstruct.tools.common.tileentity.TileCraftingStation;

@SideOnly(Side.CLIENT)
public class GuiCraftingStation extends GuiTinkerStation {

  private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/container/crafting_table.png");
  protected final TileCraftingStation tile;

  public GuiCraftingStation(InventoryPlayer playerInv, World world, BlockPos pos, TileCraftingStation tile) {
    super(world, pos, (ContainerTinkerStation) tile.createContainer(playerInv, world, pos));

    this.tile = tile;

    if(inventorySlots instanceof ContainerCraftingStation) {
      ContainerCraftingStation container = (ContainerCraftingStation) inventorySlots;
      ContainerSideInventory chestContainer = container.getSubContainer(ContainerSideInventory.class);
      if(chestContainer != null) {
        if(chestContainer.getTile() instanceof TileEntityChest) {
          // Fix: chests don't update their single/double chest status clientside once accessed
          ((TileEntityChest) chestContainer.getTile()).doubleChestHandler = null;
        }
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
