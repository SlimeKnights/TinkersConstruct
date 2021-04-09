package slimeknights.tconstruct.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.shared.AchievementEvents;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;

@Mixin(CraftingResultSlot.class)
public class MixinCraftingResultSlot {

  @Shadow
  @Final
  private PlayerEntity player;

  @Inject(method = "onCrafted(Lnet/minecraft/item/ItemStack;)V", at = @At(value = "HEAD", target = "Lnet/minecraft/item/ItemStack;onCraft(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;I)V"))
  public void onCrafted(ItemStack stack, CallbackInfo ci) {
    AchievementEvents.onCraft(player, stack);
  }
}
