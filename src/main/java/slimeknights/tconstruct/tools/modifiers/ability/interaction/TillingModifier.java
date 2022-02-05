package slimeknights.tconstruct.tools.modifiers.ability.interaction;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class TillingModifier extends BlockTransformModifier {
  public static final ToolAction HOE_TILL = ToolAction.get("hoe_till");
  public TillingModifier(int priority) {
    super(priority, HOE_TILL, SoundEvents.HOE_TILL, true);
  }

  @Override
  protected boolean transform(UseOnContext context, BlockState original, boolean playSound) {
    Pair<Predicate<UseOnContext>,Consumer<UseOnContext>> pair = HoeItem.TILLABLES.get(original.getBlock());
    if (pair != null && pair.getFirst().test(context)) {
      Level level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      Player player = context.getPlayer();
      level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
      if (!level.isClientSide) {
        pair.getSecond().accept(context);
      }
      return true;
    }
    return false;
  }
}
