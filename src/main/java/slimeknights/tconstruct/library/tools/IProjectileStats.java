package slimeknights.tconstruct.library.tools;

import com.google.common.collect.Multimap;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;

public interface IProjectileStats {
  Multimap<String, AttributeModifier> getProjectileAttributeModifier(ItemStack stack);
}
