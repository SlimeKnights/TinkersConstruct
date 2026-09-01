package slimeknights.tconstruct.library.json;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.loot.LootModifierManager;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.common.CodecLoadable;
import slimeknights.mantle.data.loadable.common.RegistryLoadable;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialManager;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.library.utils.GsonLoadable;

import java.util.Set;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Transformation;

@SuppressWarnings("deprecation")
public class TinkerLoadables {
  /* Enums */
  public static final StringLoadable<Operation> OPERATION = new EnumLoadable<>(Operation.class);
  public static final StringLoadable<EquipmentSlot> EQUIPMENT_SLOT = new EnumLoadable<>(EquipmentSlot.class);
  public static final Loadable<Set<EquipmentSlot>> EQUIPMENT_SLOT_SET = EQUIPMENT_SLOT.set();
  public static final StringLoadable<ArmorItem.Type> ARMOR_SLOT = new EnumLoadable<>(ArmorItem.Type.class);
  public static final StringLoadable<LightLayer> LIGHT_LAYER = new EnumLoadable<>(LightLayer.class);
  public static final StringLoadable<InteractionSource> INTERACTION_SOURCE = new EnumLoadable<>(InteractionSource.class);
  public static final StringLoadable<OreRateType> ORE_RATE_TYPE = new EnumLoadable<>(OreRateType.class);
  public static final StringLoadable<TooltipKey> TOOLTIP_KEY = new EnumLoadable<>(TooltipKey.class);

  /* Registries */
  public static final StringLoadable<StatType<?>> STAT_TYPE = new RegistryLoadable<>(BuiltInRegistries.STAT_TYPE);
  public static final StringLoadable<ResourceLocation> CUSTOM_STAT = new RegistryLoadable<>(BuiltInRegistries.CUSTOM_STAT);
  public static final StringLoadable<RecipeType<?>> RECIPE_TYPE = new RegistryLoadable<>(BuiltInRegistries.RECIPE_TYPE);

  /* Tag keys */
  public static final StringLoadable<TagKey<Instrument>> INSTRUMENT_TAGS = Loadables.tagKey(Registries.INSTRUMENT);
  public static final StringLoadable<TagKey<Modifier>> MODIFIER_TAGS = Loadables.tagKey(ModifierManager.REGISTRY_KEY);
  public static final StringLoadable<TagKey<IMaterial>> MATERIAL_TAGS = Loadables.tagKey(MaterialManager.REGISTRY_KEY);

  /* Mapped items */
  public static final StringLoadable<IMaterialItem> MATERIAL_ITEM = instance(Loadables.ITEM, IMaterialItem.class, "Expected item to be instance of IMaterialItem");
  public static final StringLoadable<IModifiable> MODIFIABLE_ITEM = instance(Loadables.ITEM, IModifiable.class, "Expected item to be instance of IModifiable");
  public static final StringLoadable<IToolPart> TOOL_PART_ITEM = instance(Loadables.ITEM, IToolPart.class, "Expected item to be instance of IToolPart");
  public static final StringLoadable<SimpleParticleType> SIMPLE_PARTICLE = instance(Loadables.PARTICLE_TYPE, SimpleParticleType.class, "Expected particle type to be instance of SimpleParticleType");
  public static final StringLoadable<BlockItem> BLOCK_ITEM = instance(Loadables.ITEM, BlockItem.class, "Expected item to be instance of BlockItem");

  /** Tier loadable from the forge tier sorting registry */
  public static final StringLoadable<Tier> TIER = Loadables.RESOURCE_LOCATION.xmap((id, error) -> {
    Tier tier = TierSortingRegistry.byName(id);
    if (tier != null) {
      return tier;
    }
    throw error.create("Unknown harvest tier " + id);
  }, (tier, error) -> {
    ResourceLocation id = TierSortingRegistry.getName(tier);
    if (id != null) {
      return id;
    }
    throw error.create("Attempt to serialize unregistered tier " + tier);
  });

  /* Loot tables */
  /** Loadable for a loot entry instance */
  public static final Loadable<LootPoolEntryContainer> LOOT_ENTRY = new GsonLoadable<>(LootModifierManager.GSON_INSTANCE, LootPoolEntryContainer.class);

  /** Loadble requiring the argument to be an instance of the passed class */
  @SuppressWarnings("unchecked")  // The type works when deserializing, so it works when serializing
  public static <B, T> StringLoadable<T> instance(StringLoadable<B> loadable, Class<T> typeClass, String errorMsg) {
    return loadable.comapFlatMap((base, error) -> {
      if (typeClass.isInstance(base)) {
        return typeClass.cast(base);
      }
      throw error.create(errorMsg);
    }, t -> (B)t);
  }

  
  /** Models */
  public static final Loadable<BlockElement> BLOCK_ELEMENT = new GsonLoadable<>(new GsonBuilder().registerTypeAdapter(BlockElement.class, new BlockElement.Deserializer()).registerTypeAdapter(BlockElementFace.class, new BlockElementFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer()).create(), BlockElement.class);
  public static final Loadable<Transformation> TRANSFORMATION = new CodecLoadable(Transformation.CODEC);

  /** Generic */
  public abstract static class EitherLoadable<A, B> implements Loadable<Either<A, B>> {
    public static final <A, B> EitherLoadable<A, B> create(Loadable<A> leftLoader, Loadable<B> rightLoader) {
      return new EitherLoadable<A, B>() {
        @Override
        public Either<A, B> convert(JsonElement element, String key, TypedMap context) {
          try {
            return Either.left(leftLoader.convert(element, key, context));
          } catch (JsonSyntaxException e1) {
            try {
              return Either.right(rightLoader.convert(element, key, context));
            } catch (JsonSyntaxException e2) {
              throw new JsonSyntaxException(
                  "Cannot parse '%s': First error: %s, Second error: %s".formatted(key, e1.toString(), e2.toString()),
                  e1);
            }
          }
        }

        @Override
        public JsonElement serialize(Either<A, B> object) {
          if (object.left().isPresent())
            return leftLoader.serialize(object.left().get());
          else
            return rightLoader.serialize(object.right().get());
        }

        @Override
        public Either<A, B> decode(FriendlyByteBuf buffer, TypedMap context) {
          if (buffer.readBoolean())
            return Either.left(leftLoader.decode(buffer, context));
          return Either.right(rightLoader.decode(buffer, context));
        }

        @Override
        public void encode(FriendlyByteBuf buffer, Either<A, B> value) {
          if (value.left().isPresent()) {
            buffer.writeBoolean(true);
            leftLoader.encode(buffer, value.left().get());
          } else {
            buffer.writeBoolean(false);
            rightLoader.encode(buffer, value.right().get());
          }
        }
      };
    }
  }
}
