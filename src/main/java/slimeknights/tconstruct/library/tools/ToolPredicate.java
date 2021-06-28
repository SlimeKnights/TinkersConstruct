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
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.common.TinkerTags.Items;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierMatch;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.StatPredicate;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/** Variant of ItemPredicate for matching Tinker tools */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ToolPredicate extends ItemPredicate {
  public static final ResourceLocation ID = Util.getResource("tool");

  @Nullable
  protected final Item item;
  @Nullable
  protected final ITag<Item> tag;
  protected final List<MaterialId> materials;
  protected final boolean hasUpgrades;
  protected final ModifierMatch upgrades;
  protected final ModifierMatch modifiers;
  protected final List<StatPredicate> stats;

  @Override
  public boolean test(ItemStack stack) {
    // first validate item and tag
    if (this.tag != null && !this.tag.contains(stack.getItem())) {
      return false;
    }
    if (this.item != null && stack.getItem() != this.item) {
      return false;
    }
    // prevent changing NBT for non-tools
    if (!stack.getItem().isIn(Items.MODIFIABLE)) {
      return false;
    }
    ToolStack tool = ToolStack.from(stack);

    // materials
    matLoop:
    for (MaterialId check : materials) {
      for (IMaterial mat : tool.getMaterials().getMaterials()) {
        if (mat.getIdentifier().equals(check)) {
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
  public JsonElement serialize() {
    JsonObject json = new JsonObject();
    json.addProperty("type", ID.toString());
    if (this.item != null) {
      json.addProperty("item", Registry.ITEM.getKey(this.item).toString());
    }
    if (this.tag != null) {
      json.addProperty("tag", TagCollectionManager.getManager().getItemTags().getValidatedIdFromTag(this.tag).toString());
    }
    if (!materials.isEmpty()) {
      json.add("materials", toArray(materials, mat -> new JsonPrimitive(mat.toString())));
    }
    if (hasUpgrades) {
      json.addProperty("has_upgrades", hasUpgrades);
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
      item = RecipeHelper.deserializeItem(JSONUtils.getString(json, "item"), "item", Item.class);
    }
    // tag
    ITag<Item> tag = null;
    if (json.has("tag")) {
      ResourceLocation tagName = new ResourceLocation(JSONUtils.getString(json, "tag"));
      tag = TagCollectionManager.getManager().getItemTags().get(tagName);
      if (tag == null) {
        throw new JsonSyntaxException("Unknown item tag '" + tagName + "'");
      }
    }
    // materials
    List<MaterialId> materials = Collections.emptyList();
    if (json.has("materials")) {
      materials = JsonHelper.parseList(json, "materials", (element, key) -> new MaterialId(JSONUtils.getString(element, key)));
    }
    // upgrades
    boolean hasUpgrades = JSONUtils.getBoolean(json, "has_upgrades", false);
    ModifierMatch upgrades = ModifierMatch.ALWAYS;
    if (json.has("upgrades")) {
      upgrades = ModifierMatch.deserialize(JSONUtils.getJsonObject(json, "upgrades"));
    }
    // modifiers
    ModifierMatch modifiers = ModifierMatch.ALWAYS;
    if (json.has("modifiers")) {
      modifiers = ModifierMatch.deserialize(JSONUtils.getJsonObject(json, "modifiers"));
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
  public static Builder builder(ITag<Item> tag) {
    return new Builder(null, tag);
  }

  /** Creates a new builder instance for any item */
  public static Builder builder() {
    return new Builder(null, null);
  }

  /** Builder for data generators */
  @Setter @Accessors(fluent = true)
  public static class Builder {
    /** Item that must match */
    @Nullable
    protected final Item item;
    /** Tag that must match */
    @Nullable
    protected final ITag<Item> tag;
    /** Materials that must be contained in the tool */
    protected final List<MaterialId> materials = new ArrayList<>();
    /** If true, the tool must have at least 1 upgrade */
    protected boolean hasUpgrades = false;
    /** List of upgrades that must exist in the tool */
    protected ModifierMatch upgrades = ModifierMatch.ALWAYS;
    /** List of modifiers that must exist in the tool */
    protected ModifierMatch modifiers = ModifierMatch.ALWAYS;
    protected final List<StatPredicate> stats = new ArrayList<>();

    protected Builder(@Nullable Item item, @Nullable ITag<Item> tag) {
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
