package tconstruct.test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.registry.GameData;

import tconstruct.CommonProxy;
import tconstruct.TConstruct;
import tconstruct.library.client.MultiModelLoader;

public class TestClientProxy extends CommonProxy {
  private static final MultiModelLoader loader = new MultiModelLoader();
  private static final String GENERATED_PREFIX = "_generated.";

  @Override
  public void registerModels() {
    //Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
      //  .register(testItem, 0, new ModelResourceLocation("TConstruct:TestTool", "inventory"));
    registerModel(TinkerTest.testItem, 0);
    //registerModel(TinkerTest.testItem, 0, "pick_head", "pick_handle", "pick_binding");

    //ModelBakery.addVariantName(TinkerTest.testItem, "tconstruct:TestTool", "tconstruct:pick_head", "tconstruct:pick_handle",
      //                         "tconstruct:pick_binding");

    //ModelBakery.addVariantName(TinkerTest.testItem, "tconstruct:pick_head", "tconstruct:pick_handle",
      //                       "tconstruct:pick_binding");



    //ModelBakery.addVariantName(TinkerTest.testItem, Item.itemRegistry.getNameForObject(TinkerTest.testItem).toString(), "tconstruct:pick_head", "tconstruct:pick_handle",
      //                     "tconstruct:pick_binding");

    ModelLoaderRegistry.registerLoader(loader);
  }

  /**
   * Registers a model variant for you. :3
   * The model-string is obtained through the game registry.
   */
  protected ResourceLocation registerModel(Item item, int meta, String... customVariants) {
    // get the registered name for the object
    Object o = GameData.getItemRegistry().getNameForObject(item);

    // are you trying to add an unregistered item...?
    if(o == null) {
      TConstruct.log.error("Trying to register a model for an unregistered item: %s" + item.getUnlocalizedName());
      // bad boi
      return null;
    }

    ResourceLocation original = (ResourceLocation)o;

    ResourceLocation location = new ResourceLocation(original.getResourceDomain(), GENERATED_PREFIX + original.getResourcePath());
    ResourceLocation res = new ResourceLocation(location.getResourceDomain(), "models/item/" + location.getResourcePath());


    loader.addModel(original, res);

    // and plop it in.
    // This here is needed for the model to be found ingame when the game looks for a model to render an Itemstack (Item:Meta)
    Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta,
                                                                           new ModelResourceLocation(location,
                                                                                                     "inventory"));

    // needed so that the textures of the layers are available
    ModelBakery.addVariantName(item, original.toString());
    // We have to readd the default variant if we have custom variants, since it wont be added otherwise
    ModelBakery.addVariantName(item, location.toString());

    for(String customVariant : customVariants) {
      String custom = location.getResourceDomain() + ":" + GENERATED_PREFIX + customVariant;
      ModelBakery.addVariantName(item, custom);

      custom = location.getResourceDomain() + ":" + customVariant;
      ModelBakery.addVariantName(item, custom);
    }

    return location;
  }
}
