package slimeknights.tconstruct.smeltery.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.particle.FluidParticleData;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.component.TankBlockEntity;
import slimeknights.tconstruct.tools.entity.FluidEffectProjectile;

import static net.minecraft.world.level.block.DirectionalBlock.FACING;

/** Tank block entity which also shoots a fluid */
public class FluidCannonBlockEntity extends TankBlockEntity {
  private final IFluidCannon block;
  public FluidCannonBlockEntity(BlockPos pos, BlockState state) {
    this(pos, state, state.getBlock() instanceof IFluidCannon tank
                     ? tank
                     : TinkerSmeltery.searedFluidCannon.get());
  }

  /** Main constructor */
  public FluidCannonBlockEntity(BlockPos pos, BlockState state, IFluidCannon block) {
    this(TinkerSmeltery.fluidCannon.get(), pos, state, block);
  }

  protected FluidCannonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, IFluidCannon block) {
    super(type, pos, state, block);
    this.block = block;
  }

  /** Runs the cannon */
  public void shoot(BlockState state, ServerLevel level, RandomSource random) {
    FluidStack fluid = tank.getFluid();
    // if no fluid, just cancel out
    if (!fluid.isEmpty()) {
      FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
      if (recipe.hasEffects()) {
        float power = block.getPower();
        Direction facing = state.getValue(FACING);
        BlockPos target = worldPosition.relative(facing);
        BlockState targetState = level.getBlockState(target);

        // if it has block effects, try applying to the block directly in front
        if (recipe.hasBlockEffects() && !targetState.getShape(level, target).isEmpty()) {
          // if a block is in the way, apply directly to the block
          // this saves fluid compared to the projectile for many interactions, but also means no multi-hit behavior
          BlockHitResult hit = Util.createTraceResult(target, facing.getOpposite(), false);
          int consumed = recipe.applyToBlock(fluid, power, new FluidEffectContext.Block(level, null, null, hit), FluidAction.EXECUTE);
          if (consumed > 0) {
            Vec3 location = hit.getLocation();
            level.sendParticles(new FluidParticleData(TinkerCommons.fluidParticle.get(), fluid), location.x(), location.y(), location.z(), 10, 0.1, 0.2, 0.1, 0.2);
            fluid.shrink(consumed);
            tank.setFluid(fluid);
            tank.onContentsChanged();
            level.levelEvent(LevelEvent.PARTICLES_SHOOT, worldPosition, facing.get3DDataValue());
            return;
          }
        }


        // if we could not apply the fluid to the block, make a projectile provided its not blocked
        if (!targetState.isFaceSturdy(level, target, facing.getOpposite())) {
          // setup projectile
          FluidEffectProjectile projectile = new FluidEffectProjectile(level);
          int amount = Math.min(fluid.getAmount(), (int)(recipe.getAmount(fluid.getFluid()) * power));
          projectile.setFluid(new FluidStack(fluid, amount));
          projectile.setPower(power);
          projectile.setPos(worldPosition.getX() + 0.5 + (0.7 * facing.getStepX()),
                            worldPosition.getY() + 0.5 + (0.7 * facing.getStepY()),
                            worldPosition.getZ() + 0.5 + (0.7 * facing.getStepZ()));

          // setup projectile target - numbers based on arrow dispenser behavior
          projectile.shoot(facing.getStepX(), facing.getStepY() + 0.1f, facing.getStepZ(), block.getVelocity(), block.getInaccuracy());

          // finally, fire the projectile
          level.addFreshEntity(projectile);
          level.playSound(null, projectile.getX(), projectile.getY(), projectile.getZ(), SoundEvents.LLAMA_SPIT, SoundSource.BLOCKS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + 0.5F);

          fluid.shrink(amount);
          tank.setFluid(fluid);
          tank.onContentsChanged();
          level.levelEvent(LevelEvent.PARTICLES_SHOOT, worldPosition, facing.get3DDataValue());
          return;
        }
      }
    }

    // did not run a recipe, play the sound of failure
    level.levelEvent(LevelEvent.SOUND_DISPENSER_FAIL, worldPosition, 0);
    level.gameEvent(GameEvent.BLOCK_ACTIVATE, worldPosition, GameEvent.Context.of(state));
  }

  /** Interface for cannons to return power and capacity */
  public interface IFluidCannon extends ITankBlock {
    /** Gets the projectile power, determining how much fluid is used per shot. More fluid means a stronger projectile */
    float getPower();

    /** Gets the speed for the projectile to move */
    float getVelocity();

    /** Gets the amount the projectile may randomly stray from the target */
    float getInaccuracy();
  }
}
