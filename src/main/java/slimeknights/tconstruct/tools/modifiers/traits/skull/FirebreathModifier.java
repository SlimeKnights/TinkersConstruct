package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;

public class FirebreathModifier extends SingleUseModifier implements IArmorInteractModifier {
  public FirebreathModifier() {
    super(0xFC9600);
  }

  @Override
  public boolean startArmorInteract(IModifierToolStack tool, int level, PlayerEntity player, EquipmentSlotType slot) {
    // stopped by water and by cooldown
    if (!player.isSneaking() && !player.isPotionActive(TinkerModifiers.fireballCooldownEffect.get()) && !player.isInWaterRainOrBubbleColumn()) {
      // if not creative, this costs a fire charge
      boolean hasFireball = true;
      if (!player.isCreative()) {
        hasFireball = false;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
          ItemStack stack = player.inventory.getStackInSlot(i);
          if (!stack.isEmpty() && TinkerTags.Items.FIREBALLS.contains(stack.getItem())) {
            hasFireball = true;
            if (!player.world.isRemote) {
              stack.shrink(1);
              if (stack.isEmpty()) {
                player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
              }
            }
            break;
          }
        }
      }
      // if we found a fireball, fire it
      if (hasFireball) {
        player.playSound(SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 2.0F, (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2F + 1.0F);
        if (!player.world.isRemote) {
          Vector3d lookVec = player.getLookVec().mul(2.0f, 2.0f, 2.0f);
          SmallFireballEntity fireball = new SmallFireballEntity(player.world, player, lookVec.x + player.getRNG().nextGaussian() / 16, lookVec.y, lookVec.z + player.getRNG().nextGaussian() / 16);
          fireball.setPosition(fireball.getPosX(), player.getPosYHeight(0.5D) + 0.5D, fireball.getPosZ());
          player.world.addEntity(fireball);
          TinkerModifiers.fireballCooldownEffect.get().apply(player, 100, 0, true);
        }
        return true;
      }
    }
    return false;
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, IArmorInteractModifier.class, this);
  }
}
