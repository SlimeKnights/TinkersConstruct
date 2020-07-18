package slimeknights.tconstruct.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
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
import slimeknights.tconstruct.library.client.model.tools.MaterialModel;
import slimeknights.tconstruct.library.client.model.tools.ToolModelLoader;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolData;
import slimeknights.tconstruct.tables.client.inventory.library.ToolBuildScreenInfo;

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

    registerToolBuildInformation();
  }

  @SubscribeEvent
  static void itemColors(ColorHandlerEvent.Item event) {
    final ItemColors colors = event.getItemColors();

    // tint tool textures for fallback
    registerToolItemColors(colors, TinkerTools.pickaxe);
    registerToolItemColors(colors, TinkerTools.hammer);
    registerToolItemColors(colors, TinkerTools.shovel);
    registerToolItemColors(colors, TinkerTools.broadSword);

    // tint tool part textures for fallback
    registerMaterialItemColors(colors, TinkerToolParts.pickaxeHead);
    registerMaterialItemColors(colors, TinkerToolParts.hammerHead);
    registerMaterialItemColors(colors, TinkerToolParts.shovelHead);
    registerMaterialItemColors(colors, TinkerToolParts.swordBlade);
    registerMaterialItemColors(colors, TinkerToolParts.smallBinding);
    registerMaterialItemColors(colors, TinkerToolParts.wideGuard);
    registerMaterialItemColors(colors, TinkerToolParts.largePlate);
    registerMaterialItemColors(colors, TinkerToolParts.toolRod);
    registerMaterialItemColors(colors, TinkerToolParts.toughToolRod);
  }

  /** Color handler instance for MaterialItem */
  private static final IItemColor materialColorHandler = (stack, index) -> {
    return Optional.of(IMaterialItem.getMaterialFromStack(stack))
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

  private static void registerToolBuildInformation() {
    ToolBuildScreenInfo info;

    // pickaxe
    info = new ToolBuildScreenInfo(new ItemStack(TinkerTools.pickaxe.get()));
    info.addSlotPosition(53, 22); // pick head
    info.addSlotPosition(15, 60); // rod
    info.addSlotPosition(33, 42); // binding
    ToolRegistry.addToolBuilding(info);

    // hammer
    info = new ToolBuildScreenInfo(new ItemStack(TinkerTools.hammer.get()));
    info.addSlotPosition(44, 29); // head
    info.addSlotPosition(21, 52); // handle
    info.addSlotPosition(57, 48); // plate 1
    info.addSlotPosition(25, 16); // plate 2
    ToolRegistry.addToolBuilding(info);

    info = new ToolBuildScreenInfo(new ItemStack(TinkerTools.shovel.get()));
    info.addSlotPosition(51, 24); // shovel head
    info.addSlotPosition(33, 42); // rod
    info.addSlotPosition(13, 62); // binding
    ToolRegistry.addToolBuilding(info);

    // broad sword
    info = new ToolBuildScreenInfo(new ItemStack(TinkerTools.broadSword.get()));
    info.addSlotPosition(48, 26); // blade
    info.addSlotPosition(12, 62); // handle
    info.addSlotPosition(30, 44); // guard
    ToolRegistry.addToolBuilding(info);
  }
}
