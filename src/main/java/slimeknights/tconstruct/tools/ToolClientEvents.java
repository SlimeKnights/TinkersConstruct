package slimeknights.tconstruct.tools;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.Input;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
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
import slimeknights.mantle.data.listener.ISafeManagerReloadListener;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.client.armor.AbstractArmorModel;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.client.model.DynamicTextureLoader;
import slimeknights.tconstruct.library.client.model.TinkerItemProperties;
import slimeknights.tconstruct.library.client.model.tools.MaterialModel;
import slimeknights.tconstruct.library.client.model.tools.ToolModel;
import slimeknights.tconstruct.library.client.modifiers.DyedModifierModel;
import slimeknights.tconstruct.library.client.modifiers.FluidModifierModel;
import slimeknights.tconstruct.library.client.modifiers.MaterialModifierModel;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelManager;
import slimeknights.tconstruct.library.client.modifiers.ModifierModelManager.ModifierModelRegistrationEvent;
import slimeknights.tconstruct.library.client.modifiers.NormalModifierModel;
import slimeknights.tconstruct.library.client.modifiers.TankModifierModel;
import slimeknights.tconstruct.library.client.particle.AttackParticle;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorStatModule;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.HarvestTiers;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.client.CrystalshotRenderer;
import slimeknights.tconstruct.tools.client.FluidEffectProjectileRenderer;
import slimeknights.tconstruct.tools.client.OverslimeModifierModel;
import slimeknights.tconstruct.tools.client.SlimeskullArmorModel;
import slimeknights.tconstruct.tools.client.ToolContainerScreen;
import slimeknights.tconstruct.tools.item.ModifierCrystalItem;
import slimeknights.tconstruct.tools.logic.InteractionHandler;
import slimeknights.tconstruct.tools.modifiers.ability.armor.DoubleJumpModifier;
import slimeknights.tconstruct.tools.network.TinkerControlPacket;

