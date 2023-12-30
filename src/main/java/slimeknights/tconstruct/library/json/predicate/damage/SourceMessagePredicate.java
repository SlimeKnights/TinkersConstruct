package slimeknights.tconstruct.library.json.predicate.damage;

import net.minecraft.world.damagesource.DamageSource;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.serializer.GenericStringLoader;

/** Predicate that matches a named source */
public record SourceMessagePredicate(String message) implements DamageSourcePredicate {
  public static final IGenericLoader<SourceMessagePredicate> LOADER = new GenericStringLoader<>("message", SourceMessagePredicate::new, SourceMessagePredicate::message);

  public SourceMessagePredicate(DamageSource source) {
    this(source.getMsgId());
  }

  @Override
  public boolean matches(DamageSource source) {
    return message.equals(source.getMsgId());
  }

  @Override
  public IGenericLoader<? extends IJsonPredicate<DamageSource>> getLoader() {
    return LOADER;
  }
}
