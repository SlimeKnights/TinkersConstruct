package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.armor.MaxArmorAttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.data.ModifierIds;

/** @deprecated use {@link MaxArmorAttributeModule} and {@link ProtectionModule} */
@Deprecated(forRemoval = true)
public class ProjectileProtectionModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(MaxArmorAttributeModule.builder(Attributes.KNOCKBACK_RESISTANCE, Operation.ADDITION).heldTag(TinkerTags.Items.HELD).uniqueFrom(ModifierIds.projectileProtection).eachLevel(0.05f));
    hookBuilder.addModule(ProtectionModule.builder().sources(DamageSourcePredicate.CAN_PROTECT, DamageSourcePredicate.PROJECTILE).eachLevel(2.5f));
  }
}
