package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule.Constant;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

public class SoulSpeedModifier extends Modifier implements TooltipModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(new Constant(Enchantments.SOUL_SPEED, 1));
    hookBuilder.addHook(this, ModifierHooks.TOOLTIP);
  }

  /** Gets the position this entity is standing on, cloned from protected living entity method */
  private static BlockPos getOnPosition(LivingEntity living) {
    Vec3 position = living.position();
    int x = Mth.floor(position.x);
    int y = Mth.floor(position.y - (double)0.2F);
    int z = Mth.floor(position.z);
    BlockPos pos = new BlockPos(x, y, z);
    Level level = living.level();
    if (level.isEmptyBlock(pos)) {
      BlockPos below = pos.below();
      BlockState blockstate = level.getBlockState(below);
      if (blockstate.collisionExtendsVertically(level, below, living)) {
        return below;
      }
    }

    return pos;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    // must either have no player or a player on soulsand
    if (player == null || key != TooltipKey.SHIFT || (!player.isFallFlying() && player.level().getBlockState(getOnPosition(player)).is(BlockTags.SOUL_SPEED_BLOCKS))) {
      // multiplies boost by 10 and displays as a percent as the players base movement speed is 0.1 and is in unknown units
      // percentages make sense
      TooltipModifierHook.addPercentBoost(this, getDisplayName(), 0.3f + modifier.getLevel() * 0.105f, tooltip);
    }
  }
}
