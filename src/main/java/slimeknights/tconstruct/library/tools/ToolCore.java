package slimeknights.tconstruct.library.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.IToolStationDisplay;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.library.utils.TooltipBuilder;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.traits.InfiTool;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Intermediate abstraction layer for all tools/melee weapons. This class has all the callbacks for blocks and enemies
 * so tools and weapons can share behaviour.
 */
public abstract class ToolCore extends TinkersItem implements IToolStationDisplay {

  public final static int DEFAULT_MODIFIERS = 3;
  public static final String TAG_SWITCHED_HAND_HAX = "SwitchedHand";

  public ToolCore(PartMaterialType... requiredComponents) {
    super(requiredComponents);

    this.setCreativeTab(TinkerRegistry.tabTools);
    this.setNoRepair(); // >_>

    TinkerRegistry.registerTool(this);
    addCategory(Category.TOOL);
  }

  @Override
  public int getMaxDamage(ItemStack stack) {
    return ToolHelper.getDurabilityStat(stack);
  }

  @Override
  public void setDamage(ItemStack stack, int damage) {
    int max = getMaxDamage(stack);
    super.setDamage(stack, Math.min(max, damage));

    if(getDamage(stack) == max) {
      ToolHelper.breakTool(stack, null);
    }
  }

  @Override
  public boolean isDamageable() {
    return true;
  }

  @Override
  public boolean showDurabilityBar(ItemStack stack) {
    return super.showDurabilityBar(stack) && !ToolHelper.isBroken(stack);
  }

  /* Tool and Weapon specific properties */

  /**
   * Multiplier applied to the actual mining speed of the tool
   * Internally a hammer and pick have the same speed, but a hammer is 2/3 slower
   */
  public float miningSpeedModifier() {
    return 1f;
  }

  /** Multiplier for damage from materials. Should be fixed per tool. */
  public abstract float damagePotential();

  /**
   * A fixed damage value where the calculations start to apply dimishing returns.
   * Basically if you'd hit more than that damage with this tool, the damage is gradually reduced depending on how much the cutoff is exceeded.
   */
  public float damageCutoff() {
    return 15.0f; // in general this should be sufficient and only needs increasing if it's a stronger weapon
    // fun fact: diamond sword with sharpness V has 15 damage
  }

  /**
   * Allows you set the base attack speed, can be changed by modifiers. Equivalent to the vanilla attack speed.
   * 4 is equal to any standard item. Value has to be greater than zero.
   */
  public abstract double attackSpeed();

  /**
   * Knockback modifier. Basically this takes the vanilla knockback on hit and modifies it by this factor.
   */
  public float knockback() {
    return 1.0f;
  }

