package slimeknights.tconstruct.tools.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
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
import slimeknights.tconstruct.tools.logic.InteractionHandler;
import slimeknights.tconstruct.tools.network.OnChestplateUsePacket;

/**
 * Client side interaction hooks
 */
@EventBusSubscriber(modid = TConstruct.MOD_ID, bus = Bus.FORGE, value = Dist.CLIENT)
public class ClientInteractionHandler {
  /** If true, next offhand interaction should be canceled, used since we cannot tell Forge to break the hand loop from the main hand */
  private static boolean cancelNextOffhand = false;

  /** Implements the client side of chestplate {@link slimeknights.tconstruct.library.modifiers.Modifier#onToolUse(slimeknights.tconstruct.library.tools.nbt.IModifierToolStack, int, net.minecraft.world.World, net.minecraft.entity.player.PlayerEntity, net.minecraft.util.Hand, net.minecraft.inventory.EquipmentSlotType)} */
  @SubscribeEvent
  static void chestplateToolUse(PlayerInteractEvent.RightClickEmpty event) {
    // figure out if we have a chestplate making us care
    PlayerEntity player = event.getPlayer();
    ItemStack chestplate = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
    if (!player.isSpectator() && TinkerTags.Items.CHESTPLATES.contains(chestplate.getItem())) {
      // found an interaction, time to notify the server and run logic for the client
      Hand hand = event.getHand();
      TinkerNetwork.getInstance().sendToServer(OnChestplateUsePacket.from(hand));
      ActionResultType result = InteractionHandler.onChestplateUse(player, chestplate, hand);
      if (result.isSuccessOrConsume()) {
        if (result.isSuccess()) {
          player.swingArm(hand);
        }
        Minecraft.getInstance().gameRenderer.itemRenderer.resetEquippedProgress(hand);
        if (hand == Hand.MAIN_HAND) {
          cancelNextOffhand = true;
        }
      }
    }
  }

  /** Prevents an empty right click from running the offhand */
  @SubscribeEvent(priority = EventPriority.HIGH)
  static void preventDoubleInteract(InputEvent.ClickInputEvent event) {
    if (cancelNextOffhand) {
      cancelNextOffhand = false;
      if (event.getHand() == Hand.OFF_HAND) {
        event.setCanceled(true);
        event.setSwingHand(false);
      }
    }
  }
}
