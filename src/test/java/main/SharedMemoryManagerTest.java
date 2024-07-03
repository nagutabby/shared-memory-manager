package main;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class SharedMemoryManagerTest {
    private static SharedMemoryManager sharedMemoryManager;

    @BeforeEach
    public void init() {
        sharedMemoryManager = new SharedMemoryManager();
        sharedMemoryManager.init();
    }

    @Nested
    class DataStructureTest {
        @Test
        @DisplayName("共有メモリマネージャーが管理するページ数は16である")
        void getSharedMemoryPageSize() {
            assertEquals(16, sharedMemoryManager.memoryPages.size());
        }

        @ParameterizedTest
        @MethodSource("generateIndices")
        @DisplayName("それぞれのメモリページが持つアドレス数は256である")
        void getSharedMemoryAddressSize(Integer index) {
            assertEquals(256, sharedMemoryManager.memoryPages.get(index).size());
        }

        @Test
        @DisplayName("メモリページのデータ型はLinkedHashMap<Integer, Integer>である")
        void getMemoryPageType() {
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
        @ParameterizedTest
        @MethodSource("generateIndicesExceptFrontPage")
        @DisplayName("表ページ以外のページにデータが書き込まれない")
        void isNotWrittenOnPagesExceptFrontPage(Integer index) {
            sharedMemoryManager.open();
            sharedMemoryManager.write(0x00, 0x01);
            sharedMemoryManager.close();
            assertEquals(0, sharedMemoryManager.read(index));
        }

        @Test
        @DisplayName("openメソッドが実行されてから、closeメソッドが実行されるまでの間ににデータを書き込める")
        void writeDataBetweenOpeningAndClosing() {
            sharedMemoryManager.open();
            assertDoesNotThrow(() -> sharedMemoryManager.write(0x00, 0x01));
        }

        @Test
        @DisplayName("openメソッドが実行される前にデータを書き込もうとすると、OperationExceptionが発生する")
        void writeDataBeforeOpening() {
            assertThrows(OperationException.class, () -> sharedMemoryManager.write(0x00, 0x01));
        }

        @Test
        @DisplayName("closeメソッドが実行された後にデータを書き込もうとすると、OperationExceptionが発生する")
        void writeDataAfterClosing() {
            sharedMemoryManager.open();
            sharedMemoryManager.close();
            assertThrows(OperationException.class, () -> sharedMemoryManager.write(0x00, 0x01));
        }

        private static Stream<Integer> generateIndicesExceptFrontPage() {
            Stream<Integer> streamIndices = Stream.of();

            for (int i = 1; i < 16; i++) {
                streamIndices = Stream.concat(streamIndices, Stream.of(i));
            }
            System.out.println(streamIndices);
            return streamIndices;
        }
    }

    @Nested
    class ReadTest {
    }
}
