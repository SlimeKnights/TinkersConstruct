package slimeknights.tconstruct.tools.modifiers.traits.general;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.slotless.OverslimeModifier;

public class OvergrowthModifier extends Modifier {
  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    TinkerModifiers.overslime.get().setFriend(volatileData);
  }

  @Override
  public void onInventoryTick(IToolStackView tool, int level, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
    // update 1 times a second, but skip when active (messes with pulling bow back)
    if (!world.isClientSide && holder.tickCount % 20 == 0 && holder.getUseItem() != stack) {
      // ensure we have overslime
      OverslimeModifier overslime = TinkerModifiers.overslime.get();
      int current = overslime.getOverslime(tool);
      int cap = overslime.getCapacity(tool);
      // has a 5% chance of restoring each second per level
      if (current < cap && RANDOM.nextFloat() < (level * 0.05)) {
        overslime.addOverslime(tool, 1);
      }
    }
  }
}
