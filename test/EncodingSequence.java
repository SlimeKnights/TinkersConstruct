package test;

import mods.tinker.tconstruct.entity.genetics.Trait;

public class EncodingSequence
{
    Trait strength;
    Trait agility;
    Trait intelligence;
    
    public EncodingSequence()
    {
        strength = new Trait(10, 30, 4, true).setName("Strength");
        agility = new Trait(10, 30, 4, true).setName("Agility");
        intelligence = new Trait(10, 30, 4, true).setName("Intelligence");
    }
}
