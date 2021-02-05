package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.free.OverslimeModifier;

public class OvergrowthModifier extends Modifier {
  public OvergrowthModifier() {
    super(0x82c873);
  }

  @Override
  public void addVolatileData(IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    volatileData.putBoolean(OverslimeModifier.KEY_OVERSLIME_FRIEND, true);
  }

  @Override
  public void onInventoryTick(IModifierToolStack tool, int level, World world, Entity holder, boolean isHeld, boolean isActive) {
    // update 1 times a second, but skip when active (messes with pulling bow back)
    if (!isActive && !world.isRemote && holder.ticksExisted % 20 == 0) {
      // ensure we have overslime
      if (tool.getModifierLevel(TinkerModifiers.overslime.get()) > 0) {
        int overslime = OverslimeModifier.getOverslime(tool);
        int cap = OverslimeModifier.getCap(tool);
        // has a 5% chance of restoring each second per level
        if (overslime < cap && RANDOM.nextFloat() < (level * 0.05)) {
          OverslimeModifier.addOverslime(tool, 1);
        }
      }
    }
  }
}
