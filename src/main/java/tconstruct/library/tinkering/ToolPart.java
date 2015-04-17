package tconstruct.library.tinkering;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tconstruct.library.TinkerRegistry;

public class ToolPart extends Item implements IToolPart {

  private static final Map<Integer, Material> metadataCache = new HashMap<>();

  public ToolPart() {
    this.setHasSubtypes(true);
  }

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
    // todo: check if the part supports the material
    for(Material mat : TinkerRegistry.getAllMaterials())
      subItems.add(new ItemStack(this, 1, mat.metadata));
  }

  @Override
  public String getMaterialID(ItemStack stack) {
    return getMaterial(stack).identifier;
  }

  @Override
  public Material getMaterial(ItemStack stack) {
    Integer meta = stack.getItemDamage();
    // already cached the value?
    if (metadataCache.containsKey(meta)) {
      return metadataCache.get(meta);
    }

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
