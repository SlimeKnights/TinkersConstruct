package slimeknights.tconstruct.tools.modifiers.internal;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.EquipmentSlotType.Group;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class TwoHandedAbilityModifier extends SingleUseModifier {
	public TwoHandedAbilityModifier() {
		super(0xcef1f5);
  }

	@Override
	public boolean shouldDisplay(boolean advanced) {
		return false;
	}
	
  @Override
  public int getPriority() {
    return Integer.MIN_VALUE;
  }

  @Override
  public ActionResultType onToolUse(IModifierToolStack tool, int level, World world, PlayerEntity player, Hand hand, EquipmentSlotType slotType) {
    return slotType.getType() == Group.ARMOR ? ActionResultType.CONSUME : ActionResultType.PASS;
  }
}
