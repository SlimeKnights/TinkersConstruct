package slimeknights.tconstruct.tools;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.ISafeManagerReloadListener;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.client.model.DynamicTextureLoader;
import slimeknights.tconstruct.library.client.model.TinkerItemProperties;
import slimeknights.tconstruct.library.client.model.tools.MaterialModel;
import slimeknights.tconstruct.library.client.model.tools.ToolModel;
import slimeknights.tconstruct.library.client.modifiers.BreakableDyedModifierModel;
import slimeknights.tconstruct.library.client.modifiers.BreakableMaterialModifierModel;
import slimeknights.tconstruct.library.client.modifiers.BreakableModifierModel;
import slimeknights.tconstruct.library.client.modifiers.FluidModifierModel;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelManager;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelManager.ModifierModelRegistrationEvent;
import slimeknights.tconstruct.library.client.modifiers.NormalModifierModel;
import slimeknights.tconstruct.library.client.modifiers.TankModifierModel;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.HarvestTiers;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.client.ArmorModelHelper;
import slimeknights.tconstruct.tools.client.CrystalshotRenderer;
import slimeknights.tconstruct.tools.client.OverslimeModifierModel;
import slimeknights.tconstruct.tools.client.PlateArmorModel;
import slimeknights.tconstruct.tools.client.SlimelytraArmorModel;
import slimeknights.tconstruct.tools.client.SlimeskullArmorModel;
import slimeknights.tconstruct.tools.client.ToolContainerScreen;
import slimeknights.tconstruct.tools.client.particles.AxeAttackParticle;
import slimeknights.tconstruct.tools.client.particles.HammerAttackParticle;
import slimeknights.tconstruct.tools.item.ModifiableCrossbowItem;
import slimeknights.tconstruct.tools.item.ModifierCrystalItem;
import slimeknights.tconstruct.tools.logic.InteractionHandler;
import slimeknights.tconstruct.tools.modifiers.ability.armor.DoubleJumpModifier;
import slimeknights.tconstruct.tools.network.TinkerControlPacket;

import java.util.Objects;

import static slimeknights.tconstruct.library.client.model.tools.ToolModel.registerItemColors;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = TConstruct.MOD_ID, value = Dist.CLIENT, bus = Bus.MOD)
public class ToolClientEvents extends ClientEventBase {
  /** Keybinding for interacting using a helmet */
  private static final KeyMapping HELMET_INTERACT = new KeyMapping(TConstruct.makeTranslationKey("key", "helmet_interact"), KeyConflictContext.IN_GAME, InputConstants.getKey("key.keyboard.z"), "key.categories.tconstruct");
  /** Keybinding for interacting using leggings */
  private static final KeyMapping LEGGINGS_INTERACT = new KeyMapping(TConstruct.makeTranslationKey("key", "leggings_interact"), KeyConflictContext.IN_GAME, InputConstants.getKey("key.keyboard.i"), "key.categories.tconstruct");

  /** Listener to clear modifier cache */
  private static final ISafeManagerReloadListener MODIFIER_RELOAD_LISTENER = manager -> {
    ModifierManager.INSTANCE.getAllValues().forEach(modifier -> modifier.clearCache(PackType.CLIENT_RESOURCES));
  };

  @SubscribeEvent
  static void addResourceListener(RegisterClientReloadListenersEvent manager) {
    ModifierModelManager.init(manager);
    MaterialTooltipCache.init(manager);
    DynamicTextureLoader.init(manager);
    manager.registerReloadListener(MODIFIER_RELOAD_LISTENER);
    manager.registerReloadListener(PlateArmorModel.RELOAD_LISTENER);
    manager.registerReloadListener(SlimeskullArmorModel.RELOAD_LISTENER);
    manager.registerReloadListener(SlimelytraArmorModel.RELOAD_LISTENER);
    manager.registerReloadListener(HarvestTiers.RELOAD_LISTENER);
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
    event.registerModel(TConstruct.getResource("breakable_material"), BreakableMaterialModifierModel.UNBAKED_INSTANCE);
    event.registerModel(TConstruct.getResource("breakable_dyed"), BreakableDyedModifierModel.UNBAKED_INSTANCE);
  }

