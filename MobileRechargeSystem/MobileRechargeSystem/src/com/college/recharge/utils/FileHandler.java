package com.college.recharge.utils;

import com.college.recharge.core.RechargeSystem;
import java.io.*;

public class FileHandler {

    private static final String FILE_PATH = "rechargeSystem.dat"; 

    // Saves the entire RechargeSystem object to a file
    public static void saveSystemData(RechargeSystem system) {
        try (FileOutputStream fileOut = new FileOutputStream(FILE_PATH);
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(system);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //  Loads the entire RechargeSystem object from a file
    public static RechargeSystem loadSystemData() {
        try (FileInputStream fileIn = new FileInputStream(FILE_PATH);
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            return (RechargeSystem) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new RechargeSystem();
        }
    }
}
