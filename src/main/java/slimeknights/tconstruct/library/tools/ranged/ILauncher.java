package slimeknights.tconstruct.library.tools.ranged;

import com.google.common.collect.Multimap;

import net.minecraft.entity.ai.attributes.AttributeModifier;

public interface ILauncher {

  void modifyProjectileAttributes(Multimap<String, AttributeModifier> projectileAttributes);
}
