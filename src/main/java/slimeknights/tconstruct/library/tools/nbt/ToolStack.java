package slimeknights.tconstruct.library.tools.nbt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Class handling parsing all tool related NBT
 */
@RequiredArgsConstructor(staticName = "from")
public class ToolStack {
  public static final int DEFAULT_MODIFIERS = 3;

  protected static final String TAG_MATERIALS = "tic_materials";
  protected static final String TAG_STATS = "tic_stats";
  protected static final String TAG_PERSISTENT_MOD_DATA = "tic_persistent_mod_data";
  protected static final String TAG_VOLATILE_MOD_DATA = "tic_volatile_mod_data";
  public static final String TAG_BROKEN = "tic_broken";
  // vanilla tags
  protected static final String TAG_DAMAGE = "Damage";
  public static final String TAG_UNBREAKABLE = "Unbreakable";

  /** Item representing this tool */
  @Getter
  private final Item item;
  /** Tool definition, describing part count and alike */
  @Getter
  private final ToolDefinition definition;
  /** Original tool NBT */
  private final CompoundNBT nbt;

  // lazy loaded data
  /** Current damage of the tool, -1 means unloaded */
  private int damage = -1;
  /** If true, tool is broken. Null means unloaded */
  private Boolean broken;
  /** Data object containing materials */
  @Nullable
  private MaterialNBT materials;
  /** Data object containing the original tool stats */
  @Nullable
  private StatsNBT stats;

  /** Data object containing persistent modifier data */
  private ModDataNBT persistantModData;

  /* Creating */

  /**
   * Creates a tool stack from an item stack
   * @param stack    Base stack
   * @param copyNbt  If true, NBT is copied from the stack
   * @return  Tool stack
   */
  private static ToolStack from(ItemStack stack, boolean copyNbt) {
    Item item = stack.getItem();
    ToolDefinition definition = ToolDefinition.EMPTY;
    if (item instanceof ToolCore) {
      definition = ((ToolCore)item).getToolDefinition();
    }
    CompoundNBT nbt = stack.getTag();
    if (nbt == null) {
      nbt = new CompoundNBT();
    } else if (copyNbt) {
      nbt = nbt.copy();
    }
    return from(item, definition, nbt);
  }

  /**
   * Creates a tool stack from the given item stack, not copying NBT
   * @param stack  Stack
   * @return  Tool stack
   */
  public static ToolStack from(ItemStack stack) {
    return from(stack, false);
  }

  /**
   * Creates a tool stack from the given item stack, copying the NBT
   * @param stack  Stack
   * @return  Tool stack
   */
  public static ToolStack copyFrom(ItemStack stack) {
    return from(stack, true);
  }

  /**
   * Creates a new tool stack
   * @param item        Item
   * @param definition  Tool definition
   * @return  Tool stack
   */
  public static ToolStack from(Item item, ToolDefinition definition) {
    return from(item, definition, new CompoundNBT());
  }

  /**
   * Creates an item stack from this tool stack
   */
  public ItemStack createStack() {
    ItemStack stack = new ItemStack(item, 1);
    stack.setTag(nbt);
    return stack;
  }

  /**
   * Sets the NBT on the given stack
   * @param stack  Stack instance
   * @return  New NBT
   */
  public ItemStack updateStack(ItemStack stack) {
    if (stack.getItem() != item) {
      throw new IllegalArgumentException("Wrong item in stack");
    }
    stack.setTag(nbt.copy());
    return stack;
  }


  /* Damaging */

  /**
   * Checks if this tool is currently broken
   * @return  True if broken
   */
  public boolean isBroken() {
    if (broken == null) {
      broken = nbt.getBoolean(TAG_BROKEN);
    }
    return broken;
  }

  /**
   * Sets the broken state on the tool
   * @param broken  New broken value
   */
  public void setBroken(boolean broken) {
    this.broken = broken;
    nbt.putBoolean(TAG_BROKEN, broken);
  }

  /**
   * Gets damage, ignoring broken checks
   * @return  Damage ignoring broken state
   */
  protected int getDamageRaw() {
    if (damage == -1) {
      damage = nbt.getInt(TAG_DAMAGE);
    }
    return damage;
  }

  /**
   * Gets the tools current damage from NBT
   * @return  Current damage
   */
  public int getDamage() {
    // if broken, return full damage
    if (isBroken()) {
      return getStats().getDurability();
    }
    return getDamageRaw();
  }

  /**
   * Gets the current durability remaining for this tool
   * @return  Tool durability
   */
  public int getCurrentDurability() {
    if (isBroken()) {
      return 0;
    }
    return getStats().getDurability() - getDamageRaw();
  }

  /**
   * Sets the tools damage
   * @param  damage  New damage
   */
  public void setDamage(int damage) {
    int durability = getStats().getDurability();
    if (damage >= durability) {
      damage = durability - 1;
      setBroken(true);
    } else {
      setBroken(false);
    }
    this.damage = damage;
    nbt.putInt(TAG_DAMAGE, damage);
  }

