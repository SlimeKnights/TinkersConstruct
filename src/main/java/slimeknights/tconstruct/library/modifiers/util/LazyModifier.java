package slimeknights.tconstruct.library.modifiers.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.tags.TagKey;
import slimeknights.mantle.registration.object.IdAwareObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

import java.util.Objects;
import java.util.function.Supplier;

/** Supplier that will return a modifier once they are fully registered, typically used with {@link ModifierDeferredRegister} */
@RequiredArgsConstructor
public class LazyModifier implements Supplier<Modifier>, IdAwareObject {
  /** ID of the modifier to fetch */
  @Getter
  protected final ModifierId id;
  /** Cached value for the modifier */
  protected Modifier result;

  public LazyModifier(Modifier modifier) {
    this.id = modifier.getId();
    this.result = modifier;
  }

  public LazyModifier(LazyModifier modifier) {
    this.id = modifier.id;
    this.result = modifier.result;
  }

  /** Gets the modifier, using the cached value if fetched before */
  protected Modifier getUnchecked() {
    if (result == null) {
      result = ModifierManager.getValue(id);
      if (result == ModifierManager.INSTANCE.getDefaultValue() && !ModifierManager.EMPTY.equals(id)) {
        TConstruct.LOG.error("Attempted to fetch modifier with ID {}, but it was not registered. Returning the empty modifier and hoping things don't break.", id);
      }
    }
    return result;
  }

  /** Returns true if this static modifier has a value. A return of true here means a useful value is returned by {@link #get()} */
  public boolean isBound() {
    if (!ModifierManager.INSTANCE.isDynamicModifiersLoaded()) {
      return false;
    }
    return getUnchecked() != ModifierManager.INSTANCE.getDefaultValue();
  }

  /**
   * Fetches the modifier from the modifier manager. Should not be called until after the modifier registration event fires
   * @return  Modifier instance, or default if the modifier is missing
   */
  @Override
  public Modifier get() {
    if (!ModifierManager.INSTANCE.isDynamicModifiersLoaded()) {
      throw new IllegalStateException("Attempted to load a modifier before dynamic modifiers are loaded");
      //return ModifierManager.INSTANCE.getDefaultValue();
    }
    return getUnchecked();
  }

  /** Checks if the modifier is in the given tag */
  public boolean is(TagKey<Modifier> tag) {
    return ModifierManager.isInTag(getId(), tag);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LazyModifier that = (LazyModifier)o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return "LazyModifier{" + id + '}';
  }
}
