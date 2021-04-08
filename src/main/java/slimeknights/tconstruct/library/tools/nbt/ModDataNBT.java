package slimeknights.tconstruct.library.tools.nbt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import java.util.function.BiFunction;

/**
 * NBT representing extra data on the tool, including modifier slots and a wrapper around the compound for namespaced data.
 * On a typical tool, there are two copies of this class, one for persistent data, and one that rebuilds when the modifiers refresh
 * Note unlike other NBT classes, the data inside this one is mutable as most of it is directly used by the tools.
 */
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ModDataNBT implements IModDataReadOnly {
  protected static final String TAG_UPGRADES = "upgrades";
  protected static final String TAG_ABILITIES = "abilities";
  protected static final String TAG_TRAITS = "traits";

  /** Compound representing modifier data */
  @Getter(AccessLevel.PROTECTED)
  private final CompoundTag data;

  /** Upgrades remaining in this data */
  @Getter
  private int upgrades;
  /** Abilities remaining in this data */
  @Getter
  private int abilities;
  /** Trait remaining in this data, for use in the soul forge */
  @Getter
  private int traits;

  /**
   * Creates a new mod data containing empty data
   */
  public ModDataNBT() {
    this(new CompoundTag(), 0, 0, 0);
  }

  /** Updates the upgrade slots */
  public void setUpgrades(int value) {
    this.upgrades = value;
    data.putInt(TAG_UPGRADES, value);
  }

  /** Adds the given number of upgrades, use negative to remove */
  public void addUpgrades(int add) {
    if (add != 0) {
      setUpgrades(upgrades + add);
    }
  }

  /** Updates the ability slots */
  public void setAbilities(int value) {
    this.abilities = value;
    data.putInt(TAG_ABILITIES, value);
  }

  /** Adds the given number of ability slots, use negative to remove */
  public void addAbilities(int add) {
    if (add != 0) {
      setAbilities(abilities + add);
    }
  }

  /** Updates the bonus trait slots, used in the soul forge */
  public void setTraits(int value) {
    this.traits = value;
    data.putInt(TAG_TRAITS, value);
  }

  /** Adds the given number of trait slots, use negative to remove */
  public void addTraits(int add) {
    if (add != 0) {
      setTraits(traits + add);
    }
  }

  @Override
  public <T> T get(Identifier name, BiFunction<CompoundTag,String,T> function) {
    return function.apply(data, name.toString());
  }

  @Override
  public boolean contains(Identifier name, int type) {
    return data.contains(name.toString(), type);
  }

  /**
   * Sets the given NBT into the data
   * @param name  Key name
   * @param nbt   NBT value
   */
  public void put(Identifier name, Tag nbt) {
    data.put(name.toString(), nbt);
  }

  /**
   * Sets an integer from the mod data
   * @param name  Name
   * @param value  Integer value
   */
  public void putInt(Identifier name, int value) {
    data.putInt(name.toString(), value);
  }

  /**
   * Sets an boolean from the mod data
   * @param name  Name
   * @param value  Boolean value
   */
  public void putBoolean(Identifier name, boolean value) {
    data.putBoolean(name.toString(), value);
  }

  /**
   * Sets an float from the mod data
   * @param name  Name
   * @param value  Float value
   */
  public void putFloat(Identifier name, float value) {
    data.putFloat(name.toString(), value);
  }

  /**
   * Reads a string from the mod data
   * @param name  Name
   * @param value  String value
   */
  public void putString(Identifier name, String value) {
    data.putString(name.toString(), value);
  }

  /**
   * Parses the mod data from NBT
   * @param data  data
   * @return  Parsed mod data
   */
  public static ModDataNBT readFromNBT(CompoundTag data) {
    int upgrades = data.getInt(TAG_UPGRADES);
    int abilities = data.getInt(TAG_ABILITIES);
    int traits = data.getInt(TAG_TRAITS);
    return new ModDataNBT(data, upgrades, abilities, traits);
  }
}
