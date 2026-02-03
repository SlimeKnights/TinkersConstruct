package slimeknights.tconstruct.library.modifiers;

import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

import java.util.function.Supplier;

/**
 * Helper for moving static modifier fields which contain callable methods to JSON registered without breaking existing addons.
 * @param <T>  Modifier type
 */
public class FakeModifier<T extends Modifier> extends StaticModifier<T> {
  private final Supplier<T> supplier;
  public FakeModifier(ModifierId id, Supplier<T> supplier) {
    super(id);
    this.supplier = supplier;
  }

  @Override
  protected Modifier getUnchecked() {
    if (result == null) {
      result = supplier.get();
      result.setId(id);
    }
    return result;
  }
}
