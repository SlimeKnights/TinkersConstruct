package slimeknights.tconstruct.library.tools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.IAoeTool;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tinkering.IModifiable;
import slimeknights.tconstruct.library.tinkering.IRepairable;
import slimeknights.tconstruct.library.tinkering.ITinkerStationDisplay;
import slimeknights.tconstruct.library.tinkering.ITinkerable;
import slimeknights.tconstruct.library.tinkering.IndestructibleEntityItem;
import slimeknights.tconstruct.library.tinkering.PartMaterialRequirement;
import slimeknights.tconstruct.library.tinkering.ToolPartItem;
import slimeknights.tconstruct.library.tools.helper.AoeToolInteractionUtil;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolInteractionUtil;
import slimeknights.tconstruct.library.tools.helper.ToolMiningLogic;
import slimeknights.tconstruct.library.tools.helper.TraitUtil;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolData;
import slimeknights.tconstruct.library.utils.TooltipBuilder;
import slimeknights.tconstruct.tools.ToolStatsBuilder;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * An indestructible item constructed from different parts.
 * This class handles how all the data for items made out of different
 * The NBT representation of tool stats, what the tool is made of, which modifier have been applied, etc.
 */
public abstract class ToolCore extends Item implements ITinkerable, IModifiable, IRepairable, ITinkerStationDisplay {

  private final ToolDefinition toolDefinition;
  private final ToolMiningLogic toolMiningLogic;

  public ToolCore(Properties properties, ToolDefinition toolDefinition) {
    this(properties.maxStackSize(1).setNoRepair(), toolDefinition, new ToolMiningLogic());
  }

  protected ToolCore(Properties properties, ToolDefinition toolDefinition, ToolMiningLogic toolMiningLogic) {
    super(properties);
    this.toolDefinition = toolDefinition;
    this.toolMiningLogic = toolMiningLogic;
  }

  public ToolDefinition getToolDefinition() {
    return this.toolDefinition;
  }

  public ToolMiningLogic getToolMiningLogic() {
    return this.toolMiningLogic;
  }

  public StatsNBT buildToolStats(List<IMaterial> materials) {
    return ToolStatsBuilder.from(materials, this.toolDefinition).buildDefaultStats();
  }

  /* Item Entity -> INDESTRUCTIBLE */

  @Override
  public boolean hasCustomEntity(ItemStack stack) {
    return true;
  }

  @Override
  public Entity createEntity(World world, Entity original, ItemStack itemstack) {
    IndestructibleEntityItem entity = new IndestructibleEntityItem(world, original.getPosX(), original.getPosY(), original.getPosZ(), itemstack);
    entity.setPickupDelayFrom(original);
    return entity;
  }

  /* Damage/Durability */

  @Override
  public int getMaxDamage(ItemStack stack) {
    StatsNBT stats = ToolData.from(stack).getStats();
    // the tool can only have damage when it's not broken, to prevent vanilla from deleting the itemstack
    return stats.broken ? 0 : stats.durability;
  }

  @Override
  public void setDamage(ItemStack stack, int damage) {
    int max = this.getMaxDamage(stack);
    super.setDamage(stack, Math.min(max, damage));

    if (this.getDamage(stack) >= max) {
      stack.getOrCreateTag().putInt("Damage", max);

      ToolData toolData = ToolData.from(stack);

      if (!toolData.getStats().broken) {
        ToolData newData = toolData.createNewDataWithBroken(true);
        newData.updateStack(stack);
      }
    }
  }

