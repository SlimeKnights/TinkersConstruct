package slimeknights.tconstruct.library.tools;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
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
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.IAoeTool;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tinkering.IModifiable;
import slimeknights.tconstruct.library.tinkering.IRepairable;
import slimeknights.tconstruct.library.tinkering.ITinkerable;
import slimeknights.tconstruct.library.tinkering.IToolStationDisplay;
import slimeknights.tconstruct.library.tinkering.IndestructibleEntityItem;
import slimeknights.tconstruct.library.tinkering.PartMaterialRequirement;
import slimeknights.tconstruct.library.tinkering.ToolPartItem;
import slimeknights.tconstruct.library.tools.helper.AoeToolInteractionUtil;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolBreakUtil;
import slimeknights.tconstruct.library.tools.helper.ToolInteractionUtil;
import slimeknights.tconstruct.library.tools.helper.ToolMiningLogic;
import slimeknights.tconstruct.library.tools.helper.TraitUtil;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolData;
import slimeknights.tconstruct.library.utils.TooltipBuilder;
import slimeknights.tconstruct.shared.CommonsClientEvents;
import slimeknights.tconstruct.tools.ToolStatsBuilder;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * An indestructible item constructed from different parts.
 * This class handles how all the data for items made out of different
 * The NBT representation of tool stats, what the tool is made of, which modifier have been applied, etc.
 */
public abstract class ToolCore extends Item implements ITinkerable, IModifiable, IToolStationDisplay, IRepairable {

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

  private void afterBlockBreak(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity player, int damage, boolean wasEffective) {
    TraitUtil.forEachTrait(stack, trait -> trait.afterBlockBreak(stack, world, state, pos, player, wasEffective));
    stack.damageItem(damage, player,
      livingEntity -> livingEntity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
  }

  @Override
  public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
    StatsNBT stats = ToolData.from(stack).getStats();

    if (stats.broken) {
      return false;
    }

    boolean effective = isEffective(state) || ToolInteractionUtil.isToolEffectiveAgainstBlock(stack, worldIn.getBlockState(pos));
    int damage = effective ? 1 : 2;

    this.afterBlockBreak(stack, worldIn, state, pos, entityLiving, damage, effective);

    return effective && this.toolDefinition.hasCategory(Category.HARVEST);
  }

  public abstract boolean isEffective(BlockState state);

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

  protected boolean readyForSpecialAttack(LivingEntity player) {
    return player instanceof PlayerEntity && ((PlayerEntity) player).getCooledAttackStrength(0.5f) > 0.9f;
  }

  @Override
  public float getDestroySpeed(ItemStack stack, BlockState state) {
    if (this.isEffective(state) || ToolInteractionUtil.isToolEffectiveAgainstBlock(stack, state)) {
      return this.toolMiningLogic.calcDigSpeed(stack, state);
    }

    return super.getDestroySpeed(stack, state);
  }

