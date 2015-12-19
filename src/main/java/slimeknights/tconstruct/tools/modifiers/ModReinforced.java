package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class ModReinforced extends ModifierTrait {

  public ModReinforced() {
    super("reinforced", 0x502e83, 5);
  }

  @Override
  public int onToolDamage(ItemStack tool, int damage, int newDamage, EntityLivingBase entity) {
    // get reinforced level
    NBTTagCompound tag = TinkerUtil.getModifierTag(tool, identifier);
    ModifierNBT data = ModifierNBT.readTag(tag);

    float chance = (float)data.level * 0.20f;
    if(chance >= random.nextFloat()) {
      newDamage -= damage;
    }

    return Math.max(0, newDamage);
  }
}
