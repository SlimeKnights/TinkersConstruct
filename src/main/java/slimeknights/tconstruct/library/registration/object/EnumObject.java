package slimeknights.tconstruct.library.registration.object;

import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EnumObject<T extends Enum<T>, I extends IForgeRegistryEntry<? super I>> {
  private Map<T,Supplier<? extends I>> map;

  public EnumObject(Map<T,Supplier<? extends I>> map) {
    this.map = map;
  }

  /**
   * Gets a block supplier for the given value
   * @param value  Value to get
   * @return  BlockItemObject
   */
  public Supplier<? extends I> getSupplier(T value) {
    return map.get(value);
  }

  /**
   * Gets the value for the given enum
   * @param value  Value to get
   * @return  Block
   */
  public I get(T value) {
    if (!map.containsKey(value)) {
      return null;
    }
    return getSupplier(value).get();
  }

  /**
   * Checks if this enum object contains the given value
   * @param value  Value to check for
   * @return  True if the value is contained, false otherwise
   */
  public boolean contains(IForgeRegistryEntry<? super I> value) {
    return this.map.values().stream().anyMatch((sup) -> sup.get() == value);
  }

  /**
   * Gets a list of values in this enum object
   * @return  List of values in the object
   */
  public List<I> values() {
    return this.map.values().stream().map(Supplier::get).collect(Collectors.toList());
  }
}
