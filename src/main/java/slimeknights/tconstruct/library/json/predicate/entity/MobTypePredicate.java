package slimeknights.tconstruct.library.json.predicate.entity;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.NamedComponentRegistry;

/** Predicate matching a specific mob type */
@RequiredArgsConstructor
public class MobTypePredicate implements LivingEntityPredicate {
  /**
   * Registry of mob types, to allow addons to register types
   * TODO: support registering via IMC
   */
  public static final NamedComponentRegistry<MobType> MOB_TYPES = new NamedComponentRegistry<>("Unknown mob type");

  private final MobType type;

  @Override
  public boolean matches(LivingEntity input) {
    return input.getMobType() == type;
  }

  @Override
  public IGenericLoader<? extends LivingEntityPredicate> getLoader() {
    return LOADER;
  }

  /** Loader for a mob type predicate */
  public static final IGenericLoader<MobTypePredicate> LOADER = new IGenericLoader<>() {
    @Override
    public MobTypePredicate deserialize(JsonObject json) {
      return new MobTypePredicate(MOB_TYPES.deserialize(json, "mobs"));
    }

    @Override
    public MobTypePredicate fromNetwork(FriendlyByteBuf buffer) {
      return new MobTypePredicate(MOB_TYPES.fromNetwork(buffer));
    }

    @Override
    public void serialize(MobTypePredicate object, JsonObject json) {
      json.addProperty("mobs", MOB_TYPES.getKey(object.type).toString());
    }

    @Override
    public void toNetwork(MobTypePredicate object, FriendlyByteBuf buffer) {
      MOB_TYPES.toNetwork(object.type, buffer);
    }
  };
}
