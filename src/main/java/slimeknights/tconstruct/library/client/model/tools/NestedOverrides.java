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
  /** If true, we are currently resolving a nested model and should ignore further nesting */
  private static boolean ignoreNested = false;

  private final ItemOverrides nested;

  @Override
  @Nullable
  public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
    if (!ignoreNested) {
      BakedModel overridden = nested.resolve(originalModel, stack, world, entity, seed);
      if (overridden != null && overridden != originalModel) {
        ignoreNested = true;
        // if the override does have a new model, make sure to fetch its overrides to handle the nested texture as its most likely a tool model
        BakedModel finalModel = overridden.getOverrides().resolve(overridden, stack, world, entity, seed);
        ignoreNested = false;
        return finalModel;
      }
    }
    return originalModel;
  }
}
