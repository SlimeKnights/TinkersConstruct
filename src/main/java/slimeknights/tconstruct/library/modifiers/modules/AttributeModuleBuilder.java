package slimeknights.tconstruct.library.modifiers.modules;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import slimeknights.tconstruct.library.json.LevelingValue;

/**
 * Shared builder logic for {@link slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule} and {@link slimeknights.tconstruct.library.modifiers.modules.combat.MeleeAttributeModule}
 * @param <T>  Builder type
 * @param <M>  Module return type
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AttributeModuleBuilder<T extends AttributeModuleBuilder<T,M>, M> extends ModifierModuleCondition.Builder<T> implements LevelingValue.Builder<M> {
  protected final Attribute attribute;
  protected final Operation operation;
  protected String unique;

  /**
   * Sets the unique string directly
   */
  @SuppressWarnings("unchecked")
  public T unique(String unique) {
    this.unique = unique;
    return (T)this;
  }

  /**
   * Sets the unique string using a resource location
   */
  public T uniqueFrom(ResourceLocation id) {
    return unique(id.getNamespace() + ".modifier." + id.getPath());
  }
}
