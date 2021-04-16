package slimeknights.tconstruct.tools;

import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.item.ItemConvertible;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.tools.client.particles.AxeAttackParticle;
import slimeknights.tconstruct.tools.client.particles.HammerAttackParticle;

import java.util.Optional;
import java.util.function.Supplier;

public class ToolClientEvents extends ClientEventBase {

//  TODO: Models
//  @SubscribeEvent
//  static void registerModelLoaders(ModelRegistryEvent event) {
//    ModelLoaderRegistry.registerLoader(Util.getResource("tool"), ToolModelLoader.INSTANCE);
//    ModelLoaderRegistry.registerLoader(Util.getResource("material"), MaterialModel.LOADER);
//  }

  /** Color handler instance for MaterialItem */
  private static final ItemColorProvider materialColorHandler = (stack, index) -> {
    return Optional.of(IMaterialItem.getMaterialFromStack(stack))
      .filter((material) -> IMaterial.UNKNOWN != material)
      .map(IMaterial::getIdentifier)
      .flatMap(MaterialRenderInfoLoader.INSTANCE::getRenderInfo)
      .map(MaterialRenderInfo::getVertexColor)
      .orElse(-1);
  };

  /** Color handler instance for ToolCore */
  private static final ItemColorProvider toolColorHandler = (stack, index) -> {
    MaterialId material = MaterialIdNBT.from(stack).getMaterial(index);
    if (!IMaterial.UNKNOWN_ID.equals(material)) {
      return MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material)
                                              .map(MaterialRenderInfo::getVertexColor)
                                              .orElse(-1);
    }
    return -1;

  };

  /**
   * Registers an item color handler for a part item
   * @param colors  Item colors instance
   * @param item    Material item
   */
  private static void registerMaterialItemColors(ColorProviderRegistry<ItemConvertible, ItemColorProvider> colors, Supplier<? extends MaterialItem> item) {
    colors.register(materialColorHandler, item.get());
  }

  /**
   * Registers an item color handler for a part item
   * @param colors  Item colors instance
   * @param item    Material item
   */
  private static void registerToolItemColors(ColorProviderRegistry<ItemConvertible, ItemColorProvider> colors, Supplier<? extends ToolCore> item) {
    colors.register(toolColorHandler, item.get());
  }

  @Override
  public void onInitializeClient() {
    ColorProviderRegistry<ItemConvertible, ItemColorProvider> colors = ColorProviderRegistry.ITEM;

    registerToolItemColors(colors, TinkerTools.pickaxe);
    registerToolItemColors(colors, TinkerTools.sledgeHammer);
    registerToolItemColors(colors, TinkerTools.mattock);
    registerToolItemColors(colors, TinkerTools.excavator);
    registerToolItemColors(colors, TinkerTools.axe);
    registerToolItemColors(colors, TinkerTools.kama);
    registerToolItemColors(colors, TinkerTools.broadSword);

    // tint tool part textures for fallback
    registerMaterialItemColors(colors, TinkerToolParts.pickaxeHead);
    registerMaterialItemColors(colors, TinkerToolParts.hammerHead);
    registerMaterialItemColors(colors, TinkerToolParts.axeHead);
    registerMaterialItemColors(colors, TinkerToolParts.kamaHead);
    registerMaterialItemColors(colors, TinkerToolParts.swordBlade);
    registerMaterialItemColors(colors, TinkerToolParts.toolBinding);
    registerMaterialItemColors(colors, TinkerToolParts.largePlate);
    registerMaterialItemColors(colors, TinkerToolParts.toolRod);
    registerMaterialItemColors(colors, TinkerToolParts.toughToolRod);

    EntityRendererRegistry.INSTANCE.register(TinkerTools.indestructibleItem, (manager, context) -> new ItemEntityRenderer(manager, MinecraftClient.getInstance().getItemRenderer()));

    ModelLoadingRegistry.INSTANCE.registerResourceProvider(new JsonModelResourceProvider());
  }
}
