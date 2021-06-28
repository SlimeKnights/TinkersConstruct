package slimeknights.tconstruct.tools.modifiers.free;

import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

/**
 * Simple modifier that sets a flag in volatile mod data to true
 */
public class VolatileFlagModifier extends SingleUseModifier {
  private final Rarity rarity;
  private final ResourceLocation flag;
  public VolatileFlagModifier(int color, ResourceLocation flag, Rarity rarity) {
    super(color);
    this.flag = flag;
    this.rarity = rarity;
  }

  public VolatileFlagModifier(int color, ResourceLocation flag) {
    this(color, flag, Rarity.COMMON);
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    if (rarity != Rarity.COMMON) {
      ToolCore.setRarity(volatileData, rarity);
    }
    volatileData.putBoolean(flag, true);
  }
}
