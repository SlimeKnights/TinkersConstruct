package slimeknights.tconstruct.library.json.variable.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;

/** Variable that fetches an attribute value */
public record AttributeEntityVariable(Attribute attribute) implements EntityVariable {
  public static final RecordLoadable<AttributeEntityVariable> LOADER = RecordLoadable.create(Loadables.ATTRIBUTE.requiredField("attribute", AttributeEntityVariable::attribute), AttributeEntityVariable::new);

  @Override
  public float getValue(LivingEntity entity) {
    return (float)entity.getAttributeValue(attribute);
  }

  @Override
  public RecordLoadable<AttributeEntityVariable> getLoader() {
    return LOADER;
  }
}
