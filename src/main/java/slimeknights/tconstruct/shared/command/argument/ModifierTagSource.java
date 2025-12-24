package slimeknights.tconstruct.shared.command.argument;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import slimeknights.mantle.command.argument.TagSource;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** {@link TagSource} for {@link slimeknights.tconstruct.library.modifiers.ModifierManager} */
public enum ModifierTagSource implements TagSource<Modifier> {
  INSTANCE;

  /* Basic */

  @Override
  public ResourceKey<? extends Registry<Modifier>> key() {
    return ModifierManager.REGISTRY_KEY;
  }

  @Override
  public String folder() {
    return ModifierManager.TAG_FOLDER;
  }


  /* Tags */

  @Override
  public boolean hasTag(TagKey<Modifier> tag) {
    return ModifierManager.getTagOrNull(tag) != null;
  }

  @Override
  public Stream<TagKey<Modifier>> tagKeys() {
    return ModifierManager.getAllTags().map(Entry::getKey);
  }


  /* Tag entries */

  @Nullable
  @Override
  public List<Modifier> valuesInTag(TagKey<Modifier> tag) {
    return ModifierManager.getTagOrNull(tag);
  }

  @Nullable
  @Override
  public List<ResourceLocation> keysInTag(TagKey<Modifier> tag) {
    List<Modifier> entries = ModifierManager.getTagOrNull(tag);
    if (entries == null) {
      return null;
    }
    return entries.stream().map(Modifier::getId).collect(Collectors.toList());
  }


  /* Entries */

  @Nullable
  @Override
  public Modifier getValue(ResourceLocation key) {
    ModifierId id = new ModifierId(key);
    if (ModifierManager.INSTANCE.contains(id)) {
      return ModifierManager.INSTANCE.get(id);
    }
    return null;
  }

  @Override
  public Stream<TagKey<Modifier>> tagsFor(Modifier modifier) {
    return ModifierManager.getTagKeys(modifier.getId());
  }

  @Override
  public Stream<ResourceLocation> valueKeys() {
    return ModifierManager.INSTANCE.getAllLocations();
  }
}
