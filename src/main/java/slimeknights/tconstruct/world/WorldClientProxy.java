package slimeknights.tconstruct.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.world.client.SlimeColorizer;

public class WorldClientProxy extends ClientProxy {
  public static SlimeColorizer slimeColorizer = new SlimeColorizer();

  @Override
  public void preInit() {
    ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(slimeColorizer);

    super.preInit();
  }
}
