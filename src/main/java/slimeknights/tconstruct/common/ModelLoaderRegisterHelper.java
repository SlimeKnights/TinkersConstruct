package slimeknights.tconstruct.common;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.tables.client.model.chest.ChestLoader;

public class ModelLoaderRegisterHelper {
  public static Runnable registerModelLoader() {
    return ModelLoaderRegisterHelper::registerModelLoader1;
  }

  @SuppressWarnings("ConstantConditions")
  public static void registerModelLoader1() {
    if (Minecraft.getInstance() != null) {
      ModelLoaderRegistry.registerLoader(Util.getResource("chest"), ChestLoader.INSTANCE);
    }
  }
}
