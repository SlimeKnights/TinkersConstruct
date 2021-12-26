package slimeknights.tconstruct.gadgets.block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import net.minecraft.block.AbstractBlock.Properties;

/**
 * Extension of cake that utalizes a food instance for properties
 */
public class FoodCakeBlock extends CakeBlock {
  private final Food food;
  public FoodCakeBlock(Properties properties, Food food) {
    super(properties);
    this.food = food;
  }

  @Override
  public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    ActionResultType result = this.eatSlice(world, pos, state, player);
    if (result.consumesAction()) {
      return result;
    }
    if (world.isClientSide() && player.getItemInHand(handIn).isEmpty()) {
      return ActionResultType.CONSUME;
    }
    return ActionResultType.PASS;
  }

  /** Checks if the given player has all potion effects from the food */
  private boolean hasAllEffects(PlayerEntity player) {
    for (Pair<EffectInstance,Float> pair : food.getEffects()) {
      if (pair.getFirst() != null && !player.hasEffect(pair.getFirst().getEffect())) {
        return false;
      }
    }
    return true;
  }

  /** Eats a single slice of cake if possible */
  private ActionResultType eatSlice(IWorld world, BlockPos pos, BlockState state, PlayerEntity player) {
    if (!player.canEat(false) && !food.canAlwaysEat()) {
      return ActionResultType.PASS;
    }
    // repurpose fast eating, will mean no eating if we have the effect
    if (!food.isFastFood() && hasAllEffects(player)) {
      return ActionResultType.PASS;
    }
    player.awardStat(Stats.EAT_CAKE_SLICE);
    // apply food stats
    player.getFoodData().eat(food.getNutrition(), food.getSaturationModifier());
    for (Pair<EffectInstance,Float> pair : food.getEffects()) {
      if (!world.isClientSide() && pair.getFirst() != null && world.getRandom().nextFloat() < pair.getSecond()) {
        player.addEffect(new EffectInstance(pair.getFirst()));
      }
    }
    // remove one bite from the cake
    int i = state.getValue(BITES);
    if (i < 6) {
      world.setBlock(pos, state.setValue(BITES, i + 1), 3);
    } else {
      world.removeBlock(pos, false);
    }
    return ActionResultType.SUCCESS;
  }
}
