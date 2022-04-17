package slimeknights.tconstruct.library.modifiers.dynamic;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.JsonUtils;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/** Modifier that boosts damage against a conditional mob, applying an optional potion effect */
@RequiredArgsConstructor
public class ConditionalDamageModifier extends IncrementalModifier {
  /** Requirement for entities to match */
  private final IJsonPredicate<LivingEntity> predicate;
  /** Damage bonus */
  private final float damage;
  /** Optional effect to add */
  @Nullable
  private final MobEffect effect;
  /** Optional effect level */
  private final int effectLevel;

  public ConditionalDamageModifier(IJsonPredicate<LivingEntity> predicate, float damage) {
    this(predicate, damage, null, 0);
  }

  @Override
  public float getEntityDamage(IToolStackView tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    LivingEntity target = context.getLivingTarget();
    if (target != null && predicate.matches(target)) {
      damage += getScaledLevel(tool, level) * damage * tool.getMultiplier(ToolStats.ATTACK_DAMAGE);
    }
    return damage;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    addDamageTooltip(tool, level, damage, tooltip);
  }

  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    if (effect != null) {
      LivingEntity target = context.getLivingTarget();
      if (target != null && predicate.matches(target)) {
        int duration = 20;
        int maxBonus = (int)(10 * getScaledLevel(tool, level));
        if (maxBonus > 0) {
          duration += context.getAttacker().getRandom().nextInt(maxBonus);
        }
        target.addEffect(new MobEffectInstance(effect, duration, effectLevel - 1));
      }
    }
    return 0;
  }

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return LOADER;
  }

  /** Loader for this modifier */
  public static final IGenericLoader<ConditionalDamageModifier> LOADER = new IGenericLoader<>() {
    @Override
    public ConditionalDamageModifier deserialize(JsonObject json) {
      IJsonPredicate<LivingEntity> predicate = LivingEntityPredicate.LOADER.getAndDeserialize(json, "entity");
      float damage = GsonHelper.getAsFloat(json, "damage");
      MobEffect effect = null;
      int level = 0;
      if (json.has("effect")) {
        JsonObject effectJson = GsonHelper.getAsJsonObject(json, "effect");
        effect = JsonUtils.getAsEntry(ForgeRegistries.MOB_EFFECTS, effectJson, "name");
        level = JsonUtils.getIntMin(effectJson, "level", 1);
      }
      return new ConditionalDamageModifier(predicate, damage, effect, level);
    }

    @Override
    public void serialize(ConditionalDamageModifier object, JsonObject json) {
      json.add("entity", LivingEntityPredicate.LOADER.serialize(object.predicate));
      json.addProperty("damage", object.damage);
      if (object.effect != null && object.effectLevel > 0) {
        JsonObject effectJson = new JsonObject();
        effectJson.addProperty("name", Objects.requireNonNull(object.effect.getRegistryName()).toString());
        effectJson.addProperty("level", object.effectLevel);
        json.add("effect", effectJson);
      }
    }

    @Override
    public ConditionalDamageModifier fromNetwork(FriendlyByteBuf buffer) {
      IJsonPredicate<LivingEntity> predicate = LivingEntityPredicate.LOADER.fromNetwork(buffer);
      float damage = buffer.readFloat();
      MobEffect effect = null;
      int level = buffer.readVarInt();
      if (level > 0) {
        effect = buffer.readRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS);
      }
      return new ConditionalDamageModifier(predicate, damage, effect, level);
    }

    @Override
    public void toNetwork(ConditionalDamageModifier object, FriendlyByteBuf buffer) {
      LivingEntityPredicate.LOADER.toNetwork(object.predicate, buffer);
      buffer.writeFloat(object.damage);
      if (object.effectLevel > 0 && object.effect != null) {
        buffer.writeVarInt(object.effectLevel);
        buffer.writeRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS, object.effect);
      } else {
        buffer.writeVarInt(0);
      }
    }
  };
}
