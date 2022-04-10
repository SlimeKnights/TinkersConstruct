package slimeknights.tconstruct.library.tools.definition.weapon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Objects;

/** Weapon attack that just spawns an extra particle */
@RequiredArgsConstructor
public class ParticleWeaponAttack implements IWeaponAttack {
  public static final Loader LOADER = new Loader();

  private final SimpleParticleType particle;

  @Override
  public boolean dealDamage(IToolStackView tool, ToolAttackContext context, float damage) {
    boolean hit = ToolAttackUtil.dealDefaultDamage(context.getAttacker(), context.getTarget(), damage);
    if (hit && context.isFullyCharged()) {
      ToolAttackUtil.spawnAttackParticle(particle, context.getAttacker(), 0.8d);
    }
    return hit;
  }

  @Override
  public IGenericLoader<? extends IWeaponAttack> getLoader() {
    return LOADER;
  }

  private static class Loader implements IGenericLoader<ParticleWeaponAttack> {
    @Override
    public ParticleWeaponAttack deserialize(JsonObject json) {
      ResourceLocation location = JsonHelper.getResourceLocation(json, "particle");
      if (!ForgeRegistries.PARTICLE_TYPES.containsKey(location)) {
        throw new JsonSyntaxException("Unknown particle ID " + location);
      }
      ParticleType<?> type = Objects.requireNonNull(ForgeRegistries.PARTICLE_TYPES.getValue(location));
      if (type instanceof SimpleParticleType simple) {
        return new ParticleWeaponAttack(simple);
      }
      throw new JsonSyntaxException("Particle " + type + " be a simple particle, got " + type);
    }

    @Override
    public ParticleWeaponAttack fromNetwork(FriendlyByteBuf buffer) {
      ParticleType<?> type = buffer.readRegistryIdUnsafe(ForgeRegistries.PARTICLE_TYPES);
      if (type instanceof SimpleParticleType simple) {
        return new ParticleWeaponAttack(simple);
      }
      throw new DecoderException("Particle " + type + " be a simple particle, got " + type);
    }

    @Override
    public void serialize(ParticleWeaponAttack object, JsonObject json) {
      json.addProperty("particle", Objects.requireNonNull(object.particle.getRegistryName()).toString());
    }

    @Override
    public void toNetwork(ParticleWeaponAttack object, FriendlyByteBuf buffer) {
      buffer.writeRegistryIdUnsafe(ForgeRegistries.PARTICLE_TYPES, object.particle);
    }
  }
}
