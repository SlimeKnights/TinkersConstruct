package slimeknights.tconstruct.tools;

import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.item.ItemConvertible;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import slimeknights.mantle.client.model.JsonModelResourceProvider;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.model.tools.MaterialModel;
import slimeknights.tconstruct.library.client.model.tools.ToolModel;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ToolClientEvents extends ClientEventBase {

  /** Color handler instance for MaterialItem */
  private static final ItemColorProvider materialColorHandler = (stack, index) -> Optional.of(IMaterialItem.getMaterialIdFromStack(stack))
    .filter(material -> !material.equals(IMaterial.UNKNOWN_ID))
    .flatMap(MaterialRenderInfoLoader.INSTANCE::getRenderInfo)
    .map(MaterialRenderInfo::getVertexColor)
    .orElse(-1);

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
    //registerMaterialItemColors(colors, TinkerToolParts.toolRod);
    //registerMaterialItemColors(colors, TinkerToolParts.toughToolRod);

    EntityRendererRegistry.INSTANCE.register(TinkerTools.indestructibleItem, (manager, context) -> new ItemEntityRenderer(manager, MinecraftClient.getInstance().getItemRenderer()));

    ModelLoadingRegistry.INSTANCE.registerResourceProvider((manager) -> ToolModel.LOADER);
    ModelLoadingRegistry.INSTANCE.registerResourceProvider((manager) -> MaterialModel.LOADER);
  }
}
