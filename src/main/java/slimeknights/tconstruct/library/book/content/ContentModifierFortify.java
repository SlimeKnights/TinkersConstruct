package slimeknights.tconstruct.library.book.content;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.tools.TinkerTools;

@SideOnly(Side.CLIENT)
public class ContentModifierFortify extends ContentModifier {

  public static final transient String ID = "modifier_fortify";

  public ContentModifierFortify() {
  }

  public ContentModifierFortify(IModifier modifier) {
    super(modifier);
  }

  @Override
  protected ItemStack[] getDemoTools(ItemStack[][] inputItems) {
    if(inputItems.length == 0) {
      return new ItemStack[0];
    }

    ItemStack[] demo = super.getDemoTools(inputItems);

    List<ItemStack> out = Lists.newArrayList();

    for(int i = 0; i < inputItems[0].length; i++) {
      if(inputItems[0][i].getItem() != null && inputItems[0][i].getItem() == TinkerTools.sharpeningKit) {
        Material material = TinkerTools.sharpeningKit.getMaterial(inputItems[0][i]);
        IModifier modifier = TinkerRegistry.getModifier("fortify" + material.getIdentifier());
        if(modifier != null) {
          ItemStack stack = demo[i % demo.length].copy();
          modifier.apply(stack);
          out.add(stack);
        }
      }
    }

    return out.toArray(new ItemStack[out.size()]);
  }
}
