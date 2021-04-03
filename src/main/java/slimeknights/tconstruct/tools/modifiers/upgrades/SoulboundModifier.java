package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Iterator;

public class SoulboundModifier extends SingleUseModifier {
  public SoulboundModifier() {
    super(0xD1A75D);
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
    if (!entity.getEntityWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && entity instanceof PlayerEntity && !(entity instanceof FakePlayer)) {
      PlayerEntity player = (PlayerEntity) entity;
      Iterator<ItemEntity> iter = event.getDrops().iterator();
      while (iter.hasNext()) {
        ItemEntity itemEntity = iter.next();
        ItemStack stack = itemEntity.getItem();
        // find tools with soulbound
        if (TinkerTags.Items.MODIFIABLE.contains(stack.getItem())) {
          ToolStack tool = ToolStack.from(stack);
          if (tool.getModifierLevel(this) > 0) {
            player.inventory.addItemStackToInventory(stack);
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
    PlayerEntity original = event.getOriginal();
    PlayerEntity clone = event.getPlayer();
    // inventory already copied
    if (clone.getEntityWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || original.isSpectator()) {
      return;
    }
    // find the soulbound items
    for(int i = 0; i < original.inventory.getSizeInventory(); i++) {
      // find tools with soulbound
      ItemStack stack = original.inventory.getStackInSlot(i);
      if (!stack.isEmpty() && TinkerTags.Items.MODIFIABLE.contains(stack.getItem())) {
        ToolStack tool = ToolStack.from(stack);
        if (tool.getModifierLevel(this) > 0) {
          clone.inventory.addItemStackToInventory(stack);
        }
      }
    }
  }
}
