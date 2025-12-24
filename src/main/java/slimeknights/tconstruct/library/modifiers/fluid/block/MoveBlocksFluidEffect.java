package slimeknights.tconstruct.library.modifiers.fluid.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.tools.network.PushBlockRowPacket;

import static net.minecraft.world.level.block.piston.PistonStructureResolver.MAX_PUSH_DEPTH;

/** Fluid effect to move a set of blocks */
public record MoveBlocksFluidEffect(boolean push, SoundEvent sound) implements FluidEffect<FluidEffectContext.Block> {
  public static final RecordLoadable<MoveBlocksFluidEffect> LOADER = RecordLoadable.create(
    BooleanLoadable.INSTANCE.requiredField("push", MoveBlocksFluidEffect::push),
    Loadables.SOUND_EVENT.requiredField("sound", MoveBlocksFluidEffect::sound),
    MoveBlocksFluidEffect::new);

  public static MoveBlocksFluidEffect push(SoundEvent sound) {
    return new MoveBlocksFluidEffect(true, sound);
  }

  public static MoveBlocksFluidEffect pull(SoundEvent sound) {
    return new MoveBlocksFluidEffect(false, sound);
  }

  @Override
  public RecordLoadable<MoveBlocksFluidEffect> getLoader() {
    return LOADER;
  }

  /** Destroys the block marked for destroying */
  private static void destroyBlock(Level level, BlockPos pos, BlockState state) {
    BlockEntity targetBE = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
    Block.dropResources(state, level, pos, targetBE);
    level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS);
    level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(state));
    if (!state.is(Blocks.FIRE)) {
      level.addDestroyBlockEffect(pos, state);
    }
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, FluidEffectContext.Block context, FluidAction action) {
    if (level.isFull()) {
      if (context.breakRestricted()) {
        return 0;
      }
      // first step is to find how many blocks we can move
      BlockPos pos = context.getBlockPos();
      Level world = context.getLevel();
      BlockState originalState = world.getBlockState(pos);
      // shouldn't ever hit air, but just in case
      if (originalState.isAir()) {
        return 0;
      }
      // if the first block breaks on push, just break it and return
      if (originalState.getPistonPushReaction() == PushReaction.DESTROY) {
        if (originalState.getBlock() instanceof LiquidBlock) {
          return 0;
        }
        if (action.execute()) {
          destroyBlock(world, pos, originalState);
          // add breaking particles and sound
          world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(originalState));
          // update neighbors that the block was destroyed
          BlockState airState = Blocks.AIR.defaultBlockState();
          originalState.updateIndirectNeighbourShapes(world, pos, Block.UPDATE_CLIENTS);
          airState.updateNeighbourShapes(world, pos, Block.UPDATE_CLIENTS);
          airState.updateIndirectNeighbourShapes(world, pos, Block.UPDATE_CLIENTS);
        }
        return 1;
      }

      // figure out which way we go
      Direction facing = context.getHitResult().getDirection().getOpposite();
      Direction direction = facing;
      if (!push) {
        direction = direction.getOpposite();
      }
      // even though isPushable will eventually check for block entities, some mods mixin to remove that check
      // since we don't have moving block entity logic, just add back tne (sometimes redundant) check
      if (originalState.hasBlockEntity() || !PistonBaseBlock.isPushable(originalState, world, pos, direction, false, facing)) {
        return 0;
      }

      // figure out how many blocks can move
      BlockState state;
      int moving = 0;
      do {
        moving++;
        // max depth means we never found an open spot, give up
        if (moving > MAX_PUSH_DEPTH) {
          return 0;
        }
        // air mean we found our destination
        BlockPos target = pos.relative(direction, moving);
        state = world.getBlockState(target);
        if (!PistonBaseBlock.isPushable(state, world, target, direction, true, facing)) {
          return 0;
        }
        // if we see destroy, we also found our destination
      } while (!state.isAir() && state.getPistonPushReaction() != PushReaction.DESTROY);

      // if execute, we actually get to move the blocks
      if (action.execute()) {
        moveBlocks(world, pos, originalState, facing, direction, moving);
        TinkerNetwork.getInstance().sendToClientsAround(new PushBlockRowPacket(pos, direction, push, moving), world, pos);
        world.playSound(null, pos, Sounds.SLIME_SLING.getSound(), SoundSource.BLOCKS, 0.5F, world.random.nextFloat() * (push ? 0.25F : 0.15f) + 0.6F);
      }
      return 1;
    }
    return 0;
  }

  /** Called to move a line of blocks at the given location */
  public static void moveBlocks(Level world, BlockPos pos, BlockState originalState, Direction facing, Direction direction, int moving) {
    // push reaction will be destroyed if we need to destroy the final block. otherwise it will be normal/push_only

    // get a list of all positions to consider,
    BlockPos[] targets = new BlockPos[moving+1];
    for (int i = 0; i <= moving; i++) {
      targets[i] = pos.relative(direction, i);
    }

    // remove the target block
    BlockState removedState = world.getBlockState(targets[moving]);
    if (!removedState.isAir()) {
      destroyBlock(world, targets[moving], removedState);
    }

    // push all the blocks
    Block[] blocksToUpdate = new Block[moving];
    for (int i = moving - 1; i >= 0; i--) {
      BlockState pushedState = world.getBlockState(targets[i]);
      blocksToUpdate[i] = pushedState.getBlock();
      BlockState movingBlockState = Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, facing);
      world.setBlock(targets[i+1], movingBlockState, Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_NONE);
      world.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(targets[i+1], movingBlockState, pushedState, facing, facing == direction, false));
    }

    // replace the original position with air
    BlockState airState = Blocks.AIR.defaultBlockState();
    world.setBlock(pos, airState, Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS);
    originalState.updateIndirectNeighbourShapes(world, pos, Block.UPDATE_CLIENTS);
    airState.updateNeighbourShapes(world, pos, Block.UPDATE_CLIENTS);
    airState.updateIndirectNeighbourShapes(world, pos, Block.UPDATE_CLIENTS);

    // update the removed state
    if (!removedState.isAir()) {
      removedState.updateIndirectNeighbourShapes(world, targets[moving], Block.UPDATE_CLIENTS);
      world.updateNeighborsAt(targets[moving], removedState.getBlock());
    }
    // update all piston push blocks
    for (int i = moving - 1; i >= 0; i--) {
      world.updateNeighborsAt(targets[i], blocksToUpdate[i]);
    }
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    return Component.translatable(FluidEffect.getTranslationKey(getLoader()) + (push ? ".push" : ".pull"));
  }
}
