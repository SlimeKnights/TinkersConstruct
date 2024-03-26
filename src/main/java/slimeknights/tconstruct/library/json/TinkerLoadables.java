package slimeknights.tconstruct.library.json;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.common.TierSortingRegistry;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;

import java.util.Set;

public class TinkerLoadables {
  /* Enums */
  public static final Loadable<Operation> OPERATION = new EnumLoadable<>(Operation.class);
  public static final Loadable<EquipmentSlot> EQUIPMENT_SLOT = new EnumLoadable<>(EquipmentSlot.class);
  public static final Loadable<Set<EquipmentSlot>> EQUIPMENT_SLOT_SET = EQUIPMENT_SLOT.set();
  public static final Loadable<LightLayer> LIGHT_LAYER = new EnumLoadable<>(LightLayer.class);
  public static final Loadable<InteractionSource> INTERACTION_SOURCE = new EnumLoadable<>(InteractionSource.class);

  /* Tag keys */
  public static final Loadable<TagKey<Modifier>> MODIFIER_TAGS = Loadables.tagKey(ModifierManager.REGISTRY_KEY);

  /** Tier loadable from the forge tier sorting registry */
  public static final Loadable<Tier> TIER = Loadables.RESOURCE_LOCATION.map((id, error) -> {
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
}
