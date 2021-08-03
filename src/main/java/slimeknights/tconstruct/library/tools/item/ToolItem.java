package slimeknights.tconstruct.library.tools.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

/**
 * Extension of a modifiable tool that also is capable of harvesting blocks
 */
public class ToolItem extends ModifiableItem implements IModifiableHarvest {
  protected ToolItem(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }


  /* Mining */

  @Override
  public Set<ToolType> getToolTypes(ItemStack stack) {
    // no classes if broken
    if (ToolDamageUtil.isBroken(stack)) {
      return Collections.emptySet();
    }

    return super.getToolTypes(stack);
  }

  @Override
  public int getHarvestLevel(ItemStack stack, ToolType toolClass, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
    // brokenness is calculated in by the toolTypes check
    if (this.getToolTypes(stack).contains(toolClass)) {
      return ToolStack.from(stack).getStats().getInt(ToolStats.HARVEST_LEVEL);
    }

    return -1;
  }

  @Override
  public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
    ToolStack tool = ToolStack.from(stack);
    if (tool.isBroken()) {
      return false;
    }

    if (!worldIn.isRemote && worldIn instanceof ServerWorld) {
      boolean isEffective = getToolHarvestLogic().isEffective(tool, stack, state);
      ToolHarvestContext context = new ToolHarvestContext((ServerWorld) worldIn, entityLiving, state, pos, Direction.UP, true, isEffective);
      for (ModifierEntry entry : tool.getModifierList()) {
        entry.getModifier().afterBlockBreak(tool, entry.getLevel(), context);
      }
      ToolDamageUtil.damageAnimated(tool, getToolHarvestLogic().getDamage(tool, stack, worldIn, pos, state), entityLiving);
    }

    return true;
  }

  @Override
  public final boolean canHarvestBlock(ItemStack stack, BlockState state) {
    return this.getToolHarvestLogic().isEffective(ToolStack.from(stack), stack, state);
  }

  @Override
  public final float getDestroySpeed(ItemStack stack, BlockState state) {
    return this.getToolHarvestLogic().getDestroySpeed(stack, state);
  }

  @Override
  public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
    return getToolHarvestLogic().handleBlockBreak(stack, pos, player);

    // TODO: consider taking over PlayerInteractionManager#tryHarvestBlock and PlayerController#onPlayerDestroyBlock
    // will grant better AOE control, https://github.com/mekanism/Mekanism/blob/1.16.x/src/main/java/mekanism/common/item/gear/ItemMekaTool.java#L238

    /*// this is a really dumb hack.
    // Basically when something with silktouch harvests a block from the offhand
    // the game can't detect that. so we have to switch around the items in the hands for the break call
    // it's switched back in onBlockDestroyed
    if (DualToolHarvestUtil.shouldUseOffhand(player, pos, player.getHeldItemMainhand())) {
      ItemStack off = player.getHeldItemOffhand();

      this.switchItemsInHands(player);
      // remember, off is in the mainhand now
      CompoundNBT tag = off.getOrCreateTag();
      tag.putLong(TAG_SWITCHED_HAND_HAX, player.getEntityWorld().getGameTime());
      off.setTag(tag);
    }*/

    //return this.breakBlock(stack, pos, player);
  }
}
