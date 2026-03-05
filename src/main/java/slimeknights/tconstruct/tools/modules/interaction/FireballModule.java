package slimeknights.tconstruct.tools.modules.interaction;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.array.ArrayLoadable;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.fluid.entity.DamageFluidEffect.DamageTypePair;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.entity.CustomFireball;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Module to shoot a fireball like projectile on right click.
 * @param options        List of ammo options
 * @param damageType     Default damage type if the ammo type does not specify
 * @param durability     Durability consumed on usage
 * @param ammoModifiers  Extra modifiers to add to the projectile unconditionally
 * @param condition      Standard modifier conditions
 */
public record FireballModule(List<FireballType> options, DamageTypePair damageType, float damageMultiplier, List<ModifierEntry> ammoModifiers, LevelingInt durability, SoundEvent sound, ModifierCondition<IToolStackView> condition) implements ModifierModule, GeneralInteractionModifierHook, KeybindInteractModifierHook, ConditionalModule<IToolStackView>, Predicate<ItemStack> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<FireballModule>defaultHooks(ModifierHooks.GENERAL_INTERACT, ModifierHooks.ARMOR_INTERACT);
  public static final RecordLoadable<FireballModule> LOADER = RecordLoadable.create(
    FireballType.LOADABLE.list(ArrayLoadable.COMPACT).requiredField("options", FireballModule::options),
    DamageTypePair.LOADER.requiredField("damage_type", FireballModule::damageType),
    FloatLoadable.FROM_ZERO.defaultField("damage_multiplier", 1f, false, FireballModule::damageMultiplier),
    ModifierEntry.LOADABLE.list(0).defaultField("ammo_modifiers", List.of(), false, FireballModule::ammoModifiers),
    LevelingInt.LOADABLE.requiredField("durability", FireballModule::durability),
    Loadables.SOUND_EVENT.defaultField("sound", SoundEvents.BLAZE_SHOOT, true, FireballModule::sound),
    ModifierCondition.TOOL_FIELD,
    FireballModule::new);

  /**
   * Single option of a fireball.
   * @param match          Ingredient to match for this option
   * @param damageType     Damage type to deal
   * @param ammoModifiers  Extra modifiers to add to this option
   */
  protected record FireballType(Ingredient match, @Nullable DamageTypePair damageType, float damageMultiplier, List<ModifierEntry> ammoModifiers) {
    public static final RecordLoadable<FireballType> LOADABLE = RecordLoadable.create(
      IngredientLoadable.DISALLOW_EMPTY.requiredField("match", FireballType::match),
      DamageTypePair.LOADER.nullableField("damage_type", FireballType::damageType),
      FloatLoadable.FROM_ZERO.defaultField("damage_multiplier", 1f, false, FireballType::damageMultiplier),
      ModifierEntry.LOADABLE.list(0).defaultField("ammo_modifiers", List.of(), false, FireballType::ammoModifiers),
      FireballType::new);
    /** Empty instance to make cache easier */
    public static final FireballType EMPTY = new FireballType(Ingredient.EMPTY, null, 1f, List.of());

    /** Gets the damage type with the given fallback */
    public DamageTypePair damageType(DamageTypePair fallback) {
      return damageType == null ? fallback : damageType;
    }
  }

  /** @apiNote use {@link #builder()} */
  @Internal
  public FireballModule {}

  @Override
  public RecordLoadable<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Gets the fireball type for the given stack */
  private FireballType getFireballType(ItemStack stack) {
    for (FireballType type : options) {
      if (type.match.test(stack)) {
        return type;
      }
    }
    return FireballType.EMPTY;
  }

  @Override
  public boolean test(ItemStack stack) {
    return getFireballType(stack) != FireballType.EMPTY;
  }

  /** Shoots the actual projectile */
  private boolean shoot(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, @Nullable Player player, EquipmentSlot slot) {
    Level level = entity.level();

    // first, try to find the requested ammo
    // TODO: multishot?
    ItemStack fireball = BowAmmoModifierHook.consumeAmmo(tool, ItemStack.EMPTY, entity, player, this, 1);
    if (!fireball.isEmpty()) {
      // if we found a fireball, fire it
      if (!level.isClientSide) {
        // fetch stats
        float power = ConditionalStatModifierHook.getModifiedStat(tool, entity, ToolStats.PROJECTILE_DAMAGE);
        float velocity = ConditionalStatModifierHook.getModifiedStat(tool, entity, ToolStats.VELOCITY);
        float inaccuracy = ModifierUtil.getInaccuracy(tool, entity) / 16f;


        // prepare projectile
        Vec3 lookVec = entity.getLookAngle().scale(2);
        RandomSource random = entity.getRandom();
        CustomFireball projectile = new CustomFireball(level, entity, lookVec.x + random.nextGaussian() * inaccuracy, lookVec.y, lookVec.z + random.nextGaussian() * inaccuracy);
        projectile.xPower *= velocity;
        projectile.yPower *= velocity;
        projectile.zPower *= velocity;
        projectile.setPower(power);
        projectile.setPos(projectile.getX(), entity.getY(0.5D) + 0.5D, projectile.getZ());

        // add in type specific behavior
        projectile.setItem(fireball);
        FireballType type = getFireballType(fireball);
        projectile.setDamageMultiplier(damageMultiplier * type.damageMultiplier);
        DamageTypePair damageTypes = type.damageType(this.damageType);
        projectile.setDamageType(damageTypes.ranged(), damageTypes.melee());

        // set projectile modifiers
        ModifierNBT modifiers = tool.getModifiers();
        if (!ammoModifiers.isEmpty()) {
          ModifierNBT.Builder builder = ModifierNBT.builder();
          builder.add(modifiers);
          builder.add(ammoModifiers);
          builder.add(type.ammoModifiers);
          modifiers = builder.build();
        }
        EntityModifierCapability.getCapability(projectile).setModifiers(modifiers);

        // fetch the persistent data for the fireball as modifiers may want to store data
        ModDataNBT projectileData = PersistentDataCapability.getOrWarn(projectile);
        // let modifiers set properties
        for (ModifierEntry entry : tool.getModifierList()) {
          entry.getHook(ModifierHooks.PROJECTILE_LAUNCH).onProjectileLaunch(tool, entry, entity, ItemStack.EMPTY, projectile, null, projectileData, true);
        }

        // finally, release the projectile
        level.addFreshEntity(projectile);

        // damage tool if not creative
        if (player != null && !player.isCreative()) {
          ToolDamageUtil.damageAnimated(tool, durability.compute(modifier), entity, slot, modifier.getId());
        }
      }
      entity.playSound(sound, 2.0F, (level.random.nextFloat() - level.random.nextFloat()) * 0.2F + 1.0F);
      return true;
    }
    return false;
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (condition.matches(tool, modifier) && !tool.isBroken() && tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      if (shoot(tool, modifier, player, player, Util.getSlotType(hand))) {
        GeneralInteractionModifierHook.addCooldown(tool, player, 1);
        return InteractionResult.sidedSuccess(player.level().isClientSide);
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
    if (keyModifier == TooltipKey.NORMAL && condition.matches(tool, modifier) && !tool.isBroken() && !player.hasEffect(TinkerModifiers.fireballCooldownEffect.get())) {
      if (shoot(tool, modifier, player, player, slot)) {
        if (!player.level().isClientSide) {
          player.addEffect(new MobEffectInstance(TinkerModifiers.fireballCooldownEffect.get(), GeneralInteractionModifierHook.getDrawtime(tool, player, 1)));
        }
        return true;
      }
    }
    return false;
  }


  /* Builder */
  public static Builder builder() {
    return new Builder();
  }

  @Setter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModuleBuilder.Stack<Builder> {
    private final List<FireballType> options = new ArrayList<>();
    private final List<ModifierEntry> ammoModifiers = new ArrayList<>();
    private DamageTypePair damageType = TinkerDamageTypes.FLUID_FIRE;
    private float damageMultiplier = 1.5f;
    private LevelingInt durability = LevelingInt.ONE;
    private SoundEvent sound = SoundEvents.BLAZE_SHOOT;

    /** Adds the given modifiers */
    public Builder modifier(ModifierEntry... modifiers) {
      Collections.addAll(this.ammoModifiers, modifiers);
      return this;
    }

    /** Adds the given modifiers */
    public Builder modifier(ModifierId... modifiers) {
      for (ModifierId modifier : modifiers) {
        this.ammoModifiers.add(new ModifierEntry(modifier, 1));
      }
      return this;
    }

    /** Creates a new fireball option */
    public Fireball fireball(Ingredient match) {
      return new Fireball(match);
    }

    /** Creates a new fireball option */
    public Fireball fireball(TagKey<Item> tag) {
      return fireball(Ingredient.of(tag));
    }

    /** Creates a new fireball option */
    public Fireball fireball(ItemLike item) {
      return fireball(Ingredient.of(item));
    }

    @Setter
    @Accessors(fluent = true)
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public class Fireball {
      private final Ingredient match;
      private final List<ModifierEntry> ammoModifiers = new ArrayList<>();
      @Nullable
      private DamageTypePair damageType;
      private float damageMultiplier = 1f;

      /** Adds the given modifiers */
      public Fireball modifier(ModifierEntry... modifiers) {
        Collections.addAll(this.ammoModifiers, modifiers);
        return this;
      }

      /** Adds the given modifiers */
      public Fireball modifier(ModifierId... modifiers) {
        for (ModifierId modifier : modifiers) {
          this.ammoModifiers.add(new ModifierEntry(modifier, 1));
        }
        return this;
      }

      /** Finishes this option */
      public Builder end() {
        options.add(new FireballType(match, damageType, damageMultiplier, ammoModifiers));
        return Builder.this;
      }
    }

    /** Builds the final module */
    public FireballModule build() {
      return new FireballModule(options, damageType, damageMultiplier, ammoModifiers, durability, sound, condition);
    }
  }
}
