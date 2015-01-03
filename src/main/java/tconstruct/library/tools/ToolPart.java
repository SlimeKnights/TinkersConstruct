package tconstruct.library.tools;

import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

import tconstruct.library.TinkerRegistry;

public class ToolPart implements IToolPart {

  private static final Map<Integer, Material> metadataCache = new HashMap<>();

  @Override
  public String getMaterialID(ItemStack stack) {
    return getMaterial(stack).identifier;
  }

  @Override
  public Material getMaterial(ItemStack stack) {
    Integer meta = stack.getItemDamage();
    // already cached the value?
    if(metadataCache.containsKey(meta))
      return metadataCache.get(meta);

    // if none is found, we return unknown
    Material material = Material.UNKNOWN;
    for (Material mat : TinkerRegistry.getAllMaterials()) {
      if (mat.metadata == meta) {
        material = mat;
        break;
      }
    }

    // put this into the cache for the next time
    metadataCache.put(meta, material);
    return material;
  }
}
