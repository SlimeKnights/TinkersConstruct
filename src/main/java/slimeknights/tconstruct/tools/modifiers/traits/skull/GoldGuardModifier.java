package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.modules.armor.GoldenAttributeModule;

/** @deprecated use {@link GoldenAttributeModule} with {@link Attributes#MAX_HEALTH} */
@Deprecated(forRemoval = true)
public class GoldGuardModifier extends NoLevelsModifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(GoldenAttributeModule.builder(Attributes.MAX_HEALTH, Operation.ADDITION).amount(4, 4));
  }
}
