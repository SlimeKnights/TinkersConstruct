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
import slimeknights.tconstruct.tools.ToolDefinitions;
import slimeknights.tconstruct.tools.harvest.PickaxeTool;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ToolItems {

  private static final Item.Properties generalProps = new Item.Properties().group(TinkerRegistry.tabGeneral);
  private static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(TConstruct.modID);

  public static void init() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    ITEMS.register(modEventBus);
  }

  public static final ItemObject<Item> green_slime_crystal = ITEMS.register("green_slime_crystal", generalProps);
  public static final ItemObject<Item> blue_slime_crystal = ITEMS.register("blue_slime_crystal", generalProps);
  public static final ItemObject<Item> magma_slime_crystal = ITEMS.register("magma_slime_crystal", generalProps);
  public static final ItemObject<Item> width_expander = ITEMS.register("width_expander", generalProps);
  public static final ItemObject<Item> height_expander = ITEMS.register("height_expander", generalProps);
  public static final ItemObject<Item> reinforcement = ITEMS.register("reinforcement", generalProps);
  public static final ItemObject<Item> silky_cloth = ITEMS.register("silky_cloth", generalProps);
  public static final ItemObject<Item> silky_jewel = ITEMS.register("silky_jewel", generalProps);
  public static final ItemObject<Item> necrotic_bone = ITEMS.register("necrotic_bone", generalProps);
  public static final ItemObject<Item> moss = ITEMS.register("moss", generalProps);
  public static final ItemObject<Item> mending_moss = ITEMS.register("mending_moss", generalProps);
  public static final ItemObject<Item> creative_modifier = ITEMS.register("creative_modifier", generalProps);

  public static final ItemObject<PickaxeTool> pickaxe = ITEMS.register("pickaxe", () -> new PickaxeTool(new Item.Properties().group(TinkerRegistry.tabTools), ToolDefinitions.PICKAXE));
}
