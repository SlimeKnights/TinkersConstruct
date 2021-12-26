package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.modifiers.base.InteractionModifier;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.shared.TinkerCommons;

public class GlowingModifier extends InteractionModifier.SingleUse {
  public GlowingModifier() {
    super(0xffffaa);
  }

  @Override
  public int getPriority() {
    return 70; // after bucketing
  }
  
  @Override
  public ActionResultType afterBlockUse(IModifierToolStack tool, int level, ItemUseContext context, EquipmentSlotType slotType) {
    PlayerEntity player = context.getPlayer();
    if (tool.getCurrentDurability() >= 10) {
      if (!context.getLevel().isClientSide) {
        World world = context.getLevel();
        Direction face = context.getClickedFace();
        BlockPos pos = context.getClickedPos().relative(face);
        if (TinkerCommons.glow.get().addGlow(world, pos, face.getOpposite())) {
          // damage the tool, showing animation if relevant
          if (ToolDamageUtil.damage(tool, 10, player, context.getItemInHand()) && player != null) {
            player.broadcastBreakEvent(slotType);
          }
          world.playSound(null, pos, world.getBlockState(pos).getSoundType(world, pos, player).getPlaceSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
      }
      return ActionResultType.sidedSuccess(context.getLevel().isClientSide);
    }
    return ActionResultType.PASS;
  }
}
