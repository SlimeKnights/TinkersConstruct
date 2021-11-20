package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.commons.lang3.mutable.MutableInt;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.client.model.tools.MaterialModel;
import slimeknights.tconstruct.library.client.model.tools.ToolModel;
import slimeknights.tconstruct.library.client.modifiers.BreakableModifierModel;
import slimeknights.tconstruct.library.client.modifiers.FluidModifierModel;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelManager;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelManager.ModifierModelRegistrationEvent;
import slimeknights.tconstruct.library.client.modifiers.NormalModifierModel;
import slimeknights.tconstruct.library.client.modifiers.TankModifierModel;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.MaterialItem;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.client.particles.AxeAttackParticle;
import slimeknights.tconstruct.tools.client.particles.HammerAttackParticle;
import slimeknights.tconstruct.tools.modifiers.ability.armor.DoubleJumpModifier;
import slimeknights.tconstruct.tools.network.DoubleJumpPacket;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = TConstruct.MOD_ID, value = Dist.CLIENT, bus = Bus.MOD)
public class ToolClientEvents extends ClientEventBase {
  /**
   * Called by TinkerClient to add the resource listeners, runs during constructor
   */
  public static void addResourceListener(IReloadableResourceManager manager) {
    ModifierModelManager.init(manager);
  }

  @SubscribeEvent
  static void registerModelLoaders(ModelRegistryEvent event) {
    ModelLoaderRegistry.registerLoader(TConstruct.getResource("material"), MaterialModel.LOADER);
    ModelLoaderRegistry.registerLoader(TConstruct.getResource("tool"), ToolModel.LOADER);
  }

  @SubscribeEvent
  static void registerModifierModels(ModifierModelRegistrationEvent event) {
    event.registerModel(TConstruct.getResource("normal"), NormalModifierModel.UNBAKED_INSTANCE);
    event.registerModel(TConstruct.getResource("breakable"), BreakableModifierModel.UNBAKED_INSTANCE);
    event.registerModel(TConstruct.getResource("overslime"), OverslimeModifierModel.UNBAKED_INSTANCE);
    event.registerModel(TConstruct.getResource("fluid"), FluidModifierModel.UNBAKED_INSTANCE);
    event.registerModel(TConstruct.getResource("tank"), TankModifierModel.UNBAKED_INSTANCE);
  }

  @SubscribeEvent
  static void clientSetupEvent(FMLClientSetupEvent event) {
    RenderingRegistry.registerEntityRenderingHandler(TinkerTools.indestructibleItem.get(), manager -> new ItemRenderer(manager, Minecraft.getInstance().getItemRenderer()));
    MinecraftForge.EVENT_BUS.addListener(ToolClientEvents::onTooltipEvent);
    MinecraftForge.EVENT_BUS.addListener(ToolClientEvents::renderHand);
    MinecraftForge.EVENT_BUS.addListener(ToolClientEvents::handleKeyBindings);
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
  }

  // registered with FORGE bus
  private static void onTooltipEvent(ItemTooltipEvent event) {
    if (event.getItemStack().getItem() instanceof IModifiableDisplay) {
      boolean isShift = Screen.hasShiftDown();
      boolean isCtrl = !isShift && ((IModifiableDisplay) event.getItemStack().getItem()).getToolDefinition().isMultipart() && Screen.hasControlDown();
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
    ItemStack mainhand = player.getHeldItemMainhand();
    ItemStack offhand = event.getItemStack();
    if (mainhand.getItem().isIn(TinkerTags.Items.TWO_HANDED)) {
      ToolStack tool = ToolStack.from(mainhand);
      // special support for replacing modifier
      if (!tool.getVolatileData().getBoolean(IModifiable.DEFER_OFFHAND)) {
        if (!(offhand.getItem() instanceof BlockItem) || tool.getModifierLevel(TinkerModifiers.exchanging.get()) == 0) {
          event.setCanceled(true);
          return;
        }
      }
    }

    // if the data is set, render the empty offhand
    if (offhand.isEmpty() && !player.isInvisible() && ModifierUtil.getTotalModifierLevel(player, TinkerDataKeys.SHOW_EMPTY_OFFHAND) > 0) {
      MatrixStack matrices = event.getMatrixStack();
      matrices.push();
      Minecraft.getInstance().getFirstPersonRenderer().renderArmFirstPerson(matrices, event.getBuffers(), event.getLight(), event.getEquipProgress(), event.getSwingProgress(), player.getPrimaryHand().opposite());
      matrices.pop();
      event.setCanceled(true);
    }
  }

  // registered with FORGE bus

  /** If true, we were jumping last tick. Safe as a static value as we only care about a single player client side */
  private static boolean wasJumping = false;
  /** If true, we were interacting last tick. Safe as a static value as we only care about a single player client side */
  private static boolean wasHelmetInteracting = false;

  /** Called on player tick to handle keybinding presses */
  private static void handleKeyBindings(PlayerTickEvent event) {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft.player != null && event.phase == Phase.START && event.side == LogicalSide.CLIENT) {

      // jumping in mid air for double jump
      // ensure we pressed the key since the last tick, holding should not use all your jumps at once
      boolean isJumping = minecraft.gameSettings.keyBindJump.isKeyDown();
      if (!wasJumping && isJumping) {
        if (DoubleJumpModifier.extraJump(event.player)) {
          TinkerNetwork.getInstance().sendToServer(DoubleJumpPacket.INSTANCE);
        }
      }
      wasJumping = isJumping;

      // helmet interaction
      // TODO: holding right click probably should be on a timer, what does vanilla use?
      boolean isHelmetInteracting = HELMET_INTERACT.isKeyDown();
      if (!wasHelmetInteracting && isHelmetInteracting) {
        TConstruct.LOG.info("Key is pressed");
      }

      wasHelmetInteracting = isHelmetInteracting;
    }
  }

  /** @deprecated No longer required, colors are baked into the model */
  @Deprecated
  public static void registerMaterialItemColors(ItemColors colors, Supplier<? extends MaterialItem> item) {}

  /**
   * Registers an item color handler for a part item, TODO: move to API class
   * @param colors  Item colors instance
   * @param item    Material item
   */
  public static void registerToolItemColors(ItemColors colors, Supplier<? extends IModifiable> item) {
    colors.register(ToolModel.COLOR_HANDLER, item.get());
  }
}
