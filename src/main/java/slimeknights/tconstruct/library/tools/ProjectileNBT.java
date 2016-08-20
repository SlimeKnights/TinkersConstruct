package slimeknights.tconstruct.library.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.materials.ArrowShaftMaterialStats;
import slimeknights.tconstruct.library.materials.FletchingMaterialStats;
import slimeknights.tconstruct.library.tools.ranged.ProjectileCore;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;

public class ProjectileNBT extends ToolNBT {

  public float accuracy;

  public ProjectileNBT() {
    accuracy = 1f;
  }

  public ProjectileNBT(NBTTagCompound tag) {
    super(tag);
  }

  public ProjectileNBT shafts(ProjectileCore projectileCore, ArrowShaftMaterialStats... shafts) {
    // (Average Head Durability + Average Extra Durability) * Average Handle Modifier + Average Handle Durability

    int dur = 0;
    float modifier = 0f;
    for(ArrowShaftMaterialStats shaft : shafts) {
      if(shaft != null) {
        dur += shaft.bonusAmmo;
        modifier += shaft.modifier;
      }
    }

    dur *= projectileCore.getDurabilityPerAmmo();
    modifier /= (float) shafts.length;
    this.durability = Math.round((float) this.durability * modifier);

    // add in handle durability change
    this.durability += Math.round((float) dur / (float) shafts.length);

    this.durability = Math.max(1, this.durability);

    return this;
  }

  public ProjectileNBT fletchings(FletchingMaterialStats... fletchings) {

    float modifier = 0f;
    float accuracy = 0f;
    for(FletchingMaterialStats fletching : fletchings) {
      if(fletching != null) {
        modifier += fletching.modifier;
        accuracy += fletching.accuracy;
      }
    }

    accuracy /= (float) fletchings.length;
    modifier /= (float) fletchings.length;

    this.accuracy = Math.min(1f, Math.max(0, accuracy));
    this.durability = Math.round((float) this.durability * modifier);
    this.durability = Math.max(1, this.durability);

    return this;
  }


  @Override
  public void read(NBTTagCompound tag) {
    super.read(tag);
    this.accuracy = tag.getFloat(Tags.ACCURACY);
  }

  @Override
  public void write(NBTTagCompound tag) {
    super.write(tag);
    tag.setFloat(Tags.ACCURACY, accuracy);
  }

  public static ProjectileNBT from(ItemStack itemStack) {
    return new ProjectileNBT(TagUtil.getToolTag(itemStack));
  }
}
