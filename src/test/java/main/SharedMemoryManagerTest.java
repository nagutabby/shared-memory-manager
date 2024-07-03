package main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class SharedMemoryManagerTest {
    private static SharedMemoryManager sharedMemoryManager;

    @BeforeAll
    public static void createSharedMemoryManager() {
        sharedMemoryManager = new SharedMemoryManager();
    }

    @Nested
    class DataStructureTest {
        @Test
        @DisplayName("共有メモリマネージャーが管理するページ数は16である")
        void getSharedMemoryPageSize() {
            sharedMemoryManager.init();
            assertEquals(16, sharedMemoryManager.memoryPages.size());
        }

        @ParameterizedTest
        @MethodSource("generateIndices")
        @DisplayName("それぞれのメモリページが持つアドレス数は256である")
        void getSharedMemoryAddressSize(Integer index) {
            sharedMemoryManager.init();
            assertEquals(256, sharedMemoryManager.memoryPages.get(index).size());
        }

        @Test
        @DisplayName("メモリページのデータ型はLinkedHashMap<Integer, Integer>である")
        void getMemoryPageType() {
            sharedMemoryManager.init();
            assertTrue(sharedMemoryManager.memoryPages.get(0) instanceof LinkedHashMap<Integer, Integer>);
        }

        private static Stream<Integer> generateIndices() {
            Stream<Integer> streamIndices = Stream.of();

            for (int i = 0; i < 16; i++) {
                streamIndices = Stream.concat(streamIndices, Stream.of(i));
            }
            System.out.println(streamIndices);
            return streamIndices;
        }
    }

    @Nested
    class WriteTest {
    }

    @Nested
    class ReadTest {
    }
}
