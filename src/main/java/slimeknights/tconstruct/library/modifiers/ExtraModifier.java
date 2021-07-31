package slimeknights.tconstruct.library.modifiers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

/**
 * Shared logic for all modifiers that just grant a bonus modifier
 */
public class ExtraModifier extends Modifier {
  private final SlotType type;
  private final ModifierSource source;
  private final int slotsPerLevel;

  public ExtraModifier(int color, SlotType type, ModifierSource source, int slotsPerLevel) {
    super(color);
    this.type = type;
    this.source = source;
    this.slotsPerLevel = slotsPerLevel;
  }

  public ExtraModifier(int color, SlotType type, ModifierSource source) {
    this(color, type, source, 1);
  }

  public ExtraModifier(int color) {
    this(color, ExtraType.UPGRADE, ModifierSource.SINGLE_LEVEL);
  }

  /** @deprecated use {@link #ExtraModifier(int, SlotType, ModifierSource, int)} */
  @Deprecated
  public ExtraModifier(int color, ExtraType type, ModifierSource source, int slotsPerLevel) {
    this(color, type.getSlotType(), source, slotsPerLevel);
  }

  /** @deprecated use {@link #ExtraModifier(int, SlotType, ModifierSource)} */
  @Deprecated
  public ExtraModifier(int color, ExtraType type, ModifierSource source) {
    this(color, type, source, 1);
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return source.alwaysShow() || advanced;
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT data) {
    data.addSlots(type, level * slotsPerLevel);
  }

  @Override
  public ITextComponent getDisplayName(int level) {
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

  /** @deprecated use {@link SlotType} */
  @RequiredArgsConstructor
  @Deprecated
  public enum ExtraType {
    /** Boosts upgrade slots */
    UPGRADE(SlotType.UPGRADE),
    /** Boosts ability slots */
    ABILITY(SlotType.ABILITY),
    /** Boosts trait slots in the soul forge */
    TRAIT(SlotType.TRAIT);

    @Getter
    private final SlotType slotType;
  }
}
