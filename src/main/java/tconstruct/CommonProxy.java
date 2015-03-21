package tconstruct;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * This class contains all the base functions for server and clientside proxy that should be called.
 * Can be used when no specific handling is needed. Can be replaced with a specific implementation at any time.
 *
 * Also doubles as documentation when what should happen.
 */
public class CommonProxy {
  public void registerModels() {}

  /**
   * Registers a model variant for you. :3
   * The model-string is obtained through the game registry.
   */
  protected void registerModel(Item item, int meta, String... customVariants) {
    // get the registered name for the object
    Object o = GameData.getItemRegistry().getNameForObject(item);

    // are you trying to add an unregistered item...?
    if(o == null) {
      TConstruct.log.error("Trying to register a model for an unregistered item: %s" + item.getUnlocalizedName());
      // bad boi
      return;
    }

    ResourceLocation location = (ResourceLocation)o;

    // and plop it in.
    // This here is needed for the model to be found ingame when the game looks for a model to render an Itemstack (Item:Meta)
    Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, new ModelResourceLocation(location, "inventory"));

    // We have to readd the default variant if we have custom variants, since it wont be added otherwise
    if(customVariants.length > 0)
      ModelBakery.addVariantName(item, location.toString());

    for(String customVariant : customVariants) {
      String custom = location.getResourceDomain() + ":" + customVariant;
      ModelBakery.addVariantName(item, custom);
    }
  }
}
