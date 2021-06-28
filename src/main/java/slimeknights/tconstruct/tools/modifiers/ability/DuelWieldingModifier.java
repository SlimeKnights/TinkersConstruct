package slimeknights.tconstruct.tools.modifiers.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.modifiers.internal.OffhandAttackModifier;

public class DuelWieldingModifier extends OffhandAttackModifier {
  public DuelWieldingModifier() {
    super(0xA6846A, 30);
  }

  @Override
  protected boolean canAttack(IModifierToolStack tool, PlayerEntity player, Hand hand) {
    if (!super.canAttack(tool, player, hand)) {
      return false;
    }
    // must have nothing in the main hand, or the main hand must also have this modifier applied
    ItemStack mainStack = player.getHeldItemMainhand();
    return mainStack.isEmpty() || (TinkerTags.Items.MODIFIABLE.contains(mainStack.getItem()) && ToolStack.from(mainStack).getVolatileData().getBoolean(DUEL_WIELDING));
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return true;
  }
}
