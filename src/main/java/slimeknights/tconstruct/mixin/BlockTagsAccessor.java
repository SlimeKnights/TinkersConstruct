package slimeknights.tconstruct.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;

@Mixin(BlockTags.class)
public interface BlockTagsAccessor {

  @Invoker("register")
    static Tag.Identified<Block> invokeRegister(String id) {
    throw new AssertionError();
  }

}
