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
                return "0 1400";
            case 1:
                return "0 13500";
            case 2:
                return "23000 1400";
            case 3:
                return "23000 13500";
            case 4:
                return "52000 1400";
            case 5:
                return "52000 13500";
            case 6:
                return "79000 1400";
            case 7:
                return "79000 13500";
            case 8:
                return "107000 1400";
            case 9:
                return "107000 13500";
            case 10:
                return "134000 1400";
            case 11:
                return "134000 13500";
            case 12:
                return "161000 1400";
            case 13:
                return "161000 13500";
            case 14:
                return "188000 1400";
            case 15:
                return "188000 13500";
            case 16:
                return "209000 1400";
            case 17:
                return "209000 13500";
            default:
                throw new Exception("Out of bounds!");
        }
    }
}
