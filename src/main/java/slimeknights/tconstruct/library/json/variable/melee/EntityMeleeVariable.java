package slimeknights.tconstruct.library.json.variable.melee;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.loader.NestedLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.json.variable.entity.EntityVariable;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * Reads an entity variable from melee content
 * @param entity   Entity variable
 * @param which    Determines whether to read the attacker or the target
 * @param fallback Fallback if the entity is not found
 */
public record EntityMeleeVariable(EntityVariable entity, WhichEntity which, float fallback) implements MeleeVariable {
  @Override
  public float getValue(IToolStackView tool, @Nullable ToolAttackContext context, @Nullable LivingEntity attacker) {
    LivingEntity entity = null;
    if (which == WhichEntity.ATTACKER) {
      entity = attacker;
    } else if (context != null) {
      entity = context.getLivingTarget();
    }
    if (entity != null) {
      return this.entity.getValue(entity);
    }
    return fallback;
  }

  @Override
  public IGenericLoader<? extends MeleeVariable> getLoader() {
    return LOADER;
  }

  public enum WhichEntity { ATTACKER, TARGET }

  public static final IGenericLoader<EntityMeleeVariable> LOADER = new IGenericLoader<>() {
    @Override
    public EntityMeleeVariable deserialize(JsonObject json) {
      NestedLoader.mapType(json, "entity_type");
      return new EntityMeleeVariable(
        EntityVariable.LOADER.deserialize(json),
        JsonHelper.getAsEnum(json, "which", WhichEntity.class),
        GsonHelper.getAsFloat(json, "fallback")
      );
    }

    @Override
    public void serialize(EntityMeleeVariable object, JsonObject json) {
      NestedLoader.serializeInto(json, "entity_type", EntityVariable.LOADER, object.entity);
      json.addProperty("which", object.which.toString().toLowerCase(Locale.ROOT));
      json.addProperty("fallback", object.fallback);
    }

    @Override
    public EntityMeleeVariable fromNetwork(FriendlyByteBuf buffer) {
      return new EntityMeleeVariable(EntityVariable.LOADER.fromNetwork(buffer), buffer.readEnum(WhichEntity.class), buffer.readFloat());
    }

    @Override
    public void toNetwork(EntityMeleeVariable object, FriendlyByteBuf buffer) {
      EntityVariable.LOADER.toNetwork(object.entity, buffer);
      buffer.writeEnum(object.which);
      buffer.writeFloat(object.fallback);
    }
  };
}
