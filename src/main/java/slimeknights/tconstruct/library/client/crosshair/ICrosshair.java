package slimeknights.tconstruct.library.client.crosshair;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ICrosshair {

  ICrosshair DEFAULT = new ICrosshair() {
    @Override
    public void render(float charge, float width, float height, float partialTicks) {
      // do nothing
    }
  };

  void render(float charge, float width, float height, float partialTicks);
}