  @SubscribeEvent
  static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(TinkerTools.indestructibleItem.get(), ItemEntityRenderer::new);
    event.registerEntityRenderer(TinkerTools.crystalshotEntity.get(), CrystalshotRenderer::new);
  }

  @SubscribeEvent
  static void clientSetupEvent(FMLClientSetupEvent event) {
    MinecraftForge.EVENT_BUS.addListener(ToolClientEvents::handleKeyBindings);
    ArmorModelHelper.init();

    // keybinds
    ClientRegistry.registerKeyBinding(HELMET_INTERACT);
    ClientRegistry.registerKeyBinding(LEGGINGS_INTERACT);

    // screens
    MenuScreens.register(TinkerTools.toolContainer.get(), ToolContainerScreen::new);

    // properties
    TinkerItemProperties.registerBowProperties(TinkerTools.crossbow.asItem());
    TinkerItemProperties.registerBowProperties(TinkerTools.longbow.asItem());
    // no sense having two keys for ammo, just set 1 for arrow, 2 for fireworks
    String fireworksID = Objects.requireNonNull(Items.FIREWORK_ROCKET.getRegistryName()).toString();
    ItemProperties.register(TinkerTools.crossbow.asItem(), TConstruct.getResource("ammo"), (stack, level, entity, seed) -> {
      CompoundTag nbt = stack.getTag();
      if (nbt != null) {
        CompoundTag persistentData = nbt.getCompound(ToolStack.TAG_PERSISTENT_MOD_DATA);
        if (!persistentData.isEmpty()) {
          CompoundTag ammo = persistentData.getCompound(ModifiableCrossbowItem.KEY_CROSSBOW_AMMO.toString());
          if (!ammo.isEmpty()) {
            return ammo.getString("id").equals(fireworksID) ? 2 : 1;
          }
        }
      }
      return 0;
    });
  }

  @SubscribeEvent
  static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
    Minecraft.getInstance().particleEngine.register(TinkerTools.hammerAttackParticle.get(), HammerAttackParticle.Factory::new);
    Minecraft.getInstance().particleEngine.register(TinkerTools.axeAttackParticle.get(), AxeAttackParticle.Factory::new);
  }

  @SubscribeEvent
  static void itemColors(ColorHandlerEvent.Item event) {
    final ItemColors colors = event.getItemColors();

    // tint tool textures for fallback
    // rock
    registerItemColors(colors, TinkerTools.pickaxe);
    registerItemColors(colors, TinkerTools.sledgeHammer);
    registerItemColors(colors, TinkerTools.veinHammer);
    // dirt
    registerItemColors(colors, TinkerTools.mattock);
    registerItemColors(colors, TinkerTools.pickadze);
    registerItemColors(colors, TinkerTools.excavator);
    // wood
    registerItemColors(colors, TinkerTools.handAxe);
    registerItemColors(colors, TinkerTools.broadAxe);
    // scythe
    registerItemColors(colors, TinkerTools.kama);
    registerItemColors(colors, TinkerTools.scythe);
    // weapon
    registerItemColors(colors, TinkerTools.dagger);
    registerItemColors(colors, TinkerTools.sword);
    registerItemColors(colors, TinkerTools.cleaver);
    // bow
    registerItemColors(colors, TinkerTools.longbow);

    // modifier crystal
    colors.register((stack, index) -> {
      ModifierId modifier = ModifierCrystalItem.getModifier(stack);
      if (modifier != null) {
        return ResourceColorManager.getColor(Util.makeTranslationKey("modifier", modifier));
      }
      return -1;
    }, TinkerModifiers.modifierCrystal.asItem());
  }

  // values to check if a key was being pressed last tick, safe as a static value as we only care about a single player client side
  /** If true, we were jumping last tick */
  private static boolean wasJumping = false;
  /** If true, we were interacting with helmet last tick */
  private static boolean wasHelmetInteracting = false;
  /** If true, we were interacting with leggings last tick */
  private static boolean wasLeggingsInteracting = false;

  /** Called on player tick to handle keybinding presses */
  private static void handleKeyBindings(PlayerTickEvent event) {
    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft.player != null && minecraft.player == event.player && event.phase == Phase.START && event.side == LogicalSide.CLIENT && !minecraft.player.isSpectator()) {

      // jumping in mid air for double jump
      // ensure we pressed the key since the last tick, holding should not use all your jumps at once
      boolean isJumping = minecraft.options.keyJump.isDown();
      if (!wasJumping && isJumping) {
        if (DoubleJumpModifier.extraJump(event.player)) {
          TinkerNetwork.getInstance().sendToServer(TinkerControlPacket.DOUBLE_JUMP);
        }
      }
      wasJumping = isJumping;

      // helmet interaction
      boolean isHelmetInteracting = HELMET_INTERACT.isDown();
      if (!wasHelmetInteracting && isHelmetInteracting) {
        TooltipKey key = SafeClientAccess.getTooltipKey();
        if (InteractionHandler.startArmorInteract(event.player, EquipmentSlot.HEAD, key)) {
          TinkerNetwork.getInstance().sendToServer(TinkerControlPacket.getStartHelmetInteract(key));
        }
      }
      if (wasHelmetInteracting && !isHelmetInteracting) {
        if (InteractionHandler.stopArmorInteract(event.player, EquipmentSlot.HEAD)) {
          TinkerNetwork.getInstance().sendToServer(TinkerControlPacket.STOP_HELMET_INTERACT);
        }
      }

      // leggings interaction
      boolean isLeggingsInteract = LEGGINGS_INTERACT.isDown();
      if (!wasLeggingsInteracting && isLeggingsInteract) {
        TooltipKey key = SafeClientAccess.getTooltipKey();
        if (InteractionHandler.startArmorInteract(event.player, EquipmentSlot.LEGS, key)) {
          TinkerNetwork.getInstance().sendToServer(TinkerControlPacket.getStartLeggingsInteract(key));
        }
      }
      if (wasLeggingsInteracting && !isLeggingsInteract) {
        if (InteractionHandler.stopArmorInteract(event.player, EquipmentSlot.LEGS)) {
          TinkerNetwork.getInstance().sendToServer(TinkerControlPacket.STOP_LEGGINGS_INTERACT);
        }
      }

      wasHelmetInteracting = isHelmetInteracting;
      wasLeggingsInteracting = isLeggingsInteract;
    }
  }
}
