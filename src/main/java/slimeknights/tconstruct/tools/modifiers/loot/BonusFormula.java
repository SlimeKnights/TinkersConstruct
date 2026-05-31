package slimeknights.tconstruct.tools.modifiers.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Shared count bonus formulas mirroring vanilla apply_bonus formulas. */
public interface BonusFormula {
  Map<ResourceLocation, Type> TYPES = Stream.of(BinomialWithBonusCount.TYPE, OreDrops.TYPE, UniformBonusCount.TYPE)
    .collect(Collectors.toMap(Type::id, Function.identity()));
  Codec<Type> TYPE_CODEC = ResourceLocation.CODEC.comapFlatMap(
    id -> {
      Type type = TYPES.get(id);
      return type != null ? DataResult.success(type) : DataResult.error(() -> "No formula type with id: '" + id + "'");
    },
    Type::id);
  MapCodec<BonusFormula> CODEC = ExtraCodecs.dispatchOptionalValue("formula", "parameters", TYPE_CODEC, BonusFormula::getType, Type::codec);

  int calculateNewCount(RandomSource random, int originalCount, int level);

  Type getType();

  record Type(ResourceLocation id, Codec<? extends BonusFormula> codec) {}

  record BinomialWithBonusCount(int extraRounds, float probability) implements BonusFormula {
    public static final Codec<BinomialWithBonusCount> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
        Codec.INT.fieldOf("extra").forGetter(BinomialWithBonusCount::extraRounds),
        Codec.FLOAT.fieldOf("probability").forGetter(BinomialWithBonusCount::probability)
      ).apply(instance, BinomialWithBonusCount::new)
    );
    public static final Type TYPE = new Type(ResourceLocation.withDefaultNamespace("binomial_with_bonus_count"), CODEC);

    @Override
    public int calculateNewCount(RandomSource random, int originalCount, int level) {
      for (int i = 0; i < level + extraRounds; i++) {
        if (random.nextFloat() < probability) {
          originalCount++;
        }
      }
      return originalCount;
    }

    @Override
    public Type getType() {
      return TYPE;
    }
  }

  record OreDrops() implements BonusFormula {
    public static final Codec<OreDrops> CODEC = Codec.unit(OreDrops::new);
    public static final Type TYPE = new Type(ResourceLocation.withDefaultNamespace("ore_drops"), CODEC);

    @Override
    public int calculateNewCount(RandomSource random, int originalCount, int level) {
      if (level > 0) {
        int bonus = random.nextInt(level + 2) - 1;
        if (bonus < 0) {
          bonus = 0;
        }
        return originalCount * (bonus + 1);
      }
      return originalCount;
    }

    @Override
    public Type getType() {
      return TYPE;
    }
  }

  record UniformBonusCount(int bonusMultiplier) implements BonusFormula {
    public static final Codec<UniformBonusCount> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(Codec.INT.fieldOf("bonusMultiplier").forGetter(UniformBonusCount::bonusMultiplier))
                          .apply(instance, UniformBonusCount::new)
    );
    public static final Type TYPE = new Type(ResourceLocation.withDefaultNamespace("uniform_bonus_count"), CODEC);

    @Override
    public int calculateNewCount(RandomSource random, int originalCount, int level) {
      return originalCount + random.nextInt(bonusMultiplier * level + 1);
    }

    @Override
    public Type getType() {
      return TYPE;
    }
  }
}
