package slimeknights.tconstruct.tools.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.client.model.tools.MaterialModel;
import slimeknights.tconstruct.library.client.model.tools.ToolModel;
import slimeknights.tconstruct.library.client.modifiers.BreakableModifierModel;
import slimeknights.tconstruct.library.client.modifiers.FluidModifierModel;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelManager;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelManager.ModifierModelRegistrationEvent;
import slimeknights.tconstruct.library.client.modifiers.NormalModifierModel;
import slimeknights.tconstruct.library.client.modifiers.TankModifierModel;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.part.MaterialItem;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.client.particles.AxeAttackParticle;
import slimeknights.tconstruct.tools.client.particles.HammerAttackParticle;
import slimeknights.tconstruct.tools.logic.InteractionHandler;
import slimeknights.tconstruct.tools.modifiers.ability.armor.DoubleJumpModifier;
import slimeknights.tconstruct.tools.network.TinkerControlPacket;

import java.util.function.Supplier;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = TConstruct.MOD_ID, value = Dist.CLIENT, bus = Bus.MOD)
public class ToolClientEvents extends ClientEventBase {
  /** Keybinding for interacting using a helmet */
  private static final KeyBinding HELMET_INTERACT = new KeyBinding(TConstruct.makeTranslationKey("key", "helmet_interact"), KeyConflictContext.IN_GAME, InputMappings.getInputByName("key.keyboard.z"), "key.categories.gameplay");

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
    MinecraftForge.EVENT_BUS.addListener(ToolClientEvents::handleKeyBindings);

    // keybinds
    ClientRegistry.registerKeyBinding(HELMET_INTERACT);
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
          TinkerNetwork.getInstance().sendToServer(TinkerControlPacket.DOUBLE_JUMP);
        }
      }
      wasJumping = isJumping;

      // helmet interaction
      boolean isHelmetInteracting = HELMET_INTERACT.isKeyDown();
      if (!wasHelmetInteracting && isHelmetInteracting) {
        if (InteractionHandler.startHelmetInteract(event.player)) {
          TinkerNetwork.getInstance().sendToServer(TinkerControlPacket.START_HELMET_INTERACT);
        }
      }
      if (wasHelmetInteracting && !isHelmetInteracting) {
        if (InteractionHandler.stopHelmetInteract(event.player)) {
          TinkerNetwork.getInstance().sendToServer(TinkerControlPacket.STOP_HELMET_INTERACT);
        }
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
