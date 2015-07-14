package tconstruct.tools.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import tconstruct.library.Util;
import tconstruct.tools.tileentity.TilePatternChest;

public class GuiPatternChest extends GuiContainer {
  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/patternchest.png");

  public GuiPatternChest(InventoryPlayer playerInv, World world, BlockPos pos, TilePatternChest tile) {
    super(tile.createContainer(playerInv, world, pos));

    this.xSize = 194;
    this.ySize = 168;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.getTextureManager().bindTexture(BACKGROUND);
    int cornerX = (this.width - this.xSize) / 2;
    int cornerY = (this.height - this.ySize) / 2;
    this.drawTexturedModalRect(cornerX, cornerY, 0, 0, this.xSize, this.ySize);
  }
}