  @Override
  public boolean canHarvestBlock(ItemStack stack, BlockState state) {
    return isEffective(state) && !ToolData.isBroken(stack);
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

  public int[] getRepairParts() {
    return new int[] { 0 }; // index 0 usually is the head. 1 is handle.
  }

  @Nonnull
  @Override
  public ItemStack repair(List<MaterialRecipe> recipes, ItemStack repairable, NonNullList<ItemStack> repairItems) {
    if (repairable.getDamage() == 0 && !ToolData.isBroken(repairable)) {
      // undamaged and not broken - no need to repair
      return ItemStack.EMPTY;
    }

    ToolData toolData = ToolData.readFromNBT(repairable.getTag());

    List<IMaterial> materials = toolData.getMaterials();
    if (materials.isEmpty()) {
      return ItemStack.EMPTY;
    }

    NonNullList<ItemStack> items = Util.copyItemStackList(repairItems);
    boolean foundMatch = false;

    for (int index : this.getRepairParts()) {
      IMaterial material = materials.get(index);

      if (this.repairCustom(material, items) > 0) {
        foundMatch = true;
      }

      for (ItemStack itemStack : items) {
        if (!itemStack.isEmpty()) {
          for (MaterialRecipe materialRecipe : recipes) {
            if (materialRecipe.matches(itemStack) && materialRecipe.getMaterial() == material) {
              int itemCount = 1;

              while (itemCount != itemStack.getCount()) {
                itemCount++;
              }

              foundMatch = true;

              itemStack.shrink(itemCount);
            }
          }
        }
      }
    }

    if (!foundMatch) {
      return ItemStack.EMPTY;
    }

    // check if all items were used
    for (int i = 0; i < repairItems.size(); i++) {
      // was non-null and did not get modified (stacksize changed or null now, usually)
      if (!repairItems.get(i).isEmpty() && ItemStack.areItemStacksEqual(repairItems.get(i), items.get(i))) {
        // found an item that was not touched
        return ItemStack.EMPTY;
      }
    }

    // now do it all over again with the real items, to actually repair \o/
    ItemStack item = repairable.copy();

    do {
      int amount = this.calculateRepairAmount(recipes, materials, repairItems);

      // nothing to repair with, we're therefore done
      if (amount <= 0) {
        break;
      }

      ToolBreakUtil.repairTool(item, this.calculateRepair(item, amount));
    }
    while (item.getDamage() > 0);

    return item;
  }

  public float getRepairModifierForPart(int index) {
    return 1f;
  }

  protected int repairCustom(IMaterial material, NonNullList<ItemStack> repairItems) {
    return 0;
  }

  protected int calculateRepairAmount(List<MaterialRecipe> recipes, List<IMaterial> materials, NonNullList<ItemStack> repairItems) {
    Set<IMaterial> materialsMatched = Sets.newHashSet();
    float durability = 0f;
    // try to match each material once
    for (int index : this.getRepairParts()) {
      IMaterial material = materials.get(index);

      if (materialsMatched.contains(material)) {
        continue;
      }

      // custom repairing
      durability += this.repairCustom(material, repairItems) * this.getRepairModifierForPart(index);

      for (ItemStack itemStack : repairItems) {
        if (!itemStack.isEmpty()) {
          for (MaterialRecipe materialRecipe : recipes) {
            if (materialRecipe.matches(itemStack) && materialRecipe.getMaterial() == material) {
              int itemCount = 1;
              float costPerPart = materialRecipe.getValue() / (float) materialRecipe.getNeeded();
              float currentValue = costPerPart;

              while (itemCount != itemStack.getCount()) {
                currentValue += costPerPart;
                itemCount++;
              }

              Optional<IMaterialStats> headMaterialStats = MaterialRegistry.getInstance().getMaterialStats(material.getIdentifier(), HeadMaterialStats.ID);

              if (headMaterialStats.isPresent()) {
                materialsMatched.add(material);
                durability += ((float) ((HeadMaterialStats) headMaterialStats.get()).getDurability() * currentValue * getRepairModifierForPart(index)) / 1;
                itemStack.shrink(itemCount);
              }
            }
          }
        }
      }
    }

    durability *= 1f + ((float) materialsMatched.size() - 1) / 9f;

    return (int) durability;
  }

  protected int calculateRepair(ItemStack tool, int amount) {
    float ordinalDurability = ToolData.from(tool).getStats().durability;
    float actualDurability = ToolCore.getCurrentDurability(tool);

    // calculate in modifiers that change the total durability of a tool, like diamond
    // they should not punish the player with higher repair costs
    float durabilityFactor = actualDurability / ordinalDurability;
    float increase = amount * Math.min(10f, durabilityFactor);

    increase = Math.max(increase, actualDurability / 64f);

    int modifiersFree = ToolData.from(tool).getStats().freeModifiers;
    float mods = 1.0f;

    increase *= mods;

    return (int) Math.ceil(increase);
  }

  /* World interaction */

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
  public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
    Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

    float speed = ToolData.from(stack).getStats().attackSpeedMultiplier;

    if (!stack.isEmpty() && stack.getItem() instanceof ToolCore) {
      speed *= ((ToolCore) stack.getItem()).getToolDefinition().getBaseStatDefinition().getAttackSpeed();
    }

    float damage = ToolData.from(stack).getStats().attack;

    if (!stack.isEmpty() && stack.getItem() instanceof ToolCore) {
      damage *= ((ToolCore) stack.getItem()).getToolDefinition().getBaseStatDefinition().getDamageModifier();
    }

    if (slot == EquipmentSlotType.MAINHAND && !ToolData.isBroken(stack)) {
      multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", damage, AttributeModifier.Operation.ADDITION));
      multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", speed - 4d, AttributeModifier.Operation.ADDITION));
    }

    TraitUtil.forEachTrait(stack, trait -> trait.getAttributeModifiers(slot, stack, multimap));

    return multimap;
  }

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

