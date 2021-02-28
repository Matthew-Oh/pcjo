
/**
 * Class test
 * 
 */
public class test
{
    public enum DayOfWeek
    {SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY}
    
    //PCJO_IGNORE_2
    public static void main(String[] args)
    {
        int i;
        for (i = 0; i < 7; i++)
        {
            DayOfWeek day = DayOfWeek.values()[i];
            System.out.print(day);
            System.out.print(" is the ");
            if (i == 0)
                System.out.print("1st ");
            else if (i == 1)
                System.out.print("2nd ");
            else if (i == 2)
                System.out.print("3rd ");
            else
                System.out.print((i+1) + "th ");
            System.out.println("day of the week");
        }
    }
}
