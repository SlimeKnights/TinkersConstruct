package slimeknights.tconstruct.library.client.model.tools;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/** Helper class for delegating to nested overrides while also doing item specific NBT overrides. */
@RequiredArgsConstructor
public class NestedOverrides extends ItemOverrides {
  /** Nested overrides instance */
  private final ItemOverrides nested;
  /** If true, ignores calling further nesting on these overrides */
  private boolean ignoreNested = false;

  @Override
  @Nullable
  public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
    if (!ignoreNested) {
      BakedModel resolved = nested.resolve(originalModel, stack, world, entity, seed);
      if (resolved != null && resolved != originalModel) {
        // if the override does have a new model, make sure to fetch its overrides to handle the nested texture as its most likely a tool model
        // however, we don't want it to run its nested overrides in that time

        // start with a quick exit if it has no overrides
        ItemOverrides resolvedOverrides = resolved.getOverrides();
        if (resolvedOverrides != ItemOverrides.EMPTY) {
          // if its nested, disable its ability to do nested for now
          if (resolvedOverrides instanceof NestedOverrides resolvedNested) {
            resolvedNested.ignoreNested = true;
            resolved = resolvedOverrides.resolve(resolved, stack, world, entity, seed);
            resolvedNested.ignoreNested = false;
          // if it's the vanilla class, skip entirely as those overrides should not be considered
          } else if (resolvedOverrides.getClass() != ItemOverrides.class) {
            resolved = resolvedOverrides.resolve(resolved, stack, world, entity, seed);
          }
        }
        return resolved;
      }
    }
    return originalModel;
  }
}
