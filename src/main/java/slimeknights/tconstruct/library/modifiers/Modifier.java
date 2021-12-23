package slimeknights.tconstruct.library.modifiers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.translation.I18n;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import slimeknights.mantle.util.RecipeMatchRegistry;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public abstract class Modifier extends RecipeMatchRegistry implements IModifier {

  public static final String LOC_Name = "modifier.%s.name";
  public static final String LOC_Desc = "modifier.%s.desc";
  public static final String LOC_Extra = "modifier.%s.extra";

  protected static final Random random = new Random();

  public final String identifier;

  protected final List<ModifierAspect> aspects = Lists.newLinkedList();

  public Modifier(String identifier) {
    this.identifier = Util.sanitizeLocalizationString(identifier);

    TinkerRegistry.registerModifier(this);
  }

  @Override
  public String getIdentifier() {
    return identifier;
  }

  @Override
  public boolean isHidden() {
    return false;
  }

  protected void addAspects(ModifierAspect... aspects) {
    this.aspects.addAll(Arrays.asList(aspects));
  }

  @Override
  public final boolean canApply(ItemStack stack, ItemStack original) throws TinkerGuiException {

    Set<Enchantment> enchantments = EnchantmentHelper.getEnchantments(stack).keySet();

    NBTTagList traits = TagUtil.getTraitsTagList(stack);
    for(int i = 0; i < traits.tagCount(); i++) {
      String id = traits.getStringTagAt(i);
      ITrait trait = TinkerRegistry.getTrait(id);
      if(trait != null) {
        if(!canApplyTogether(trait) || !trait.canApplyTogether(this)) {
          throw new TinkerGuiException(Util.translateFormatted("gui.error.incompatible_trait", this.getLocalizedName(), trait.getLocalizedName()));
        }
        canApplyWithEnchantment(trait, enchantments);
      }
    }

    NBTTagList modifiers = TagUtil.getBaseModifiersTagList(stack);
    for(int i = 0; i < modifiers.tagCount(); i++) {
      String id = modifiers.getStringTagAt(i);
      IModifier mod = TinkerRegistry.getModifier(id);
      if(mod != null) {
        if(!canApplyTogether(mod) || !mod.canApplyTogether(this)) {
          throw new TinkerGuiException(Util.translateFormatted("gui.error.incompatible_modifiers", this.getLocalizedName(), mod.getLocalizedName()));
        }
        canApplyWithEnchantment(mod, enchantments);
      }
    }

    canApplyWithEnchantment(this, enchantments);

    // aspects
    for(ModifierAspect aspect : aspects) {
      if(!aspect.canApply(stack, original)) {
        return false;
      }
    }

    return canApplyCustom(stack);
  }

  private static void canApplyWithEnchantment(IToolMod iToolMod, Set<Enchantment> enchantments)
      throws TinkerGuiException {
    for(Enchantment enchantment : enchantments) {
      if(!iToolMod.canApplyTogether(enchantment)) {
        String enchName = I18n.translateToLocal(enchantment.getName());
        throw new TinkerGuiException(Util.translateFormatted("gui.error.incompatible_enchantments", iToolMod.getLocalizedName(), enchName));
      }
    }
  }

  @Override
  public boolean canApplyTogether(Enchantment enchantment) {
    return true;
  }

  @Override
  public boolean canApplyTogether(IToolMod otherModifier) {
    return true;
  }

  protected boolean canApplyCustom(ItemStack stack) throws TinkerGuiException {
    return true;
  }

  @Override
  public void updateNBT(NBTTagCompound modifierTag) {
    // nothing to do in most cases, aspects handle the updating for most modifier
  }

  @Override
  public void apply(ItemStack stack) {
    NBTTagCompound root = TagUtil.getTagSafe(stack);
    apply(root);
    stack.setTagCompound(root);
  }

  @Override
  public void apply(NBTTagCompound root) {
    // add the modifier to its data
    NBTTagList tagList;

    // if the modifier wasn't present before, add it and safe it to the tool
    if(!TinkerUtil.hasModifier(root, getIdentifier())) {
      tagList = TagUtil.getBaseModifiersTagList(root);
      tagList.appendTag(new NBTTagString(getIdentifier()));
      TagUtil.setBaseModifiersTagList(root, tagList);
    }

    // have the modifier itself save its data
    NBTTagCompound modifierTag = new NBTTagCompound();
    tagList = TagUtil.getModifiersTagList(root);
    int index = TinkerUtil.getIndexInList(tagList, identifier);
    if(index >= 0) {
      modifierTag = tagList.getCompoundTagAt(index);
    }

    // update NBT through aspects
    for(ModifierAspect aspect : aspects) {
      aspect.updateNBT(root, modifierTag);
    }

    updateNBT(modifierTag);

    // some modifiers might not save data, don't save them
    if(!modifierTag.hasNoTags()) {
      // but if they do, ensure that the identifier is correct
      ModifierNBT data = ModifierNBT.readTag(modifierTag);
      if(!identifier.equals(data.identifier)) {
        data.identifier = identifier;
        data.write(modifierTag);
      }
    }

    // update the tools NBT
    if(index >= 0) {
      tagList.set(index, modifierTag);
    }
    else {
      tagList.appendTag(modifierTag);
    }

    TagUtil.setModifiersTagList(root, tagList);

    applyEffect(root, modifierTag);
  }

  @Override
  public String getTooltip(NBTTagCompound modifierTag, boolean detailed) {
    StringBuilder sb = new StringBuilder();

    ModifierNBT data = ModifierNBT.readTag(modifierTag);

    sb.append(getLocalizedName());
    if(data.level > 1) {
      sb.append(" ");
      sb.append(TinkerUtil.getRomanNumeral(data.level));
    }

    return sb.toString();
  }

  public String getLeveledTooltip(NBTTagCompound modifierTag, boolean detailed) {
    ModifierNBT data = ModifierNBT.readInteger(modifierTag);
    return getLeveledTooltip(data.level, detailed ? " " + data.extraInfo : "");
  }

  public String getLeveledTooltip(int level, @Nullable String suffix) {
    // the most important function in the whole file!

    String basic = getLocalizedName(); // backup
    if(level == 0) {
      return basic;
    }
    else if(level > 1) {
      basic += " " + TinkerUtil.getRomanNumeral(level);
    }

    for(int i = level; i > 1; i--) {
      if(I18n.canTranslate(String.format(LOC_Name + i, getIdentifier()))) {
        basic = I18n.translateToLocal(String.format(LOC_Name + i, getIdentifier()));
        break;
      }
    }

    if(suffix != null) {
      basic += suffix;
    }
    return basic;
  }

  @Override
  public String getLocalizedName() {
    return Util.translate(LOC_Name, getIdentifier());
  }

  @Override
  public String getLocalizedDesc() {
    return Util.translate(LOC_Desc, getIdentifier());
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    return ImmutableList.of();
  }

  @Override
  public boolean equalModifier(NBTTagCompound modifierTag1, NBTTagCompound modifierTag2) {
    ModifierNBT data1 = ModifierNBT.readTag(modifierTag1);
    ModifierNBT data2 = ModifierNBT.readTag(modifierTag2);

    return data1.identifier.equals(data2.identifier) && data1.level == data2.level;
  }

  @Override
  public boolean hasTexturePerMaterial() {
    return false;
  }

  protected static boolean attackEntitySecondary(DamageSource source, float damage, Entity entity, boolean ignoreInvulv, boolean resetInvulv) {
    return attackEntitySecondary(source, damage, entity, ignoreInvulv, resetInvulv, true);
  }

  protected static boolean attackEntitySecondary(DamageSource source, float damage, Entity entity, boolean ignoreInvulv, boolean resetInvulv, boolean noKnockback) {
    Optional<EntityLivingBase> entityLivingBase = Optional.of(entity)
                                                          .filter(e -> e instanceof EntityLivingBase)
                                                          .map(e -> (EntityLivingBase) e);
    Optional<IAttributeInstance> knockbackAttribute = entityLivingBase.map(living -> living.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE))
                                                                      .filter(attribute -> !attribute.hasModifier(ANTI_KNOCKBACK_MOD));
    float oldLastDamage = entityLivingBase.map(living -> living.lastDamage).orElse(0f);

     if(noKnockback) {
      knockbackAttribute.ifPresent(attribute -> attribute.applyModifier(ANTI_KNOCKBACK_MOD));
    }

    // set hurt resistance time to 0 because we always want to deal damage in traits
    if(ignoreInvulv) {
      entity.hurtResistantTime = 0;
    }
    boolean hit = entity.attackEntityFrom(source, damage);
    // set total received damage, important for AI and stuff
    entityLivingBase.ifPresent(living -> living.lastDamage += oldLastDamage);

    // reset hurt resistance time if desired
    if(hit && resetInvulv) {
      entity.hurtResistantTime = 0;
    }

    if(noKnockback) {
      knockbackAttribute.ifPresent(attribute -> attribute.removeModifier(ANTI_KNOCKBACK_MOD));
    }

    return hit;
  }

  @Override
  public boolean hasItemsToApplyWith() {
    return !items.isEmpty();
  }

  private static final AttributeModifier ANTI_KNOCKBACK_MOD = new AttributeModifier("Anti Modifier Knockback", 1f, 0);

}
