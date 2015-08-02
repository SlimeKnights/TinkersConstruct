package tconstruct.tools.client;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import tconstruct.library.Util;
import tconstruct.common.inventory.ContainerMultiModule;
import tconstruct.tools.tileentity.TilePatternChest;

@SideOnly(Side.CLIENT)
public class GuiPatternChest extends GuiTinkerStation {

  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/patternchest.png");

  public GuiPatternChest(InventoryPlayer playerInv, World world, BlockPos pos, TilePatternChest tile) {
    super(world, pos, (ContainerMultiModule)tile.createContainer(playerInv, world, pos));

    this.xSize = 194;
    this.ySize = 168;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }
}
