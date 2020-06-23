package slimeknights.tconstruct.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.materials.IMaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.model.MaterialModel;
import slimeknights.tconstruct.library.client.model.ToolModelLoader;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tinkering.ToolPart;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = TConstruct.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class ToolClientEvents extends ClientEventBase {

  @SubscribeEvent
  static void registerModelLoaders(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(Util.getResource("tool"), ToolModelLoader.INSTANCE);
    ModelLoaderRegistry.registerLoader(Util.getResource("material"), MaterialModel.Loader.INSTANCE);
  }

  @SubscribeEvent
  static void clientSetupEvent(FMLClientSetupEvent event) {
    RenderingRegistry.registerEntityRenderingHandler(TinkerTools.indestructibleItem.get(), manager -> new ItemRenderer(manager, Minecraft.getInstance().getItemRenderer()));
  }

  @SubscribeEvent
  static void modelRegistry(ModelRegistryEvent event) {
    registerGuiPart(TinkerToolParts.pickaxeHead);
    registerGuiPart(TinkerToolParts.hammerHead);
    registerGuiPart(TinkerToolParts.shovelHead);
    registerGuiPart(TinkerToolParts.swordBlade);
    registerGuiPart(TinkerToolParts.smallBinding);
    registerGuiPart(TinkerToolParts.wideGuard);
    registerGuiPart(TinkerToolParts.largePlate);
    registerGuiPart(TinkerToolParts.toolRod);
    registerGuiPart(TinkerToolParts.toughToolRod);
  }

  /**
   * Registers a part model for the GUI
   * @param itemSup  Material item model supplier
   */
  private static void registerGuiPart(Supplier<ToolPart> itemSup) {
    ResourceLocation location = itemSup.get().getRegistryName();
    ModelLoader.addSpecialModel(new ResourceLocation(location.getNamespace(), "gui/part/" + location.getPath()));
  }

  @SubscribeEvent
  static void itemColors(ColorHandlerEvent.Item event) {
    final ItemColors colors = event.getItemColors();

    // tint tool textures for fallback
    registerToolItemColors(colors, TinkerTools.pickaxe);

    // tint tool part textures for fallback
    registerMaterialItemColors(colors, TinkerToolParts.pickaxeHead);
    registerMaterialItemColors(colors, TinkerToolParts.smallBinding);
    registerMaterialItemColors(colors, TinkerToolParts.toolRod);
  }

  /** Color handler instance for MaterialItem */
  private static final IItemColor materialColorHandler = (stack, index) -> {
    return Optional.of(MaterialItem.getMaterialFromStack(stack))
      .filter((material) -> IMaterial.UNKNOWN != material)
      .map(IMaterial::getIdentifier)
      .flatMap(MaterialRenderInfoLoader.INSTANCE::getRenderInfo)
      .map(IMaterialRenderInfo::getVertexColor)
      .orElse(-1);
  };

  /** Color handler instance for ToolCore */
  private static final IItemColor toolColorHandler = (stack, index) -> {
    return Optional.ofNullable(stack.getTag())
      .map(ToolData::readFromNBT)
      .map(ToolData::getMaterials)
      .filter((mats) -> index < mats.size())
      .map((mats) -> mats.get(index))
      .map(IMaterial::getIdentifier)
      .flatMap(MaterialRenderInfoLoader.INSTANCE::getRenderInfo)
      .map(IMaterialRenderInfo::getVertexColor)
      .orElse(-1);
  };

  /**
   * Registers an item color handler for a part item
   * @param colors  Item colors instance
   * @param item    Material item
   */
  private static void registerMaterialItemColors(ItemColors colors, Supplier<? extends MaterialItem> item) {
    colors.register(materialColorHandler, item.get());
  }

  /**
   * Registers an item color handler for a part item
   * @param colors  Item colors instance
   * @param item    Material item
   */
  private static void registerToolItemColors(ItemColors colors, Supplier<? extends ToolCore> item) {
    colors.register(toolColorHandler, item.get());
  }
}
