package slimeknights.tconstruct.tools.modifiers.internal;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.common.ToolAction;
import slimeknights.tconstruct.library.modifiers.base.InteractionModifier;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.item.IModifiableHarvest;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class BlockTransformModifier extends InteractionModifier.SingleUse {
  private final ToolAction toolAction;
  private final SoundEvent sound;
  private final boolean requireGround;
  private final int priority;

  public BlockTransformModifier(int color, int priority, ToolAction toolAction, SoundEvent sound, boolean requireGround) {
    super(color);
    this.priority = priority;
    this.toolAction = toolAction;
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
  public InteractionResult afterBlockUse(IModifierToolStack tool, int level, UseOnContext context, EquipmentSlot slotType) {
    // tool must not be broken
    if (tool.isBroken()) {
      return InteractionResult.PASS;
    }

    Item item = tool.getItem();
    ToolHarvestLogic harvestLogic;
    if (item instanceof IModifiableHarvest) {
      harvestLogic = ((IModifiableHarvest)item).getToolHarvestLogic();
    } else {
      harvestLogic = ToolHarvestLogic.DEFAULT;
    }
    return harvestLogic.transformBlocks(tool, context, toolAction, sound, requireGround);
  }
}
