package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class SoulboundModifier extends SingleUseModifier {
  public SoulboundModifier() {
    super(0xD1A75D);
    // high priority so we do it before other possibly death-inventory-modifying mods

    //TODO: PORTING
//    MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onPlayerDeath);
    ServerPlayerEvents.COPY_FROM.register(this::onPlayerClone);
  }

  /** Called when the player dies to store the item in the original inventory */
/*  private void onPlayerDeath(LivingDropsEvent event) {
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
        ItemStack stack = itemEntity.getStack();
        // find tools with soulbound
        if (TinkerTags.Items.MODIFIABLE.contains(stack.getItem())) {
          ToolStack tool = ToolStack.from(stack);
          if (tool.getModifierLevel(this) > 0) {
            player.inventory.insertStack(stack);
            iter.remove();
          }
        }
      }
    }
  }*/

  /** Called when the new player is created to fetch the soulbound item from the old */
  private void onPlayerClone(ServerPlayerEntity original, ServerPlayerEntity clone, boolean alive) {
    if (alive) {
      return;
    }
    // inventory already copied
    if (clone.getEntityWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || original.isSpectator()) {
      return;
    }
    // find the soulbound items
    for(int i = 0; i < original.inventory.size(); i++) {
      // find tools with soulbound
      ItemStack stack = original.inventory.getStack(i);
      if (!stack.isEmpty() && TinkerTags.Items.MODIFIABLE.contains(stack.getItem())) {
        ToolStack tool = ToolStack.from(stack);
        if (tool.getModifierLevel(this) > 0) {
          clone.inventory.insertStack(stack);
        }
      }
    }
  }
}
