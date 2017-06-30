package slimeknights.tconstruct.common;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.model.MaterialModelLoader;
import slimeknights.tconstruct.library.client.model.ToolModelLoader;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolCore;

/** This class basically is what you want when you got models to register */
@SideOnly(Side.CLIENT)
public final class ModelRegisterUtil {

  public static final String VARIANT_INVENTORY = "inventory";

  // Regular ITEM MODELS //

  /** Registers the item-meta combo in the itemstack with the given location for the inventory-variant */
  public static void registerItemModel(ItemStack itemStack, ResourceLocation name) {
    if(!itemStack.isEmpty() && name != null) {
      // tell the loader to load the model
      ModelLoader.registerItemVariants(itemStack.getItem(), name);
      // tell the game which model to use for this item-meta combination
      ModelLoader.setCustomModelResourceLocation(itemStack.getItem(), itemStack.getMetadata(), new ModelResourceLocation(name, VARIANT_INVENTORY));
    }
  }

  /** Registers the given item with its registry name for all metadata values for the inventory variant */
  public static ResourceLocation registerItemModel(Item item) {
    ResourceLocation itemLocation = null;
    if(item != null) {
      itemLocation = item.getRegistryName();
    }
    if(itemLocation != null) {
      itemLocation = registerIt(item, itemLocation);
    }

    return itemLocation;
  }

  /** Registers the item of the given block with its registry name for all metadata values for the inventory variant */
  public static ResourceLocation registerItemModel(Block block) {
    return registerItemModel(Item.getItemFromBlock(block));
  }

  /** Registers an itemblockmeta model for the blocks inventory variant. */
  public static void registerItemBlockMeta(Block block) {
    if(block != null) {
      Item item = Item.getItemFromBlock(block);
      if(item instanceof ItemBlockMeta) {
        ((ItemBlockMeta) item).registerItemModels();
      }
      else {
        TConstruct.log.error("Trying to register an ItemBlockMeta model for a non itemblockmeta block: " + block.getRegistryName());
      }
    }
  }

  /** Registers the item with the given metadata and its registry name for the inventory variant */
  public static void registerItemModel(Item item, int meta) {
    registerItemModel(item, meta, VARIANT_INVENTORY);
  }

  /** Registers the given item with the given meta and its registry name for the given variant */
  public static void registerItemModel(Item item, int meta, String variant) {
    if(item != null) {
      registerItemModel(item, meta, item.getRegistryName(), variant);
    }
  }

  /** Registers the given item/meta combination with the model at the given location, and the given variant */
  public static void registerItemModel(Item item, int meta, ResourceLocation location, String variant) {
    if(item != null && !StringUtils.isNullOrEmpty(variant)) {
      //ModelLoader.registerItemVariants(item, location);
      ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), variant));
    }
  }

  // TOOL MODELS //

  /**
   * Registers a multimodel that should be loaded via our multimodel loader.
   * The model-string is obtained through the items registry name.
   */
  public static ResourceLocation registerToolModel(ToolCore tool) {
    if(tool == null || tool.getRegistryName() == null) {
      return null;
    }
    ResourceLocation itemLocation = tool.getRegistryName();
    String path = "tools/" + itemLocation.getResourcePath() + ToolModelLoader.EXTENSION;

    ResourceLocation location = new ResourceLocation(itemLocation.getResourceDomain(), path);
    ToolModelLoader.addPartMapping(location, tool);

    return registerToolModel(tool, location);
  }

  /** Manual registration of a tool model. You probably shouldn't be using this. */
  public static ResourceLocation registerToolModel(Item item, final ResourceLocation location) {
    if(!location.getResourcePath().endsWith(ToolModelLoader.EXTENSION)) {
      TConstruct.log.error("The material-model " + location.toString() + " does not end with '"
                           + ToolModelLoader.EXTENSION
                           + "' and will therefore not be loaded by the custom model loader!");
    }

    return registerIt(item, location);
  }

  // TOOL PART MODELS //

  /** Register a toolpart to be loaded via the material model loader by its registry name */
  public static <T extends Item & IToolPart> ResourceLocation registerPartModel(T item) {
    if(item == null || item.getRegistryName() == null) {
      return null;
    }
    ResourceLocation itemLocation = item.getRegistryName();

    String path = "parts/" + itemLocation.getResourcePath() + MaterialModelLoader.EXTENSION;
    ResourceLocation location = new ResourceLocation(itemLocation.getResourceDomain(), path);

    MaterialModelLoader.addPartMapping(location, item);

    return registerMaterialModel(item, location);
  }

  // GENERAL MATERIAL MODELS //

  /** Registers a material model to be loaded via the material modelloader */
  public static ResourceLocation registerMaterialItemModel(Item item) {
    if(item == null || item.getRegistryName() == null) {
      return null;
    }
    ResourceLocation itemLocation = item.getRegistryName();
    itemLocation = new ResourceLocation(itemLocation.getResourceDomain(),
                                        itemLocation.getResourcePath() + MaterialModelLoader.EXTENSION);

    return registerMaterialModel(item, itemLocation);
  }

  /** Manual registration of a material model. You probably shouldn't be using this. */
  static ResourceLocation registerMaterialModel(Item item, final ResourceLocation location) {
    if(!location.getResourcePath().endsWith(MaterialModelLoader.EXTENSION)) {
      TConstruct.log.error("The material-model " + location.toString() + " does not end with '"
                           + MaterialModelLoader.EXTENSION
                           + "' and will therefore not be loaded by the custom model loader!");
    }

    return registerIt(item, location);
  }

  // MODIFIER MODELS //

  /** Registers a modifier to be loaded via the modifier model loader */
  public static void registerModifierModel(IModifier modifier, ResourceLocation location) {
    ClientProxy.modifierLoader.registerModifierFile(modifier.getIdentifier(), location);
  }

  // INTERNAL //

  private static ResourceLocation registerIt(Item item, final ResourceLocation location) {
    // plop it in.
    // This here is needed for the model to be found ingame when the game looks for a model to render an Itemstack
    // we use an ItemMeshDefinition because it allows us to do it no matter what metadata we use
    ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
      @Nonnull
      @Override
      public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
        return new ModelResourceLocation(location, VARIANT_INVENTORY);
      }
    });

    // We have to readd the default variant if we have custom variants, since it wont be added otherwise and therefore not loaded
    ModelLoader.registerItemVariants(item, location);

    return location;
  }

  private ModelRegisterUtil() {
  }
}
