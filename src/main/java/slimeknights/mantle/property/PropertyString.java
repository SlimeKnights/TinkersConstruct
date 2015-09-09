package slimeknights.mantle.property;

import net.minecraftforge.common.property.IUnlistedProperty;

public class PropertyString implements IUnlistedProperty<String> {

  private final String name;

  public PropertyString(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean isValid(String value) {
    return !value.isEmpty();
  }

  @Override
  public Class<String> getType() {
    return String.class;
  }

  @Override
  public String valueToString(String value) {
    return value;
  }
}
