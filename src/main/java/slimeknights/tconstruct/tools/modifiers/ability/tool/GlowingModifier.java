package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.impl.InteractionModifier;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.shared.TinkerCommons;

public class GlowingModifier extends InteractionModifier.SingleUse {
  @Override
  public int getPriority() {
    return 70; // after bucketing
  }
  
  @Override
  public InteractionResult afterBlockUse(IToolStackView tool, int level, UseOnContext context, EquipmentSlot slotType) {
    Player player = context.getPlayer();
    if (tool.getCurrentDurability() >= 10) {
      if (!context.getLevel().isClientSide) {
        Level world = context.getLevel();
        Direction face = context.getClickedFace();
        BlockPos pos = context.getClickedPos().relative(face);
        if (TinkerCommons.glow.get().addGlow(world, pos, face.getOpposite())) {
          // damage the tool, showing animation if relevant
          if (ToolDamageUtil.damage(tool, 10, player, context.getItemInHand()) && player != null) {
            player.broadcastBreakEvent(slotType);
          }
          world.playSound(null, pos, world.getBlockState(pos).getSoundType(world, pos, player).getPlaceSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
        }
      }
      return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
    return InteractionResult.PASS;
  }
}
