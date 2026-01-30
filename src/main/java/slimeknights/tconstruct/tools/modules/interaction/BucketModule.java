package slimeknights.tconstruct.tools.modules.interaction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.fluid.FluidPredicate;
import slimeknights.mantle.fluid.FluidTransferHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Objects;

import static slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;

/** Module allowing a tool to act as a bucket, placing fluids when sneaking and picking up when not. */
public record BucketModule(IJsonPredicate<Fluid> fluids) implements ModifierModule, BlockInteractionModifierHook, GeneralInteractionModifierHook, EquipmentChangeModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<BucketModule>defaultHooks(ModifierHooks.BLOCK_INTERACT, ModifierHooks.GENERAL_INTERACT);
  public static final RecordLoadable<BucketModule> LOADER = RecordLoadable.create(FluidPredicate.LOADER.defaultField("fluids", BucketModule::fluids), BucketModule::new);

  @Override
  public RecordLoadable<BucketModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
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
    return !(block instanceof LiquidBlockContainer container && container.canPlaceLiquid(world, pos, state, fluid));
  }

  @Override
  public InteractionResult afterBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
    if (!tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      return InteractionResult.PASS;
    }
    // only place fluid if sneaking, we contain at least a bucket, its a block, and its within our whitelist
    Player player = context.getPlayer();
    if (player == null || !player.isShiftKeyDown()) {
      return InteractionResult.PASS;
    }
    FluidStack fluidStack = TANK_HELPER.getFluid(tool);
    if (fluidStack.getAmount() < FluidType.BUCKET_VOLUME) {
      return InteractionResult.PASS;
    }
    Fluid fluid = fluidStack.getFluid();
    if (!fluids.matches(fluid) || !(fluid instanceof FlowingFluid flowing)) {
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
      if (!existing.isAir() && !existing.canBeReplaced(fluid) && cannotContainFluid(world, target, existing, fluidStack.getFluid())) {
        return InteractionResult.PASS;
      }
    }

    // if water, evaporate
    boolean placed = false;
    // start with forge vaporizing
    FluidType fluidType = fluid.getFluidType();
    if (fluidType.isVaporizedOnPlacement(world, target, fluidStack)) {
      fluidType.onVaporize(player, world, target, fluidStack);
      placed = true;
      // next, try vanilla vaporizing
    } else if (world.dimensionType().ultraWarm() && fluid.is(FluidTags.WATER)) {
      world.playSound(player, target, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
      for(int l = 0; l < 8; ++l) {
        world.addParticle(ParticleTypes.LARGE_SMOKE, target.getX() + Math.random(), target.getY() + Math.random(), target.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
      }
      placed = true;
      // continue with container blocks
    } else if (existing.getBlock() instanceof LiquidBlockContainer container) {
      container.placeLiquid(world, target, existing, flowing.getSource(false));
      world.playSound(null, target, FluidTransferHelper.getEmptySound(fluidStack), SoundSource.BLOCKS, 1.0F, 1.0F);
      placed = true;
      // finally, just replace the existing block with the fluid
    } else if (existing.canBeReplaced(fluid)) {
      if (!world.isClientSide && !existing.liquid()) {
        world.destroyBlock(target, true);
      }
      if (world.setBlockAndUpdate(target, fluid.defaultFluidState().createLegacyBlock()) || existing.getFluidState().isSource()) {
        world.playSound(null, target, FluidTransferHelper.getEmptySound(fluidStack), SoundSource.BLOCKS, 1.0F, 1.0F);
        placed = true;
      }
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

    // need at least a bucket worth of empty space in a fluid we can pickup, and cannot have NBT on the stored fluid
    FluidStack fluidStack = TANK_HELPER.getFluid(tool);
    Fluid currentFluid = fluidStack.getFluid();
    if (fluidStack.hasTag() || TANK_HELPER.getCapacity(tool) - fluidStack.getAmount() < FluidType.BUCKET_VOLUME || !fluidStack.isEmpty() && !fluids.matches(currentFluid)) {
      return InteractionResult.PASS;
    }
    // have to trace to find the fluid, ensure we can edit the position
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

    // try to find a fluid here, which must also be valid
    FluidState fluidState = world.getFluidState(target);
    Fluid targetedFluid = fluidState.getType();
    if (fluidState.isEmpty() || !fluids.matches(targetedFluid) || (!fluidStack.isEmpty() && !currentFluid.isSame(targetedFluid))) {
      return InteractionResult.PASS;
    }

    // finally, pickup the fluid
    BlockState state = world.getBlockState(target);
    // note that not all bucket pickup is a fluid, but we validated fluid state above
    if (state.getBlock() instanceof BucketPickup bucketPickup) {
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
