import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntStackTest {

    @Test
    void pushPop() {
        IntStack s = new IntStack();

        s.push(100);
        s.push(50);

        assertEquals(50, s.pop());
        assertEquals(100, s.pop());

        assertThrows(IndexOutOfBoundsException.class, s::pop);
    }
    
    @Test
    void applyFunction() {
        IntStack s = new IntStack();
        s.push(10);
        s.push(2);
        s.performOn2((i1, i2) -> i1 / i2);
        assertEquals(5, s.pop());
        assertThrows(IndexOutOfBoundsException.class, s::pop);
    }

    @Test
    void resize() {
        IntStack s = new IntStack();
        for (int i = 0; i < 1000; i++) {
            s.push(i);
        }

        for (int i = 999; i >= 0; i--) {
            assertEquals(i, s.pop());
        }
    }
}