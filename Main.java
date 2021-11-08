package com.company;

//import de.vandermeer.asciitable.AsciiTable;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {

        if (args.length == 0) {
            System.out.println("The input cannot be void");
            return;
        } else if (args.length % 2 == 0) {
            System.out.println("The number of input lines must be odd");
            return;
        } else if (args.length < 3 || args.length > 7) {
            System.out.println("The number of input strings must be >= 3 and <=7");
            return;
        }

        for (int i = 0; i < args.length; i++) {
            for (int j = i + 1; j < args.length; j++) {
                if (args[i].equals(args[j])) {
                    System.out.println("An entered lines cannot be duplicated");
                    System.exit(0);
                }
            }
        }

        Map<String, String> moves = new LinkedHashMap<>();
        for (int i = 0, j = 1; i < args.length; i++, j++) {
            moves.put(String.valueOf(j), String.valueOf(args[i]));
        }

        Generator generator = new Generator();

        String key = generator.generateKey();

        SecureRandom secureRandom = new SecureRandom();
        int computerInput = secureRandom.nextInt(args.length-1);

        String hmac = generator.generateHmac(key, args[computerInput]);

        System.out.println("\n" + hmac);
        Menu menu = new Menu();
        menu.showMenu(args);

        Scanner scanner = new Scanner(System.in);
        String userIndex = scanner.nextLine();
        String userInput = moves.get(userIndex);

        if (userIndex.equals("0")) {
            System.out.println("Your move: exit");
            System.exit(0);
        } else if (userIndex.equals("?")) {
            System.out.println("Here must be a HelpTable");
            return;
        } else if (!(Integer.valueOf(userIndex) > args.length)) {

            System.out.println("Your move:" + userInput);

            System.out.println("Computer move: " + args[computerInput]);

            System.out.println(Rules.compareMoves(args, userIndex, computerInput));

            System.out.println(key);
            System.out.println("\n");
        }
    }
}

class Generator {

    private Object DatatypeConverter;

    public String generateKey() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] values = new byte[16];
        random.nextBytes(values);
        StringBuilder sb = new StringBuilder();
        for (byte b : values) {
            sb.append(String.format("%02x", b));
        }
        return "HMAC key: " + sb.toString();
    }

    public String generateHmac(String key, String computerInput ) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        sha256_HMAC.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));
        byte[] result = sha256_HMAC.doFinal(computerInput.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : result) {
            sb.append(String.format("%02x", b));
        }
        return "HMAC: \n" + sb.toString();

    }

}

class Menu {

    public void showMenu(String[] args) {
        System.out.println("Available moves: ");
        for (int i = 0, j = 1; i < args.length; i++, j++) {
            System.out.println(j + " - " + args[i]);
        }
        System.out.println(0 + " - " + "exit");
        System.out.println("? - help\n");
        System.out.println("Enter your move: ");
    }
}

class Rules {
    public static String compareMoves(String [] args, String userInput, int computerInput) {
        int userInp = Integer.parseInt(userInput);
        int size = args.length;
        int halfSize = args.length / 2;
        String result = "You lose!";

        if(computerInput+1 == userInp) {
            result =  "Tie.";
        } else if(userInp > computerInput && userInp - computerInput+1 <= halfSize) {
            result =  "You win!";
        } else if(computerInput+1 > userInp && userInp - computerInput+1 >= halfSize) {
            result = "You lose!";
        }

        return result;
    }
}

//class HelpTable {
//    AsciiTable at = new AsciiTable();
//}