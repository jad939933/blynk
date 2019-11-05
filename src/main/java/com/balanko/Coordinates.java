/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.balanko;

/**
 *
 * @author dev
 */
public class Coordinates {

    public static Object[] position(int cursor) throws Exception {
        return positionAsString(cursor).split(" ");
//        return new int[]{Integer.parseInt(s[0]), Integer.parseInt(s[1])};
    }

 
 
    
    public static String positionAsString(int cursor) throws Exception {
        switch (cursor) {
            case 0:
                return "0 200";
            case 1:
                return "0 12000";
            case 2:
                return "24600 200";
            case 3:
                return "24600 12000";
            case 4:
                return "52000 200";
            case 5:
                return "52000 12000";
            case 6:
                return "79000 200";
            case 7:
                return "79000 12000";
            case 8:
                return "107000 200";
            case 9:
                return "107000 12000";
            case 10:
                return "134800 200";
            case 11:
                return "134800 12000";
            case 12:
                return "161800 200";
            case 13:
                return "161800 12000";
            case 14:
                return "189000 200";
            case 15:
                return "189000 12000";
            case 16:
                return "209000 200";
            case 17:
                return "209000 12000";
            default:
                throw new Exception("Out of bounds!");
        }
    }
}
