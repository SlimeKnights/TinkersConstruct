package slimeknights.tconstruct.tools.modifiers.ability;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.shared.TinkerCommons;

public class GlowingModifier extends PlaceBlockModifier {
  private static final int DURABILITY_DRAIN = 5;
  public GlowingModifier() {
    super(0xffffaa);
  }

  @Override
  public int getPriority() {
    return 180;
  }
  
  @Override
  public ActionResultType onItemUse(IModifierToolStack tool, int level, ItemUseContext context) {
    if (tool.getCurrentDurability() >= DURABILITY_DRAIN && context.getPlayer().isSneaking()) {
      return super.onItemUse(tool, level, context);
    }
    return ActionResultType.PASS;
  }

  @Override
  protected Block getBlock(BlockItemUseContext context) {
    return TinkerCommons.glow.get();
  }
  
  @Override
  protected void onBlockPlacement(IModifierToolStack tool, ItemStack stack, PlayerEntity entity) {
    ToolDamageUtil.damage(tool, DURABILITY_DRAIN, entity, stack);
  }
}