import java.util.function.Consumer;

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
    manager.registerReloadListener(SlimeskullArmorModel.RELOAD_LISTENER);
    manager.registerReloadListener(HarvestTiers.RELOAD_LISTENER);
    ArmorModelManager.init(manager);
  }

  @SubscribeEvent
  static void registerModelLoaders(RegisterGeometryLoaders event) {
    event.register("material", MaterialModel.LOADER);
    event.register("tool", ToolModel.LOADER);
  }

  @SubscribeEvent
  static void registerModifierModels(ModifierModelRegistrationEvent event) {
    event.registerModel(TConstruct.getResource("normal"), NormalModifierModel.UNBAKED_INSTANCE);
    event.registerModel(TConstruct.getResource("overslime"), OverslimeModifierModel.UNBAKED_INSTANCE);
    event.registerModel(TConstruct.getResource("fluid"), FluidModifierModel.UNBAKED_INSTANCE);
    event.registerModel(TConstruct.getResource("tank"), TankModifierModel.UNBAKED_INSTANCE);
    event.registerModel(TConstruct.getResource("material"), MaterialModifierModel.UNBAKED_INSTANCE);
    event.registerModel(TConstruct.getResource("dyed"), DyedModifierModel.UNBAKED_INSTANCE);
  }

  @SubscribeEvent
  static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(TinkerTools.indestructibleItem.get(), ItemEntityRenderer::new);
    event.registerEntityRenderer(TinkerTools.crystalshotEntity.get(), CrystalshotRenderer::new);
    event.registerEntityRenderer(TinkerModifiers.fluidSpitEntity.get(), FluidEffectProjectileRenderer::new);
  }

  @SubscribeEvent
  static void registerKeyBinding(RegisterKeyMappingsEvent event) {
    event.register(HELMET_INTERACT);
    event.register(LEGGINGS_INTERACT);
  }

  @SubscribeEvent
  static void clientSetupEvent(FMLClientSetupEvent event) {
    MinecraftForge.EVENT_BUS.addListener(ToolClientEvents::handleKeyBindings);
    MinecraftForge.EVENT_BUS.addListener(ToolClientEvents::handleInput);
    AbstractArmorModel.init();

    // keybinds
    event.enqueueWork(() -> {
      // screens
      MenuScreens.register(TinkerTools.toolContainer.get(), ToolContainerScreen::new);

      // properties
      // stone
      TinkerItemProperties.registerToolProperties(TinkerTools.pickaxe.asItem());
      TinkerItemProperties.registerToolProperties(TinkerTools.sledgeHammer.asItem());
      TinkerItemProperties.registerToolProperties(TinkerTools.veinHammer.asItem());
      // dirt
      TinkerItemProperties.registerToolProperties(TinkerTools.mattock.asItem());
      TinkerItemProperties.registerToolProperties(TinkerTools.pickadze.asItem());
      TinkerItemProperties.registerToolProperties(TinkerTools.excavator.asItem());
      // axe
      TinkerItemProperties.registerToolProperties(TinkerTools.handAxe.asItem());
      TinkerItemProperties.registerToolProperties(TinkerTools.broadAxe.asItem());
      // leaves
      TinkerItemProperties.registerToolProperties(TinkerTools.kama.asItem());
      TinkerItemProperties.registerToolProperties(TinkerTools.scythe.asItem());
      // sword
      TinkerItemProperties.registerToolProperties(TinkerTools.dagger.asItem());
      TinkerItemProperties.registerToolProperties(TinkerTools.sword.asItem());
      TinkerItemProperties.registerToolProperties(TinkerTools.cleaver.asItem());
      // bow
      TinkerItemProperties.registerCrossbowProperties(TinkerTools.crossbow.asItem());
      TinkerItemProperties.registerToolProperties(TinkerTools.longbow.asItem());
      // misc
      TinkerItemProperties.registerToolProperties(TinkerTools.flintAndBrick.asItem());
      TinkerItemProperties.registerToolProperties(TinkerTools.skyStaff.asItem());
      TinkerItemProperties.registerToolProperties(TinkerTools.earthStaff.asItem());
      TinkerItemProperties.registerToolProperties(TinkerTools.ichorStaff.asItem());
      TinkerItemProperties.registerToolProperties(TinkerTools.enderStaff.asItem());
      // ancient
      TinkerItemProperties.registerToolProperties(TinkerTools.meltingPan.asItem());
      TinkerItemProperties.registerCrossbowProperties(TinkerTools.warPick.asItem());
      TinkerItemProperties.registerToolProperties(TinkerTools.battlesign.asItem());
      // armor
      TinkerItemProperties.registerToolProperties(TinkerTools.travelersShield.asItem());
      TinkerItemProperties.registerToolProperties(TinkerTools.plateShield.asItem());
      Consumer<Item> brokenConsumer = TinkerItemProperties::registerBrokenProperty;
      TinkerTools.travelersGear.forEach(brokenConsumer);
      TinkerTools.plateArmor.forEach(brokenConsumer);
      TinkerTools.slimesuit.forEach(brokenConsumer);
    });
  }

  @SubscribeEvent
  static void registerParticleFactories(RegisterParticleProvidersEvent event) {
    ParticleEngine.SpriteParticleRegistration<SimpleParticleType> factory = AttackParticle.Factory::new;
    event.register(TinkerTools.hammerAttackParticle.get(), factory);
    event.register(TinkerTools.axeAttackParticle.get(), factory);
    event.register(TinkerTools.bonkAttackParticle.get(), factory);
  }

  @SubscribeEvent
  static void itemColors(RegisterColorHandlersEvent.Item event) {
    final ItemColors colors = event.getItemColors();

    // tint modifiers
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
    registerItemColors(colors, TinkerTools.crossbow);
    registerItemColors(colors, TinkerTools.longbow);
    // ancient
    registerItemColors(colors, TinkerTools.meltingPan);
    registerItemColors(colors, TinkerTools.warPick);
    registerItemColors(colors, TinkerTools.battlesign);
    // armor
    registerItemColors(colors, TinkerTools.travelersShield);
    registerItemColors(colors, TinkerTools.plateShield);
    Consumer<Item> brokenConsumer = item -> event.register(ToolModel.COLOR_HANDLER, item);
    TinkerTools.travelersGear.forEach(brokenConsumer);
    TinkerTools.plateArmor.forEach(brokenConsumer);
    TinkerTools.slimesuit.forEach(brokenConsumer);

    // modifier crystal
    event.register((stack, index) -> {
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

  private static void handleInput(MovementInputUpdateEvent event) {
    Player player = event.getEntity();
    if (player.isUsingItem() && !player.isPassenger()) {
      ItemStack using = player.getUseItem();
      // start by calculating tool stat
      float speed = 0.2f;
      if (using.is(TinkerTags.Items.HELD)) {
        ToolStack tool = ToolStack.from(using);
        speed = tool.getStats().get(ToolStats.USE_ITEM_SPEED);
      }
      // next, add in armor bonus
      speed = Mth.clamp(speed + ArmorStatModule.getStat(player, TinkerDataKeys.USE_ITEM_SPEED), 0, 1);
      // update speed, note if the armor stat is 0 and the held tool is not tinkers this is a no-op effectively
      Input input = event.getInput();
      // multiply by 5 to cancel out the vanilla 20%
      input.leftImpulse *= speed * 5;
      input.forwardImpulse *= speed * 5;
    }
  }
}
