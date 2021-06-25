package slimeknights.tconstruct.tools;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.commons.lang3.mutable.MutableInt;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.model.tools.MaterialModel;
import slimeknights.tconstruct.library.client.model.tools.ToolModel;
import slimeknights.tconstruct.library.client.modifiers.BreakableModifierModel;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelManager;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelManager.ModifierModelRegistrationEvent;
import slimeknights.tconstruct.library.client.modifiers.NormalModifierModel;
import slimeknights.tconstruct.library.client.modifiers.TankModifierModel;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tools.OffhandCooldownTracker;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.client.OverslimeModifierModel;
import slimeknights.tconstruct.tools.client.particles.AxeAttackParticle;
import slimeknights.tconstruct.tools.client.particles.HammerAttackParticle;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = TConstruct.modID, value = Dist.CLIENT, bus = Bus.MOD)
public class ToolClientEvents extends ClientEventBase {
  /**
   * Called by TinkerClient to add the resource listeners, runs during constructor
   */
  public static void addResourceListener(IReloadableResourceManager manager) {
    ModifierModelManager.init(manager);
  }

  @SubscribeEvent
  static void registerModelLoaders(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(Util.getResource("material"), MaterialModel.LOADER);
    ModelLoaderRegistry.registerLoader(Util.getResource("tool"), ToolModel.LOADER);
  }

  @SubscribeEvent
  static void registerModifierModels(ModifierModelRegistrationEvent event) {
    event.registerModel(Util.getResource("normal"), NormalModifierModel.UNBAKED_INSTANCE);
    event.registerModel(Util.getResource("breakable"), BreakableModifierModel.UNBAKED_INSTANCE);
    event.registerModel(Util.getResource("overslime"), OverslimeModifierModel.UNBAKED_INSTANCE);
    event.registerModel(Util.getResource("tank"), TankModifierModel.UNBAKED_INSTANCE);
  }

