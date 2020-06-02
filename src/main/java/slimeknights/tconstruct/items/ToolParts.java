package slimeknights.tconstruct.items;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.registration.ItemDeferredRegister;
import slimeknights.tconstruct.library.registration.object.ItemObject;
import slimeknights.tconstruct.library.tinkering.MaterialItem;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ToolParts {

  private static final Item.Properties partProps = new Item.Properties().group(TinkerRegistry.tabParts);
  private static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(TConstruct.modID);

  public static void init() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    ITEMS.register(modEventBus);
  }

  public static ItemObject<MaterialItem> pickaxe_head = ITEMS.register("pickaxe_head", () -> new MaterialItem(partProps));
  public static ItemObject<MaterialItem> small_binding = ITEMS.register("small_binding", () -> new MaterialItem(partProps));
  public static ItemObject<MaterialItem> tool_rod = ITEMS.register("tool_rod", () -> new MaterialItem(partProps));
}
