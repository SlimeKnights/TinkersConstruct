package slimeknights.tconstruct.library.data.tinkering;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.json.loot.equipment.MobEquipment;
import slimeknights.tconstruct.library.json.loot.equipment.MobEquipmentManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/** Data provider for {@link EquipmentJson} */
public abstract class AbstractMobEquipmentProvider extends GenericDataProvider {
  private final Map<String, EquipmentJson> equipment = new HashMap<>();
  private final String modId;

  public AbstractMobEquipmentProvider(PackOutput output, String modId) {
    super(output, Target.DATA_PACK, MobEquipmentManager.FOLDER, JsonHelper.DEFAULT_GSON);
    this.modId = modId;
  }

  /** Adds all equipment for this provider */
  protected abstract void addEquipment();

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    addEquipment();
    return allOf(equipment.entrySet().stream().map(entry -> saveJson(cache, new ResourceLocation(modId, entry.getKey()), entry.getValue().serialize())));
  }

  /** Creates a builder for the given entity */
  private MobEquipment.Builder equip(String name, ICondition[] conditions, String... entity) {
    MobEquipment.Builder builder = MobEquipment.builder();
    equipment.put(name, new EquipmentJson(entity, builder, conditions));
    return builder;
  }


  /* Single entity */

  /** Creates a builder for the given entity */
  public MobEquipment.Builder equip(String name, EntityType<?> entity, ICondition... conditions) {
    return equip(name, conditions, Loadables.ENTITY_TYPE.getString(entity));
  }

  /** Creates a builder for the given entity, using it as the JSON location */
  public MobEquipment.Builder equip(EntityType<?> entity, ICondition... conditions) {
    return equip(Loadables.ENTITY_TYPE.getKey(entity).getPath(), entity, conditions);
  }


  /* Compat entity */

  /** Creates a builder for the given entity ID, used for optional compat */
  public MobEquipment.Builder equip(String name, ResourceLocation entity, ICondition... conditions) {
    return equip(name, conditions, entity.toString());
  }

  /** Creates a builder for the given entity ID with an automtic mod ID condition */
  public MobEquipment.Builder equip(ResourceLocation entity) {
    return equip(entity.getNamespace() + '_' + entity.getPath(), entity, new ModLoadedCondition(entity.getNamespace()));
  }


  /* Tag */

  /** Creates a builder for the given entity tag, using it as the JSON location */
  public MobEquipment.Builder equip(String name, TagKey<EntityType<?>> tag, ICondition... conditions) {
    return equip(name, conditions, '#' + tag.location().toString());
  }

  /** Creates a builder for the given entity tag, using it as the JSON location */
  public MobEquipment.Builder equip(TagKey<EntityType<?>> tag, ICondition... conditions) {
    return equip(tag.location().getPath(), tag, conditions);
  }


  /* List */

  /** Creates a builder for the given entity tag, using it as the JSON location */
  public MobEquipment.Builder equip(String name, List<EntityType<?>> entities, ICondition... conditions) {
    return equip(name, conditions, entities.stream().map(Loadables.ENTITY_TYPE::getString).toArray(String[]::new));
  }
  

  /** JSON entry for the given equipment entry */
  private record EquipmentJson(String[] entity, MobEquipment.Builder equipment, ICondition[] conditions) {
    /** Serializes this to JSON */
    public JsonObject serialize() {
      JsonObject json = new JsonObject();
      // serialize entity
      if (entity.length == 1) {
        json.addProperty("entity", entity[0]);
      } else {
        JsonArray array = new JsonArray();
        for (String s : entity) {
          array.add(s);
        }
        json.add("entity", array);
      }
      // serialize equipment
      json.add("equip", MobEquipment.LIST_LOADABLE.serialize(equipment.build()));
      // serialize conditions
      if (conditions.length > 0) {
        json.add("conditions", CraftingHelper.serialize(conditions));
      }
      return json;
    }
  }
}
