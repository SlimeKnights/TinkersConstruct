package slimeknights.tconstruct.library.json;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.level.LightLayer;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

import java.util.Set;

public class TinkerLoadables {
  public static final Loadable<Operation> OPERATION = new EnumLoadable<>(Operation.class);
  public static final Loadable<EquipmentSlot> EQUIPMENT_SLOT = new EnumLoadable<>(EquipmentSlot.class);
  public static final Loadable<Set<EquipmentSlot>> EQUIPMENT_SLOT_SET = EQUIPMENT_SLOT.set();
  public static final Loadable<LightLayer> LIGHT_LAYER = new EnumLoadable<>(LightLayer.class);

  public static final Loadable<TagKey<Modifier>> MODIFIER_TAGS = Loadables.tagKey(ModifierManager.REGISTRY_KEY);
}
