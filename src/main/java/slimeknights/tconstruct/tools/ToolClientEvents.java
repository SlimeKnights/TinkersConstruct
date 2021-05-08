package slimeknights.tconstruct.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
import slimeknights.tconstruct.library.client.model.tools.ToolModel;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.tools.client.particles.AxeAttackParticle;
import slimeknights.tconstruct.tools.client.particles.HammerAttackParticle;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@EventBusSubscriber(modid = TConstruct.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class ToolClientEvents extends ClientEventBase {

  @SubscribeEvent
  static void registerModelLoaders(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(Util.getResource("material"), MaterialModel.LOADER);
    ModelLoaderRegistry.registerLoader(Util.getResource("tool"), ToolModel.LOADER);
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
    // rock
    registerToolItemColors(colors, TinkerTools.pickaxe);
    registerToolItemColors(colors, TinkerTools.sledgeHammer);
    registerToolItemColors(colors, TinkerTools.veinHammer);
    // dirt
    registerToolItemColors(colors, TinkerTools.mattock);
    registerToolItemColors(colors, TinkerTools.excavator);
    // wood
    registerToolItemColors(colors, TinkerTools.handAxe);
    registerToolItemColors(colors, TinkerTools.broadAxe);
    // scythe
    registerToolItemColors(colors, TinkerTools.kama);
    registerToolItemColors(colors, TinkerTools.scythe);
    // weapon
    registerToolItemColors(colors, TinkerTools.broadSword);
    registerToolItemColors(colors, TinkerTools.cleaver);

    // tint tool part textures for fallback
    registerMaterialItemColors(colors, TinkerToolParts.repairKit);
    // heads
    registerMaterialItemColors(colors, TinkerToolParts.pickaxeHead);
    registerMaterialItemColors(colors, TinkerToolParts.hammerHead);
    registerMaterialItemColors(colors, TinkerToolParts.smallAxeHead);
    registerMaterialItemColors(colors, TinkerToolParts.broadAxeHead);
    registerMaterialItemColors(colors, TinkerToolParts.swordBlade);
    registerMaterialItemColors(colors, TinkerToolParts.broadBlade);
    // other parts
    registerMaterialItemColors(colors, TinkerToolParts.toolBinding);
    registerMaterialItemColors(colors, TinkerToolParts.largePlate);
    registerMaterialItemColors(colors, TinkerToolParts.toolHandle);
    registerMaterialItemColors(colors, TinkerToolParts.toughHandle);
  }
  // registered with FORGE bus
  private static void onTooltipEvent(ItemTooltipEvent event) {
    if (event.getItemStack().getItem() instanceof ToolCore) {
      boolean isShift = Screen.hasShiftDown();
      boolean isCtrl = !isShift && Screen.hasControlDown();
      event.getToolTip().removeIf(text -> {
        // its hard to find the blank line before attributes, so shift just removes all of them
        if (isShift && text == StringTextComponent.EMPTY) {
          return true;
        }
        // the attack damage and attack speed ones are formatted weirdly, suppress on both tooltips
        if ((isShift || isCtrl) && " ".equals(text.getUnformattedComponentText())) {
          List<ITextComponent> siblings = text.getSiblings();
          if (!siblings.isEmpty() && siblings.get(0) instanceof TranslationTextComponent) {
            return ((TranslationTextComponent) siblings.get(0)).getKey().startsWith("attribute.modifier.equals.");
          }
        }
        if (text instanceof TranslationTextComponent) {
          String key = ((TranslationTextComponent)text).getKey();
          // suppress durability from advanced, we display our own
          return key.equals("item.durability")
                 // the "when in main hand" text, don't need on either tooltip
            || ((isCtrl || isShift) && key.startsWith("item.modifiers."))
                 // individual modifiers, want on shift, ignore on control
            || (isCtrl && key.startsWith("attribute.modifier."));
        }
        return false;
      });
    }
  }

  /** Color handler instance for MaterialItem */
  private static final IItemColor materialColorHandler = (stack, index) -> {
    return Optional.of(IMaterialItem.getMaterialIdFromStack(stack))
      .filter(material -> !material.equals(IMaterial.UNKNOWN_ID))
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
   * Registers an item color handler for a part item, TODO: move to API class
   * @param colors  Item colors instance
   * @param item    Material item
   */
  public static void registerMaterialItemColors(ItemColors colors, Supplier<? extends MaterialItem> item) {
    colors.register(materialColorHandler, item.get());
  }

  /**
   * Registers an item color handler for a part item, TODO: move to API class
   * @param colors  Item colors instance
   * @param item    Material item
   */
  public static void registerToolItemColors(ItemColors colors, Supplier<? extends ToolCore> item) {
    colors.register(toolColorHandler, item.get());
  }
}
