package slimeknights.tconstruct.tools.modifiers.ability;

import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.shared.TinkerCommons;

public class GlowingModifier extends SingleUseModifier {
  public GlowingModifier() {
    super(0xffffaa);
  }

  @Override
  public int getPriority() {
    return 180;
  }
  
  @Override
  public ActionResultType onItemUse(IModifierToolStack tool, int level, ItemUseContext context) {
    if (tool.getCurrentDurability() >= 5 && context.getPlayer().isSneaking()) {
      if (!context.getWorld().isRemote) {
        if (TinkerCommons.glow.get().addGlow(context.getWorld(), context.getPos(), context.getFace().getOpposite())) {
          tool.setDamage(tool.getDamage() + 5);
          }
        }
      return ActionResultType.func_233537_a_(context.getWorld().isRemote);
    }
    return ActionResultType.PASS;
  }
}
