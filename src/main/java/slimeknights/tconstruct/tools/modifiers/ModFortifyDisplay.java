package slimeknights.tconstruct.tools.modifiers;

import com.google.common.collect.ImmutableList;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.IModifierDisplay;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.tools.TinkerTools;

/** A custom clientside modifier to handle the loading and displaying of the different fortify modifiers */
public class ModFortifyDisplay extends Modifier implements IModifierDisplay {

  public ModFortifyDisplay() {
    super("fortify");
  }

  @Override
  public boolean hasTexturePerMaterial() {
    return true;
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    // dummy
  }

  @Override
  public int getColor() {
    return 0xdddddd;
  }

  @Override
  public List<List<ItemStack>> getItems() {
    ImmutableList.Builder<List<ItemStack>> builder = ImmutableList.builder();
    for(IModifier modifier : TinkerRegistry.getAllModifiers()) {
      if(!(modifier instanceof ModFortify)) {
        continue;
      }

      ItemStack kit = TinkerTools.sharpeningKit.getItemstackWithMaterial(((ModFortify) modifier).material);
      ItemStack flint = new ItemStack(Items.FLINT);

      builder.add(ImmutableList.of(kit, flint));
    }

    return builder.build();
  }
}
