package slimeknights.tconstruct.library.tools.helper;

import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

import java.util.List;

/**
 * Helper methods that are used when the tool interacts with the world or other things
 */
public class ToolInteractionUtil {

  /**
   * Returns true if the tool is effective for harvesting the given block.
   */
  public static boolean isToolEffectiveAgainstBlock(ItemStack stack, BlockState state) {
    return stack.getItem().getToolTypes(stack).stream()
      .anyMatch(toolType -> state.getBlock().isToolEffective(state, toolType));
  }

  /**
   * Damages the tool.
   * Should not be called directly, just use ItemStack.damageItem
   */
  public static void damageTool(ItemStack stack, int amount, LivingEntity entity) {
    ToolData toolData = ToolData.from(stack);
    StatsNBT stats = toolData.getStats();
    if (amount == 0 || stats.broken) {
      return;
    }

    int actualAmount = amount;

    // todo: trait nbt
    /*for (ITrait trait : TinkerUtil.getTraitsOrdered(stack)) {
      if (amount > 0) {
        actualAmount = trait.onToolDamage(stack, amount, actualAmount, entity);
      } else {
        actualAmount = trait.onToolHeal(stack, amount, actualAmount, entity);
      }
    }*/

    // extra compatibility for unbreaking.. because things just love to mess it up.. like 3rd party stuff
    // TiC unbreaking is handled by the modifier/trait inself
    if (actualAmount > 0 && isVanillaUnbreakable(stack)) {
      actualAmount = 0;
    }

    // ensure we never deal more damage than durability
    int currentDurability = ToolCore.getCurrentDurability(stack);
    actualAmount = Math.min(actualAmount, currentDurability);
    stack.setDamage(stack.getDamage() + actualAmount);

    if (entity instanceof ServerPlayerEntity) {
      if (actualAmount != 0) {
        CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger((ServerPlayerEntity) entity, stack, stack.getDamage() + actualAmount);
      }

      if (currentDurability <= 0) {
        ToolBreakUtil.breakTool(stack);
      }
    }
  }

  /**
   * See e.g. {@link ItemStack#isDamageable()}
   */
  private static boolean isVanillaUnbreakable(ItemStack stack) {
    CompoundNBT compoundnbt = stack.getTag();
    return compoundnbt != null && compoundnbt.getBoolean("Unbreakable");
  }

  /**
   * Attempts to shear a block using IForgeShearable logic
   * @param tool the tool stack
   * @param world the current world the block is in
   * @param player the player attempting to shear a block
   * @param pos the blockpos of the block
   * @return true if the block was successfully sheared
   */
  public static boolean shearBlock(ItemStack tool, World world, PlayerEntity player, BlockPos pos) {
    // only serverside since it creates entities
    if (world.isRemote) {
      return false;
    }

    BlockState state = world.getBlockState(pos);
    Block block = state.getBlock();

    if (block instanceof IForgeShearable) {
      IForgeShearable target = (IForgeShearable) block;

      if (target.isShearable(tool, world, pos)) {
        int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool);
        List<ItemStack> drops = target.onSheared(player, tool, world, pos, fortune);

        for (ItemStack stack : drops) {
          float f = 0.7F;
          double d = TConstruct.random.nextFloat() * f + (1.0F - f) * 0.5D;
          double d1 = TConstruct.random.nextFloat() * f + (1.0F - f) * 0.5D;
          double d2 = TConstruct.random.nextFloat() * f + (1.0F - f) * 0.5D;

          ItemEntity itemEntity = new ItemEntity(player.getEntityWorld(), pos.getX() + d, pos.getY() + d1, pos.getZ() + d2, stack);

          itemEntity.setDefaultPickupDelay();

          world.addEntity(itemEntity);
        }

        tool.onBlockDestroyed(world, state, pos, player);

        world.removeBlock(pos, false);

        return true;
      }
    }

    return false;
  }
}
