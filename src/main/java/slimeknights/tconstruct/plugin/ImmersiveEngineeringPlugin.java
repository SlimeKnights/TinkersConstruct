package slimeknights.tconstruct.plugin;

import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler.ChemthrowerEffect;
import blusunrize.immersiveengineering.common.entities.ChemthrowerShotEntity;
import blusunrize.immersiveengineering.common.entities.illager.EngineerIllager;
import blusunrize.immersiveengineering.common.register.IEEntityTypes;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.common.TinkerTags.Fluids;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

import javax.annotation.Nullable;
import java.util.List;

/** Event handlers to run when Immersive Engineering is present */
public class ImmersiveEngineeringPlugin {
  @SubscribeEvent
  public void commonSetup(FMLCommonSetupEvent event) {
    ChemthrowerHandler.registerEffect(Fluids.CHEMTHROWER_BOTH_EFFECTS, new FluidEffectChemThrowerEffect(true, true));
    ChemthrowerHandler.registerEffect(Fluids.CHEMTHROWER_BLOCK_EFFECTS, new FluidEffectChemThrowerEffect(true, false));
    ChemthrowerHandler.registerEffect(Fluids.CHEMTHROWER_ENTITY_EFFECTS, new FluidEffectChemThrowerEffect(false, true));

    // register shield disabling predicates
    event.enqueueWork(() -> {
      ModifierUtil.registerShieldDisabler(entity -> {
        if (entity instanceof EngineerIllager illager && illager.isBlocking()) {
          illager.disableShield();
        }
      }, IEEntityTypes.BULWARK.get(), IEEntityTypes.COMMANDO.get(), IEEntityTypes.FUSILIER.get());
    });
  }

  /** Chem thrower effect that redirects to our fluid effect API */
  @RequiredArgsConstructor
  private static class FluidEffectChemThrowerEffect extends ChemthrowerEffect {
    private final boolean runBlock;
    private final boolean runEntity;

    @Override
    public void applyToBlock(Level world, HitResult mop, @Nullable Player shooter, ItemStack thrower, Fluid fluid) {}

    @Override
    public void applyToEntity(LivingEntity target, @Nullable Player shooter, ItemStack thrower, Fluid fluid) {}

    /** Consumes the fluid projectiles used for this action */
    private static void consumeProjectiles(List<ChemthrowerShotEntity> projectiles, float consumed, double projectileValue) {
      if (consumed > 0) {
        for (ChemthrowerShotEntity projectile : projectiles) {
          projectile.discard();
          consumed -= projectileValue;
          if (consumed <= 0) {
            break;
          }
        }
      }
    }

    @Override
    public void applyToEntity(LivingEntity target, @Nullable Player shooter, ItemStack thrower, FluidStack fluid) {
      // skip for the block tag
      if (!runEntity) {
        return;
      }
      // must have entity effects; prevents bad tag usage
      FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
      if (!recipe.hasEntityEffects()) {
        return;
      }
      // technically this might not include ourselves, but otherwise we risk applying ourselves twice
      List<ChemthrowerShotEntity> projectiles = target.level().getEntitiesOfClass(ChemthrowerShotEntity.class, target.getBoundingBox());
      if (projectiles.isEmpty()) {
        return;
      }
      // each projectile is worth the value in the config, our system automatically scales projectiles
      double projectileValue = Config.COMMON.chemthrowerShotValue.get();
      int amount = (int) (projectileValue * projectiles.size());
      if (amount > 0) {
        // run the effect and consume projectiles
        float consumed = recipe.applyToEntity(new FluidStack(fluid, amount), 1,
          FluidEffectContext.builder(target.level()).user(shooter).stack(thrower).target(target),
          FluidAction.EXECUTE);
        consumeProjectiles(projectiles, consumed, projectileValue);
      }
    }

    /** Makes an AABB from a start and end position. Same as {@link AABB#AABB(BlockPos,BlockPos)}, except adds 1 to the end postion. */
    private static AABB makeAABB(BlockPos start, BlockPos end) {
      return new AABB(start.getX(), start.getY(), start.getZ(), end.getX() + 1, end.getY() + 1, end.getZ() + 1);
    }

    @Override
    public void applyToBlock(Level world, HitResult mop, @Nullable Player shooter, ItemStack thrower, FluidStack fluid) {
      // skip for the entity tag or wrongly passed hit types
      if (!runBlock || mop.getType() != Type.BLOCK) {
        return;
      }
      // must have block effects; prevents bad tag usage
      FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
      if (!recipe.hasBlockEffects()) {
        return;
      }
      // collect all projectiles within the block and the block on the side hit, we will borrow fluid from all of them
      BlockHitResult hitResult = (BlockHitResult) mop;
      Direction direction = hitResult.getDirection();
      BlockPos pos = hitResult.getBlockPos();
      BlockPos offset = pos.relative(direction);
      AABB aabb = direction.getAxisDirection() == AxisDirection.NEGATIVE ? makeAABB(offset, pos) : makeAABB(pos, offset);
      List<ChemthrowerShotEntity> projectiles = world.getEntitiesOfClass(ChemthrowerShotEntity.class, aabb);
      // technically this might not include ourselves, but its important that we can delete projectiles that are used for the effect
      // otherwise many of our effects can go out of control, namely block breaking
      if (projectiles.isEmpty()) {
        return;
      }
      // each projectile is worth the value in the config, our system automatically scales projectiles
      double projectileValue = Config.COMMON.chemthrowerShotValue.get();
      int amount = (int) (projectileValue * projectiles.size());
      if (amount > 0) {
        // run the effect and consume projectiles
        float consumed = recipe.applyToBlock(new FluidStack(fluid, amount), 1,
          FluidEffectContext.builder(world).user(shooter).stack(thrower).block(hitResult),
          FluidAction.EXECUTE);
        consumeProjectiles(projectiles, consumed, projectileValue);
      }
    }
  }
}
