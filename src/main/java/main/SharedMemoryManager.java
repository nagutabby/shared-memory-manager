package main;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SharedMemoryManager {
    private static ArrayList<LinkedHashMap<Integer, Integer>> memoryPages = new ArrayList<>(16);
    private static int front;
    private static boolean isOpen = false;

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            LinkedHashMap<Integer, Integer> memoryPage = new LinkedHashMap<>();
            memoryPages.add(memoryPage);

            for (int j = 0; j < 256; j++) {
                memoryPages.get(i).put(j, null);
            }
        }
        init();
        open();
        write(0xff, 0x01);
        close();
        System.out.println(read(0xff));
    }

    public static void init() {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 256; j++) {
                memoryPages.get(i).put(j, 0);
            }
        }
        front = 0;
    };

    public static void open() {
        isOpen = true;
    }

    public static void close() {
        if (front == 15) {
            front = 0;
        } else {
            front++;
        }
        isOpen = false;
    }

    public static void write(int inputAddress, int data) {
        int lowerEightBitsAddress = inputAddress & 0xff;
        if (isOpen) {
            memoryPages.get(front).put(lowerEightBitsAddress, data);
        } else {
            throw new OperationException("Operation error occured");
        }
    }

    public static int read(int address) {
        if (isOpen) {
            throw new ViolationException("Violation error occured");
        } else {
            int memoryPageData = memoryPages.get(front).get(address);
            return memoryPageData;
        }
    }
}
