package slimeknights.tconstruct.tools.modifiers.ability.armor.walker;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.MutableUseOnContext;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class PlowingModifier extends AbstractWalkerModifier {

  private MutableUseOnContext context;

  @Override
  public void onWalk(IToolStackView tool, int level, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    super.onWalk(tool, level, living, prevPos, newPos);
    // clear the context to prevent memory leaks
    context = null;
  }

  @Override
  protected float getRadius(IToolStackView tool, int level) {
    return 1.5f + tool.getModifierLevel(TinkerModifiers.expanded.get());
  }

  @Override
  protected void walkOn(IToolStackView tool, int level, LivingEntity living, Level world, BlockPos target, MutableBlockPos mutable) {
    if (world.isEmptyBlock(target)) {
      mutable.set(target.getX(), target.getY() - 1, target.getZ());
      Pair<Predicate<UseOnContext>,Consumer<UseOnContext>> pair = HoeItem.TILLABLES.get(world.getBlockState(mutable).getBlock());
      if (pair != null) {
        // prepare context, reused to save effort as only the position changes
        if (context == null) {
          context = new MutableUseOnContext(living.getLevel(), living instanceof Player p ? p : null, InteractionHand.MAIN_HAND, living.getItemBySlot(EquipmentSlot.FEET), Util.createTraceResult(mutable, Direction.UP, false));
        } else {
          context.setOffsetPos(mutable);
        }
        if (pair.getFirst().test(context)) {
          pair.getSecond().accept(context);
          world.playSound(null, mutable, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0f, 1.0f);
          ToolDamageUtil.damageAnimated(tool, 1, living, EquipmentSlot.FEET);
        }
      }
    }
  }
}
