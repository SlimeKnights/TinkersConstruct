package slimeknights.tconstruct.shared.command.argument;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import slimeknights.mantle.command.argument.TagSource;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** {@link TagSource} for {@link MaterialManager} */
public record MaterialTagSource(MaterialManager manager) implements TagSource<IMaterial> {
  /* Basic */

  @Override
  public ResourceKey<? extends Registry<IMaterial>> key() {
    return MaterialManager.REGISTRY_KEY;
  }

  @Override
  public String folder() {
    return MaterialManager.TAG_FOLDER;
  }


  /* Tags */

  @Override
  public boolean hasTag(TagKey<IMaterial> tag) {
    return manager.getTagOrNull(tag) != null;
  }

  @Override
  public Stream<TagKey<IMaterial>> tagKeys() {
    return manager.getAllTags().map(Entry::getKey);
  }


  /* Tag entries */

  @Nullable
  @Override
  public List<IMaterial> valuesInTag(TagKey<IMaterial> tag) {
    return manager.getTagOrNull(tag);
  }

  @Nullable
  @Override
  public List<ResourceLocation> keysInTag(TagKey<IMaterial> tag) {
    List<IMaterial> entries = manager.getTagOrNull(tag);
    if (entries == null) {
      return null;
    }
    return entries.stream().map(IMaterial::getIdentifier).collect(Collectors.toList());
  }


  /* Entries */

  @Nullable
  @Override
  public IMaterial getValue(ResourceLocation key) {
    return manager.getMaterial(new MaterialId(key)).orElse(null);
  }

  @Override
  public Stream<TagKey<IMaterial>> tagsFor(IMaterial material) {
    return manager.getTagKeys(material.getIdentifier());
  }

  @Override
  public Stream<ResourceLocation> valueKeys() {
    return manager.getAllMaterials().stream().map(IMaterial::getIdentifier);
  }
}
