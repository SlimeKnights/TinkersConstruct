package slimeknights.tconstruct.tools.modifiers.slotless;

import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

/**
 * Simple modifier that sets a flag in volatile mod data to true
 */
@RequiredArgsConstructor
public class VolatileFlagModifier extends NoLevelsModifier {
  private final ResourceLocation flag;
  private final Rarity rarity;

  public VolatileFlagModifier(ResourceLocation flag) {
    this(flag, Rarity.COMMON);
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    if (rarity != Rarity.COMMON) {
      IModifiable.setRarity(volatileData, rarity);
    }
    volatileData.putBoolean(flag, true);
  }
}
