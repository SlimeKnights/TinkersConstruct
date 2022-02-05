package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.TankModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class BucketingModifier extends TankModifier {
  public BucketingModifier() {
    super(FluidAttributes.BUCKET_VOLUME);
  }

  @Override
  public int getPriority() {
    return 80; // little bit less so we get to add volatile data late
  }

  @Override
  public void onEquip(IToolStackView tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlot.CHEST) {
      ModifierUtil.addTotalArmorModifierLevel(tool, context, TinkerDataKeys.SHOW_EMPTY_OFFHAND, 1, true);
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlot.CHEST) {
      ModifierUtil.addTotalArmorModifierLevel(tool, context, TinkerDataKeys.SHOW_EMPTY_OFFHAND, -1, true);
    }
  }

  /**
   * Checks if the block is unable to contain fluid
   * @param world  Level
   * @param pos    Position to try
   * @param state  State
   * @param fluid  Fluid to place
   * @return  True if the block is unable to contain fluid, false if it can contain fluid
   */
  private static boolean cannotContainFluid(Level world, BlockPos pos, BlockState state, Fluid fluid) {
    Block block = state.getBlock();
    return !state.canBeReplaced(fluid) && !(block instanceof LiquidBlockContainer container && container.canPlaceLiquid(world, pos, state, fluid));
  }

  @Override
  public InteractionResult beforeBlockUse(IToolStackView tool, int level, UseOnContext context, EquipmentSlot slot) {
    if (slot.getType() != EquipmentSlot.Type.ARMOR) {
      return InteractionResult.PASS;
    }

    Level world = context.getLevel();
    BlockPos target = context.getClickedPos();
    // must have a TE that has a fluid handler capability
    BlockEntity te = world.getBlockEntity(target);
    if (te == null) {
      return InteractionResult.PASS;
    }
    Direction face = context.getClickedFace();
    LazyOptional<IFluidHandler> capability = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face);
    if (!capability.isPresent()) {
      return InteractionResult.PASS;
    }

    // only the server needs to deal with actually handling stuff
    if (!world.isClientSide) {
      Player player = context.getPlayer();
      boolean sneaking = player != null && player.isShiftKeyDown();
      capability.ifPresent(cap -> {
        FluidStack fluidStack = getFluid(tool);
        // sneaking fills, not sneak drains
        SoundEvent sound = null;
        if (sneaking) {
          // must have something to fill
          if (!fluidStack.isEmpty()) {
            int added = cap.fill(fluidStack, FluidAction.EXECUTE);
            if (added > 0) {
              sound = fluidStack.getFluid().getAttributes().getEmptySound(fluidStack);
              fluidStack.shrink(added);
              setFluid(tool, fluidStack);
            }
          }
          // if nothing currently, will drain whatever
        } else if (fluidStack.isEmpty()) {
          FluidStack drained = cap.drain(getCapacity(tool), FluidAction.EXECUTE);
          if (!drained.isEmpty()) {
            setFluid(tool, drained);
            sound = drained.getFluid().getAttributes().getFillSound(drained);
          }
        } else {
          // filter drained to be the same as the current fluid
          FluidStack drained = cap.drain(new FluidStack(fluidStack, getCapacity(tool) - fluidStack.getAmount()), FluidAction.EXECUTE);
          if (!drained.isEmpty() && drained.isFluidEqual(fluidStack)) {
            fluidStack.grow(drained.getAmount());
            setFluid(tool, fluidStack);
            sound = drained.getFluid().getAttributes().getFillSound(drained);
          }
        }
        if (sound != null) {
          world.playSound(null, target, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
      });
    }
    return InteractionResult.sidedSuccess(world.isClientSide);
  }

  @Override
  public InteractionResult afterBlockUse(IToolStackView tool, int level, UseOnContext context, EquipmentSlot slotType) {
    // only place fluid if sneaking, we contain at least a bucket, and its a block
    Player player = context.getPlayer();
    if (player == null || !player.isShiftKeyDown()) {
      return InteractionResult.PASS;
    }
    FluidStack fluidStack = getFluid(tool);
    if (fluidStack.getAmount() < FluidAttributes.BUCKET_VOLUME) {
      return InteractionResult.PASS;
    }
    Fluid fluid = fluidStack.getFluid();
    if (!(fluid instanceof FlowingFluid)) {
      return InteractionResult.PASS;
    }

    // can we interact with the position
    Direction face = context.getClickedFace();
    Level world = context.getLevel();
    BlockPos target = context.getClickedPos();
    BlockPos offset = target.relative(face);
    if (!world.mayInteract(player, target) || !player.mayUseItemAt(offset, face, context.getItemInHand())) {
      return InteractionResult.PASS;
    }

    // if the block cannot be placed at the current location, try placing at the neighbor
    BlockState existing = world.getBlockState(target);
    if (cannotContainFluid(world, target, existing, fluidStack.getFluid())) {
      target = offset;
      existing = world.getBlockState(target);
      if (cannotContainFluid(world, target, existing, fluidStack.getFluid())) {
        return InteractionResult.PASS;
      }
    }

    // if water, evaporate
    boolean placed = false;
    if (world.dimensionType().ultraWarm() && fluid.is(FluidTags.WATER)) {
      world.playSound(player, target, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
      for(int l = 0; l < 8; ++l) {
        world.addParticle(ParticleTypes.LARGE_SMOKE, target.getX() + Math.random(), target.getY() + Math.random(), target.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
      }
      placed = true;
    } else if (existing.canBeReplaced(fluid)) {
      // if its a liquid container, we should have validated it already
      if (!world.isClientSide && !existing.getMaterial().isLiquid()) {
        world.destroyBlock(target, true);
      }
      if (world.setBlockAndUpdate(target, fluid.defaultFluidState().createLegacyBlock()) || existing.getFluidState().isSource()) {
        world.playSound(null, target, fluid.getAttributes().getEmptySound(fluidStack), SoundSource.BLOCKS, 1.0F, 1.0F);
        placed = true;
      }
    } else if (existing.getBlock() instanceof LiquidBlockContainer container) {
      // if not replaceable, it must be a liquid container
      container.placeLiquid(world, target, existing, ((FlowingFluid)fluid).getSource(false));
      world.playSound(null, target, fluid.getAttributes().getEmptySound(fluidStack), SoundSource.BLOCKS, 1.0F, 1.0F);
      placed = true;
    }

    // if we placed something, consume fluid
    if (placed) {
      drain(tool, fluidStack, FluidAttributes.BUCKET_VOLUME);
      return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, int level, Level world, Player player, InteractionHand hand, EquipmentSlot slotType) {
    if (player.isCrouching()) {
      return InteractionResult.PASS;
    }

    // need at least a bucket worth of empty space
    FluidStack fluidStack = getFluid(tool);
    if (getCapacity(tool) - fluidStack.getAmount() < FluidAttributes.BUCKET_VOLUME) {
      return InteractionResult.PASS;
    }
    // have to trace again to find the fluid, ensure we can edit the position
    BlockHitResult trace = ModifiableItem.blockRayTrace(world, player, ClipContext.Fluid.SOURCE_ONLY);
    if (trace.getType() != Type.BLOCK) {
      return InteractionResult.PASS;
    }
    Direction face = trace.getDirection();
    BlockPos target = trace.getBlockPos();
    BlockPos offset = target.relative(face);
    if (!world.mayInteract(player, target) || !player.mayUseItemAt(offset, face, player.getItemBySlot(slotType))) {
      return InteractionResult.PASS;
    }
    // try to find a fluid here
    FluidState fluidState = world.getFluidState(target);
    Fluid currentFluid = fluidStack.getFluid();
    if (fluidState.isEmpty() || (!fluidStack.isEmpty() && !currentFluid.isSame(fluidState.getType()))) {
      return InteractionResult.PASS;
    }
    // finally, pickup the fluid
    BlockState state = world.getBlockState(target);
    if (state.getBlock() instanceof BucketPickup bucketPickup) {
      // TODO: not sure how to deal with this API change, this current method means we delete snow
      //Fluid pickedUpFluid = bucketPickup.takeLiquid(world, target, state);
      ItemStack bucket = bucketPickup.pickupBlock(world, target, state);
      if (!bucket.isEmpty() && bucket.getItem() instanceof BucketItem bucketItem) {
        Fluid pickedUpFluid = bucketItem.getFluid();
        if (pickedUpFluid != Fluids.EMPTY) {
          player.playSound(pickedUpFluid.getAttributes().getFillSound(fluidStack), 1.0F, 1.0F);
          // set the fluid if empty, increase the fluid if filled
          if (!world.isClientSide) {
            if (fluidStack.isEmpty()) {
              setFluid(tool, new FluidStack(pickedUpFluid, FluidAttributes.BUCKET_VOLUME));
            } else if (pickedUpFluid == currentFluid) {
              fluidStack.grow(FluidAttributes.BUCKET_VOLUME);
              setFluid(tool, fluidStack);
            } else {
              TConstruct.LOG.error("Picked up a fluid {} that does not match the current fluid state {}, this should not happen", pickedUpFluid, fluidState.getType());
            }
          }
          return InteractionResult.SUCCESS;
        }
      }
    }
    return InteractionResult.PASS;
  }
}
