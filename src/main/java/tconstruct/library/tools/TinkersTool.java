package tconstruct.library.tools;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import tconstruct.library.tinkering.PartMaterialWrapper;
import tconstruct.library.tinkering.TinkersItem;
import tconstruct.library.utils.TooltipBuilder;

/**
 * Intermediate abstraction layer for all tools/melee weapons. This class has all the callbacks for blocks and enemies
 * so tools and weapons can share behaviour.
 */
public abstract class TinkersTool extends TinkersItem {

  public TinkersTool(PartMaterialWrapper... requiredComponents) {
    super(requiredComponents);
  }

  @Override
  public float getDigSpeed(ItemStack itemstack, IBlockState state) {
    return ToolHelper.calcDigSpeed(itemstack, state);
  }

  @Override
  public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
    // deal damage
    return true;
  }

  @Override
  public String[] getInformation(ItemStack stack) {
    TooltipBuilder info = new TooltipBuilder(stack);

    info.addDurability();
    info.addHarvestLevel();
    info.addMiningSpeed();
    info.addAttack();

    return info.getTooltip();
  }
}
