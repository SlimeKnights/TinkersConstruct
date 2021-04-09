package slimeknights.tconstruct.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.EntityType;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.Tag;

@Mixin(EntityTypeTags.class)
public interface EntityTypeTagsAccessor {

  @Invoker("register")
  static Tag.Identified<EntityType<?>> register(String id) {
    throw new AssertionError();
  }
}
