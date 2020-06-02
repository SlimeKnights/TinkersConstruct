package slimeknights.tconstruct.library.registration.object;

import com.mojang.datafixers.util.Pair;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Map;
import java.util.function.Supplier;

public class EnumPairObject<F extends Enum<F>, S extends Enum<S>, I extends IForgeRegistryEntry<? super I>> {
  private Map<Pair<F,S>,Supplier<? extends I>> map;

  public EnumPairObject(Map<Pair<F,S>,Supplier<? extends I>> map) {
    this.map = map;
  }

  /**
   * Gets a block supplier for the given value
   * @param first  First key
   * @param second Second key
   * @return  Value supplier
   */
  public Supplier<? extends I> getSupplier(F first, S second) {
    return map.get(Pair.of(first, second));
  }

  /**
   * Gets the value for the given enum
   * @param first  First key
   * @param second Second key
   * @return  Value
   */
  public I get(F first, S second) {
    Pair<F,S> key = Pair.of(first, second);
    if (!map.containsKey(key)) {
      return null;
    }
    return map.get(key).get();
  }
}
