package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class FirebreathModifier extends NoLevelsModifier implements KeybindInteractModifierHook {
  @Override
  public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
    // stopped by water and by cooldown
    if (!player.isShiftKeyDown() && !player.hasEffect(TinkerModifiers.fireballCooldownEffect.get()) && !player.isInWaterRainOrBubble()) {
      // if not creative, this costs a fire charge
      boolean hasFireball = true;
      if (!player.isCreative()) {
        hasFireball = false;
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
          ItemStack stack = inventory.getItem(i);
          if (!stack.isEmpty() && stack.is(TinkerTags.Items.FIREBALLS)) {
            hasFireball = true;
            if (!player.level.isClientSide) {
              stack.shrink(1);
              if (stack.isEmpty()) {
                inventory.setItem(i, ItemStack.EMPTY);
              }
            }
            break;
          }
        }
      }
      // if we found a fireball, fire it
      if (hasFireball) {
        player.playNotifySound(SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 2.0F, (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.0F);
        if (!player.level.isClientSide) {
          Vec3 lookVec = player.getLookAngle().multiply(2.0f, 2.0f, 2.0f);
          SmallFireball fireball = new SmallFireball(player.level, player, lookVec.x + player.getRandom().nextGaussian() / 16, lookVec.y, lookVec.z + player.getRandom().nextGaussian() / 16);
          fireball.setPos(fireball.getX(), player.getY(0.5D) + 0.5D, fireball.getZ());
          player.level.addFreshEntity(fireball);
          TinkerModifiers.fireballCooldownEffect.get().apply(player, 100, 0, true);
        }
        return true;
      }
    }
    return false;
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.ARMOR_INTERACT);
  }
}
