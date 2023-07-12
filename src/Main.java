import java.io.File;

public class Main {
    static int squareDefinition=30;
    public static void main(String[] args){


        new AppFrame();


        // ░▒▓█
        //print6Char("+?ç@    ");
        //print6Char("+?ç@     ");



        /*
        print6Char(".¨,   ");
        System.out.println("\n\n");
        print6Char("¨,;   ");
        System.out.println("\n\n");
        print6Char(",;!   ");
        System.out.println("\n\n");
        print6Char(";!+   ");
        System.out.println("\n\n");
        print6Char("!+L   ");
        System.out.println("\n\n");
        print6Char("+LJ   ");
        System.out.println("\n\n");
        print6Char("LJI   ");
        System.out.println("\n\n");
        print6Char("JIC   ");
        System.out.println("\n\n");
        print6Char("ICP   ");
        System.out.println("\n\n");
        print6Char("CPH   ");
        System.out.println("\n\n");
        print6Char("PHR   ");
        System.out.println("\n\n");
        print6Char("HRB   ");
        System.out.println("\n\n");
        print6Char("RB@   ");
         */

    }


    private static void print6Char(String chars){

        for(int i=0;i<squareDefinition;i++){
            for(int j=0;j<2*squareDefinition;j++){
                System.out.print(chars.charAt(0));
            }
            for(int j=0;j<2*squareDefinition;j++){
                System.out.print(chars.charAt(1));
            }
            for(int j=0;j<2*squareDefinition;j++){
                System.out.print(chars.charAt(2));
            }
            for(int j=0;j<2*squareDefinition;j++){
                System.out.print(chars.charAt(3));
            }
            for(int j=0;j<2*squareDefinition;j++){
                System.out.print(chars.charAt(4));
            }
            for(int j=0;j<2*squareDefinition;j++){
                System.out.print(chars.charAt(5));
            }
            System.out.print("\n");
        }
    }
}








