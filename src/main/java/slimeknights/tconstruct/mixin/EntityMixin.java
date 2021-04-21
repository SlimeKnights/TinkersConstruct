package slimeknights.tconstruct.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

  @Shadow
  public World world;

  @Shadow
  public abstract BlockPos getBlockPos();

  boolean doNormalCheck;

  @Redirect(method = "updateMovementInFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"))
  private boolean isIn(FluidState state, Tag<Fluid> tag) {
    if (doNormalCheck) {
      return state.isIn(tag);
    }
    if (!state.getFluid().isIn(tag) && !tag.equals(FluidTags.LAVA) && state.getFluid() != Fluids.EMPTY) {
      return !state.isEmpty();
    } else {
      return state.isIn(tag);
    }
  }

  @Inject(method = "isWet", at = @At("HEAD"), cancellable = true)
  private void isRaining(CallbackInfoReturnable<Boolean> cir) {
    cir.setReturnValue(world.isRaining() || this.world.getFluidState(getBlockPos()).isIn(FluidTags.WATER) && world.getBlockState(getBlockPos()).getBlock() != Blocks.AIR);
  }
}
