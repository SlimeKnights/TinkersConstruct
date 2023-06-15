package slimeknights.tconstruct.library.modifiers.modules;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.serializer.GenericRegistryEntrySerializer;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module for armor modifiers that makes this entity appear to be another entity from afar
 */
public record MobDisguiseModule(EntityType<?> entity) implements EquipmentChangeModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.EQUIPMENT_CHANGE);
  public static final IGenericLoader<MobDisguiseModule> LOADER = new GenericRegistryEntrySerializer<>("entity", ForgeRegistries.ENTITIES, MobDisguiseModule::new, MobDisguiseModule::entity);

  /**
   * Data key for all disguises on an entity
   */
  public static final ComputableDataKey<Multiset<EntityType<?>>> DISGUISES = TConstruct.createKey("mob_disguise", HashMultiset::create);

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot().getType() == Type.ARMOR) {
      context.getTinkerData().ifPresent(data -> data.computeIfAbsent(DISGUISES).add(entity));
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot().getType() == Type.ARMOR) {
      context.getTinkerData().ifPresent(data -> {
        Multiset<EntityType<?>> disguises = data.get(DISGUISES);
        if (disguises != null) {
          disguises.remove(entity);
        }
      });
    }
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }
}
