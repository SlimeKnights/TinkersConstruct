package slimeknights.tconstruct.common.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.tconstruct.common.TinkerEffect;

import java.util.Objects;
import java.util.function.Supplier;

/** Handles creating fake registry entries to datagen entries based on other mods */
public class FakeRegistryEntry {
  /** Creates a dummy registry entry */
  @SuppressWarnings("UnstableApiUsage")
  private static <T> T getOrCreate(IForgeRegistry<T> registry, ResourceLocation id, Supplier<T> constructor) {
    if (!registry.containsKey(id)) {
      ((ForgeRegistry<T>)registry).unfreeze();
      T value = constructor.get();
      registry.register(id, value);
      return value;
    }
    return Objects.requireNonNull(registry.getValue(id));
  }

  /** Gets or creates a fake block with the given ID */
  public static Block block(ResourceLocation id) {
    return getOrCreate(ForgeRegistries.BLOCKS, id, () -> new Block(BlockBehaviour.Properties.of()));
  }

  /** Gets or creates a fake item with the given ID */
  public static Item item(ResourceLocation id) {
    return getOrCreate(ForgeRegistries.ITEMS, id, () -> new Item(new Item.Properties()));
  }

  /** Gets or creates a fake mob effect with the given ID */
  public static MobEffect effect(ResourceLocation id) {
    return getOrCreate(ForgeRegistries.MOB_EFFECTS, id, () -> new TinkerEffect(MobEffectCategory.NEUTRAL, false));
  }

  /** Gets or creates a fake entity with the given ID */
  public static <T extends Entity> EntityType<?> entity(ResourceLocation id) {
    return getOrCreate(ForgeRegistries.ENTITY_TYPES, id, () ->
      EntityType.Builder.of((type, level) -> {
        throw new UnsupportedOperationException("Cannot create instance of fake entity");
      }, MobCategory.MISC).build(id.toString()));
  }
}
