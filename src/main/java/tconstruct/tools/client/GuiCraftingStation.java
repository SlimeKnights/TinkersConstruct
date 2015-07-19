package tconstruct.tools.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import tconstruct.tools.client.module.GuiSideInventory;
import tconstruct.tools.inventory.ContainerCraftingStation;
import tconstruct.tools.inventory.ContainerSideInventory;
import tconstruct.tools.tileentity.TileCraftingStation;

@SideOnly(Side.CLIENT)
public class GuiCraftingStation extends GuiMultiModule {

  private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/container/crafting_table.png");
  protected final TileCraftingStation tile;

  public GuiCraftingStation(InventoryPlayer playerInv, World world, BlockPos pos, TileCraftingStation tile) {
    super(tile.createContainer(playerInv, world, pos));

    this.tile = tile;

    if(inventorySlots instanceof ContainerCraftingStation) {
      ContainerCraftingStation container = (ContainerCraftingStation) inventorySlots;
      ContainerSideInventory chestContainer = container.getSubContainer(ContainerSideInventory.class);
      if(chestContainer != null) {
        this.addModule(new GuiSideInventory(chestContainer, chestContainer.getSlotCount(), chestContainer.columns));
      }
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.getTextureManager().bindTexture(BACKGROUND);
    this.drawTexturedModalRect(cornerX, cornerY, 0, 0, realWidth, realHeight);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }
}
