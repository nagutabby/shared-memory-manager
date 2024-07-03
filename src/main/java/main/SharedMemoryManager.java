package main;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SharedMemoryManager {
    public static ArrayList<LinkedHashMap<Integer, Integer>> memoryPages = new ArrayList<>(16);
    private static LinkedHashMap<Integer, Integer> memoryPage = new LinkedHashMap<>();
    private static int front;
    private static boolean isOpen;

    public void init() {
        isOpen = false;
        front = 0;
        memoryPages.clear();
        for (int i = 0; i < 16; i++) {
            memoryPages.add(memoryPage);

            for (int j = 0; j < 256; j++) {
                memoryPages.get(i).put(j, 0);
            }
        }
        front = 0;
    };

    public void open() {
        isOpen = true;
    }

    public void close() {
        if (front == 15) {
            front = 0;
        } else {
            front++;
        }
        isOpen = false;
    }

    public void write(int inputAddress, int data) {
        int lowerEightBitsAddress = inputAddress & 0xff;
        if (isOpen) {
            memoryPages.get(front).put(lowerEightBitsAddress, data);
        } else {
            throw new OperationException("Operation error occured");
        }
    }

    public int read(int address) {
        if (isOpen) {
            throw new ViolationException("Violation error occured");
        } else {
            int memoryPageData = memoryPages.get(front).get(address);
            return memoryPageData;
        }
    }

    static void setFront(int nextFront) {
        front = nextFront;
    }
}