  /**
   * Actually deal damage to the entity we hit. Can be overridden for special behaviour
   *
   * @return True if the entity was hit. Usually the return value of {@link Entity#attackEntityFrom(DamageSource, float)}
   */
  public boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage) {
    if(player instanceof EntityPlayer) {
      return entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), damage);
    }
    return entity.attackEntityFrom(DamageSource.causeMobDamage(player), damage);
  }

  protected boolean readyForSpecialAttack(EntityLivingBase player) {
    return player instanceof EntityPlayer && ((EntityPlayer) player).getCooledAttackStrength(0.5f) > 0.9f;
  }

  /**
   * Called when an entity is getting damaged with the tool.
   * Reduce the tools durability accordingly
   * player can be null!
   */
  public void reduceDurabilityOnHit(ItemStack stack, EntityPlayer player, float damage) {
    damage = Math.max(1f, damage / 10f);
    if(!hasCategory(Category.WEAPON)) {
      damage *= 2;
    }
    ToolHelper.damageTool(stack, (int) damage, player);
  }

  @Override
  public float getStrVsBlock(ItemStack stack, IBlockState state) {
    if(isEffective(state) || ToolHelper.isToolEffective(stack, state)) {
      return ToolHelper.calcDigSpeed(stack, state);
    }
    return super.getStrVsBlock(stack, state);
  }

  public boolean isEffective(IBlockState state) {
    return false;
  }

  @Override
  public boolean canHarvestBlock(@Nonnull IBlockState state, ItemStack stack) {
    return isEffective(state) && !ToolHelper.isBroken(stack);
  }

  @Override
  public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
    if(!ToolHelper.isBroken(itemstack) && this instanceof IAoeTool && ((IAoeTool) this).isAoeHarvestTool()) {
      for(BlockPos extraPos : ((IAoeTool) this).getAOEBlocks(itemstack, player.getEntityWorld(), player, pos)) {
        breakExtraBlock(itemstack, player.getEntityWorld(), player, extraPos, pos);
      }
    }

    // this is a really dumb hack.
    // Basically when something with silktouch harvests a block from the offhand
    // the game can't detect that. so we have to switch around the items in the hands for the break call
    // it's switched back in onBlockDestroyed
    if(DualToolHarvestUtils.shouldUseOffhand(player, pos, player.getHeldItemMainhand())) {
      ItemStack off = player.getHeldItemOffhand();
      switchItemsInHands(player);
      // remember, off is in the mainhand now
      NBTTagCompound tag = TagUtil.getTagSafe(off);
      tag.setLong(TAG_SWITCHED_HAND_HAX, player.getEntityWorld().getTotalWorldTime());
      off.setTagCompound(tag);
    }

    return breakBlock(itemstack, pos, player);
  }

  /**
   * Called to break the base block, return false to perform no breaking
   * @param itemstack Tool ItemStack
   * @param pos       Current position
   * @param player    Player instance
   * @return true if the normal block break code should be skipped
   */
  protected boolean breakBlock(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
    return super.onBlockStartBreak(itemstack, pos, player);
  }

  /**
   * Called when an AOE block is broken by the tool. Use to oveerride the block breaking logic
   * @param tool      Tool ItemStack
   * @param world     World instance
   * @param player    Player instance
   * @param pos       Current position
   * @param refPos    Base position
   */
  protected void breakExtraBlock(ItemStack tool, World world, EntityPlayer player, BlockPos pos, BlockPos refPos) {
    ToolHelper.breakExtraBlock(tool, world, player, pos, refPos);
  }

  @Override
  public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
    return ToolHelper.attackEntity(stack, this, player, entity);
  }

  @Override
  public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
    /*if(attackSpeed() > 0) {
      int speed = Math.min(5, attackSpeed());
      ToolHelper.swingItem(speed, entityLiving);
      return true;
    }*/
    return super.onEntitySwing(entityLiving, stack);
  }

  @Override
  public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
    float speed = ToolHelper.getActualAttackSpeed(stack);
    int time = Math.round(20f / speed);
    if(time < target.hurtResistantTime / 2) {
      target.hurtResistantTime = (target.hurtResistantTime + time) / 2;
      target.hurtTime = (target.hurtTime + time) / 2;
    }
    return super.hitEntity(stack, target, attacker);
  }

  @Nonnull
  @Override
  public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot slot, ItemStack stack) {
    Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

    if(slot == EntityEquipmentSlot.MAINHAND && !ToolHelper.isBroken(stack)) {
      multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", ToolHelper.getActualAttack(stack), 0));
      multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", ToolHelper.getActualAttackSpeed(stack) - 4d, 0));
    }

    TinkerUtil.getTraitsOrdered(stack).forEach(trait -> trait.getAttributeModifiers(slot, stack, multimap));

    return multimap;
  }

  @Override
  public List<String> getInformation(ItemStack stack) {
    return getInformation(stack, true);
  }

  @Override
  public void getTooltip(ItemStack stack, List<String> tooltips) {
    if(ToolHelper.isBroken(stack)) {
      tooltips.add("" + TextFormatting.DARK_RED + TextFormatting.BOLD + getBrokenTooltip(stack));
    }
    super.getTooltip(stack, tooltips);
  }

  protected String getBrokenTooltip(ItemStack itemStack) {
    return Util.translate(TooltipBuilder.LOC_Broken);
  }

  @Override
  public void getTooltipDetailed(ItemStack stack, List<String> tooltips) {
    tooltips.addAll(getInformation(stack, false));
  }

  public List<String> getInformation(ItemStack stack, boolean detailed) {
    TooltipBuilder info = new TooltipBuilder(stack);

    info.addDurability(!detailed);
    if(hasCategory(Category.HARVEST)) {
      info.addHarvestLevel();
      info.addMiningSpeed();
    }
    if(hasCategory(Category.LAUNCHER)) {
      info.addDrawSpeed();
      info.addRange();
      info.addProjectileBonusDamage();
    }
    info.addAttack();

    if(ToolHelper.getFreeModifiers(stack) > 0) {
      info.addFreeModifiers();
    }

    if(detailed) {
      info.addModifierInfo();
    }

    return info.getTooltip();
  }

  @Override
  public void getTooltipComponents(ItemStack stack, List<String> tooltips) {
    List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(stack));
    List<PartMaterialType> component = getRequiredComponents();

    if(materials.size() < component.size()) {
      return;
    }

    for(int i = 0; i < component.size(); i++) {
      PartMaterialType pmt = component.get(i);
      Material material = materials.get(i);

      // get (one possible) toolpart used to craft the thing
      Iterator<IToolPart> partIter = pmt.getPossibleParts().iterator();
      if(!partIter.hasNext()) {
        continue;
      }

      IToolPart part = partIter.next();
      ItemStack partStack = part.getItemstackWithMaterial(material);
      if(partStack != null) {
        // we have the part, add it
        tooltips.add(material.getTextColor() + TextFormatting.UNDERLINE + partStack.getDisplayName());

        Set<ITrait> usedTraits = Sets.newHashSet();
        // find out which stats and traits it contributes and add it to the tooltip
        for(IMaterialStats stats : material.getAllStats()) {
          if(pmt.usesStat(stats.getIdentifier())) {
            tooltips.addAll(stats.getLocalizedInfo());
            for(ITrait trait : pmt.getApplicableTraitsForMaterial(material)) {
              if(!usedTraits.contains(trait)) {
                tooltips.add(material.getTextColor() + trait.getLocalizedName());
                usedTraits.add(trait);
              }
            }
          }
        }
        tooltips.add("");
      }
    }
  }

  @Nonnull
  @SideOnly(Side.CLIENT)
  @Override
  public FontRenderer getFontRenderer(ItemStack stack) {
    return ClientProxy.fontRenderer;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean hasEffect(ItemStack stack) {
    return TagUtil.hasEnchantEffect(stack);
  }

  @Nonnull
  @Override
  public String getItemStackDisplayName(@Nonnull ItemStack stack) {
    // if the tool is not named we use the repair tools for a prefix like thing
    List<Material> materials = TinkerUtil.getMaterialsFromTagList(TagUtil.getBaseMaterialsTagList(stack));
    // we save all the ones for the name in a set so we don't have the same material in it twice
    Set<Material> nameMaterials = Sets.newLinkedHashSet();

    for(int index : getRepairParts()) {
      if(index < materials.size()) {
        nameMaterials.add(materials.get(index));
      }
    }

    return Material.getCombinedItemName(super.getItemStackDisplayName(stack), nameMaterials);
  }

  // Creative tab items
  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
    if(this.isInCreativeTab(tab)) {
      addDefaultSubItems(subItems);
    }
  }

  protected void addDefaultSubItems(List<ItemStack> subItems, Material... fixedMaterials) {
    for(Material head : TinkerRegistry.getAllMaterials()) {
      List<Material> mats = new ArrayList<>(requiredComponents.length);

      for(int i = 0; i < requiredComponents.length; i++) {
        if(fixedMaterials.length > i && fixedMaterials[i] != null && requiredComponents[i].isValidMaterial(fixedMaterials[i])) {
          mats.add(fixedMaterials[i]);
        }
        else {
          // todo: check for applicability with stats
          mats.add(head);
        }
      }

      ItemStack tool = buildItem(mats);
      // only valid ones
      if(hasValidMaterials(tool)) {
        subItems.add(tool);
        if(!Config.listAllToolMaterials) {
          break;
        }
      }
    }
  }

  protected void addInfiTool(List<ItemStack> subItems, String name) {
    ItemStack tool = getInfiTool(name);
    if(hasValidMaterials(tool)) {
      subItems.add(tool);
    }
  }

  protected ItemStack getInfiTool(String name) {
    // The InfiHarvester!
    List<Material> materials = ImmutableList.of(TinkerMaterials.slime, TinkerMaterials.cobalt, TinkerMaterials.ardite, TinkerMaterials.ardite);
    materials = materials.subList(0, requiredComponents.length);
    ItemStack tool = buildItem(materials);
    tool.setStackDisplayName(name);
    InfiTool.INSTANCE.apply(tool);

    return tool;
  }

  @Override
  public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
    if(ToolHelper.isBroken(stack)) {
      return -1;
    }

    if(this.getToolClasses(stack).contains(toolClass)) {
      // will return 0 if the tag has no info anyway
      return ToolHelper.getHarvestLevelStat(stack);
    }

    return super.getHarvestLevel(stack, toolClass, player, blockState);
  }

  @Override
  public Set<String> getToolClasses(ItemStack stack) {
    // no classes if broken
    if(ToolHelper.isBroken(stack)) {
      return Collections.emptySet();
    }
    return super.getToolClasses(stack);
  }

  /** A simple string identifier for the tool, used for identification in texture generation etc. */
  public String getIdentifier() {
    return getRegistryName().getResourcePath();
  }

  /** The tools name completely without material information */
  @Override
  public String getLocalizedToolName() {
    return Util.translate(getUnlocalizedName() + ".name");
  }

  /** The tools name with the given material. e.g. "Wooden Pickaxe" */
  public String getLocalizedToolName(Material material) {
    return material.getLocalizedItemName(getLocalizedToolName());
  }

  /** Returns info about the Tool. Displayed in the tool stations etc. */
  public String getLocalizedDescription() {
    return Util.translate(getUnlocalizedName() + ".desc");
  }

  @Override
  protected int repairCustom(Material material, NonNullList<ItemStack> repairItems) {
    Optional<RecipeMatch.Match> matchOptional = RecipeMatch.of(TinkerTools.sharpeningKit).matches(repairItems);
    if(!matchOptional.isPresent()) {
      return 0;
    }

    RecipeMatch.Match match = matchOptional.get();
    for(ItemStack stacks : match.stacks) {
      // invalid material?
      if(TinkerTools.sharpeningKit.getMaterial(stacks) != material) {
        return 0;
      }
    }

    RecipeMatch.removeMatch(repairItems, match);
    HeadMaterialStats stats = material.getStats(MaterialTypes.HEAD);
    float durability = stats.durability * match.amount * TinkerTools.sharpeningKit.getCost();
    durability /= Material.VALUE_Ingot;
    return (int) (durability);
  }

  /* Additional Trait callbacks */

  @Override
  public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);

    onUpdateTraits(stack, worldIn, entityIn, itemSlot, isSelected);
  }

  protected void onUpdateTraits(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    final boolean isSelectedOrOffhand = isSelected ||
                                     (entityIn instanceof EntityPlayer && ((EntityPlayer) entityIn).getHeldItemOffhand() == stack);

    TinkerUtil.getTraitsOrdered(stack).forEach(trait -> trait.onUpdate(stack, worldIn, entityIn, itemSlot, isSelectedOrOffhand));
  }

  @Override
  public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
    // move item back into offhand. See onBlockBreakStart
    if(stack != null && entityLiving != null && stack.hasTagCompound()) {
      NBTTagCompound tag = stack.getTagCompound();
      assert tag != null;
      if(tag.getLong(TAG_SWITCHED_HAND_HAX) == entityLiving.getEntityWorld().getTotalWorldTime()) {
        tag.removeTag(TAG_SWITCHED_HAND_HAX);
        stack.setTagCompound(tag);

        switchItemsInHands(entityLiving);
      }
    }
    if(ToolHelper.isBroken(stack)) {
      return false;
    }

    boolean effective = isEffective(state) || ToolHelper.isToolEffective(stack, worldIn.getBlockState(pos));
    int damage = effective ? 1 : 2;

    afterBlockBreak(stack, worldIn, state, pos, entityLiving, damage, effective);

    return hasCategory(Category.TOOL);
  }

  protected void switchItemsInHands(EntityLivingBase entityLiving) {
    ItemStack main = entityLiving.getHeldItemMainhand();
    ItemStack off = entityLiving.getHeldItemOffhand();
    entityLiving.setHeldItem(EnumHand.OFF_HAND, main);
    entityLiving.setHeldItem(EnumHand.MAIN_HAND, off);
  }

  public void afterBlockBreak(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase player, int damage, boolean wasEffective) {
    TinkerUtil.getTraitsOrdered(stack).forEach(trait -> trait.afterBlockBreak(stack, world, state, pos, player, wasEffective));
    ToolHelper.damageTool(stack, damage, player);
  }

  // elevate to public
  @Override
  public RayTraceResult rayTrace(@Nonnull World worldIn, @Nonnull EntityPlayer playerIn, boolean useLiquids) {
    return super.rayTrace(worldIn, playerIn, useLiquids);
  }

  protected void preventSlowDown(Entity entityIn, float originalSpeed) {
    TinkerTools.proxy.preventPlayerSlowdown(entityIn, originalSpeed, this);
  }

  @Override
  public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
    return shouldCauseReequipAnimation(oldStack, newStack, false);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean shouldCauseReequipAnimation(ItemStack oldStack, @Nonnull ItemStack newStack, boolean slotChanged) {
    if(TagUtil.getResetFlag(newStack)) {
      TagUtil.setResetFlag(newStack, false);
      return true;
    }
    if(oldStack == newStack) {
      return false;
    }
    if(slotChanged) {
      return true;
    }

    if(oldStack.hasEffect() != newStack.hasEffect()) {
      return true;
    }

    Multimap<String, AttributeModifier> attributesNew = newStack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
    Multimap<String, AttributeModifier> attributesOld = oldStack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);

    if(attributesNew.size() != attributesOld.size()) {
      return true;
    }
    for(String key : attributesOld.keySet()) {
      if(!attributesNew.containsKey(key)) {
        return true;
      }
      Iterator<AttributeModifier> iter1 = attributesNew.get(key).iterator();
      Iterator<AttributeModifier> iter2 = attributesOld.get(key).iterator();
      while(iter1.hasNext() && iter2.hasNext()) {
        if(!iter1.next().equals(iter2.next())) {
          return true;
        }
      }
    }

    if(oldStack.getItem() == newStack.getItem() && newStack.getItem() instanceof ToolCore) {
      return !isEqualTinkersItem(oldStack, newStack);
    }
    return !ItemStack.areItemStacksEqual(oldStack, newStack);
  }

  /**
   * Builds a default tool from:
   * 1. Handle
   * 2. Head
   * 3. Accessoire (if present)
   */
  protected ToolNBT buildDefaultTag(List<Material> materials) {
    ToolNBT data = new ToolNBT();

    if(materials.size() >= 2) {
      HandleMaterialStats handle = materials.get(0).getStatsOrUnknown(MaterialTypes.HANDLE);
      HeadMaterialStats head = materials.get(1).getStatsOrUnknown(MaterialTypes.HEAD);
      // start with head
      data.head(head);

      // add in accessoires if present
      if(materials.size() >= 3) {
        ExtraMaterialStats binding = materials.get(2).getStatsOrUnknown(MaterialTypes.EXTRA);
        data.extra(binding);
      }

      // calculate handle impact
      data.handle(handle);
    }

    // 3 free modifiers
    data.modifiers = DEFAULT_MODIFIERS;

    return data;
  }

  public static boolean isEqualTinkersItem(ItemStack item1, ItemStack item2) {
    if(item1 == null || item2 == null || item1.getItem() != item2.getItem()) {
      return false;
    }
    if(!(item1.getItem() instanceof ToolCore)) {
      return false;
    }

    NBTTagCompound tag1 = TagUtil.getTagSafe(item1);
    NBTTagCompound tag2 = TagUtil.getTagSafe(item2);

    NBTTagList mods1 = TagUtil.getModifiersTagList(tag1);
    NBTTagList mods2 = TagUtil.getModifiersTagList(tag2);

    if(mods1.tagCount() != mods1.tagCount()) {
      return false;
    }

    // check modifiers
    for(int i = 0; i < mods1.tagCount(); i++) {
      NBTTagCompound tag = mods1.getCompoundTagAt(i);
      ModifierNBT data = ModifierNBT.readTag(tag);
      IModifier modifier = TinkerRegistry.getModifier(data.identifier);
      if(modifier != null && !modifier.equalModifier(tag, mods2.getCompoundTagAt(i))) {
        return false;
      }
    }

    return TagUtil.getBaseMaterialsTagList(tag1).equals(TagUtil.getBaseMaterialsTagList(tag2)) && // materials used
           TagUtil.getBaseModifiersUsed(tag1) == TagUtil.getBaseModifiersUsed(tag2) && // number of free modifiers used
           TagUtil.getOriginalToolStats(tag1).equals(TagUtil.getOriginalToolStats(tag2)); // unmodified base stats
  }
}