  @SubscribeEvent
  static void clientSetupEvent(FMLClientSetupEvent event) {
    RenderingRegistry.registerEntityRenderingHandler(TinkerTools.indestructibleItem.get(), manager -> new ItemRenderer(manager, Minecraft.getInstance().getItemRenderer()));
    MinecraftForge.EVENT_BUS.addListener(ToolClientEvents::onTooltipEvent);
    MinecraftForge.EVENT_BUS.addListener(ToolClientEvents::renderHand);
    MinecraftForge.EVENT_BUS.addListener(ToolClientEvents::renderOffhandAttackIndicator);
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
    registerToolItemColors(colors, TinkerTools.dagger);
    registerToolItemColors(colors, TinkerTools.sword);
    registerToolItemColors(colors, TinkerTools.cleaver);

    // tint tool part textures for fallback
    registerMaterialItemColors(colors, TinkerToolParts.repairKit);
    // heads
    registerMaterialItemColors(colors, TinkerToolParts.pickaxeHead);
    registerMaterialItemColors(colors, TinkerToolParts.hammerHead);
    registerMaterialItemColors(colors, TinkerToolParts.smallAxeHead);
    registerMaterialItemColors(colors, TinkerToolParts.broadAxeHead);
    registerMaterialItemColors(colors, TinkerToolParts.smallBlade);
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
      MutableInt removedWhenIn = new MutableInt(0);
      event.getToolTip().removeIf(text -> {
        // its hard to find the blank line before attributes, so shift just removes all of them
        if ((isShift || (isCtrl && removedWhenIn.intValue() > 0)) && text == StringTextComponent.EMPTY) {
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

          // we want to ignore all modifiers after "when in off hand" as its typically redundant to the main hand, you will see without shift
          if ((isCtrl || isShift) && key.startsWith("item.modifiers.")) {
            removedWhenIn.add(1);
            return true;
          }

          // suppress durability from advanced, we display our own
          return key.equals("item.durability")
                 // the "when in main hand" text, don't need on either tooltip
            || ((isCtrl || (isShift && removedWhenIn.intValue() > 1)) && key.startsWith("attribute.modifier."));
        }
        return false;
      });
    }
  }

  // registered with FORGE bus
  private static void renderHand(RenderHandEvent event) {
    Hand hand = event.getHand();
    PlayerEntity player = Minecraft.getInstance().player;
    if (hand != Hand.OFF_HAND || player == null) {
      return;
    }
    ItemStack stack = player.getHeldItemMainhand();
    if (stack.getItem().isIn(TinkerTags.Items.TWO_HANDED)) {
      // special support for replacing modifier
      if (!(event.getItemStack().getItem() instanceof BlockItem) || ToolStack.from(stack).getModifierLevel(TinkerModifiers.exchanging.get()) == 0) {
        event.setCanceled(true);
      }
    }
  }

  // registered with FORGE bus
  private static void renderOffhandAttackIndicator(RenderGameOverlayEvent.Post event) {
    // must have a player, not be in spectator, and have the indicator enabled
    Minecraft minecraft = Minecraft.getInstance();
    GameSettings settings = minecraft.gameSettings;
    if (minecraft.player == null || minecraft.playerController == null || minecraft.playerController.getCurrentGameType() == GameType.SPECTATOR || settings.attackIndicator == AttackIndicatorStatus.OFF) {
      return;
    }
    // must be holding something that can duel wield
    ItemStack held = minecraft.player.getHeldItemOffhand();
    if (!TinkerTags.Items.MODIFIABLE.contains(held.getItem())) {
      return;
    }
    // check if we have cooldown
    float cooldown = OffhandCooldownTracker.getCooldown(minecraft.player);
    if (cooldown >= 1.0f) {
      return;
    }

    // show attack indicator
    MatrixStack matrixStack = event.getMatrixStack();
    switch (settings.attackIndicator) {
      case CROSSHAIR:
        if (event.getType() == ElementType.CROSSHAIRS && minecraft.gameSettings.getPointOfView().func_243192_a()) {
          if (!settings.showDebugInfo || settings.hideGUI || minecraft.player.hasReducedDebug() || settings.reducedDebugInfo) {
            // mostly cloned from vanilla attack indicator
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            int scaledHeight = minecraft.getMainWindow().getScaledHeight();
            // integer division makes this a pain to line up, there might be a simplier version of this formula but I cannot think of one
            int y = (scaledHeight / 2) - 14 + (2 * (scaledHeight % 2));
            int x = minecraft.getMainWindow().getScaledWidth() / 2 - 8;
            int width = (int)(cooldown * 17.0F);
            minecraft.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
            minecraft.ingameGUI.blit(matrixStack, x, y, 36, 94, 16, 4);
            minecraft.ingameGUI.blit(matrixStack, x, y, 52, 94, width, 4);
          }
        }
        break;
      case HOTBAR:
        if (event.getType() == ElementType.HOTBAR && minecraft.renderViewEntity == minecraft.player) {
          int centerWidth = minecraft.getMainWindow().getScaledWidth() / 2;
          int y = minecraft.getMainWindow().getScaledHeight() - 20;
          int x;
          // opposite of the vanilla hand location, extra bit to offset past the offhand slot
          if (minecraft.player.getPrimaryHand() == HandSide.RIGHT) {
            x = centerWidth - 91 - 22 - 32;
          } else {
            x = centerWidth + 91 + 6 + 32;
          }
          minecraft.getTextureManager().bindTexture(AbstractGui.GUI_ICONS_LOCATION);
          int l1 = (int)(cooldown * 19.0F);
          RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
          minecraft.ingameGUI.blit(matrixStack, x, y, 0, 94, 18, 18);
          minecraft.ingameGUI.blit(matrixStack, x, y + 18 - l1, 18, 112 - l1, 18, l1);
        }
        break;
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
