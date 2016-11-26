package slimeknights.tconstruct.plugin.jei;

import mezz.jei.api.recipe.IFocus;

// this is simply a private copy since the JEI implementation is not in the API
public class Focus<V> implements IFocus<V> {
  private final Mode mode;
  private final V value;

  public Focus(Mode mode, V value) {
    this.mode = mode;
    this.value = value;
  }

  @Override
  public V getValue() {
    return value;
  }

  @Override
  public Mode getMode() {
    return mode;
  }
}