package slimeknights.tconstruct.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import slimeknights.mantle.item.GeneratedItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.library.TinkerRegistry;

import static slimeknights.tconstruct.common.TinkerPulse.injected;

@ObjectHolder(TConstruct.modID)
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ToolItems {

  public static final GeneratedItem green_slime_crystal = injected();
  public static final GeneratedItem blue_slime_crystal = injected();
  public static final GeneratedItem magma_slime_crystal = injected();
  public static final GeneratedItem width_expander = injected();
  public static final GeneratedItem height_expander = injected();
  public static final GeneratedItem reinforcement = injected();
  public static final GeneratedItem silky_cloth = injected();
  public static final GeneratedItem silky_jewel = injected();
  public static final GeneratedItem necrotic_bone = injected();
  public static final GeneratedItem moss = injected();
  public static final GeneratedItem mending_moss = injected();
  public static final GeneratedItem creative_modifier = injected();

  @SubscribeEvent
  static void registerItems(final RegistryEvent.Register<Item> event) {
    BaseRegistryAdapter<Item> registry = new BaseRegistryAdapter<>(event.getRegistry());
    // todo: unify GeneratedItem
    ItemGroup tabGeneral = TinkerRegistry.tabGeneral;

    // modifier items
    registry.register(new GeneratedItem(tabGeneral), "green_slime_crystal");
    registry.register(new GeneratedItem(tabGeneral), "blue_slime_crystal");
    registry.register(new GeneratedItem(tabGeneral), "magma_slime_crystal");
    registry.register(new GeneratedItem(tabGeneral), "width_expander");
    registry.register(new GeneratedItem(tabGeneral), "height_expander");
    registry.register(new GeneratedItem(tabGeneral), "reinforcement");
    registry.register(new GeneratedItem(tabGeneral), "silky_cloth");
    registry.register(new GeneratedItem(tabGeneral), "silky_jewel");
    registry.register(new GeneratedItem(tabGeneral), "necrotic_bone");
    registry.register(new GeneratedItem(tabGeneral), "moss");
    registry.register(new GeneratedItem(tabGeneral), "mending_moss");
    registry.register(new GeneratedItem(tabGeneral), "creative_modifier");
  }

  private ToolItems() {}
}
