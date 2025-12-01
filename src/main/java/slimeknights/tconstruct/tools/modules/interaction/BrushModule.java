package slimeknights.tconstruct.tools.modules.interaction;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.AreaOfEffectHighlightModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator.AOEMatchType;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;

import java.util.List;

/** Modifier module to implement behavior of {@link BrushItem} */
public enum BrushModule implements ModifierModule, GeneralInteractionModifierHook, BlockInteractionModifierHook, AreaOfEffectHighlightModifierHook {
  INSTANCE;

  public static final RecordLoadable<BrushModule> LOADER = new SingletonLoader<>(INSTANCE);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<BrushModule>defaultHooks(ModifierHooks.GENERAL_INTERACT, ModifierHooks.BLOCK_INTERACT, ModifierHooks.AOE_HIGHLIGHT);

  @Override
  public RecordLoadable<BrushModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    // runs on block click
    return InteractionResult.PASS;
  }

  /** Runs an entity raytrace for brushing. See same method on {@link BrushItem} */
  private static HitResult calculateHitResult(LivingEntity living) {
    return ProjectileUtil.getHitResultOnViewVector(living, entity -> !entity.isSpectator() && entity.isPickable(), living.getAttributeValue(ForgeMod.BLOCK_REACH.get()));
  }

  @Override
  public InteractionResult afterBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
    Player player = context.getPlayer();
    // must have a player, and no entity in the way
    if (source == InteractionSource.RIGHT_CLICK && player != null && calculateHitResult(player).getType() == HitResult.Type.BLOCK) {
      GeneralInteractionModifierHook.startUsing(tool, modifier.getId(), player, context.getHand());
    }
    return InteractionResult.CONSUME;
  }

  @Override
  public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
    return BlockingModifier.blockWhileCharging(tool, UseAnim.BRUSH);
  }

  @Override
  public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
    return 200;
  }

  /** Plays sound and shows particles */
  private static void brushEffects(Player player, BlockHitResult blockHit, BlockState state, HumanoidArm arm, SoundEvent sound) {
    Level level = player.level();

    // spawn particles
    // shouldn't be needed to do the instance of, but might as well be safe
    if (Items.BRUSH instanceof BrushItem brush) {
      brush.spawnDustParticles(level, blockHit, state, player.getViewVector(0.0F), arm);
    }

    // play sound
    level.playSound(player, blockHit.getBlockPos(), sound, SoundSource.BLOCKS);
  }

  /** Brushes a single block */
  private static boolean brushBlock(Player player, BlockHitResult blockHit, BlockState state, HumanoidArm arm) {
    // only play sound and particle if its a brushable block, reduces noice on AOE
    Level level = player.level();
    if (state.getBlock() instanceof BrushableBlock brushable) {
      brushEffects(player, blockHit, state, arm, brushable.getBrushSound());
      if (level.isClientSide) {
        return true;
      }
    }

    // brush the block
    if (!level.isClientSide) {
      return level.getBlockEntity(blockHit.getBlockPos()) instanceof BrushableBlockEntity brushable && brushable.brush(level.getGameTime(), player, blockHit.getDirection());
    }
    return false;
  }

  @Override
  public void onUsingTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
    // must not be out of time
    if (timeLeft >= 0 && entity instanceof Player player) {
      // find the block we hit
      HitResult hit = calculateHitResult(entity);
      if (hit.getType() == HitResult.Type.BLOCK && hit instanceof BlockHitResult blockHit) {
        // from this point on we have succeeded at interaction so will not stop using, but we still don't act every tick
        if ((getUseDuration(tool, modifier) - timeLeft + 1) % 10 == 5) {
          Level level = entity.level();
          BlockPos pos = blockHit.getBlockPos();
          BlockState state = level.getBlockState(pos);
          InteractionHand hand = entity.getUsedItemHand();
          HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? entity.getMainArm() : entity.getMainArm().getOpposite();

          // brush main block
          int damage = 0;
          if (brushBlock(player, blockHit, state, arm)) {
            damage += 1;
          }

          // brush AOE blocks
          UseOnContext context = new UseOnContext(level, player, hand, player.getItemInHand(hand), blockHit);
          for (BlockPos extraPos : tool.getDefinition().getHook(ToolHooks.AOE_ITERATOR).getBlocks(tool, context, state, AOEMatchType.TRANSFORM)) {
            if (brushBlock(player, Util.offset(blockHit, extraPos), level.getBlockState(extraPos), arm)) {
              damage += 1;
            }
          }

          // if nothing was brushed clientside, play the effect for the center block
          if (damage == 0 && level.isClientSide) {
            brushEffects(player, blockHit, state, arm, SoundEvents.BRUSH_GENERIC);
          }

          // apply all tool damage, and stop using if needed
          if (damage > 0 && !level.isClientSide && ToolDamageUtil.damageAnimated(tool, damage, entity, hand)) {
            entity.stopUsingItem();
          }
        }
        return;
      }
    }
    entity.releaseUsingItem();
  }

  @Override
  public boolean shouldHighlight(IToolStackView tool, ModifierEntry modifier, UseOnContext context, BlockPos offset, BlockState state) {
    return context.getLevel().getBlockEntity(offset) instanceof BrushableBlockEntity;
  }
}
