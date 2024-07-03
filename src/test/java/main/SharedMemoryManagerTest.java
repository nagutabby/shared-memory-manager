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
        @Test
        @DisplayName("openメソッドが実行される前にデータを書き込もうとすると、OperationExceptionが発生する")
        void writeDataBeforeOpening() {
            assertThrows(OperationException.class, () -> sharedMemoryManager.write(0x00, 0x01));
        }

        @Test
        @DisplayName("openメソッドが実行されてからcloseメソッドが実行されるまでの間にデータを書き込める")
        void writeDataBetweenOpeningAndClosing() {
            sharedMemoryManager.open();
            assertDoesNotThrow(() -> sharedMemoryManager.write(0x00, 0x01));
        }

        @Test
        @DisplayName("closeメソッドが実行された後にデータを書き込もうとすると、OperationExceptionが発生する")
        void writeDataAfterClosing() {
            sharedMemoryManager.open();
            sharedMemoryManager.close();
            assertThrows(OperationException.class, () -> sharedMemoryManager.write(0x00, 0x01));
        }
    }

    @Nested
    class ReadTest {
        @Test
        @DisplayName("openメソッドが実行される前にデータを読み込める")
        void readDataBeforeOpening() {
            assertDoesNotThrow(() -> sharedMemoryManager.read(0x00));
        }

        @Test
        @DisplayName("openメソッドが実行されてからcloseメソッドが実行されるまでの間ににデータを読み込もうとすると、ViolationExceptionが発生する")
        void readDataBetweenOpeningAndClosing() {
            sharedMemoryManager.open();
            assertThrows(ViolationException.class, () -> sharedMemoryManager.read(0x00));
        }

        @Test
        @DisplayName("closeメソッドが実行された後にデータを読み込める")
        void readDataAfterClosing() {
            assertDoesNotThrow(() -> sharedMemoryManager.read(0x00));
        }
    }

    @Nested
    class ReadAndWriteTest {
        @ParameterizedTest
        @MethodSource("generateIndicesExceptFrontPage")
        @DisplayName("表ページ以外のページにデータが書き込まれない")
        void isNotWrittenInPagesExceptFrontPage(Integer index) {
            sharedMemoryManager.open();
            sharedMemoryManager.write(0x00, 0x01);
            sharedMemoryManager.close();
            assertEquals(0, sharedMemoryManager.read(index));
        }

        @Test
        @DisplayName("メモリページに書き込まれたデータを読み込める")
        void readDataWrittenInMemoryPage() {
            sharedMemoryManager.open();
            sharedMemoryManager.write(0x00, 0x01);
            sharedMemoryManager.close();
            sharedMemoryManager.setFront(0);
            int memoryPageData = sharedMemoryManager.read(0x00);
            assertEquals(1, memoryPageData);
        }

        @Test
        @DisplayName("アドレスとして8ビットよりも大きい値が渡された場合は、その値の下位8ビットをアドレスとして用いる")
        void receiveLowerEightBits() {
            sharedMemoryManager.open();
            sharedMemoryManager.write(0xffff, 0x01);
            sharedMemoryManager.close();
            sharedMemoryManager.setFront(0);
            int memoryPageData = sharedMemoryManager.read(0xff);
            assertEquals(1, memoryPageData);
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
}
