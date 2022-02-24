package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolBuilder;

public class TraitSqueaky extends AbstractTrait {

  public TraitSqueaky() {
    super("squeaky", TextFormatting.YELLOW);
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    super.applyEffect(rootCompound, modifierTag);

    // add silktouch if it's not present
    ToolBuilder.addEnchantment(rootCompound, Enchantments.SILK_TOUCH);
  }

  @Override
  public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    // ALWAYS ZERO DAMAGE >:C
    return 0f;
  }

  @Override
  public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
    Sounds.playSoundForAll(player, Sounds.toy_squeak, 1.0f, 0.8f + 0.4f * random.nextFloat());
  }
}
