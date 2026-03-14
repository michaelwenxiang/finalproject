package csci570finalproject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import csci570finalproject.Efficient.GeneratedStrings;

public class EfficientTest {

    
    @Test
    public void testInput1() {
 
        GeneratedStrings strings = Efficient.getGeneratedStrings("/Users/mxiang/eclipse-workspace1/csci570finalproject/src/csci570finalproject/input0.txt");
        assertEquals("ACACTGACTACTGACTGGTGACTACTGACTGG", strings.x);
        assertEquals("TATTATACGCTATTATACGCGACGCGGACGCG", strings.y);
        assertTrue(Efficient.validStringACGT("AC"));
        assertTrue(Efficient.validStringACGT("ACGTACGT"));
        assertFalse(Efficient.validStringACGT("aCGTACGT"));
        assertFalse(Efficient.validStringACGT("ACBTACGT"));
    }
}

