package main;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SharedMemoryManager {
    ArrayList<LinkedHashMap<Integer, Integer>> memoryPages = new ArrayList<>(16);
    private int front;
    private boolean isOpen = false;

    public void init() {
        memoryPages.clear();
        for (int i = 0; i < 16; i++) {
            LinkedHashMap<Integer, Integer> memoryPage = new LinkedHashMap<>();
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

    void setFront(int front) {
        this.front = front;
    }
}
