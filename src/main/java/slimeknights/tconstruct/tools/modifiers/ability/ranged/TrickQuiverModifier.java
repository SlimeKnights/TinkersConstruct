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
import slimeknights.tconstruct.tools.modules.ranged.TrickQuiverModule;

/** @deprecated use {@link TrickQuiverModule}, {@link InventoryModule}, and {@link InventoryMenuModule} */
@Deprecated(forRemoval = true)
public class TrickQuiverModifier extends Modifier {
  private static final ResourceLocation INVENTORY_KEY = TConstruct.getResource("trick_quiver");
  private static final Pattern TRICK_ARROW = new Pattern(TConstruct.getResource("tipped_arrow"));

  @Override
  public int getPriority() {
    return 70; // run after interaction modifiers, but before crystal shot
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(InventoryModule.builder().key(INVENTORY_KEY).pattern(TRICK_ARROW)
                                         .filter(ItemPredicate.or(TinkerPredicate.ARROW, ItemPredicate.set(Items.FIREWORK_ROCKET)))
                                         .limitPerLevel(32).flatSlots(3));
    hookBuilder.addModule(TrickQuiverModule.INSTANCE);
    hookBuilder.addModule(InventoryMenuModule.ANY);
  }
}
