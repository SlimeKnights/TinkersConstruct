package slimeknights.tconstruct.library.tools.nbt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiFunction;

/**
 * NBT representing extra data on the tool, including modifier slots and a wrapper around the compound for namespaced data.
 * On a typical tool, there are two copies of this class, one for persistent data, and one that rebuilds when the modifiers refresh
 * Note unlike other NBT classes, the data inside this one is mutable as most of it is directly used by the tools.
 */
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ModDataNBT implements IModDataReadOnly {
  protected static final String TAG_UPGRADES = "upgrades";
  protected static final String TAG_ABILITIES = "abilities";

  /** Compound representing modifier data */
  @Getter(AccessLevel.PROTECTED)
  private final CompoundNBT data;

  /** Upgrades remaining in this data */
  @Getter
  private int upgrades;
  /** Abilities remaining in this data */
  @Getter
  private int abilities;

  /**
   * Creates a new mod data containing empty data
   */
  public ModDataNBT() {
    this(new CompoundNBT(), 0, 0);
  }

  /** Updates the upgrade slots */
  public void setUpgrades(int value) {
    this.upgrades = value;
    data.putInt(TAG_UPGRADES, value);
  }

  /** Adds the given number of upgrades, use negative to remove */
  public void addUpgrades(int add) {
    setUpgrades(upgrades + add);
  }

  /** Updates the ability slots */
  public void setAbilities(int value) {
    this.abilities = value;
    data.putInt(TAG_ABILITIES, value);
  }

  /** Adds the given number of ability slots, use negative to remove */
  public void addAbilities(int add) {
    setAbilities(abilities + add);
  }

  @Override
  public <T> T get(ResourceLocation name, BiFunction<CompoundNBT,String,T> function) {
    return function.apply(data, name.toString());
  }

  @Override
  public boolean contains(ResourceLocation name, int type) {
    return data.contains(name.toString(), type);
  }

  /**
   * Sets the given NBT into the data
   * @param name  Key name
   * @param nbt   NBT value
   */
  public void put(ResourceLocation name, INBT nbt) {
    data.put(name.toString(), nbt);
  }

  /**
   * Sets an integer from the mod data
   * @param name  Name
   * @param value  Integer value
   */
  public void putInt(ResourceLocation name, int value) {
    data.putInt(name.toString(), value);
  }

  /**
   * Sets an boolean from the mod data
   * @param name  Name
   * @param value  Boolean value
   */
  public void putBoolean(ResourceLocation name, boolean value) {
    data.putBoolean(name.toString(), value);
  }

  /**
   * Sets an float from the mod data
   * @param name  Name
   * @param value  Float value
   */
  public void putFloat(ResourceLocation name, float value) {
    data.putFloat(name.toString(), value);
  }

  /**
   * Reads a string from the mod data
   * @param name  Name
   * @param value  String value
   */
  public void putString(ResourceLocation name, String value) {
    data.putString(name.toString(), value);
  }

  /**
   * Parses the mod data from NBT
   * @param data  data
   * @return  Parsed mod data
   */
  public static ModDataNBT readFromNBT(CompoundNBT data) {
    int upgrades = data.getInt(TAG_UPGRADES);
    int abilities = data.getInt(TAG_ABILITIES);
    return new ModDataNBT(data, upgrades, abilities);
  }
}
