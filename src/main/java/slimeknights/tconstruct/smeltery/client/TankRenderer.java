package slimeknights.tconstruct.smeltery.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.smeltery.tileentity.TileTank;

public class TankRenderer extends TileEntitySpecialRenderer {

  protected static Minecraft mc = Minecraft.getMinecraft();

  @Override
  public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
    renderTileEntityAt((TileTank)te, x, y, z, partialTicks, destroyStage);
  }

  public void renderTileEntityAt(TileTank te, double x, double y, double z, float partialTicks, int destroyStage) {
    if(te.containsFluid()) {
      FluidTankInfo info = te.getTankInfo(null)[0];
      FluidStack liquid = info.fluid;

      float height = (float)liquid.amount / (float)info.capacity;
      height += te.renderOffset;

      float d = 0.001f;
      RenderUtil.renderFluidCuboid(liquid, te.getPos(), x,y,z, d, d, d, 1d-d, height-d, 1d-d);
    }
  }
}
