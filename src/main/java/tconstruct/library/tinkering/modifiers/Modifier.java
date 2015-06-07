package tconstruct.library.tinkering.modifiers;

import tconstruct.library.TinkerRegistry;

public abstract class Modifier implements IModifier {
  private final String identifier;

  public Modifier(String identifier) {
    this.identifier = identifier;

    TinkerRegistry.registerModifier(this);
  }

  @Override
  public String getIdentifier() {
    return identifier;
  }
}
