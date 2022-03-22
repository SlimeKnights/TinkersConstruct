package slimeknights.tconstruct.tools.modifiers.ability.interaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.ToolAction;
import slimeknights.tconstruct.library.modifiers.impl.InteractionModifier;
import slimeknights.tconstruct.library.tools.definition.aoe.CircleAOEIterator;
import slimeknights.tconstruct.library.tools.definition.aoe.IAreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Collections;

/**
 * Modifier that starts a fire at the given position
 */
@RequiredArgsConstructor
public class FirestarterModifier extends InteractionModifier.SingleUse {
  /** Compat with mods adding custom campfires */
  private static final ToolAction LIGHT_CAMPFIRE = ToolAction.get("light_campfire");

  @Getter
  private final int priority;

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return priority > Short.MIN_VALUE;
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, int level, ToolAction toolAction) {
    return toolAction == LIGHT_CAMPFIRE;
  }

  @Override
  public InteractionResult afterEntityUse(IToolStackView tool, int level, Player player, LivingEntity target, InteractionHand hand, EquipmentSlot slotType) {
    if (target instanceof Creeper creeper) {
      player.level.playSound(player, creeper.getX(), creeper.getY(), creeper.getZ(), SoundEvents.FLINTANDSTEEL_USE, creeper.getSoundSource(), 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
      if (!player.level.isClientSide) {
        creeper.ignite();
        ToolDamageUtil.damageAnimated(tool, 1, player, slotType);
      }
      return InteractionResult.sidedSuccess(player.level.isClientSide);
    }
    return InteractionResult.PASS;
  }

  /** Ignites the given block */
  private static boolean ignite(IToolStackView tool, Level world, BlockPos pos, BlockState state, Direction sideHit, Direction horizontalFacing, @Nullable Player player) {
    // campfires first
    if (CampfireBlock.canLight(state)) {
      world.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
      world.setBlock(pos, state.setValue(BlockStateProperties.LIT, true), 11);
      return true;
    }

    // ignite the TNT
    if (state.getBlock() instanceof TntBlock tnt) {
      tnt.onCaughtFire(state, world, pos, sideHit, player);
      world.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
      return true;
    }

    // fire starting
    BlockPos offset = pos.relative(sideHit);
    if (BaseFireBlock.canBePlacedAt(world, offset, horizontalFacing)) {
      world.playSound(player, offset, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
      world.setBlock(offset, BaseFireBlock.getState(world, offset), 11);
      return true;
    }
    return false;
  }

  @Override
  public InteractionResult afterBlockUse(IToolStackView tool, int level, UseOnContext context, EquipmentSlot slotType) {
    if (tool.isBroken()) {
      return InteractionResult.PASS;
    }
    Player player = context.getPlayer();
    Level world = context.getLevel();
    BlockPos pos = context.getClickedPos();
    Direction sideHit = context.getClickedFace();
    BlockState state = world.getBlockState(pos);

    // if targeting fire, offset to behind the fire
    boolean targetingFire = false;
    if (state.is(BlockTags.FIRE)) {
      pos = pos.relative(sideHit.getOpposite());
      targetingFire = true;
    }

    // AOE selection logic, get boosted from both fireprimer (unique modifer) and expanded
    int range = tool.getModifierLevel(TinkerModifiers.fireprimer.get()) + tool.getModifierLevel(TinkerModifiers.expanded.get());
    Iterable<BlockPos> targets = Collections.emptyList();
    if (range > 0 && player != null) {
      targets = CircleAOEIterator.calculate(tool, ItemStack.EMPTY, world, player, pos, sideHit, 1 + range, true, IAreaOfEffectIterator.AOEMatchType.TRANSFORM);
    }

    // burn it all in AOE
    Direction horizontalFacing = context.getHorizontalDirection();
    // first burn the center, unless we already know its fire
    boolean didIgnite = false;
    ItemStack stack = context.getItemInHand();
    if (!targetingFire) {
      didIgnite = ignite(tool, world, pos, state, sideHit, horizontalFacing, player);
      if (didIgnite && ToolDamageUtil.damage(tool, 1, player, stack)) {
        if (player != null) {
          player.broadcastBreakEvent(slotType);
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
      }
    }
    // ignite the edges, if any worked return success
    for (BlockPos target : targets) {
      if (ignite(tool, world, target, world.getBlockState(target), sideHit, horizontalFacing, player)) {
        didIgnite = true;
        if (ToolDamageUtil.damage(tool, 1, player, stack)) {
          if (player != null) {
            player.broadcastBreakEvent(slotType);
          }
          break;
        }
      }
    }
    return didIgnite ? InteractionResult.sidedSuccess(world.isClientSide) : InteractionResult.PASS;
  }
}
