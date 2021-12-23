package slimeknights.tconstruct.tools.modifiers.internal;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.modifiers.base.InteractionModifier;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.item.IModifiableHarvest;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class BlockTransformModifier extends InteractionModifier.SingleUse {
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
  public ActionResultType afterBlockUse(IModifierToolStack tool, int level, ItemUseContext context, EquipmentSlotType slotType) {
    // tool must not be broken
    if (tool.isBroken()) {
      return ActionResultType.PASS;
    }

    Item item = tool.getItem();
    ToolHarvestLogic harvestLogic;
    if (item instanceof IModifiableHarvest) {
      harvestLogic = ((IModifiableHarvest)item).getToolHarvestLogic();
    } else {
      harvestLogic = ToolHarvestLogic.DEFAULT;
    }
    return harvestLogic.transformBlocks(tool, context, toolType, sound, requireGround);
  }
}
