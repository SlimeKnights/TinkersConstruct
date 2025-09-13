package slimeknights.tconstruct.tools.modifiers.ability.interaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ToolAction;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolActionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.mining.RemoveBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ShowOffhandModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.definition.module.aoe.CircleAOEIterator;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Collections;

/**
 * Modifier that starts a fire at the given position
 */
@RequiredArgsConstructor
public class FirestarterModifier extends NoLevelsModifier implements EntityInteractionModifierHook, BlockInteractionModifierHook, ToolActionModifierHook, RemoveBlockModifierHook {
  /** Generic action for the sake of people who want compat but do not want to request a specific action */
  private static final ToolAction LIGHT_FIRE = ToolAction.get("light_fire");
  /** Compat with mods adding custom campfires */
  private static final ToolAction LIGHT_CAMPFIRE = ToolAction.get("light_campfire");

  @Getter
  private final int priority;

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ShowOffhandModule.DISALLOW_BROKEN);
    hookBuilder.addHook(this, ModifierHooks.ENTITY_INTERACT, ModifierHooks.BLOCK_INTERACT, ModifierHooks.TOOL_ACTION, ModifierHooks.REMOVE_BLOCK);
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return priority > Short.MIN_VALUE;
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
    return InteractionSource.formatModifierName(tool, this, super.getDisplayName(tool, entry, access));
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, ModifierEntry modifier, ToolAction toolAction) {
    return toolAction == LIGHT_CAMPFIRE || toolAction == LIGHT_FIRE;
  }

  @Override
  public InteractionResult afterEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, LivingEntity target, InteractionHand hand, InteractionSource source) {
    if (tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source) && target instanceof Creeper creeper) {
      Level level = player.level();
      level.playSound(player, creeper.getX(), creeper.getY(), creeper.getZ(), SoundEvents.FLINTANDSTEEL_USE, creeper.getSoundSource(), 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
      if (!level.isClientSide) {
        creeper.ignite();
        ToolDamageUtil.damageAnimated(tool, 1, player, source.getSlot(hand));
      }
      return InteractionResult.sidedSuccess(level.isClientSide);
    }
    return InteractionResult.PASS;
  }

  /** Ignites the given block */
  private static boolean ignite(IToolStackView tool, Level world, BlockPos pos, BlockState state, Direction sideHit, Direction horizontalFacing, @Nullable Player player) {
    // campfires first
    if (CampfireBlock.canLight(state) || CandleBlock.canLight(state) || CandleCakeBlock.canLight(state)) {
      world.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
      world.setBlock(pos, state.setValue(BlockStateProperties.LIT, true), 11);
      world.gameEvent(player, GameEvent.BLOCK_PLACE, pos);
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
  public InteractionResult beforeBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
    if (tool.isBroken() || !tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      return InteractionResult.PASS;
    }
    if (context.getLevel().getBlockState(context.getClickedPos()).is(BlockTags.CANDLE_CAKES)) {
      return afterBlockUse(tool, modifier, context, source);
    }
    return InteractionResult.PASS;
  }

  @Override
  public InteractionResult afterBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
    if (tool.isBroken() || !tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      return InteractionResult.PASS;
    }
    Player player = context.getPlayer();
    Level world = context.getLevel();
    UseOnContext targetContext = context;
    BlockPos pos = context.getClickedPos();
    Direction sideHit = context.getClickedFace();
    BlockState state = world.getBlockState(pos);

    // if targeting fire, offset to behind the fire
    boolean targetingFire = false;
    if (state.is(BlockTags.FIRE)) {
      pos = pos.relative(sideHit.getOpposite());
      targetContext = Util.offset(context, pos);
      targetingFire = true;
    }

    // AOE selection logic, get boosted from both fireprimer (unique modifer) and expanded
    int range = tool.getModifierLevel(TinkerModifiers.fireprimer.getId()) + tool.getModifierLevel(TinkerModifiers.expanded.getId());
    Iterable<BlockPos> targets = Collections.emptyList();
    if (range > 0) {
      targets = CircleAOEIterator.calculate(tool, targetContext, 1 + range, true, AreaOfEffectIterator.AOEMatchType.TRANSFORM);
    }

    // burn it all in AOE
    Direction horizontalFacing = context.getHorizontalDirection();
    // first burn the center, unless we already know its fire
    boolean didIgnite = false;
    ItemStack stack = context.getItemInHand();
    EquipmentSlot slotType = source.getSlot(context.getHand());
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
    // when targeting fire, return true so left click interact does not continue to run
    return didIgnite || targetingFire ? InteractionResult.sidedSuccess(world.isClientSide) : InteractionResult.PASS;
  }

  @Nullable
  @Override
  public Boolean removeBlock(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
    if (context.getState().is(Blocks.FIRE) && tool.getHook(ToolHooks.INTERACTION).canInteract(tool, getId(), InteractionSource.LEFT_CLICK)) {
      return false;
    }
    return null;
  }
}
