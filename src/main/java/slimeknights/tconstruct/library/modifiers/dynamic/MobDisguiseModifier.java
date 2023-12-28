package slimeknights.tconstruct.library.modifiers.dynamic;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.serializer.GenericRegistryEntrySerializer;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.armor.MobDisguiseModule;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** @deprecated use {@link MobDisguiseModule} */
@Deprecated
@RequiredArgsConstructor
public class MobDisguiseModifier extends NoLevelsModifier {
  /** Loader instance */
  public static final IGenericLoader<MobDisguiseModifier> LOADER = new GenericRegistryEntrySerializer<>("entity", ForgeRegistries.ENTITIES, MobDisguiseModifier::new, m -> m.type);
  /** @deprecated use {@link MobDisguiseModule#DISGUISES} */
  @Deprecated
  public static final TinkerDataKey<Multiset<EntityType<?>>> DISGUISES = MobDisguiseModule.DISGUISES;

  private final EntityType<?> type;

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return LOADER;
  }

  @Override
  public void onEquip(IToolStackView tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot().getType() == Type.ARMOR) {
      context.getTinkerData().ifPresent(data -> {
        Multiset<EntityType<?>> disguises = data.get(DISGUISES);
        if (disguises == null) {
          disguises = HashMultiset.create();
          data.put(DISGUISES, disguises);
        }
        disguises.add(type);
      });
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot().getType() == Type.ARMOR) {
      context.getTinkerData().ifPresent(data -> {
        Multiset<EntityType<?>> disguises = data.get(DISGUISES);
        if (disguises != null) {
          disguises.remove(type);
        }
      });
    }
  }
}
