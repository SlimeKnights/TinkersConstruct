package slimeknights.tconstruct.tools.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;

public class ModifiableDaggerItem extends ModifiableSwordItem {
  public ModifiableDaggerItem(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public int getItemStackLimit(ItemStack stack) {
    return getDamage(stack) == 0 ? 2 : 1;
  }

  @Override
  public boolean isBarVisible(ItemStack stack) {
    return stack.getCount() == 1 && super.isBarVisible(stack);
  }

  /* stack size 1 actions */

  @Override
  public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
    return stack.getCount() != 1 || super.onLeftClickEntity(stack, player, entity);
  }

  @Override
  public float getDestroySpeed(ItemStack stack, BlockState state) {
    return stack.getCount() == 1 ? super.getDestroySpeed(stack, state) : 0;
  }

  @Override
  public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
    return stack.getCount() != 1 || super.onBlockStartBreak(stack, pos, player);
  }

  @Override
  public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
    return stack.getCount() != 1 ? InteractionResult.PASS : super.onItemUseFirst(stack, context);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    return context.getItemInHand().getCount() != 1 ? InteractionResult.PASS : super.useOn(context);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
    ItemStack stack = playerIn.getItemInHand(hand);
    return stack.getCount() != 1 ? InteractionResultHolder.pass(stack) : super.use(worldIn, playerIn, hand);
  }

  @Override
  public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
    return stack.getCount() == 1 && super.canPerformAction(stack, toolAction);
  }
}
