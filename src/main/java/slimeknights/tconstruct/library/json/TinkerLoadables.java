package slimeknights.tconstruct.library.json;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.loot.LootModifierManager;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.common.RegistryLoadable;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
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

@SuppressWarnings("deprecation")
public class TinkerLoadables {
  /* Enums */
  public static final StringLoadable<Operation> OPERATION = new EnumLoadable<>(Operation.class);
  public static final StringLoadable<EquipmentSlot> EQUIPMENT_SLOT = new EnumLoadable<>(EquipmentSlot.class);
  public static final Loadable<Set<EquipmentSlot>> EQUIPMENT_SLOT_SET = EQUIPMENT_SLOT.set();
  public static final StringLoadable<LightLayer> LIGHT_LAYER = new EnumLoadable<>(LightLayer.class);
  public static final StringLoadable<InteractionSource> INTERACTION_SOURCE = new EnumLoadable<>(InteractionSource.class);
  public static final StringLoadable<OreRateType> ORE_RATE_TYPE = new EnumLoadable<>(OreRateType.class);
  public static final StringLoadable<TooltipKey> TOOLTIP_KEY = new EnumLoadable<>(TooltipKey.class);

  /* Registries */
  public static final StringLoadable<ResourceLocation> CUSTOM_STAT = new RegistryLoadable<>(Registry.CUSTOM_STAT);
  public static final StringLoadable<RecipeType<?>> RECIPE_TYPE = new RegistryLoadable<>(Registry.RECIPE_TYPE);

  /* Tag keys */
  public static final StringLoadable<TagKey<Modifier>> MODIFIER_TAGS = Loadables.tagKey(ModifierManager.REGISTRY_KEY);
  public static final StringLoadable<TagKey<IMaterial>> MATERIAL_TAGS = Loadables.tagKey(MaterialManager.REGISTRY_KEY);

  /* Mapped items */
  public static final StringLoadable<IMaterialItem> MATERIAL_ITEM = instance(Loadables.ITEM, IMaterialItem.class, "Expected item to be instance of IMaterialItem");
  public static final StringLoadable<IModifiable> MODIFIABLE_ITEM = instance(Loadables.ITEM, IModifiable.class, "Expected item to be instance of IModifiable");
  public static final StringLoadable<IToolPart> TOOL_PART_ITEM = instance(Loadables.ITEM, IToolPart.class, "Expected item to be instance of IToolPart");

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
}
