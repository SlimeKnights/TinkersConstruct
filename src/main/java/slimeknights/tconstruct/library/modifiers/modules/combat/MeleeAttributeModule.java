package slimeknights.tconstruct.library.modifiers.modules.combat;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Adds an attribute modifier to the mob before hitting, then removes the modifier after hitting.
 * @param unique     Unique string used to generate the UUID and as the attribute name
 * @param attribute  Attribute to apply
 * @param uuid       UUID generated via {@link UUID#nameUUIDFromBytes(byte[])}
 * @param operation  Attribute operation
 * @param amount     Amount of the attribute to apply
 * @param scale      If true, multiples the amount by the modifier level. If false, amount is flat
 */
public record MeleeAttributeModule(String unique, Attribute attribute, UUID uuid, Operation operation, float amount, boolean scale) implements ModifierModule, MeleeHitModifierHook {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.MELEE_HIT);

  public MeleeAttributeModule(String unique, Attribute attribute, Operation operation, float amount, boolean scale) {
    this(unique, attribute, UUID.nameUUIDFromBytes(unique.getBytes()), operation, amount, scale);
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    LivingEntity target = context.getLivingTarget();
    if (target != null) {
      AttributeInstance instance = target.getAttribute(attribute);
      if (instance != null) {
        instance.addTransientModifier(new AttributeModifier(uuid, unique, scale ? amount * modifier.getEffectiveLevel(tool) : amount, operation));
      }
    }
    return baseKnockback;
  }

  private void removeAttribute(@Nullable LivingEntity target) {
    if (target != null) {
      AttributeInstance instance = target.getAttribute(attribute);
      if (instance != null) {
        instance.removeModifier(uuid);
      }
    }
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    removeAttribute(context.getLivingTarget());
  }

  @Override
  public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
    removeAttribute(context.getLivingTarget());
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<MeleeAttributeModule> LOADER = new IGenericLoader<>() {
    @Override
    public MeleeAttributeModule deserialize(JsonObject json) {
      String unique = GsonHelper.getAsString(json, "unique");
      Attribute attribute = JsonHelper.getAsEntry(ForgeRegistries.ATTRIBUTES, json, "attribute");
      Operation op = JsonHelper.getAsEnum(json, "operation", Operation.class);
      float amount = GsonHelper.getAsFloat(json, "amount");
      boolean scale = GsonHelper.getAsBoolean(json, "scale");
      return new MeleeAttributeModule(unique, attribute, op, amount, scale);
    }

    @Override
    public void serialize(MeleeAttributeModule object, JsonObject json) {
      json.addProperty("unique", object.unique);
      json.addProperty("attribute", Objects.requireNonNull(object.attribute.getRegistryName()).toString());
      json.addProperty("operation", object.operation.name().toLowerCase(Locale.ROOT));
      json.addProperty("amount", object.amount);
      json.addProperty("scale", object.scale);
    }

    @Override
    public MeleeAttributeModule fromNetwork(FriendlyByteBuf buffer) {
      String unique = buffer.readUtf(Short.MAX_VALUE);
      Attribute attribute = buffer.readRegistryIdUnsafe(ForgeRegistries.ATTRIBUTES);
      Operation operation = buffer.readEnum(Operation.class);
      float amount = buffer.readFloat();
      boolean scale = buffer.readBoolean();
      return new MeleeAttributeModule(unique, attribute, operation, amount, scale);
    }

    @Override
    public void toNetwork(MeleeAttributeModule object, FriendlyByteBuf buffer) {
      buffer.writeUtf(object.unique);
      buffer.writeRegistryIdUnsafe(ForgeRegistries.ATTRIBUTES, object.attribute);
      buffer.writeEnum(object.operation);
      buffer.writeFloat(object.amount);
      buffer.writeBoolean(object.scale);
    }
  };
}
