package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.shared.TinkerAttributes;
import slimeknights.tconstruct.tools.modules.armor.GoldenAttributeModule;

import javax.annotation.Nullable;
import java.util.Optional;

/** @deprecated use {@link GoldenAttributeModule} with {@link TinkerAttributes#CHRYSOPHILITE} */
@Deprecated(forRemoval = true)
public class ChrysophiliteModifier extends NoLevelsModifier {
  /** @deprecated use {@link slimeknights.tconstruct.shared.TinkerAttributes#CHRYSOPHILITE} */
  @Deprecated(forRemoval = true)
  public static final ComputableDataKey<TotalGold> TOTAL_GOLD = TConstruct.createKey("chrysophilite", TotalGold::new);

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(GoldenAttributeModule.builder(TinkerAttributes.CHRYSOPHILITE, Operation.ADDITION).amount(1, 1));
  }

  /** @deprecated use {@link GoldenAttributeModule#hasGold(EquipmentChangeContext, EquipmentSlot)} */
  @Deprecated(forRemoval = true)
  public static boolean hasGold(EquipmentChangeContext context, EquipmentSlot slotType) {
    return GoldenAttributeModule.hasGold(context, slotType);
  }

  /** @deprecated use {@link slimeknights.tconstruct.shared.TinkerAttributes#CHRYSOPHILITE} */
  @Deprecated(forRemoval = true)
  public static int getTotalGold(@Nullable Entity entity) {
    return Optional.ofNullable(entity)
                   .flatMap(e -> e.getCapability(TinkerDataCapability.CAPABILITY).resolve())
                   .map(data -> data.get(ChrysophiliteModifier.TOTAL_GOLD))
                   .map(TotalGold::getTotalGold)
                   .orElse(0);
  }


  /** @deprecated use {@link GoldenAttributeModule.TotalGold} */
  @Deprecated(forRemoval = true)
  public static class TotalGold extends GoldenAttributeModule.TotalGold {}
}
