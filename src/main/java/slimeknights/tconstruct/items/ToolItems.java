package slimeknights.tconstruct.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.tools.TestTool;
import slimeknights.tconstruct.tools.ToolDefinitions;

import static slimeknights.tconstruct.common.TinkerPulse.injected;

@ObjectHolder(TConstruct.modID)
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ToolItems {

  public static final Item green_slime_crystal = injected();
  public static final Item blue_slime_crystal = injected();
  public static final Item magma_slime_crystal = injected();
  public static final Item width_expander = injected();
  public static final Item height_expander = injected();
  public static final Item reinforcement = injected();
  public static final Item silky_cloth = injected();
  public static final Item silky_jewel = injected();
  public static final Item necrotic_bone = injected();
  public static final Item moss = injected();
  public static final Item mending_moss = injected();
  public static final Item creative_modifier = injected();

  public static final MaterialItem test_part = new MaterialItem(new Item.Properties().group(TinkerRegistry.tabParts));
  public static final ToolCore test_tool = injected();

  @SubscribeEvent
  static void registerItems(final RegistryEvent.Register<Item> event) {
    BaseRegistryAdapter<Item> registry = new BaseRegistryAdapter<>(event.getRegistry());
    ItemGroup tabGeneral = TinkerRegistry.tabGeneral;

    registerTools(registry);

    // modifier items
    registry.register(new Item(new Item.Properties().group(tabGeneral)), "green_slime_crystal");
    registry.register(new Item(new Item.Properties().group(tabGeneral)), "blue_slime_crystal");
    registry.register(new Item(new Item.Properties().group(tabGeneral)), "magma_slime_crystal");
    registry.register(new Item(new Item.Properties().group(tabGeneral)), "width_expander");
    registry.register(new Item(new Item.Properties().group(tabGeneral)), "height_expander");
    registry.register(new Item(new Item.Properties().group(tabGeneral)), "reinforcement");
    registry.register(new Item(new Item.Properties().group(tabGeneral)), "silky_cloth");
    registry.register(new Item(new Item.Properties().group(tabGeneral)), "silky_jewel");
    registry.register(new Item(new Item.Properties().group(tabGeneral)), "necrotic_bone");
    registry.register(new Item(new Item.Properties().group(tabGeneral)), "moss");
    registry.register(new Item(new Item.Properties().group(tabGeneral)), "mending_moss");
    registry.register(new Item(new Item.Properties().group(tabGeneral)), "creative_modifier");
  }

  private static void registerTools(BaseRegistryAdapter<Item> registry) {
    registry.register(test_part, "test_part");
    registry.register(new TestTool(new Item.Properties().group(TinkerRegistry.tabTools), ToolDefinitions.PICKAXE), "test_tool");
  }
  private ToolItems() {}
}
