package slimeknights.tconstruct.items;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.tinkering.MaterialItem;

@ObjectHolder(TConstruct.modID)
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ToolParts {

  private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, TConstruct.modID);

  static {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    ITEMS.register(modEventBus);
  }

  public static RegistryObject<MaterialItem> pickaxe_head = registerNewPart("pickaxe_head");
  public static RegistryObject<MaterialItem> small_binding = registerNewPart("small_binding");
  public static RegistryObject<MaterialItem> tool_rod = registerNewPart("tool_rod");

  private static RegistryObject<MaterialItem> registerNewPart(String name) {
    return ITEMS.register(name, () -> new MaterialItem(new Item.Properties().group(TinkerRegistry.tabParts)));
  }
}