  /**
   * We basically emulate Itemstack.damageItem here. We always return 0 to skip the handling in ItemStack.
   * If we don't broken tools will be deleted.
   */
  @Override
  public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T damager, Consumer<T> onBroken) {
    ToolInteractionUtil.damageTool(stack, amount, damager);

    if (ToolData.from(stack).getStats().broken) {
      onBroken.accept(damager);
    }

    return 0;
  }

  @Override
  public boolean isDamageable() {
    return true;
  }

  @Override
  public boolean showDurabilityBar(ItemStack stack) {
    return super.showDurabilityBar(stack) && !ToolData.from(stack).getStats().broken;
  }

  /**
   * Gets the current tool durability
   *
   * @param stack the tool stack to use
   * @return the currently durability of the tool stack
   */
  public static int getCurrentDurability(ItemStack stack) {
    if (ToolData.isBroken(stack)) {
      return ToolData.from(stack).getStats().durability - stack.getDamage();
    }

    return stack.getMaxDamage() - stack.getDamage();
  }

  /* Mining */

  @Override
  public Set<ToolType> getToolTypes(ItemStack stack) {
    // no classes if broken
    if (ToolData.from(stack).getStats().broken) {
      return Collections.emptySet();
    }

    return super.getToolTypes(stack);
  }

  @Override
  public int getHarvestLevel(ItemStack stack, ToolType toolClass, @Nullable PlayerEntity player, @Nullable BlockState blockState) {
    StatsNBT stats = ToolData.from(stack).getStats();

    // brokenness is calculated in by the toolTypes check
    if (this.getToolTypes(stack).contains(toolClass)) {
      return stats.harvestLevel;
    }

    return -1;
  }

  /**
   * Handles damaging the tool and applying the traits
   * Called by onBlockDestroyed
   *
   * @param stack the tool stack
   * @param world the current world
   * @param state the block state at the given position
   * @param pos the block pos affected
   * @param livingEntity the entity
   * @param damage the damage to apply to the tool
   * @param wasEffective if the break was effective
   */
  public void afterBlockBreak(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity livingEntity, int damage, boolean wasEffective) {
    TraitUtil.forEachTrait(stack, trait -> trait.afterBlockBreak(stack, world, state, pos, livingEntity, wasEffective));
    stack.damageItem(damage, livingEntity,
      entity -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
  }

  @Override
  public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
    StatsNBT stats = ToolData.from(stack).getStats();

    if (stats.broken) {
      return false;
    }

    boolean effective = this.isEffective(state) || ToolInteractionUtil.isToolEffectiveAgainstBlock(stack, worldIn.getBlockState(pos));
    int damage = effective ? 1 : 2;

    this.afterBlockBreak(stack, worldIn, state, pos, entityLiving, damage, effective);

    return effective && this.toolDefinition.hasCategory(Category.HARVEST);
  }

  public abstract boolean isEffective(BlockState state);

  @Override
  public float getDestroySpeed(ItemStack stack, BlockState state) {
    if (this.isEffective(state) || ToolInteractionUtil.isToolEffectiveAgainstBlock(stack, state)) {
      return this.toolMiningLogic.calcDigSpeed(stack, state);
    }

    return super.getDestroySpeed(stack, state);
  }

  @Override
  public boolean canHarvestBlock(ItemStack stack, BlockState state) {
    return this.isEffective(state) && !ToolData.isBroken(stack);
  }

  /* Repairing */

  @Override
  public boolean needsRepair(ItemStack repairable) {
    if (repairable.getDamage() == 0 && !ToolData.isBroken(repairable)) {
      // undamaged and not broken - no need to repair
      return false;
    }

    ToolData toolData = ToolData.readFromNBT(repairable.getTag());

    List<IMaterial> materials = toolData.getMaterials();
    return !materials.isEmpty();
  }

  @Nonnull
  @Override
  public ItemStack repair(ItemStack repairable, NonNullList<ItemStack> repairItems) {
    //todo decide how to handle this
    return repairable;
  }

  /* Attacking */

  /**
   * Actually deal damage to the entity we hit. Can be overridden for special behaviour
   *
   * @return True if the entity was hit. Usually the return value of {@link Entity#attackEntityFrom(DamageSource, float)}
   */
  public boolean dealDamage(ItemStack stack, LivingEntity player, Entity entity, float damage) {
    if (player instanceof PlayerEntity) {
      return entity.attackEntityFrom(DamageSource.causePlayerDamage((PlayerEntity) player), damage);
    }

    return entity.attackEntityFrom(DamageSource.causeMobDamage(player), damage);
  }

  /**
   * Checks if the player is ready to special attack
   *
   * @param player the player to check
   * @return whether or not if the player is ready for special attack
   */
  protected boolean readyForSpecialAttack(LivingEntity player) {
    return player instanceof PlayerEntity && ((PlayerEntity) player).getCooledAttackStrength(0.5f) > 0.9f;
  }

  @Override
  public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
    return ToolAttackUtil.attackEntity(stack, this, player, entity);
  }

  @Override
  public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    float speed = ToolData.from(stack).getStats().attackSpeedMultiplier;

    if (!stack.isEmpty() && stack.getItem() instanceof ToolCore) {
      speed *= ((ToolCore) stack.getItem()).getToolDefinition().getBaseStatDefinition().getAttackSpeed();
    }

    int time = Math.round(20f / speed);
    if (time < target.hurtResistantTime / 2) {
      target.hurtResistantTime = (target.hurtResistantTime + time) / 2;
      target.hurtTime = (target.hurtTime + time) / 2;
    }

    return super.hitEntity(stack, target, attacker);
  }

  @Override
  public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

    StatsNBT statsNBT = ToolData.from(stack).getStats();

    float speed = statsNBT.attackSpeedMultiplier;

    if (!stack.isEmpty() && stack.getItem() instanceof ToolCore) {
      speed *= ((ToolCore) stack.getItem()).getToolDefinition().getBaseStatDefinition().getAttackSpeed();
    }

    float damage = statsNBT.attack;

    if (!stack.isEmpty() && stack.getItem() instanceof ToolCore) {
      damage *= ((ToolCore) stack.getItem()).getToolDefinition().getBaseStatDefinition().getDamageModifier();
    }

    if (slot == EquipmentSlotType.MAINHAND && !ToolData.isBroken(stack)) {
      builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", damage, AttributeModifier.Operation.ADDITION));
      builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", speed - 4d, AttributeModifier.Operation.ADDITION));
    }

    Multimap<Attribute, AttributeModifier> attributeMap = builder.build();

    TraitUtil.forEachTrait(stack, trait -> trait.getAttributeModifiers(slot, stack, attributeMap));

    return attributeMap;
  }

  /* World interaction */

  @Override
  public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
    if (!ToolData.isBroken(itemstack) && this instanceof IAoeTool && ((IAoeTool) this).isAoeHarvestTool()) {
      for (BlockPos extraPos : ((IAoeTool) this).getAOEBlocks(itemstack, player.getEntityWorld(), player, pos)) {
        this.breakExtraBlock(itemstack, player.getEntityWorld(), player, extraPos, pos);
      }
    }

    /*// this is a really dumb hack.
    // Basically when something with silktouch harvests a block from the offhand
    // the game can't detect that. so we have to switch around the items in the hands for the break call
    // it's switched back in onBlockDestroyed
    if (DualToolHarvestUtil.shouldUseOffhand(player, pos, player.getHeldItemMainhand())) {
      ItemStack off = player.getHeldItemOffhand();

      this.switchItemsInHands(player);
      // remember, off is in the mainhand now
      CompoundNBT tag = off.getOrCreateTag();
      tag.putLong(TAG_SWITCHED_HAND_HAX, player.getEntityWorld().getGameTime());
      off.setTag(tag);
    }*/

    return this.breakBlock(itemstack, pos, player);
  }

  /**
   * Called to break the base block, return false to perform no breaking
   * @param itemstack Tool ItemStack
   * @param pos       Current position
   * @param player    Player instance
   * @return true if the normal block break code should be skipped
   */
  // todo: find a better way to solve this and breakExtraBlock?
  protected boolean breakBlock(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
    return super.onBlockStartBreak(itemstack, pos, player);
  }

  /**
   * Called when an AOE block is broken by the tool. Use to override the block breaking logic
   *
   * @param tool   Tool ItemStack
   * @param world  World instance
   * @param player Player instance
   * @param pos    Current position
   * @param refPos Base position
   */
  protected void breakExtraBlock(ItemStack tool, World world, PlayerEntity player, BlockPos pos, BlockPos refPos) {
    AoeToolInteractionUtil.breakExtraBlock(tool, world, player, pos, refPos);
  }

  /* Trait interactions */

  @Override
  public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

    this.onUpdateTraits(stack, worldIn, entityIn, itemSlot, isSelected);
  }

  protected void onUpdateTraits(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    final boolean isSelectedOrOffhand = isSelected || (entityIn instanceof PlayerEntity && ((PlayerEntity) entityIn).getHeldItemOffhand() == stack);

    TraitUtil.forEachTrait(stack, trait -> trait.onUpdate(stack, worldIn, entityIn, itemSlot, isSelectedOrOffhand));
  }

  /* Information */

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    boolean shift = Util.isShiftKeyDown();
    boolean ctrl = Util.isCtrlKeyDown();

    // modifiers
    if (!shift && !ctrl) {
      this.getTooltip(stack, tooltip);

      tooltip.add(StringTextComponent.EMPTY);

      tooltip.add(new TranslationTextComponent("tooltip.tool.hold_shift"));
      tooltip.add(new TranslationTextComponent("tooltip.tool.hold_ctrl"));

      if (worldIn != null) {
        tooltip.add((new TranslationTextComponent("attribute.modifier.plus.0", Util.df.format(ToolAttackUtil.getActualDamage(stack, Minecraft.getInstance().player)), new TranslationTextComponent("attribute.name.generic.attack_damage"))).mergeStyle(TextFormatting.BLUE));
      }
    }
    // detailed data
    else if (shift) {
      this.getTooltipDetailed(stack, tooltip);
    }
    // component data
    else if (ctrl) {
      this.getTooltipComponents(stack, tooltip);
    }
  }

  @Override
  public void getTooltip(ItemStack stack, List<ITextComponent> tooltips) {
    if (ToolData.isBroken(stack)) {
      tooltips.add(this.getBrokenToolTip(stack).mergeStyle(TextFormatting.DARK_RED, TextFormatting.BOLD));
    }
  }

  protected IFormattableTextComponent getBrokenToolTip(ItemStack itemStack) {
    return new TranslationTextComponent(TooltipBuilder.BROKEN_LOCALIZATION);
  }

  @Override
  public void getTooltipDetailed(ItemStack stack, List<ITextComponent> tooltips) {
    tooltips.addAll(this.getInformation(stack, false));
  }

  @Override
  public ITextComponent getLocalizedName() {
    return new TranslationTextComponent(this.getTranslationKey());
  }

  @Override
  public List<ITextComponent> getInformation(ItemStack stack) {
    return this.getInformation(stack, true);
  }

  public List<ITextComponent> getInformation(ItemStack stack, boolean detailed) {
    TooltipBuilder info = new TooltipBuilder(stack);

    info.addDurability(!detailed);

    if (this.getToolDefinition().hasCategory(Category.HARVEST)) {
      info.addHarvestLevel();
      info.addMiningSpeed();
    }

    if (this.getToolDefinition().hasCategory(Category.LAUNCHER)) {
      info.addDrawSpeed();
      info.addRange();
      info.addProjectileBonusDamage();
    }

    info.addAttack();

    if (ToolData.from(stack).getStats().freeModifiers > 0) {
      info.addFreeModifiers();
    }

    if (detailed) {
      info.addModifierInfo();
    }

    return info.getTooltip();
  }

  @Override
  public void getTooltipComponents(ItemStack stack, List<ITextComponent> tooltips) {
    CompoundNBT tag = stack.getTag();
    if (tag == null) {
      tooltips.add(new StringTextComponent("No tool data. NBT missing."));
      return;
    }

    ToolData toolData = ToolData.readFromNBT(tag);

    List<IMaterial> materials = toolData.getMaterials();
    List<PartMaterialRequirement> components = this.getToolDefinition().getRequiredComponents();

    if (materials.size() < components.size()) {
      return;
    }

    for (int i = 0; i < components.size(); i++) {
      PartMaterialRequirement requirement = components.get(i);
      IMaterial material = materials.get(i);

      Item toolPart = requirement.getPart();

      if (toolPart instanceof IMaterialItem) {
        ItemStack partStack = ((IMaterialItem) toolPart).getItemstackWithMaterial(material);

        tooltips.add(partStack.getDisplayName().copyRaw().mergeStyle(TextFormatting.UNDERLINE).modifyStyle(style -> style.setColor(material.getColor())));

        for (IMaterialStats stat : MaterialRegistry.getInstance().getAllStats(material.getIdentifier())) {
          if (requirement.usesStat(stat.getIdentifier())) {
            tooltips.addAll(stat.getLocalizedInfo());
          }
        }

        tooltips.add(StringTextComponent.EMPTY);
      }
      else {
        tooltips.add(new ItemStack(toolPart).getDisplayName().copyRaw().mergeStyle(TextFormatting.UNDERLINE));
      }
    }
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.isInGroup(group)) {
      this.addDefaultSubItems(items);
    }

    super.fillItemGroup(group, items);
  }

  protected void addDefaultSubItems(List<ItemStack> items, Material... fixedMaterials) {
    if (MaterialRegistry.initialized()) {
      for (IMaterial material : MaterialRegistry.getInstance().getMaterials()) {
        List<IMaterial> materials = new ArrayList<>(this.getToolDefinition().getRequiredComponents().size());

        for (int i = 0; i < this.getToolDefinition().getRequiredComponents().size(); i++) {
          if (fixedMaterials.length > i && fixedMaterials[i] != null && this.getToolDefinition().getRequiredComponents().get(i).isValidMaterial(fixedMaterials[i])) {
            materials.add(fixedMaterials[i]);
          }
          else {
            // todo: check for applicability with stats
            materials.add(material);
          }
        }

        ItemStack tool = ToolBuildHandler.buildItemFromMaterials(this, materials);
        // only valid ones
        if (this.hasValidMaterials(tool)) {
          items.add(tool);
          if (!Config.COMMON.listAllToolMaterials.get()) {
            break;
          }
        }
      }
    }
  }

  public boolean hasValidMaterials(ItemStack stack) {
    // checks if the materials used support all stats needed
    List<IMaterial> materials = ToolData.from(stack).getMaterials();

    // something went wrong
    if (materials.size() != this.getToolDefinition().getRequiredComponents().size()) {
      return false;
    }

    // check if all materials used have the stats needed
    for (int i = 0; i < materials.size(); i++) {
      IMaterial material = materials.get(i);
      PartMaterialRequirement required = this.getToolDefinition().getRequiredComponents().get(i);
      if (!required.isValidMaterial(material)) {
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
    return false;
  }

  @Override
  public ITextComponent getDisplayName(ItemStack stack) {
    // if the tool is not named we use the repair tools for a prefix like thing
    List<IMaterial> materials = ToolData.from(stack).getMaterials();
    List<PartMaterialRequirement> components = ((ToolCore) stack.getItem()).getToolDefinition().getRequiredComponents();
    // we save all the ones for the name in a set so we don't have the same material in it twice
    Set<IMaterial> nameMaterials = Sets.newLinkedHashSet();

    if (materials.size() == components.size()) {
      for (int i = 0; i < components.size(); i++) {
        if (components.get(i).usesStat(HeadMaterialStats.ID) && i < materials.size()) {
          nameMaterials.add(materials.get(i));
        }
      }
    }

    return ToolCore.getCombinedItemName(super.getDisplayName(stack), nameMaterials);
  }

  public static ITextComponent getCombinedItemName(ITextComponent itemName, Collection<IMaterial> materials) {
    if (materials.isEmpty() || materials.stream().allMatch(IMaterial.UNKNOWN::equals)) {
      return itemName;
    }

    if (materials.size() == 1) {
      IMaterial material = materials.iterator().next();

      if (Util.canTranslate(material.getTranslationKey() + ".format")) {
        return new TranslationTextComponent(material.getTranslationKey() + ".format", itemName);
      }

      return new TranslationTextComponent(materials.iterator().next().getTranslationKey()).append(new StringTextComponent(" ")).append(itemName);
    }

    // multiple materials. we'll have to combine
    StringTextComponent name = new StringTextComponent("");

    Iterator<IMaterial> iter = materials.iterator();

    IMaterial material = iter.next();
    name.append(new TranslationTextComponent(material.getTranslationKey()));

    while (iter.hasNext()) {
      material = iter.next();
      name.appendString("-").append(new TranslationTextComponent(material.getTranslationKey()));
    }

    name.appendString(" ").append(itemName);

    return name;
  }

  public ItemStack buildToolForRendering() {
    if (MaterialRegistry.initialized()) {
      List<PartMaterialRequirement> requirements = this.getToolDefinition().getRequiredComponents();
      List<IMaterial> toolMaterials = new ArrayList<>(requirements.size());
      IMaterial material = IMaterial.UNKNOWN;

      for (int i = 0; i < requirements.size(); i++) {
        PartMaterialRequirement requirement = requirements.get(i);

        if (requirement.getPart() instanceof ToolPartItem) {
          ToolPartItem toolPart = (ToolPartItem) requirement.getPart();

          List<IMaterial> materials = MaterialRegistry.getInstance().getMaterials().stream().filter(toolPart::canUseMaterial).collect(Collectors.toList());

          if (material == IMaterial.UNKNOWN) {
            material = materials.get(TConstruct.random.nextInt(materials.size()));
          }

          toolMaterials.add(i, material);
        }
      }

      return ToolBuildHandler.buildItemFromMaterials(this, toolMaterials);
    }

    return ItemStack.EMPTY;
  }

  @Override
  public Rarity getRarity(ItemStack stack) {
    return Rarity.COMMON;
  }

  //  @OnlyIn(Dist.CLIENT)
  //  @Override
  //  public boolean hasEffect(ItemStack stack) {
  //    return TagUtil.hasEnchantEffect(stack);
  //  }
  //
  //  /* NBT loading */
  //
  //  @Override
  //  public boolean updateItemStackNBT(CompoundNBT nbt) {
  //    // when the itemstack is loaded from NBT we recalculate all the data
  //    if(nbt.contains(Tags.BASE)) {
  //      try {
  //        // todo ToolBuilder.rebuildTool(nbt, this);
  //        throw new TinkerGuiException();
  //      }
  //      catch(TinkerGuiException e) {
  //        // nothing to do
  //      }
  //    }
  //
  //    // return value shouldn't matter since it's never checked
  //    return true;
  //  }

  /* Misc */

  public BlockRayTraceResult blockRayTrace(World worldIn, PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
    return (BlockRayTraceResult) Item.rayTrace(worldIn, player, fluidMode);
  }

  //
  //  @Override
  //  public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
  //    return shouldCauseReequipAnimation(oldStack, newStack, false);
  //  }
  //
  //  @Override
  //  public boolean shouldCauseReequipAnimation(ItemStack oldStack, @Nonnull ItemStack newStack, boolean slotChanged) {
  //    if(TagUtil.getResetFlag(newStack)) {
  //      TagUtil.setResetFlag(newStack, false);
  //      return true;
  //    }
  //    if(oldStack == newStack) {
  //      return false;
  //    }
  //    if(slotChanged) {
  //      return true;
  //    }
  //
  //    if(oldStack.hasEffect() != newStack.hasEffect()) {
  //      return true;
  //    }
  //
  //    Multimap<String, AttributeModifier> attributesNew = newStack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
  //    Multimap<String, AttributeModifier> attributesOld = oldStack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
  //
  //    if(attributesNew.size() != attributesOld.size()) {
  //      return true;
  //    }
  //    for(String key : attributesOld.keySet()) {
  //      if(!attributesNew.containsKey(key)) {
  //        return true;
  //      }
  //      Iterator<AttributeModifier> iter1 = attributesNew.get(key).iterator();
  //      Iterator<AttributeModifier> iter2 = attributesOld.get(key).iterator();
  //      while(iter1.hasNext() && iter2.hasNext()) {
  //        if(!iter1.next().equals(iter2.next())) {
  //          return true;
  //        }
  //      }
  //    }
  //
  //    if(oldStack.getItem() == newStack.getItem() && newStack.getItem() instanceof ToolCore) {
  //      return !isEqualTinkersItem(oldStack, newStack);
  //    }
  //    return !ItemStack.areItemStacksEqual(oldStack, newStack);
  //  }
}
