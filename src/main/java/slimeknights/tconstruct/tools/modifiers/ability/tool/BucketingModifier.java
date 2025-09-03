package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.fluid.FluidTransferHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ShowOffhandModule;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Objects;

import static slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;

public class BucketingModifier extends Modifier implements BlockInteractionModifierHook, GeneralInteractionModifierHook, EquipmentChangeModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ToolTankHelper.TANK_HANDLER);
    hookBuilder.addModule(StatBoostModule.add(ToolTankHelper.CAPACITY_STAT).flat(FluidType.BUCKET_VOLUME));
    hookBuilder.addModule(ToolTankHelper.TANK_HANDLER);
    hookBuilder.addHook(this, ModifierHooks.BLOCK_INTERACT, ModifierHooks.GENERAL_INTERACT);
    hookBuilder.addModule(ShowOffhandModule.ALLOW_BROKEN);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
    return InteractionSource.formatModifierName(tool, this, super.getDisplayName(tool, entry, access));
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
  public InteractionResult beforeBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
    if (source != InteractionSource.ARMOR) {
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
    LazyOptional<IFluidHandler> capability = te.getCapability(ForgeCapabilities.FLUID_HANDLER, face);
    if (!capability.isPresent()) {
      return InteractionResult.PASS;
    }

    // only the server needs to deal with actually handling stuff
    if (!world.isClientSide) {
      Player player = context.getPlayer();
      boolean sneaking = player != null && player.isShiftKeyDown();
      capability.ifPresent(cap -> {
        FluidStack fluidStack = TANK_HELPER.getFluid(tool);
        // sneaking fills, not sneak drains
        SoundEvent sound = null;
        if (sneaking) {
          // must have something to fill
          if (!fluidStack.isEmpty()) {
            int added = cap.fill(fluidStack, FluidAction.EXECUTE);
            if (added > 0) {
              sound = FluidTransferHelper.getEmptySound(fluidStack);
              fluidStack.shrink(added);
              TANK_HELPER.setFluid(tool, fluidStack);
            }
          }
          // if nothing currently, will drain whatever
        } else if (fluidStack.isEmpty()) {
          FluidStack drained = cap.drain(TANK_HELPER.getCapacity(tool), FluidAction.EXECUTE);
          if (!drained.isEmpty()) {
            TANK_HELPER.setFluid(tool, drained);
            sound = FluidTransferHelper.getFillSound(fluidStack);
          }
        } else {
          // filter drained to be the same as the current fluid
          FluidStack drained = cap.drain(new FluidStack(fluidStack, TANK_HELPER.getCapacity(tool) - fluidStack.getAmount()), FluidAction.EXECUTE);
          if (!drained.isEmpty() && drained.isFluidEqual(fluidStack)) {
            fluidStack.grow(drained.getAmount());
            TANK_HELPER.setFluid(tool, fluidStack);
            sound = FluidTransferHelper.getFillSound(fluidStack);
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
  public InteractionResult afterBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
    if (!tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      return InteractionResult.PASS;
    }
    // only place fluid if sneaking, we contain at least a bucket, and its a block
    Player player = context.getPlayer();
    if (player == null || !player.isShiftKeyDown()) {
      return InteractionResult.PASS;
    }
    FluidStack fluidStack = TANK_HELPER.getFluid(tool);
    if (fluidStack.getAmount() < FluidType.BUCKET_VOLUME) {
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
      if (!world.isClientSide && !existing.liquid()) {
        world.destroyBlock(target, true);
      }
      if (world.setBlockAndUpdate(target, fluid.defaultFluidState().createLegacyBlock()) || existing.getFluidState().isSource()) {
        world.playSound(null, target, FluidTransferHelper.getEmptySound(fluidStack), SoundSource.BLOCKS, 1.0F, 1.0F);
        placed = true;
      }
    } else if (existing.getBlock() instanceof LiquidBlockContainer container) {
      // if not replaceable, it must be a liquid container
      container.placeLiquid(world, target, existing, ((FlowingFluid)fluid).getSource(false));
      world.playSound(null, target, FluidTransferHelper.getEmptySound(fluidStack), SoundSource.BLOCKS, 1.0F, 1.0F);
      placed = true;
    }

    // if we placed something, consume fluid
    if (placed) {
      fluidStack.shrink(FluidType.BUCKET_VOLUME);
      TANK_HELPER.setFluid(tool, fluidStack);
      return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (player.isCrouching() || !tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      return InteractionResult.PASS;
    }

    // need at least a bucket worth of empty space
    FluidStack fluidStack = TANK_HELPER.getFluid(tool);
    if (TANK_HELPER.getCapacity(tool) - fluidStack.getAmount() < FluidType.BUCKET_VOLUME) {
      return InteractionResult.PASS;
    }
    // have to trace again to find the fluid, ensure we can edit the position
    Level world = player.level();
    BlockHitResult trace = ModifiableItem.blockRayTrace(world, player, ClipContext.Fluid.SOURCE_ONLY);
    if (trace.getType() != Type.BLOCK) {
      return InteractionResult.PASS;
    }
    Direction face = trace.getDirection();
    BlockPos target = trace.getBlockPos();
    BlockPos offset = target.relative(face);
    if (!world.mayInteract(player, target) || !player.mayUseItemAt(offset, face, player.getItemBySlot(source.getSlot(hand)))) {
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
          player.playSound(Objects.requireNonNullElse(pickedUpFluid.getFluidType().getSound(SoundActions.BUCKET_FILL), SoundEvents.BUCKET_FILL), 1.0F, 1.0F);
          // set the fluid if empty, increase the fluid if filled
          if (!world.isClientSide) {
            if (fluidStack.isEmpty()) {
              TANK_HELPER.setFluid(tool, new FluidStack(pickedUpFluid, FluidType.BUCKET_VOLUME));
            } else if (pickedUpFluid == currentFluid) {
              fluidStack.grow(FluidType.BUCKET_VOLUME);
              TANK_HELPER.setFluid(tool, fluidStack);
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
