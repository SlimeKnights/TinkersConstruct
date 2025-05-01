package slimeknights.tconstruct.smeltery.block.entity;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import slimeknights.mantle.fluid.FluidTransferHelper;
import slimeknights.mantle.inventory.SingleItemHandler;
import slimeknights.tconstruct.common.network.InventorySlotSyncPacket;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.particle.FluidParticleData;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.ITankBlockEntity.ITankInventoryBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.component.TankBlockEntity;
import slimeknights.tconstruct.tools.entity.FluidEffectProjectile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.world.level.block.DirectionalBlock.FACING;

/** Tank block entity which also shoots a fluid */
public class FluidCannonBlockEntity extends TankBlockEntity implements ITankInventoryBlockEntity {
  private final IFluidCannon block;
  @Getter
  private final FluidCannonItemHandler itemHandler = new FluidCannonItemHandler();
  private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> itemHandler);

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

  /** Called when a player interacts with the fluid cannon */
  public void interact(Player player, InteractionHand hand, boolean clickedTank) {
    // skip client side, and skip if the recipe already started
    if (level == null || level.isClientSide) {
      return;
    }
    // transfer fluid if clicked tank
    if (clickedTank) {
      if (!FluidTransferHelper.interactWithContainer(level, worldPosition, tank, player, hand).didTransfer()) {
        FluidTransferHelper.interactWithFilledBucket(level, worldPosition, tank, player, hand, getBlockState().getValue(FACING));
      }
    } else {
      // no fluid transfer? swap items around
      ItemStack held = player.getItemInHand(hand);
      ItemStack inventory = itemHandler.getStack();
      // inventory is empty means place item inside
      if (inventory.isEmpty()) {
        if (!held.isEmpty()) {
          // just place the whole stack
          itemHandler.setStack(held);
          player.setItemInHand(hand, ItemStack.EMPTY);
        }
        // if not holding anything, pick up the stack
      } else if (held.isEmpty()) {
        player.setItemInHand(hand, inventory);
        itemHandler.setStack(ItemStack.EMPTY);
      } else {
        inventory = inventory.copy();
        player.addItem(inventory);
        itemHandler.setStack(inventory);
      }
    }
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
          ItemStack stack = itemHandler.getStack().copy();
          int consumed = recipe.applyToBlock(fluid, power, FluidEffectContext.builder(level).stack(stack).block(hit), FluidAction.EXECUTE);
          if (consumed > 0) {
            Vec3 location = hit.getLocation();
            level.sendParticles(new FluidParticleData(TinkerCommons.fluidParticle.get(), fluid), location.x(), location.y(), location.z(), 10, 0.1, 0.2, 0.1, 0.2);
            fluid.shrink(consumed);
            tank.setFluid(fluid);
            tank.onContentsChanged();
            itemHandler.setStack(stack);
            level.levelEvent(LevelEvent.PARTICLES_SHOOT, worldPosition, facing.get3DDataValue());
            return;
          }
        }


        // if we could not apply the fluid to the block, make a projectile provided its not blocked
        if (!targetState.isFaceSturdy(level, target, facing.getOpposite())) {
          // setup projectile
          int amount = Math.min(fluid.getAmount(), (int)(recipe.getAmount(fluid.getFluid()) * power));
          FluidEffectProjectile projectile = new FluidEffectProjectile(level, worldPosition, facing, new FluidStack(fluid, amount), power);

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


  /* Inventory */
  private static final String TAG_ITEM = "item";

  @Nonnull
  @Override
  public <C> LazyOptional<C> getCapability(Capability<C> capability, @Nullable Direction facing) {
    if (capability == ForgeCapabilities.ITEM_HANDLER) {
      return itemCapability.cast();
    }
    return super.getCapability(capability, facing);
  }

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    itemCapability.invalidate();
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    tank.readFromNBT(tag.getCompound(NBTTags.TANK));
    if (tag.contains(TAG_ITEM, Tag.TAG_COMPOUND)) {
      itemHandler.readFromNBT(tag.getCompound(TAG_ITEM));
    }
  }

  @Override
  public void saveSynced(CompoundTag tag) {
    super.saveSynced(tag);
    tag.put(TAG_ITEM, itemHandler.writeToNBT());
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

  private class FluidCannonItemHandler extends SingleItemHandler<FluidCannonBlockEntity> {
    public FluidCannonItemHandler() {
      super(FluidCannonBlockEntity.this, 64);
    }

    @Override
    protected boolean isItemValid(ItemStack stack) {
      return true;
    }

    @Override
    public void setStack(ItemStack newStack) {
      Level world = parent.getLevel();
      boolean hasChange = world != null && !world.isClientSide && !ItemStack.matches(getStack(), newStack);
      super.setStack(newStack);
      if (hasChange) {
        BlockPos pos = parent.getBlockPos();
        TinkerNetwork.getInstance().sendToClientsAround(new InventorySlotSyncPacket(newStack, 0, pos), world, pos);
      }
    }
  }
}
