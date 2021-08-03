package slimeknights.tconstruct.tools.modifiers.internal;

import lombok.Getter;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.TNTBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic.AOEMatchType;
import slimeknights.tconstruct.library.tools.helper.aoe.CircleAOEHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Collections;

/**
 * Modifier that starts a fire at the given position
 */
public class FirestarterModifier extends SingleUseModifier {
  @Getter
  private final int priority;
  public FirestarterModifier(int color, int priority) {
    super(color);
    this.priority = priority;
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return priority > Short.MIN_VALUE;
  }

  @Override
  public ActionResultType onEntityUse(IModifierToolStack tool, int level, PlayerEntity player, LivingEntity target, Hand hand) {
    if (target instanceof CreeperEntity) {
      CreeperEntity creeper = (CreeperEntity) target;
      player.world.playSound(player, creeper.getPosX(), creeper.getPosY(), creeper.getPosZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, creeper.getSoundCategory(), 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
      if (!player.world.isRemote) {
        creeper.ignite();
        ToolDamageUtil.damageAnimated(tool, 1, player, hand);
      }
      return ActionResultType.func_233537_a_(player.world.isRemote);
    }
    return ActionResultType.PASS;
  }

  /** Ignites the given block */
  private static boolean ignite(IModifierToolStack tool, World world, BlockPos pos, BlockState state, Direction sideHit, Direction horizontalFacing, @Nullable PlayerEntity player) {
    // campfires first
    if (CampfireBlock.canBeLit(state)) {
      world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
      world.setBlockState(pos, state.with(BlockStateProperties.LIT, true), 11);
      return true;
    }

    // ignite the TNT
    if (state.getBlock() instanceof TNTBlock) {
      TNTBlock tnt = (TNTBlock) state.getBlock();
      tnt.catchFire(state, world, pos, sideHit, player);
      world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
      return true;
    }

    // fire starting
    BlockPos offset = pos.offset(sideHit);
    if (AbstractFireBlock.canLightBlock(world, offset, horizontalFacing)) {
      world.playSound(player, offset, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
      world.setBlockState(offset, AbstractFireBlock.getFireForPlacement(world, offset), 11);
      return true;
    }
    return false;
  }

  @Override
  public ActionResultType afterBlockUse(IModifierToolStack tool, int level, ItemUseContext context) {
    if (tool.isBroken()) {
      return ActionResultType.PASS;
    }
    PlayerEntity player = context.getPlayer();
    World world = context.getWorld();
    BlockPos pos = context.getPos();
    Direction sideHit = context.getFace();
    BlockState state = world.getBlockState(pos);
    ItemStack stack = context.getItem();

    // if targeting fire, offset to behind the fire
    boolean targetingFire = false;
    if (state.isIn(BlockTags.FIRE)) {
      pos = pos.offset(sideHit.getOpposite());
      targetingFire = true;
    }

    // AOE selection logic, get boosted from both fireprimer (unique modifer) and expanded
    int range = tool.getModifierLevel(TinkerModifiers.fireprimer.get()) + tool.getModifierLevel(TinkerModifiers.expanded.get());
    Iterable<BlockPos> targets = Collections.emptyList();
    if (range > 0 && player != null) {
      targets = CircleAOEHarvestLogic.calculate(ToolHarvestLogic.DEFAULT, tool, ItemStack.EMPTY, world, player, pos, sideHit, 1 + range, true, AOEMatchType.TRANSFORM);
    }

    // burn it all in AOE
    Hand hand = context.getHand();
    Direction horizontalFacing = context.getPlacementHorizontalFacing();
    // first burn the center, unless we already know its fire
    boolean didIgnite = false;
    if (!targetingFire) {
      didIgnite = ignite(tool, world, pos, state, sideHit, horizontalFacing, player);
      if (didIgnite && ToolDamageUtil.damage(tool, 1, player, stack)) {
        if (player != null) {
          player.sendBreakAnimation(hand);
        }
        return ActionResultType.func_233537_a_(world.isRemote);
      }
    }
    // ignite the edges, if any worked return success
    for (BlockPos target : targets) {
      if (ignite(tool, world, target, world.getBlockState(target), sideHit, horizontalFacing, player)) {
        didIgnite = true;
        if (ToolDamageUtil.damage(tool, 1, player, stack)) {
          if (player != null) {
            player.sendBreakAnimation(hand);
          }
          break;
        }
      }
    }
    return didIgnite ? ActionResultType.func_233537_a_(world.isRemote) : ActionResultType.PASS;
  }
}
