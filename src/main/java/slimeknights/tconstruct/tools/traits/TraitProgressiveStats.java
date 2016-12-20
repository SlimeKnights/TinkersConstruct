package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;

/**
 * Base class for tools that progressively gain/award stats.
 * The modifier persists 2 different stat-data on the tool:
 * - A 'pool' of stats to award
 * - A 'bonus' of already awarded stats
 *
 * The modifier reapplies the 'bonus' stats on application.
 * The pool is not touched inheritly but only provided for the logic of the deriving trait.
 */
public abstract class TraitProgressiveStats extends AbstractTrait {

  protected final String pool_key;    // Key to the tag that contains the free unassigned
  protected final String applied_key; // Key to the tag that contains the already applied bonus stats

  public TraitProgressiveStats(String identifier, TextFormatting color) {
    super(identifier, color);

    pool_key = identifier + "StatPool";
    applied_key = identifier + "StatBonus";
  }

  public TraitProgressiveStats(String identifier, int color) {
    super(identifier, color);

    pool_key = identifier + "StatPool";
    applied_key = identifier + "StatBonus";
  }

    /* Modifier management */

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    super.applyEffect(rootCompound, modifierTag);
    // called on tool loading only
    // we just apply the saved bonus stats
    ToolNBT data = TagUtil.getToolStats(rootCompound);
    StatNBT bonus = getBonus(rootCompound);

    data.durability += bonus.durability;
    data.speed += bonus.speed;
    data.attack += bonus.attack;

    TagUtil.setToolTag(rootCompound, data.get());
  }

  protected boolean hasPool(NBTTagCompound root) {
    return TagUtil.getExtraTag(root).hasKey(pool_key);
  }

  protected StatNBT getPool(NBTTagCompound root) {
    return getStats(root, pool_key);
  }

  protected void setPool(NBTTagCompound root, StatNBT data) {
    setStats(root, data, pool_key);
  }

  protected StatNBT getBonus(NBTTagCompound root) {
    return getStats(root, applied_key);
  }

  protected void setBonus(NBTTagCompound root, StatNBT data) {
    setStats(root, data, applied_key);
  }

  protected static StatNBT getStats(NBTTagCompound root, String key) {
    return ModifierNBT.readTag(TagUtil.getTagSafe(TagUtil.getExtraTag(root), key), StatNBT.class);
  }

  protected static void setStats(NBTTagCompound root, StatNBT data, String key) {
    NBTTagCompound extra = TagUtil.getExtraTag(root);
    NBTTagCompound tag = new NBTTagCompound();
    data.write(tag);
    extra.setTag(key, tag);
    TagUtil.setExtraTag(root, extra);
  }

  protected boolean playerIsBreakingBlock(Entity entity) {
    return false;
  }

  public static class StatNBT extends ModifierNBT {

    // statpool
    public int durability;
    public float attack;
    public float speed;

    @Override
    public void read(NBTTagCompound tag) {
      super.read(tag);
      durability = tag.getInteger("durability");
      attack = tag.getFloat("attack");
      speed = tag.getFloat("speed");
    }

    @Override
    public void write(NBTTagCompound tag) {
      super.write(tag);
      tag.setInteger("durability", durability);
      tag.setFloat("attack", attack);
      tag.setFloat("speed", speed);
    }
  }
}
