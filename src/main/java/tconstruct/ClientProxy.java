package tconstruct;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameData;

import tconstruct.library.client.CustomTextureCreator;
import tconstruct.library.client.MaterialModelLoader;
import tconstruct.library.client.MultiModelLoader;

public abstract class ClientProxy extends CommonProxy {
  protected static final MultiModelLoader loader = new MultiModelLoader();
  protected static final MaterialModelLoader materialLoader = new MaterialModelLoader();

  static void initClient() {
    // i wonder if this is OK :D
    ModelLoaderRegistry.registerLoader(loader);
    ModelLoaderRegistry.registerLoader(materialLoader);
    MinecraftForge.EVENT_BUS.register(new CustomTextureCreator());
  }

  protected ResourceLocation registerModel(Item item, String... customVariants) {
    return registerModel(item, 0, customVariants);
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

    ResourceLocation location = (ResourceLocation)o;

    location = new ResourceLocation(location.getResourceDomain(), location.getResourcePath());

    // and plop it in.
    // This here is needed for the model to be found ingame when the game looks for a model to render an Itemstack (Item:Meta)
    ModelLoader.setCustomModelResourceLocation(item, meta,
                                               new ModelResourceLocation(location,
                                                                         "inventory"));

    // We have to readd the default variant if we have custom variants, since it wont be added otherwise
    if(customVariants.length > 0)
      ModelBakery.addVariantName(item, location.toString());

    for(String customVariant : customVariants) {
      String custom = location.getResourceDomain() + ":generated_" + customVariant;
      ModelBakery.addVariantName(item, custom);
    }

    return location;
  }

  /**
   * Registers a multimodel that should be loaded via our multimodel loader
   * The model-string is obtained through the game registry.
   */
  protected ResourceLocation registerToolModel(Item item) {
    ResourceLocation itemLocation = getItemLocation(item);
    if(itemLocation == null)
      return null;
    return registerToolModel(item, new ResourceLocation(itemLocation.getResourceDomain(),
                                                        itemLocation.getResourcePath() + MultiModelLoader.TOOLMODEL_EXTENSION));
  }

  protected ResourceLocation registerToolModel(Item item, final ResourceLocation location) {
    if(!location.getResourcePath().endsWith(MultiModelLoader.TOOLMODEL_EXTENSION)) {
      TConstruct.log.error("The material-model " + location.toString() + " does not end with '"
                           + MultiModelLoader.TOOLMODEL_EXTENSION
                           + "' and will therefore not be loaded by the custom model loader!");
    }

    return registerIt(item, location);
  }

  public ResourceLocation registerMaterialItemModel(Item item) {
    ResourceLocation itemLocation = getItemLocation(item);
    if(itemLocation == null)
      return null;
    return registerMaterialModel(item, new ResourceLocation(itemLocation.getResourceDomain(), itemLocation.getResourcePath() + MaterialModelLoader.MATERIALMODEL_EXTENSION));
  }

  public ResourceLocation registerMaterialModel(Item item, final ResourceLocation location) {
    if(!location.getResourcePath().endsWith(MaterialModelLoader.MATERIALMODEL_EXTENSION)) {
      TConstruct.log.error("The material-model " + location.toString() + " does not end with '"
                           + MaterialModelLoader.MATERIALMODEL_EXTENSION
                           + "' and will therefore not be loaded by the custom model loader!");
    }

    return registerIt(item, location);
  }

  private static ResourceLocation registerIt(Item item, final ResourceLocation location) {
    // plop it in.
    // This here is needed for the model to be found ingame when the game looks for a model to render an Itemstack
    // we use an ItemMeshDefinition because it allows us to do it no matter what metadata we use
    ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
      @Override
      public ModelResourceLocation getModelLocation(ItemStack stack) {
        return new ModelResourceLocation(location, "inventory");
      }
    });

    // We have to readd the default variant if we have custom variants, since it wont be added otherwise and therefore not loaded
    ModelBakery.addVariantName(item, location.toString());

    return location;
  }

  private static ResourceLocation getItemLocation(Item item) {
    // get the registered name for the object
    Object o = GameData.getItemRegistry().getNameForObject(item);

    // are you trying to add an unregistered item...?
    if(o == null) {
      TConstruct.log.error("Trying to register a model for an unregistered item: %s" + item.getUnlocalizedName());
      // bad boi
      return null;
    }

    return (ResourceLocation)o;
  }
}
