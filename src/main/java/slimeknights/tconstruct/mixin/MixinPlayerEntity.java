package slimeknights.tconstruct.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

  protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
    super(entityType, world);
  }

  @Inject(method = "tick", at = @At("TAIL"))
  private void tick(CallbackInfo ci) {
    for(SlimeBounceHandler handler : SlimeBounceHandler.getBouncingEntities().values()) {
      handler.playerTickPost((PlayerEntity) (Object) this);
    }
  }

  @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"))
  private void modifierBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
    ItemStack stack = this.getMainHandStack();
    if (!TinkerTags.Items.HARVEST.contains(stack.getItem())) {
      return;
    }
    ToolStack tool = ToolStack.from(stack);
    if (!tool.isBroken()) {
      for (ModifierEntry entry : tool.getModifierList()) {
        entry.getModifier().onBreakSpeed(tool, entry.getLevel(), (PlayerEntity)(Object)this);
      }
    }
  }
}
