package org.example;

import java.util.Scanner;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
//        IO.println(String.format("Hello and welcome!"));
        String myName;
        Scanner sc = new Scanner(System.in);
//        IO.println("Type ur name below:");
        myName = sc.nextLine();

//        myName = "Emanoel";
        System.out.printf("Hello, World! Welcome %s\n", myName);

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
//            IO.println("i = " + i);
        }
    }
}
