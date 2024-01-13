package slimeknights.tconstruct.library.json.variable.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.serializer.GenericRegistryEntrySerializer;

/** Variable that fetches an attribute value */
public record AttributeEntityVariable(Attribute attribute) implements EntityVariable {
  public static final IGenericLoader<AttributeEntityVariable> LOADER = new GenericRegistryEntrySerializer<>("attribute", ForgeRegistries.ATTRIBUTES, AttributeEntityVariable::new, AttributeEntityVariable::attribute);

  @Override
  public float getValue(LivingEntity entity) {
    return (float)entity.getAttributeValue(attribute);
  }

  @Override
  public IGenericLoader<? extends EntityVariable> getLoader() {
    return LOADER;
  }
}
