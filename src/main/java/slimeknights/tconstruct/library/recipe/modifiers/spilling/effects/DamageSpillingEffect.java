package slimeknights.tconstruct.library.recipe.modifiers.spilling.effects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
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

  @SuppressWarnings("unused")
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
      Player player = context.getPlayerAttacker();
      if (player != null) {
        source = DamageSource.playerAttack(player);
      } else {
        source = DamageSource.mobAttack(context.getAttacker());
      }
      // special effects
      type.apply(source);
      ToolAttackUtil.attackEntitySecondary(source, this.damage * scale, context.getTarget(), livingTarget, true);
    }
  }

  @Override
  public IGenericLoader<?> getLoader() {
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
        source.setIsFire();
      }
    },
    MAGIC {
      @Override
      public void apply(DamageSource source) {
        source.setMagic();
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
        source.bypassArmor();
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
        return living.isSensitiveToWater();
      }
    },
    UNDEAD {
      @Override
      public boolean test(LivingEntity living) {
        return living.getMobType() == MobType.UNDEAD;
      }
    },
    ARTHROPOD {
      @Override
      public boolean test(LivingEntity living) {
        return living.getMobType() == MobType.ARTHROPOD;
      }
    },
    NOT_FIRE_IMMUNE {
      @Override
      public boolean test(LivingEntity living) {
        return !living.fireImmune();
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

  private static class Loader implements IGenericLoader<DamageSpillingEffect> {
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
      String typeName = GsonHelper.getAsString(json, "damage_type");
      DamageType type = DamageType.byName(typeName);
      if (type == null) {
        throw new JsonSyntaxException("Unknown damage type '" + typeName + "'");
      }
      float damage = GsonHelper.getAsFloat(json, "damage_amount");
      return new DamageSpillingEffect(entity, predicate, type, damage);
    }

    @Override
    public DamageSpillingEffect fromNetwork(FriendlyByteBuf buffer) {
      EntityIngredient entity = null;
      if (buffer.readBoolean()) {
        entity = EntityIngredient.read(buffer);
      }
      LivingEntityPredicate predicate = buffer.readEnum(LivingEntityPredicate.class);
      DamageType type = buffer.readEnum(DamageType.class);
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
    public void toNetwork(DamageSpillingEffect effect, FriendlyByteBuf buffer) {
      if (effect.entity != null) {
        buffer.writeBoolean(true);
        effect.entity.write(buffer);
      } else {
        buffer.writeBoolean(false);
      }
      buffer.writeEnum(effect.predicate);
      buffer.writeEnum(effect.type);
      buffer.writeFloat(effect.damage);
    }
  }
}
