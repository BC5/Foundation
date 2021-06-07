package uk.lsuth.mc.foundation.language;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestItemSearch
{
    @Test
    public void TestSimilarity()
    {
        assertEquals(4,ItemSearch.similarity("tester","testificate"));
    }
}
