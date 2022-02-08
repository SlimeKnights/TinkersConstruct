package slimeknights.tconstruct.library.tools;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags.Items;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.StatPredicate;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/** Variant of ItemPredicate for matching Tinker tools */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ToolPredicate extends ItemPredicate {
  public static final ResourceLocation ID = TConstruct.getResource("tool");

  @Nullable
  protected final Item item;
  @Nullable
  protected final Tag<Item> tag;
  protected final List<MaterialId> materials;
  protected final boolean hasUpgrades;
  protected final ModifierMatch upgrades;
  protected final ModifierMatch modifiers;
  protected final List<StatPredicate> stats;

  @Override
  public boolean matches(ItemStack stack) {
    // first validate item and tag
    if (this.tag != null && !this.tag.contains(stack.getItem())) {
      return false;
    }
    if (this.item != null && stack.getItem() != this.item) {
      return false;
    }
    // prevent changing NBT for non-tools
    if (!stack.is(Items.MODIFIABLE)) {
      return false;
    }
    ToolStack tool = ToolStack.from(stack);

    // materials
    matLoop:
    for (MaterialId check : materials) {
      for (MaterialVariant mat : tool.getMaterials().getList()) {
        if (mat.getId().equals(check)) {
          continue matLoop;
        }
      }
      return false;
    }

    // modifiers
    if (hasUpgrades && tool.getUpgrades().isEmpty()) {
      return false;
    }
    if (upgrades != ModifierMatch.ALWAYS && !upgrades.test(tool.getUpgrades().getModifiers())) {
      return false;
    }
    if (modifiers != ModifierMatch.ALWAYS && !modifiers.test(tool.getModifierList())) {
      return false;
    }
    // stats
    if (!stats.isEmpty()) {
      StatsNBT toolStats = tool.getStats();
      for (StatPredicate predicate : stats) {
        if (!predicate.test(toolStats)) {
          return false;
        }
      }
    }

    return true;
  }

  /** Converts the given list to a json array */
  private static <D> JsonArray toArray(List<D> list, Function<D,JsonElement> mapper) {
    JsonArray array = new JsonArray();
    for (D data : list) {
      array.add(mapper.apply(data));
    }
    return array;
  }

  @Override
  public JsonElement serializeToJson() {
    JsonObject json = new JsonObject();
    json.addProperty("type", ID.toString());
    if (this.item != null) {
      json.addProperty("item", Objects.requireNonNull(item.getRegistryName()).toString());
    }
    if (this.tag != null) {
      json.addProperty("tag", SerializationTags.getInstance().getIdOrThrow(Registry.ITEM_REGISTRY, this.tag, () -> new IllegalStateException("Unknown item tag")).toString());
    }
    if (!materials.isEmpty()) {
      json.add("materials", toArray(materials, mat -> new JsonPrimitive(mat.toString())));
    }
    if (hasUpgrades) {
      json.addProperty("has_upgrades", true);
    }
    if (upgrades != ModifierMatch.ALWAYS) {
      json.add("upgrades", upgrades.serialize());
    }
    if (modifiers != ModifierMatch.ALWAYS) {
      json.add("modifiers", modifiers.serialize());
    }
    if (!stats.isEmpty()) {
      json.add("stats", toArray(stats, StatPredicate::serialize));
    }
    return json;
  }

  /** Deserializes the tool predicate from JSON */
  public static ToolPredicate deserialize(JsonObject json) {
    // item
    Item item = null;
    if (json.has("item")) {
      item = RecipeHelper.deserializeItem(GsonHelper.getAsString(json, "item"), "item", Item.class);
    }
    // tag
    Tag<Item> tag = null;
    if (json.has("tag")) {
      ResourceLocation tagName = new ResourceLocation(GsonHelper.getAsString(json, "tag"));
      tag = SerializationTags.getInstance().getTagOrThrow(Registry.ITEM_REGISTRY, tagName, name -> new JsonSyntaxException("Unknown item tag '" + name + '\''));
    }
    // materials
    List<MaterialId> materials = Collections.emptyList();
    if (json.has("materials")) {
      materials = JsonHelper.parseList(json, "materials", (element, key) -> new MaterialId(GsonHelper.convertToString(element, key)));
    }
    // upgrades
    boolean hasUpgrades = GsonHelper.getAsBoolean(json, "has_upgrades", false);
    ModifierMatch upgrades = ModifierMatch.ALWAYS;
    if (json.has("upgrades")) {
      upgrades = ModifierMatch.deserialize(GsonHelper.getAsJsonObject(json, "upgrades"));
    }
    // modifiers
    ModifierMatch modifiers = ModifierMatch.ALWAYS;
    if (json.has("modifiers")) {
      modifiers = ModifierMatch.deserialize(GsonHelper.getAsJsonObject(json, "modifiers"));
    }
    // stats
    List<StatPredicate> stats = Collections.emptyList();
    if (json.has("stats")) {
      stats = JsonHelper.parseList(json, "stats", StatPredicate::deserialize);
    }
    return new ToolPredicate(item, tag, materials, hasUpgrades, upgrades, modifiers, stats);
  }

  /** Creates a new builder instance for an item */
  public static Builder builder(Item item) {
    return new Builder(item, null);
  }

  /** Creates a new builder instance for a tag */
  public static Builder builder(Tag<Item> tag) {
    return new Builder(null, tag);
  }

  /** Creates a new builder instance for any item */
  public static Builder builder() {
    return new Builder(null, null);
  }

  /** Builder for data generators */
  @SuppressWarnings("unused")
  @Setter @Accessors(fluent = true)
  public static class Builder {
    /** Item that must match */
    @Nullable
    protected final Item item;
    /** Tag that must match */
    @Nullable
    protected final Tag<Item> tag;
    /** Materials that must be contained in the tool */
    protected final List<MaterialId> materials = new ArrayList<>();
    /** If true, the tool must have at least 1 upgrade */
    protected boolean hasUpgrades = false;
    /** List of upgrades that must exist in the tool */
    protected ModifierMatch upgrades = ModifierMatch.ALWAYS;
    /** List of modifiers that must exist in the tool */
    protected ModifierMatch modifiers = ModifierMatch.ALWAYS;
    protected final List<StatPredicate> stats = new ArrayList<>();

    protected Builder(@Nullable Item item, @Nullable Tag<Item> tag) {
      this.item = item;
      this.tag = tag;
    }

    /** Adds the given material as a requirement */
    public Builder withMaterial(MaterialId material) {
      materials.add(material);
      return this;
    }

    /** Adds the given stat predicate as a requirement */
    public Builder withStat(StatPredicate predicate) {
      stats.add(predicate);
      return this;
    }

    /** Creates the predicate */
    public ToolPredicate build() {
      return new ToolPredicate(item, tag, materials, hasUpgrades, upgrades, modifiers, stats);
    }
  }
}
