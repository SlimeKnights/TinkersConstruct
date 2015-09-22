package slimeknights.tconstruct.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameData;

import slimeknights.mantle.network.AbstractPacket;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.model.MaterialModelLoader;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.client.model.ModifierModelLoader;
import slimeknights.tconstruct.library.client.model.ToolModelLoader;
import slimeknights.tconstruct.library.modifiers.IModifier;

public abstract class ClientProxy extends CommonProxy {

  protected static final ToolModelLoader loader = new ToolModelLoader();
  protected static final MaterialModelLoader materialLoader = new MaterialModelLoader();
  protected static final ModifierModelLoader modifierLoader = new ModifierModelLoader();

  public static void initClient() {
    // i wonder if this is OK :D
    ModelLoaderRegistry.registerLoader(loader);
    ModelLoaderRegistry.registerLoader(materialLoader);
    ModelLoaderRegistry.registerLoader(modifierLoader);

    CustomTextureCreator creator = new CustomTextureCreator();

    MinecraftForge.EVENT_BUS.register(creator);
    ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(creator);
  }

  protected ResourceLocation registerModel(Item item, String... customVariants) {
    return registerModel(item, 0, customVariants);
  }

  /**
   * Registers a model variant for you. :3 The model-string is obtained through the game registry.
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

    ResourceLocation location = (ResourceLocation) o;

    location = new ResourceLocation(location.getResourceDomain(), location.getResourcePath());

    // and plop it in.
    // This here is needed for the model to be found ingame when the game looks for a model to render an Itemstack (Item:Meta)
    ModelLoader.setCustomModelResourceLocation(item, meta,
                                               new ModelResourceLocation(location,
                                                                         "inventory"));

    // We have to readd the default variant if we have custom variants, since it wont be added otherwise
    if(customVariants.length > 0) {
      ModelBakery.addVariantName(item, location.toString());
    }

    for(String customVariant : customVariants) {
      String custom = location.getResourceDomain() + ":" + customVariant;
      ModelBakery.addVariantName(item, custom);
    }

    return location;
  }

  protected void registerItemModel(ItemStack item, String name) {

    // tell Minecraft which textures it has to load. This is resource-domain sensitive
    if(!name.contains(":"))
      name = Util.resource(name);

    ModelBakery.addVariantName(item.getItem(), name);
    // tell the game which model to use for this item-meta combination
    ModelLoader.setCustomModelResourceLocation(item.getItem(), item.getMetadata(), new ModelResourceLocation(name, "inventory"));
  }

  /**
   * Registers a multimodel that should be loaded via our multimodel loader The model-string is obtained through the
   * game registry.
   */
  protected ResourceLocation registerToolModel(Item item) {
    ResourceLocation itemLocation = getItemLocation(item);
    if(itemLocation == null) {
      return null;
    }

    String path = "tools/" + itemLocation.getResourcePath() + ToolModelLoader.EXTENSION;

    return registerToolModel(item, new ResourceLocation(itemLocation.getResourceDomain(), path));
  }

  protected ResourceLocation registerToolModel(Item item, final ResourceLocation location) {
    if(!location.getResourcePath().endsWith(ToolModelLoader.EXTENSION)) {
      TConstruct.log.error("The material-model " + location.toString() + " does not end with '"
                           + ToolModelLoader.EXTENSION
                           + "' and will therefore not be loaded by the custom model loader!");
    }

    return registerIt(item, location);
  }

  public ResourceLocation registerMaterialItemModel(Item item) {
    ResourceLocation itemLocation = getItemLocation(item);
    if(itemLocation == null) {
      return null;
    }
    return registerMaterialModel(item, new ResourceLocation(itemLocation.getResourceDomain(),
                                                            itemLocation.getResourcePath()
                                                            + MaterialModelLoader.EXTENSION));
  }

  public ResourceLocation registerMaterialModel(Item item, final ResourceLocation location) {
    if(!location.getResourcePath().endsWith(MaterialModelLoader.EXTENSION)) {
      TConstruct.log.error("The material-model " + location.toString() + " does not end with '"
                           + MaterialModelLoader.EXTENSION
                           + "' and will therefore not be loaded by the custom model loader!");
    }

    return registerIt(item, location);
  }

  public void registerModifierModel(IModifier modifier, ResourceLocation location) {
    modifierLoader.registerModifierFile(modifier.getIdentifier(), location);
  }

  public ResourceLocation registerItemModel(Item item) {
    ResourceLocation itemLocation = getItemLocation(item);
    if(itemLocation == null) {
      return null;
    }

    return registerIt(item, itemLocation);
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

  public static ResourceLocation getItemLocation(Item item) {
    return Util.getItemLocation(item);
  }

  @Override
  public void sendPacketToServerOnly(AbstractPacket packet) {
    TinkerNetwork.sendToServer(packet);
  }
}
