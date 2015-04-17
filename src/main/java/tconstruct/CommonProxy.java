package tconstruct;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * This class contains all the base functions for server and clientside proxy that should be called.
 * Can be used when no specific handling is needed. Can be replaced with a specific implementation at any time.
 *
 * Also doubles as documentation when what should happen.
 */
public class CommonProxy {
  public void registerModels() {
    if(!Loader.instance().hasReachedState(LoaderState.INITIALIZATION))
      TConstruct.log.error("Proxy.registerModels has to be called AFTER preInit. Best call it during Init.");
  }
}
