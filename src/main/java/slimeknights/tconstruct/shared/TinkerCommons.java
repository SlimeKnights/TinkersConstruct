package slimeknights.tconstruct.shared;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.item.EdibleItem;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.conditions.ConfigOptionEnabledCondition;
import slimeknights.tconstruct.common.item.TinkerBookItem;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.registration.object.BlockItemObject;
import slimeknights.tconstruct.library.registration.object.BuildingBlockObject;
import slimeknights.tconstruct.library.registration.object.EnumObject;
import slimeknights.tconstruct.library.registration.object.ItemObject;
import slimeknights.tconstruct.shared.block.ClearGlassBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.shared.block.GlowBlock;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.shared.block.SlimeBlock.SlimeType;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Contains items and blocks and stuff that is shared by multiple modules, but might be required individually
 */
@SuppressWarnings("unused")
public final class TinkerCommons extends TinkerModule {
  /* Drool stimulant */
  static final Logger log = Util.getLogger("tinker_commons");

  /*
   * Blocks
   */
  public static final RegistryObject<GlowBlock> glow = BLOCKS.registerNoItem("glow", () -> new GlowBlock(builder(Material.MISCELLANEOUS, NO_TOOL, SoundType.CLOTH).hardnessAndResistance(0.0F).lightValue(14).notSolid()));
  public static final BuildingBlockObject mudBricks = BLOCKS.registerBuilding("mud_bricks", builder(Material.EARTH, ToolType.SHOVEL, SoundType.GROUND).hardnessAndResistance(2.0F), GENERAL_BLOCK_ITEM);
  // clay
  private static final Block.Properties DRIED_CLAY = builder(Material.ROCK, ToolType.PICKAXE, SoundType.STONE).hardnessAndResistance(1.5F, 20.0F);
  public static final BuildingBlockObject driedClay = BLOCKS.registerBuilding("dried_clay", DRIED_CLAY, GENERAL_BLOCK_ITEM);
  public static final BuildingBlockObject driedClayBricks = BLOCKS.registerBuilding("dried_clay_bricks", DRIED_CLAY, GENERAL_BLOCK_ITEM);
  // glass
  public static final BlockItemObject<ClearGlassBlock> clear_glass = BLOCKS.register("clear_glass", () -> new ClearGlassBlock(GENERIC_GLASS_BLOCK), GENERAL_BLOCK_ITEM);
  public static final EnumObject<GlassColor,ClearStainedGlassBlock> clearStainedGlass = BLOCKS.registerEnum(GlassColor.values(), "clear_stained_glass", (color) -> new ClearStainedGlassBlock(GENERIC_GLASS_BLOCK, color), GENERAL_BLOCK_ITEM);
  // wood
  private static final Block.Properties WOOD = builder(Material.WOOD, ToolType.AXE, SoundType.WOOD).hardnessAndResistance(2.0F, 7.0F).lightValue(7);
  public static final BuildingBlockObject lavawood = BLOCKS.registerBuilding("lavawood", WOOD, GENERAL_BLOCK_ITEM);
  public static final BuildingBlockObject firewood = BLOCKS.registerBuilding("firewood", WOOD, GENERAL_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<EdibleItem> bacon = ITEMS.register("bacon", () -> new EdibleItem(TinkerFood.BACON, TinkerRegistry.tabGeneral));
  public static final ItemObject<TinkerBookItem> book = ITEMS.register("book", () -> new TinkerBookItem(new Item.Properties().group(TinkerRegistry.tabGeneral).maxStackSize(1)));
  public static final ItemObject<Item> mudBrick = ITEMS.register("mud_brick", GENERAL_PROPS);
  public static final ItemObject<Item> driedBrick = ITEMS.register("dried_brick", GENERAL_PROPS);

  /* Slime Balls are edible, believe it or not */
  public static final EnumObject<SlimeType, Item> slimeball;
  static {
    EnumObject<SlimeBlock.SlimeType,EdibleItem> tinkerSlimeballs = ITEMS.registerEnum(SlimeBlock.SlimeType.TINKER, "slime_ball", (type) -> new EdibleItem(type.getSlimeFood(type), TinkerRegistry.tabGeneral));
    Map<SlimeType,Supplier<? extends Item>> map = new EnumMap<>(SlimeBlock.SlimeType.class);
    for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.TINKER) {
      map.put(slime, tinkerSlimeballs.getSupplier(slime));
    }
    map.put(SlimeBlock.SlimeType.GREEN, Items.SLIME_BALL.delegate);
    slimeball = new EnumObject<>(map);
  }

  @SubscribeEvent
  void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
    CraftingHelper.register(ConfigOptionEnabledCondition.Serializer.INSTANCE);
  }
  
  @SubscribeEvent
  void commonSetup(final FMLCommonSetupEvent event) {
    TinkerRegistry.tabGeneral.setDisplayIcon(new ItemStack(slimeball.get(SlimeBlock.SlimeType.BLUE)));
  }
}
