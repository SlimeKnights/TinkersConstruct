package slimeknights.tconstruct.tools.modifiers.internal;

import com.google.common.collect.ImmutableSet;
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
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Set;

public class BlockTransformModifier extends InteractionModifier.SingleUse {
  private final Set<ToolAction> actions;
  private final SoundEvent sound;
  private final boolean requireGround;
  private final int priority;

  public BlockTransformModifier(int color, int priority, SoundEvent sound, boolean requireGround, ToolAction... actions) {
    super(color);
    this.priority = priority;
    this.actions = ImmutableSet.copyOf(actions);
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
  public boolean canPerformAction(ToolStack tool, int level, ToolAction toolAction) {
    return actions.contains(toolAction);
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
    for (ToolAction action : actions) {
      InteractionResult result = harvestLogic.transformBlocks(tool, context, action, sound, requireGround);
      if (result.consumesAction()) {
        return result;
      }
    }
    return InteractionResult.PASS;
  }
}
