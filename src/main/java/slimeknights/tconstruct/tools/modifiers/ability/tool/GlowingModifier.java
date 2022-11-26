package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import slimeknights.tconstruct.library.modifiers.impl.InteractionModifier.NoLevels;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.shared.TinkerCommons;

public class GlowingModifier extends NoLevels {
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

  @Override
  public boolean onDispenserUse(IToolStackView tool, int level, BlockSource source, ItemStack stack) {

    if(tool.getCurrentDurability() >= 10) {
      Level world = source.getLevel();

      Direction facing = source.getBlockState().getValue(DispenserBlock.FACING);
      if(TinkerCommons.glow.get().addGlow(world, source.getPos().relative(facing), facing)) {
        ToolDamageUtil.damage(tool, 10, null, stack);
        world.playSound(null, source.getPos(), source.getBlockState().getSoundType(world, source.getPos(), null).getPlaceSound(), SoundSource.BLOCKS, 1.0f, 10.f);
      }
      return true;
    }

    return false;
  }
}
