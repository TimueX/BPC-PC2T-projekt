package cz.vut.fekt.project.util;

import java.util.Scanner;

public class InputUtil {
    private static final Scanner sc = new Scanner(System.in);

    public static int readInt(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Zly vstup");
            }
        }
    }

    public static String readString(String msg) {
        System.out.print(msg);
        return sc.nextLine();
    }
}