      tooltip.add(new StringTextComponent(""));

      tooltip.add(new TranslationTextComponent("tooltip.tool.hold_shift"));
      tooltip.add(new TranslationTextComponent("tooltip.tool.hold_ctrl"));

      if (worldIn != null) {
        tooltip.add((new TranslationTextComponent("attribute.modifier.plus.0", Util.df.format(ToolAttackUtil.getActualDamage(stack, Minecraft.getInstance().player)), new TranslationTextComponent("attribute.name.generic.attackDamage"))).applyTextStyle(TextFormatting.BLUE));
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
      tooltips.add(new StringTextComponent(this.getBrokenToolTip(stack)).applyTextStyles(TextFormatting.DARK_RED, TextFormatting.BOLD));
    }
  }

  protected String getBrokenToolTip(ItemStack itemStack) {
    return new TranslationTextComponent(TooltipBuilder.BROKEN_LOCALIZATION).getFormattedText();
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

      Item toolPart = requirement.getPossiblePart();

      ItemStack partStack = ((IMaterialItem) toolPart).getItemstackWithMaterial(material);

      if (partStack != null || partStack != ItemStack.EMPTY) {
        tooltips.add(new StringTextComponent(material.getEncodedTextColor() + TextFormatting.UNDERLINE + partStack.getDisplayName().getFormattedText()));

        for (IMaterialStats stat : MaterialRegistry.getInstance().getAllStats(material.getIdentifier())) {
          if (requirement.usesStat(stat.getIdentifier())) {
            tooltips.addAll(stat.getLocalizedInfo());
          }
        }

        tooltips.add(new StringTextComponent(""));
      }
    }
  }

  @Nonnull
  @OnlyIn(Dist.CLIENT)
  @Override
  public FontRenderer getFontRenderer(ItemStack stack) {
    return CommonsClientEvents.fontRenderer;
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

    // something went wrooooong
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
    // we save all the ones for the name in a set so we don't have the same material in it twice
    Set<IMaterial> nameMaterials = Sets.newLinkedHashSet();

    for (int index : this.getRepairParts()) {
      if (index < materials.size()) {
        nameMaterials.add(materials.get(index));
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

      return new TranslationTextComponent(materials.iterator().next().getTranslationKey()).appendText(" ").appendSibling(itemName);
    }

    // multiple materials. we'll have to combine
    StringBuilder sb = new StringBuilder();
    Iterator<IMaterial> iter = materials.iterator();

    IMaterial material = iter.next();
    sb.append(new TranslationTextComponent(material.getTranslationKey()).getFormattedText());
    while (iter.hasNext()) {
      material = iter.next();
      sb.append("-");
      sb.append(new TranslationTextComponent(material.getTranslationKey()).getFormattedText());
    }
    sb.append(" ");
    sb.append(itemName.getFormattedText());

    return new StringTextComponent(sb.toString());
  }

  public ItemStack buildToolForRendering() {
    if (MaterialRegistry.initialized()) {
      List<PartMaterialRequirement> requirements = this.getToolDefinition().getRequiredComponents();
      List<IMaterial> toolMaterials = new ArrayList<>(requirements.size());
      IMaterial material = IMaterial.UNKNOWN;

      for (int i = 0; i < requirements.size(); i++) {
        PartMaterialRequirement requirement = requirements.get(i);

        if (requirement.getPossiblePart() instanceof ToolPartItem) {
          ToolPartItem toolPart = (ToolPartItem) requirement.getPossiblePart();

          List<IMaterial> materials = MaterialRegistry.getInstance().getMaterials().stream().filter(toolPart::canUseMaterial).collect(Collectors.toList());

          if(material == IMaterial.UNKNOWN) {
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

  //
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
