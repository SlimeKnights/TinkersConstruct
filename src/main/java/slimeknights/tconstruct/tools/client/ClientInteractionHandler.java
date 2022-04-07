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
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.logic.InteractionHandler;
import slimeknights.tconstruct.tools.network.OnChestplateUsePacket;

/**
 * Client side interaction hooks
 */
@EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Bus.FORGE, value = Dist.CLIENT)
public class ClientInteractionHandler {
  /** If true, next offhand interaction should be canceled, used since we cannot tell Forge to break the hand loop from the main hand */
  private static boolean cancelNextOffhand = false;

  /** Implements the client side of chestplate {@link slimeknights.tconstruct.library.modifiers.Modifier#onToolUse(IToolStackView, int, net.minecraft.world.World, net.minecraft.entity.player.PlayerEntity, net.minecraft.util.Hand, net.minecraft.inventory.EquipmentSlotType)} */
  @SubscribeEvent(priority = EventPriority.LOW)
  static void chestplateToolUse(PlayerInteractEvent.RightClickEmpty event) {
    // not sure if anyone sets the result, but just in case listen to it so they can stop us running
    if (event.getCancellationResult() != InteractionResult.PASS) {
      return;
    }
    // figure out if we have a chestplate making us care
    Player player = event.getPlayer();
    ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
    if (!player.isSpectator() && chestplate.is(TinkerTags.Items.CHESTPLATES)) {
      // found an interaction, time to notify the server and run logic for the client
      InteractionHand hand = event.getHand();
      TinkerNetwork.getInstance().sendToServer(OnChestplateUsePacket.from(hand));
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
  static void preventDoubleInteract(InputEvent.ClickInputEvent event) {
    if (cancelNextOffhand) {
      cancelNextOffhand = false;
      if (event.getHand() == InteractionHand.OFF_HAND) {
        event.setCanceled(true);
        event.setSwingHand(false);
      }
    }
  }
}
