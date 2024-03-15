package slimeknights.tconstruct.tools.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickEmpty;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.logic.InteractionHandler;
import slimeknights.tconstruct.tools.network.InteractWithAirPacket;

/**
 * Client side interaction hooks
 */
@EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Bus.FORGE, value = Dist.CLIENT)
public class ClientInteractionHandler {
  /** If true, next offhand interaction should be canceled, used since we cannot tell Forge to break the hand loop from the main hand */
  private static boolean cancelNextOffhand = false;

  /** Implements the client side of chestplate {@link slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook#onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)} */
  @SubscribeEvent(priority = EventPriority.LOW)
  static void chestplateToolUse(PlayerInteractEvent.RightClickEmpty event) {
    // not sure if anyone sets the result, but just in case listen to it so they can stop us running
    if (event.getCancellationResult() != InteractionResult.PASS) {
      return;
    }
    // figure out if we have a chestplate making us care
    Player player = event.getEntity();
    ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
    if (!player.isSpectator() && chestplate.is(TinkerTags.Items.INTERACTABLE_ARMOR)) {
      // found an interaction, time to notify the server and run logic for the client
      InteractionHand hand = event.getHand();
      TinkerNetwork.getInstance().sendToServer(InteractWithAirPacket.fromChestplate(hand));
      InteractionResult result = InteractionHandler.onChestplateUse(player, chestplate, hand);
      if (result.consumesAction()) {
        if (result.shouldSwing()) {
          player.swing(hand);
        }
        Minecraft.getInstance().gameRenderer.itemInHandRenderer.itemUsed(hand);
        if (hand == InteractionHand.MAIN_HAND) {
          cancelNextOffhand = true;
        }
        // set the result so later listeners see we did something
        event.setCancellationResult(result);
      }
    }
  }

  /** Prevents an empty right click from running the offhand */
  @SubscribeEvent(priority = EventPriority.HIGH)
  static void preventDoubleInteract(InputEvent.InteractionKeyMappingTriggered event) {
    if (cancelNextOffhand) {
      cancelNextOffhand = false;
      if (event.getHand() == InteractionHand.OFF_HAND) {
        event.setCanceled(true);
        event.setSwingHand(false);
      }
    }
  }

  /** Implements the client side of left click interaction for {@link slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook#onToolUse(IToolStackView, ModifierEntry, Player, InteractionHand, InteractionSource)} */
  @SubscribeEvent
  static void leftClickAir(LeftClickEmpty event) {
    // not sure if anyone sets the result, but just in case listen to it so they can stop us running
    if (event.getCancellationResult() != InteractionResult.PASS) {
      return;
    }
    // figure out if we have a chestplate making us care
    Player player = event.getEntity();
    ItemStack tool = event.getItemStack();
    if (!player.isSpectator() && tool.is(TinkerTags.Items.INTERACTABLE_LEFT)) {
      // found an interaction, time to notify the server and run logic for the client
      InteractionHand hand = event.getHand();
      TinkerNetwork.getInstance().sendToServer(InteractWithAirPacket.LEFT_CLICK);
      InteractionResult result = InteractionHandler.onLeftClickInteraction(player, tool, hand);
      if (result.consumesAction()) {
        if (result.shouldSwing()) {
          player.swing(hand);
        }
        Minecraft.getInstance().gameRenderer.itemInHandRenderer.itemUsed(hand);
        // set the result so later listeners see we did something
        event.setCancellationResult(result);
      }
    }
  }
}
