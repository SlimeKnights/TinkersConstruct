package slimeknights.tconstruct.library.book.content;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.util.ItemStackList;
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
  protected ItemStackList getDemoTools(ItemStack[][] inputItems) {
    if(inputItems.length == 0) {
      return ItemStackList.create();
    }

    ItemStackList demo = super.getDemoTools(inputItems);

    ItemStackList out = ItemStackList.create();

    for(int i = 0; i < inputItems[0].length; i++) {
      if(inputItems[0][i].getItem() == TinkerTools.sharpeningKit) {
        Material material = TinkerTools.sharpeningKit.getMaterial(inputItems[0][i]);
        IModifier modifier = TinkerRegistry.getModifier("fortify" + material.getIdentifier());
        if(modifier != null) {
          ItemStack stack = demo.get(i % demo.size()).copy();
          modifier.apply(stack);
          out.add(stack);
        }
      }
    }

    return out;
  }
}
