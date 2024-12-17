package slimeknights.tconstruct.tools.modifiers.ability.ranged;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryMenuModule;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule;
import slimeknights.tconstruct.tools.modules.ranged.BulkQuiverModule;

/** @deprecated use {@link InventoryModule}, {@link BulkQuiverModule}, and {@link InventoryMenuModule} */
@Deprecated(forRemoval = true)
public class BulkQuiverModifier extends Modifier {
  private static final ResourceLocation INVENTORY_KEY = TConstruct.getResource("bulk_quiver");
  private static final Pattern ARROW = new Pattern(TConstruct.getResource("arrow"));

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(InventoryModule.builder().key(INVENTORY_KEY).pattern(ARROW)
                                         .filter(ItemPredicate.or(TinkerPredicate.ARROW, ItemPredicate.set(Items.FIREWORK_ROCKET)))
                                         .flatSlots(2));
    hookBuilder.addModule(BulkQuiverModule.INSTANCE);
    hookBuilder.addModule(InventoryMenuModule.ANY);
  }

  @Override
  public int getPriority() {
    return 60; // before crystalshot
  }
}
