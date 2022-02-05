package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.impl.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Iterator;

public class SoulboundModifier extends SingleUseModifier {
  public SoulboundModifier() {
    // high priority so we do it before other possibly death-inventory-modifying mods
    MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onPlayerDeath);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onPlayerClone);
  }

  /** Called when the player dies to store the item in the original inventory */
  private void onPlayerDeath(LivingDropsEvent event) {
    if (event.isCanceled()) {
      return;
    }
    // only care about real players with keep inventory off
    LivingEntity entity = event.getEntityLiving();
    if (!entity.getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && entity instanceof Player player && !(entity instanceof FakePlayer)) {
      Iterator<ItemEntity> iter = event.getDrops().iterator();
      Inventory inventory = player.getInventory();
      while (iter.hasNext()) {
        ItemEntity itemEntity = iter.next();
        ItemStack stack = itemEntity.getItem();
        // find tools with soulbound
        if (TinkerTags.Items.MODIFIABLE.contains(stack.getItem())) {
          ToolStack tool = ToolStack.from(stack);
          if (tool.getModifierLevel(this) > 0) {
            inventory.add(stack);
            iter.remove();
          }
        }
      }
    }
  }

  /** Called when the new player is created to fetch the soulbound item from the old */
  private void onPlayerClone(PlayerEvent.Clone event) {
    if (!event.isWasDeath()) {
      return;
    }
    Player original = event.getOriginal();
    Player clone = event.getPlayer();
    // inventory already copied
    if (clone.getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || original.isSpectator()) {
      return;
    }
    // find the soulbound items
    Inventory originalInv = original.getInventory();
    Inventory cloneInv = clone.getInventory();
    for(int i = 0; i < originalInv.getContainerSize(); i++) {
      // find tools with soulbound
      ItemStack stack = originalInv.getItem(i);
      if (!stack.isEmpty() && TinkerTags.Items.MODIFIABLE.contains(stack.getItem())) {
        ToolStack tool = ToolStack.from(stack);
        if (tool.getModifierLevel(this) > 0) {
          cloneInv.add(stack);
        }
      }
    }
  }
}
