package slimeknights.tconstruct.library.modifiers.modules;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.util.ModifierAttribute;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Module to add an attribute to a tool
 */
public record AttributeModule(ModifierAttribute attribute) implements AttributesModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.ATTRIBUTES);

  public AttributeModule(String name, Attribute attribute, Operation operation, float amount, List<EquipmentSlot> slots) {
    this(new ModifierAttribute(name, attribute, operation, amount, slots));
  }

  public AttributeModule(String name, Attribute attribute, Operation operation, float amount, EquipmentSlot... slots) {
    this(new ModifierAttribute(name, attribute, operation, amount, slots));
  }

  @Override
  public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    attribute.apply(tool, modifier.getEffectiveLevel(tool), slot, consumer);
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<AttributeModule> LOADER = new IGenericLoader<>() {
    @Override
    public AttributeModule deserialize(JsonObject json) {
      return new AttributeModule(ModifierAttribute.fromJson(json));
    }

    @Override
    public AttributeModule fromNetwork(FriendlyByteBuf buffer) {
      return new AttributeModule(ModifierAttribute.fromNetwork(buffer));
    }

    @Override
    public void serialize(AttributeModule object, JsonObject json) {
      object.attribute.toJson(json);
    }

    @Override
    public void toNetwork(AttributeModule object, FriendlyByteBuf buffer) {
      object.attribute.toNetwork(buffer);
    }
  };
}
