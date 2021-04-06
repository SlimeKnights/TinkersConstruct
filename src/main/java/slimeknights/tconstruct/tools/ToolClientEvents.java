package slimeknights.tconstruct.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.model.tools.MaterialModel;
import slimeknights.tconstruct.library.client.model.tools.ToolModelLoader;
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

@EventBusSubscriber(modid = TConstruct.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class ToolClientEvents extends ClientEventBase {

  @SubscribeEvent
  static void registerModelLoaders(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(Util.getResource("tool"), ToolModelLoader.INSTANCE);
    ModelLoaderRegistry.registerLoader(Util.getResource("material"), MaterialModel.LOADER);
  }

  @SubscribeEvent
  static void clientSetupEvent(FMLClientSetupEvent event) {
    RenderingRegistry.registerEntityRenderingHandler(TinkerTools.indestructibleItem.get(), manager -> new ItemRenderer(manager, Minecraft.getInstance().getItemRenderer()));
    MinecraftForge.EVENT_BUS.addListener(ToolClientEvents::onTooltipEvent);
  }

  @SubscribeEvent
  static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
    Minecraft.getInstance().particles.registerFactory(TinkerTools.hammerAttackParticle.get(), HammerAttackParticle.Factory::new);
    Minecraft.getInstance().particles.registerFactory(TinkerTools.axeAttackParticle.get(), AxeAttackParticle.Factory::new);
  }

  @SubscribeEvent
  static void itemColors(ColorHandlerEvent.Item event) {
    final ItemColors colors = event.getItemColors();

    // tint tool textures for fallback
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
  }

  // registered with FORGE bus
  private static void onTooltipEvent(ItemTooltipEvent event) {
    if (event.getFlags().isAdvanced() && event.getItemStack().getItem() instanceof ToolCore) {
      // remove the advanced tooltip durability, we supply that
      event.getToolTip().removeIf(text -> text instanceof TranslationTextComponent && ((TranslationTextComponent)text).getKey().equals("item.durability"));
    }
  }

  /** Color handler instance for MaterialItem */
  private static final IItemColor materialColorHandler = (stack, index) -> {
    return Optional.of(IMaterialItem.getMaterialFromStack(stack))
      .filter((material) -> IMaterial.UNKNOWN != material)
      .map(IMaterial::getIdentifier)
      .flatMap(MaterialRenderInfoLoader.INSTANCE::getRenderInfo)
      .map(MaterialRenderInfo::getVertexColor)
      .orElse(-1);
  };

  /** Color handler instance for ToolCore */
  private static final IItemColor toolColorHandler = (stack, index) -> {
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
