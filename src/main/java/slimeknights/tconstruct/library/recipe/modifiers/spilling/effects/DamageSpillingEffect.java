package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;

import javax.annotation.Nullable;
import java.util.Locale;

/**
 * Effect that damages an entity
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class DamageSpillingEffect implements ISpillingEffect {
  public static final Loader LOADER = new Loader();

  @Nullable
  private final EntityIngredient entity;
  private final LivingEntityPredicate predicate;
  private final DamageType type;
  private final float damage;

  public DamageSpillingEffect(EntityIngredient entity, DamageType type, float damage) {
    this(entity, LivingEntityPredicate.ANY, type, damage);
  }

  public DamageSpillingEffect(LivingEntityPredicate entity, DamageType type, float damage) {
    this(null, entity, type, damage);
  }

  public DamageSpillingEffect(DamageType type, float damage) {
    this(LivingEntityPredicate.ANY, type, damage);
  }

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    Entity target = context.getTarget();
    LivingEntity livingTarget = context.getLivingTarget();
    if ((entity == null || entity.test(target.getType())) && (livingTarget == null ? predicate == LivingEntityPredicate.ANY : predicate.test(livingTarget) )) {
      DamageSource source;
      PlayerEntity player = context.getPlayerAttacker();
      if (player != null) {
        source = DamageSource.causePlayerDamage(player);
      } else {
        source = DamageSource.causeMobDamage(context.getAttacker());
      }
      // special effects
      type.apply(source);
      ToolAttackUtil.attackEntitySecondary(source, this.damage * scale, context.getTarget(), livingTarget, true);
    }
  }

  @Override
  public ISpillingEffectLoader<?> getLoader() {
    return LOADER;
  }

  /**
   * All valid types of damage supported
   * TODO: consider how to extend this in addons
   */
  public enum DamageType {
    NORMAL {
      @Override
      public void apply(DamageSource source) {}
    },
    FIRE {
      @Override
      public void apply(DamageSource source) {
        source.setFireDamage();
      }
    },
    MAGIC {
      @Override
      public void apply(DamageSource source) {
        source.setMagicDamage();
      }
    },
    EXPLOSION {
      @Override
      public void apply(DamageSource source) {
        source.setExplosion();
      }
    },
    PIERCING {
      @Override
      public void apply(DamageSource source) {
        source.setDamageBypassesArmor();
      }
    };

    @Getter
    private final String name = this.name().toLowerCase(Locale.US);

    /** Applies this effect to the given damage source */
    public abstract void apply(DamageSource source);

    /** Gets the type for the given name */
    @Nullable
    public static DamageType byName(String name) {
      for (DamageType type : DamageType.values()) {
        if (type.getName().equals(name)) {
          return type;
        }
      }
      return null;
    }
  }

  /** TODO: consider how to extend this in addons */
  public enum LivingEntityPredicate {
    ANY {
      @Override
      public boolean test(LivingEntity living) {
        return true;
      }
    },
    WATER_SENSITIVE {
      @Override
      public boolean test(LivingEntity living) {
        return living.isWaterSensitive();
      }
    },
    UNDEAD {
      @Override
      public boolean test(LivingEntity living) {
        return living.getCreatureAttribute() == CreatureAttribute.UNDEAD;
      }
    },
    ARTHROPOD {
      @Override
      public boolean test(LivingEntity living) {
        return living.getCreatureAttribute() == CreatureAttribute.ARTHROPOD;
      }
    },
    NOT_FIRE_IMMUNE {
      @Override
      public boolean test(LivingEntity living) {
        return !living.isImmuneToFire();
      }
    };

    @Getter
    private final String name = this.name().toLowerCase(Locale.US);

    /** Checks if the entity matches this predicate */
    public abstract boolean test(LivingEntity living);

    /** Gets the type for the given name */
    @Nullable
    public static LivingEntityPredicate byName(String name) {
      for (LivingEntityPredicate type : LivingEntityPredicate.values()) {
        if (type.getName().equals(name)) {
          return type;
        }
      }
      return null;
    }
  }

  private static class Loader implements ISpillingEffectLoader<DamageSpillingEffect> {
    @Override
    public DamageSpillingEffect deserialize(JsonObject json) {
      EntityIngredient entity = null;
      LivingEntityPredicate predicate = LivingEntityPredicate.ANY;
      if (json.has("entity")) {
        JsonElement element = JsonHelper.getElement(json, "entity");
        if (element.isJsonPrimitive()) {
          predicate = LivingEntityPredicate.byName(element.getAsString());
          if (predicate == null) {
            throw new JsonSyntaxException("Invalid entity predicate " + element.getAsString());
          }
        } else {
          entity = EntityIngredient.deserialize(element);
        }
      }
      String typeName = JSONUtils.getString(json, "damage_type");
      DamageType type = DamageType.byName(typeName);
      if (type == null) {
        throw new JsonSyntaxException("Unknown damage type '" + typeName + "'");
      }
      float damage = JSONUtils.getFloat(json, "damage_amount");
      return new DamageSpillingEffect(entity, predicate, type, damage);
    }

    @Override
    public DamageSpillingEffect read(PacketBuffer buffer) {
      EntityIngredient entity = null;
      if (buffer.readBoolean()) {
        entity = EntityIngredient.read(buffer);
      }
      LivingEntityPredicate predicate = buffer.readEnumValue(LivingEntityPredicate.class);
      DamageType type = buffer.readEnumValue(DamageType.class);
      float damage = buffer.readFloat();
      return new DamageSpillingEffect(entity, predicate, type, damage);
    }

    @Override
    public void serialize(DamageSpillingEffect effect, JsonObject json) {
      if (effect.entity != null) {
        json.add("entity", effect.entity.serialize());
      } else if (effect.predicate != LivingEntityPredicate.ANY) {
        json.addProperty("entity", effect.predicate.getName());
      }
      json.addProperty("damage_type", effect.type.getName());
      json.addProperty("damage_amount", effect.damage);
    }

    @Override
    public void write(DamageSpillingEffect effect, PacketBuffer buffer) {
      if (effect.entity != null) {
        buffer.writeBoolean(true);
        effect.entity.write(buffer);
      } else {
        buffer.writeBoolean(false);
      }
      buffer.writeEnumValue(effect.predicate);
      buffer.writeEnumValue(effect.type);
      buffer.writeFloat(effect.damage);
    }
  }
}
