package tconstruct.util;

import java.util.LinkedList;

public class DozenalConverter
{
    public static String convertToDozenal (byte i)
    {
        return convertDecimal(i);
    }

    public static String convertToDozenal (short i)
    {
        return convertDecimal(i);
    }

    public static String convertToDozenal (int i)
    {
        return convertDecimal(i);
    }

    public static String convertDecimal (int i)
    {
        int num = i;
        LinkedList<Character> list = new LinkedList();
        while (num > 0)
        {
            list.add(getDozenalCharacter(num % 12));
            num /= 12;
        }
        StringBuilder builder = new StringBuilder();
        for (int iter = list.size() - 1; iter > 0; iter--)
        {
            builder.append(list.get(iter));
        }
        return builder.toString();
    }

    public static char getDozenalCharacter (int i)
    {
        assert i < 12 : "Base 10 number should be less than 12";

        switch (i)
        {
        case 0:
            return '0'; //Zero
        case 1:
            return '1'; //One
        case 2:
            return '2'; //Two
        case 3:
            return '3'; //Three
        case 4:
            return '4'; //Four
        case 5:
            return '5'; //Five
        case 6:
            return '6'; //Six
        case 7:
            return '7'; //Seven
        case 8:
            return '8'; //Eight
        case 9:
            return '9'; //Nine
        case 10:
            return 'X'; //Dec
        case 11:
            return 'E'; //El
        }

        return 'Q';
    }
}
