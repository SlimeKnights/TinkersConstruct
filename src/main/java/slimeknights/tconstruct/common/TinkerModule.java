package slimeknights.tconstruct.common;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.item.TooltipItem;
import slimeknights.mantle.registration.deferred.BlockEntityTypeDeferredRegister;
import slimeknights.mantle.registration.deferred.EntityTypeDeferredRegister;
import slimeknights.mantle.registration.deferred.EnumDeferredRegister;
import slimeknights.mantle.registration.deferred.MenuTypeDeferredRegister;
import slimeknights.mantle.registration.deferred.SynchronizedDeferredRegister;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registration.BlockDeferredRegisterExtension;
import slimeknights.tconstruct.common.registration.FluidDeferredRegisterExtension;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Contains base helpers for all Tinker modules. Should not be extended by other mods, this is only for internal usage.
 */
public abstract class TinkerModule {
  protected TinkerModule() {
    TConstruct.sealTinkersClass(this, "TinkerModule", "This is a bug with the mod containing that class, they should create their own deferred registers.");
  }

  // deferred register instances
  // gameplay singleton
  protected static final BlockDeferredRegisterExtension BLOCKS = new BlockDeferredRegisterExtension(TConstruct.MOD_ID);
  protected static final ItemDeferredRegisterExtension ITEMS = new ItemDeferredRegisterExtension(TConstruct.MOD_ID);
  protected static final FluidDeferredRegisterExtension FLUIDS = new FluidDeferredRegisterExtension(TConstruct.MOD_ID);
  protected static final EnumDeferredRegister<MobEffect> MOB_EFFECTS = new EnumDeferredRegister<>(Registries.MOB_EFFECT, TConstruct.MOD_ID);
  protected static final SynchronizedDeferredRegister<ParticleType<?>> PARTICLE_TYPES = SynchronizedDeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, TConstruct.MOD_ID);
  protected static final SynchronizedDeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS = SynchronizedDeferredRegister.create(Keys.ENTITY_DATA_SERIALIZERS, TConstruct.MOD_ID);
  protected static final SynchronizedDeferredRegister<CreativeModeTab> CREATIVE_TABS = SynchronizedDeferredRegister.create(Registries.CREATIVE_MODE_TAB, TConstruct.MOD_ID);
  // gameplay instances
  protected static final BlockEntityTypeDeferredRegister BLOCK_ENTITIES = new BlockEntityTypeDeferredRegister(TConstruct.MOD_ID);
  protected static final EntityTypeDeferredRegister ENTITIES = new EntityTypeDeferredRegister(TConstruct.MOD_ID);
  protected static final MenuTypeDeferredRegister MENUS = new MenuTypeDeferredRegister(TConstruct.MOD_ID);
  // datapacks
  protected static final SynchronizedDeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = SynchronizedDeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TConstruct.MOD_ID);
  protected static final SynchronizedDeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIERS = SynchronizedDeferredRegister.create(Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, TConstruct.MOD_ID);
  protected static final SynchronizedDeferredRegister<LootItemConditionType> LOOT_CONDITIONS = SynchronizedDeferredRegister.create(Registries.LOOT_CONDITION_TYPE, TConstruct.MOD_ID);
  protected static final SynchronizedDeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS = SynchronizedDeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, TConstruct.MOD_ID);
  protected static final SynchronizedDeferredRegister<LootPoolEntryType> LOOT_ENTRIES = SynchronizedDeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, TConstruct.MOD_ID);

  // base item properties
  protected static final Item.Properties ITEM_PROPS = new Item.Properties();
  protected static final Item.Properties UNSTACKABLE_PROPS = new Item.Properties().stacksTo(1);
  protected static final Function<Block,? extends BlockItem> BLOCK_ITEM = (b) -> new BlockItem(b, ITEM_PROPS);
  protected static final Function<Block,? extends BlockItem> TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, ITEM_PROPS);
  protected static final Function<Block,? extends BlockItem> UNSTACKABLE_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, UNSTACKABLE_PROPS);
  protected static final Supplier<Item> TOOLTIP_ITEM = () -> new TooltipItem(ITEM_PROPS);

  /** Called during construction to initialize the registers for this mod */
  public static void initRegisters() {
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    // gameplay singleton
    BLOCKS.register(bus);
    ITEMS.register(bus);
    FLUIDS.register(bus);
    MOB_EFFECTS.register(bus);
    PARTICLE_TYPES.register(bus);
    DATA_SERIALIZERS.register(bus);
    CREATIVE_TABS.register(bus);
    // gameplay instance
    BLOCK_ENTITIES.register(bus);
    ENTITIES.register(bus);
    MENUS.register(bus);
    // datapacks
    RECIPE_SERIALIZERS.register(bus);
    GLOBAL_LOOT_MODIFIERS.register(bus);
    LOOT_CONDITIONS.register(bus);
    LOOT_FUNCTIONS.register(bus);
    LOOT_ENTRIES.register(bus);
    TinkerRecipeTypes.init(bus);
  }

  /**
   * We use this builder to ensure that our blocks all have the most important properties set.
   * This way it'll stick out if a block doesn't have a sound set.
   * It may be a bit less clear at first, since the actual builder methods tell you what each value means,
   * but as long as we don't statically import the enums it should be just as readable.
   */
  protected static BlockBehaviour.Properties builder(SoundType soundType) {
    return Block.Properties.of().sound(soundType);
  }

  /** Same as above, but with a color */
  protected static BlockBehaviour.Properties builder(MapColor color, SoundType soundType) {
    return builder(soundType).mapColor(color);
  }

  /** Builder that pre-supplies metal properties */
  protected static BlockBehaviour.Properties metalBuilder(MapColor color) {
    return builder(color, SoundType.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0f);
  }

  /** Builder that pre-supplies glass properties */
  protected static BlockBehaviour.Properties glassBuilder(MapColor color) {
    return builder(color, SoundType.GLASS)
      .instrument(NoteBlockInstrument.HAT)
      .strength(0.3F).noOcclusion().isValidSpawn(Blocks::never)
      .isRedstoneConductor(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never);
  }

  /** Builder that pre-supplies glass properties */
  protected static BlockBehaviour.Properties woodBuilder(MapColor color) {
    return builder(color, SoundType.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.0F, 7.0F).ignitedByLava();
  }

  /** Creates a new resource key for tinkers */
  protected static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> registry, String name) {
    return ResourceKey.create(registry, TConstruct.getResource(name));
  }


  /* Creative tab helpers */

  /** Adds an enum object to the given tab */
  protected static void accept(CreativeModeTab.Output output, EnumObject<?,? extends ItemLike> items, TabVisibility visibility) {
    items.forEach(item -> output.accept(item, visibility));
  }

  /** Adds an enum object to the given tab with default visbility */
  protected static void accept(CreativeModeTab.Output output, EnumObject<?,? extends ItemLike> items) {
    accept(output, items, TabVisibility.PARENT_AND_SEARCH_TABS);
  }

  /** Adds an building block object to the given tab with default visbility */
  protected static void accept(CreativeModeTab.Output output, BuildingBlockObject object, TabVisibility visibility) {
    object.forEach(item -> output.accept(item, visibility));
  }

  /** Adds an building block object to the given tab with default visbility */
  protected static void accept(CreativeModeTab.Output output, BuildingBlockObject object) {
    accept(output, object, TabVisibility.PARENT_AND_SEARCH_TABS);
  }

  /** Accepts the given item if the passed tag has items */
  protected static boolean acceptIfTag(CreativeModeTab.Output output, ItemLike item, TabVisibility visibility, TagKey<Item> tagCondition) {
    Optional<Named<Item>> tag = BuiltInRegistries.ITEM.getTag(tagCondition);
    if (tag.isPresent() && tag.get().size() > 0) {
      output.accept(item, visibility);
      return true;
    }
    return false;
  }

  /** Accepts the given item if the passed tag has items */
  protected static boolean acceptIfTag(CreativeModeTab.Output output, ItemLike item, TagKey<Item> tagCondition) {
    return acceptIfTag(output, item, TabVisibility.PARENT_AND_SEARCH_TABS, tagCondition);
  }
}
