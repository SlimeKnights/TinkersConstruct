package slimeknights.tconstruct.tools.modifiers.shared;

import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

/**
 * Shared logic for all modifiers that just grant a bonus modifier
 */
public class ExtraModifier extends Modifier {
  private final ExtraType type;
  private final ModifierSource source;
  private final int slotsPerLevel;
  public ExtraModifier(int color, ExtraType type, ModifierSource source, int slotsPerLevel) {
    super(color);
    this.type = type;
    this.source = source;
    this.slotsPerLevel = slotsPerLevel;
  }

  public ExtraModifier(int color, ExtraType type, ModifierSource source) {
    this(color, type, source, 1);
  }

  public ExtraModifier(int color) {
    this(color, ExtraType.UPGRADE, ModifierSource.SINGLE_USE);
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return source.alwaysShow() || advanced;
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT data) {
    type.add(data, source.isSingleUse() ? slotsPerLevel : level * slotsPerLevel);
  }

  @Override
  public ITextComponent getDisplayName(int level) {
    if (source.isSingleUse()) {
      return getDisplayName();
    }
    return super.getDisplayName(level);
  }

  @Override
  public int getPriority() {
    // show lower priority, the trait should be above the rest though
    return source.isSingleUse() ? 50 : 75;
  }

  /** Way this modifier is applied */
  public enum ModifierSource {
    /** Modifier can be applied once, generally hidden for simplicity */
    SINGLE_USE,
    /** Modifier can be applied multiple times, generally hidden for simplicity */
    MULTI_USE,
    /** Modifier came from a trait, should always show and is multiuse */
    TRAIT;

    /** Modifier can be used just once */
    public boolean isSingleUse() {
      return this == SINGLE_USE;
    }

    /** Modifier always shows */
    public boolean alwaysShow() {
      return this == TRAIT;
    }
  }

  /** Type of slot increased */
  public enum ExtraType {
    /** Boosts upgrade slots */
    UPGRADE {
      @Override
      public void add(ModDataNBT data, int amount) {
        data.addUpgrades(amount);
      }
    },
    /** Boosts ability slots */
    ABILITY {
      @Override
      public void add(ModDataNBT data, int amount) {
        data.addAbilities(amount);
      }
    },
    /** Boosts trait slots in the soul forge */
    TRAIT {
      @Override
      public void add(ModDataNBT data, int amount) {
        data.addTraits(amount);
      }
    };

    /** Adds this type to the block */
    public abstract void add(ModDataNBT data, int amount);
  }
}
