package slimeknights.tconstruct.library.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

import java.util.Arrays;
import java.util.List;

/** Condition requiring that items exist in the intersection of all required item tags */
@RequiredArgsConstructor
public class TagIntersectionPresentCondition implements ICondition {
  private static final ResourceLocation NAME = TConstruct.getResource("tag_intersection_present");
  public static final Serializer SERIALIZER = new Serializer();

  private final List<ResourceLocation> names;
  public TagIntersectionPresentCondition(ResourceLocation... names) {
    this(Arrays.asList(names));
  }

  @Override
  public ResourceLocation getID() {
    return NAME;
  }

  @Override
  public boolean test() {
    TagCollection<Item> itemTags = SerializationTags.getInstance().getOrEmpty(Registry.ITEM_REGISTRY);
    List<Tag<Item>> tags = names.stream().map(itemTags::getTagOrEmpty).toList();

    // if there is just one tag, just needs to be filled
    if (tags.size() == 1) {
      return !tags.get(0).getValues().isEmpty();
    }

    // if any list is empty, the intersection is empty
    if (tags.stream().anyMatch(tag -> tag.getValues().isEmpty())) {
      return false;
    }

    // all tags have something, so find the first item that is in all tags
    int count = tags.size();
    itemLoop:
    for (Item item : tags.get(0).getValues()) {
      // find the first item contained in all other intersecion tags
      for (int i = 1; i < count; i++) {
        if (!tags.get(i).contains(item)) {
          continue itemLoop;
        }
      }
      // all tags contain the item? success
      return true;
    }
    // no item in all tags
    return false;
  }

  private static class Serializer implements IConditionSerializer<TagIntersectionPresentCondition> {
    @Override
    public void write(JsonObject json, TagIntersectionPresentCondition value) {
      JsonArray names = new JsonArray();
      for (ResourceLocation name : value.names) {
        names.add(name.toString());
      }
      json.add("tags", names);
    }

    @Override
    public TagIntersectionPresentCondition read(JsonObject json) {
      return new TagIntersectionPresentCondition(JsonHelper.parseList(json, "tags", JsonHelper::convertToResourceLocation));
    }

    @Override
    public ResourceLocation getID() {
      return NAME;
    }
  }
}
