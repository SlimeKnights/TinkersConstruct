package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.modifiers.dynamic.EnchantmentModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;
public class SoulSpeedModifier extends EnchantmentModifier {
  public SoulSpeedModifier() {
    super(Enchantments.SOUL_SPEED, 1, ModifierLevelDisplay.DEFAULT);
  }

  /** Gets the position this entity is standing on, cloned from protected living entity method */
  private static BlockPos getOnPosition(LivingEntity living) {
    Vec3 position = living.position();
    int x = Mth.floor(position.x);
    int y = Mth.floor(position.y - (double)0.2F);
    int z = Mth.floor(position.z);
    BlockPos pos = new BlockPos(x, y, z);
    if (living.level.isEmptyBlock(pos)) {
      BlockPos below = pos.below();
      BlockState blockstate = living.level.getBlockState(below);
      if (blockstate.collisionExtendsVertically(living.level, below, living)) {
        return below;
      }
    }

    return pos;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    // must either have no player or a player on soulsand
    if (player == null || key != TooltipKey.SHIFT || (!player.isFallFlying() && player.level.getBlockState(getOnPosition(player)).is(BlockTags.SOUL_SPEED_BLOCKS))) {
      // multiplies boost by 10 and displays as a percent as the players base movement speed is 0.1 and is in unknown units
      // percentages make sense
      addPercentTooltip(getDisplayName(), 0.3f + level * 0.105f, tooltip);
    }
  }
}
