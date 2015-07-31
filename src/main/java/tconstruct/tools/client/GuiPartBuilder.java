package tconstruct.tools.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import tconstruct.library.Util;
import tconstruct.tools.inventory.ContainerMultiModule;
import tconstruct.tools.tileentity.TilePartBuilder;
import tconstruct.tools.tileentity.TilePatternChest;

@SideOnly(Side.CLIENT)
public class GuiPartBuilder extends GuiTinkerStation {
  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/partbuilder.png");

  public GuiPartBuilder(InventoryPlayer playerInv, World world, BlockPos pos, TilePartBuilder tile) {
    super(world, pos, (ContainerMultiModule)tile.createContainer(playerInv, world, pos));
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }
}
