package slimeknights.tconstruct.library.modifiers.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

/**
 * Shared logic for all modifiers that just grant a bonus modifier
 */
@RequiredArgsConstructor
public class ExtraModifier extends Modifier {
  private final SlotType type;
  private final ModifierSource source;
  private final int slotsPerLevel;

  public ExtraModifier(SlotType type, ModifierSource source) {
    this(type, source, 1);
  }

  public ExtraModifier() {
    this(SlotType.UPGRADE, ModifierSource.SINGLE_LEVEL);
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return source.alwaysShow() || advanced;
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    volatileData.addSlots(type, level * slotsPerLevel);
  }

  @Override
  public Component getDisplayName(int level) {
    if (source.isSingleLevel() && level == 1) {
      return getDisplayName();
    }
    return super.getDisplayName(level);
  }

  @Override
  public int getPriority() {
    // show lower priority, the trait should be above the rest though
    return source.isSingleLevel() ? 50 : 60;
  }

  /** Way this modifier is applied */
  public enum ModifierSource {
    /** Modifier can be applied once, generally hidden for simplicity */
    SINGLE_LEVEL,
    /** Modifier can be applied multiple times, generally hidden for simplicity */
    MULTI_LEVEL,
    /** Modifier came from a trait, should always show and is multiuse */
    TRAIT;

    /** Modifier can be used just once */
    public boolean isSingleLevel() {
      return this == SINGLE_LEVEL;
    }

    /** Modifier always shows */
    public boolean alwaysShow() {
      return this == TRAIT;
    }
  }
}