  /**
   * Damages the tool by the given amount
   * @param amount  Amount to damage
   * @param stack   Stack to use for criteria updates, if null creates a stack
   * @param entity  Entity for criteria updates, if null no updates run
   * @return true if the tool broke when damaging
   */
  public boolean damage(int amount, @Nullable LivingEntity entity, @Nullable ItemStack stack) {
    if (amount <= 0 || isBroken() || nbt.getBoolean(TAG_UNBREAKABLE)) {
      return false;
    }

    // TODO: modifiers

    int durability = getStats().getDurability();
    int damage = getDamage();
    int current = durability - damage;
    amount = Math.min(amount, current);
    if (amount > 0) {
      // criteria updates
      int newDamage = damage + amount;
      // TODO: needed?
      if (entity instanceof ServerPlayerEntity) {
        if (stack == null) {
          stack = createStack();
        }
        CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger((ServerPlayerEntity)entity, stack, newDamage);
      }

      setDamage(newDamage);
      return newDamage >= durability;
    }
    return false;
  }

  /**
   * Repairs the given tool stack
   * @param amount  Amount to repair
   */
  public void repair(int amount) {
    if (amount <= 0) {
      return;
    }

    // if undamaged, nothing to do
    int damage = getDamage();
    if (damage == 0) {
      return;
    }

    // todo: modifiers

    // ensure we never repair more than max durability
    int newDamage = damage - Math.min(amount, damage);
    setDamage(newDamage);

    // TODO: trigger criteria updates?
//    if (entity instanceof ServerPlayerEntity) {
//      if (stack == null) {
//        stack = createStack();
//      }
//      CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger((ServerPlayerEntity) entity, stack, newDamage);
//    }
  }


  /* Stats */

  /**
   * Gets the tool stats if parsed, or parses if not yet parsed
   * @return stats
   */
  public StatsNBT getStats() {
    if (stats == null) {
      stats = StatsNBT.readFromNBT(nbt.get(TAG_STATS));
    }
    return stats;
  }

  /**
   * Gets the tool stats if parsed, or parses if not yet parsed
   * @param stats  Stats instance
   */
  protected void setStats(StatsNBT stats) {
    this.stats = stats;
    nbt.put(TAG_STATS, stats.serializeToNBT());
    // if we no longer have enough durability, decrease the damage and mark it broken
    int newMax = stats.getDurability();
    if (getDamageRaw() >= newMax) {
      setDamage(newMax);
    }
  }


  /* Materials */

  /**
   * Gets the materials if parsed, or parses if not yet parsed
   * @return materials instance
   */
  public MaterialNBT getMaterials() {
    if (materials == null) {
      materials = MaterialNBT.readFromNBT(nbt.get(TAG_MATERIALS));
    }
    return materials;
  }

  /**
   * Sets the materials on this tool stack
   * @param materials  New materials NBT
   */
  public void setMaterials(List<IMaterial> materials) {
    setMaterials(new MaterialNBT(materials));
  }

  /**
   * Sets the materials without updating the tool stats
   * @param materials  New materials
   */
  protected void setMaterialsRaw(MaterialNBT materials) {
    this.materials = materials;
    this.nbt.put(TAG_MATERIALS, materials.serializeToNBT());
  }

  /**
   * Sets the materials on this tool stack, updating tool stats
   * @param materials  New materials NBT
   */
  public void setMaterials(MaterialNBT materials) {
    setMaterialsRaw(materials);
    // update base stats based on the new materials
    this.setStats(definition.buildStats(materials.getMaterials()));
  }

  /**
   * Replaces the material at the given index
   * @param index        Index to replace
   * @param replacement  New material
   * @throws IndexOutOfBoundsException  If the index is invalid
   */
  public void replaceMaterial(int index, IMaterial replacement) {
    setMaterials(getMaterials().replaceMaterial(index, replacement));
  }

  /**
   * Gets the list of all materials
   * @return List of all materials
   */
  public List<IMaterial> getMaterialsList() {
    return getMaterials().getMaterials();
  }

  /**
   * Gets the material at the given index
   * @param index  Index
   * @return  Material, or unknown if index is invalid
   */
  public IMaterial getMaterial(int index) {
    return getMaterials().getMaterial(index);
  }


  /* Modifiers */

  /**
   * Gets the persistant modifier data. This will be preserved when modifiers rebuild
   * @return  Persistant modifier data
   */
  public ModDataNBT getPersistantData() {
    if (persistantModData == null) {
      // parse if the tag already exists
      if (nbt.contains(TAG_PERSISTENT_MOD_DATA, NBT.TAG_COMPOUND)) {
        persistantModData = ModDataNBT.readFromNBT(nbt.getCompound(TAG_PERSISTENT_MOD_DATA));
      } else {
        // if no tag exists, create it
        CompoundNBT tag = new CompoundNBT();
        nbt.put(TAG_PERSISTENT_MOD_DATA, tag);
        persistantModData = ModDataNBT.readFromNBT(tag);
      }
    }
    return persistantModData;
  }

  /**
   * Gets the free modifiers remaining on the tool
   * @return  Free modifiers
   */
  public int getFreeModifiers() {
    // TODO: sum in
    return getPersistantData().getModifiers();
  }
}
