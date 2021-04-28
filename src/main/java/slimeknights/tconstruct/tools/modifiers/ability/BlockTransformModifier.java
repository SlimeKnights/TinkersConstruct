package slimeknights.tconstruct.tools.modifiers.ability;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.item.IModifiableHarvest;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class BlockTransformModifier extends SingleUseModifier {
  private final ToolType toolType;
  private final SoundEvent sound;
  private final boolean requireGround;
  private final int priority;

  public BlockTransformModifier(int color, int priority, ToolType toolType, SoundEvent sound, boolean requireGround) {
    super(color);
    this.priority = priority;
    this.toolType = toolType;
    this.sound = sound;
    this.requireGround = requireGround;
  }

  @Override
  public int getPriority() {
    return priority;
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return priority > Short.MIN_VALUE;
  }

  @Override
  public ActionResultType onItemUse(ToolStack tool, int level, ItemStack stack, ItemUseContext context) {
    Item item = stack.getItem();
    if (item instanceof IModifiableHarvest) {
      IModifiableHarvest toolCore = (IModifiableHarvest) item;
      return toolCore.getToolHarvestLogic().transformBlocks(context, toolType, sound, requireGround);
    }
    return ActionResultType.PASS;
  }
}
