package csci570finalproject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

public class EfficientTest {

    
    @Test
    public void testInput1() {
 
        Basic.GeneratedStrings strings = Basic.getGeneratedStrings("/Users/mxiang/eclipse-workspace1/csci570finalproject/src/csci570finalproject/input0.txt");
        assertEquals("ACACTGACTACTGACTGGTGACTACTGACTGG", strings.x);
        assertEquals("TATTATACGCTATTATACGCGACGCGGACGCG", strings.y);
        assertTrue(Basic.validStringACGT("AC"));
        assertTrue(Basic.validStringACGT("ACGTACGT"));
        assertFalse(Basic.validStringACGT("aCGTACGT"));
        assertFalse(Basic.validStringACGT("ACBTACGT"));
    }
}

