package tconstruct.tools.client;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import tconstruct.common.client.gui.GuiElement;
import tconstruct.library.Util;
import tconstruct.common.inventory.ContainerMultiModule;
import tconstruct.tools.tileentity.TileStencilTable;

@SideOnly(Side.CLIENT)
public class GuiStencilTable extends GuiTinkerStation {
  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/stenciltable.png");

  private static final GuiElement PatternBackground = new GuiElement(18,0,18,18,256,256);

  public GuiStencilTable(InventoryPlayer playerInv, World world, BlockPos pos, TileStencilTable tile) {
    super(world, pos, (ContainerMultiModule) tile.createContainer(playerInv, world, pos));
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    drawSlotBackground(inventorySlots.getSlot(0), PatternBackground);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }
}
