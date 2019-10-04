package slimeknights.tconstruct.fluids;

import net.minecraft.client.Minecraft;
import slimeknights.tconstruct.common.ClientProxy;

public class FluidsClientProxy extends ClientProxy {

  public static Minecraft minecraft = Minecraft.getInstance();

  @Override
  public void construct() {
    super.construct();
  }

  @Override
  public void init() {
  }
}
