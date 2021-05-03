package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.free.OverslimeModifier;

public class OvergrowthModifier extends Modifier {
  public OvergrowthModifier() {
    super(0x82c873);
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    TinkerModifiers.overslime.get().setFriend(volatileData);
  }

  @Override
  public void onInventoryTick(IModifierToolStack tool, int level, World world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
    // update 1 times a second, but skip when active (messes with pulling bow back)
    if (!world.isRemote && holder.ticksExisted % 20 == 0 && holder.getActiveItemStack() != stack) {
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
