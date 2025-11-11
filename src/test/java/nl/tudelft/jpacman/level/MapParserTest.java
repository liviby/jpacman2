package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.PacmanConfigurationException;
import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.npc.ghost.Blinky;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This is a test class for MapParser.
 */
@ExtendWith(MockitoExtension.class)
public class MapParserTest {
    @Mock
    private BoardFactory boardFactory;
    @Mock
    private LevelFactory levelFactory;
    @Mock
    private Blinky blinky;

    /**
     * Test for the parseMap method (good map).
     */
    @Test
    public void testParseMapGood() {
        // NOTE: MockitoAnnotations.initMocks(this) is redundant when using @ExtendWith(MockitoExtension.class)
        assertNotNull(boardFactory);
        assertNotNull(levelFactory);

        // Stubbing: When createGhost() is called, return the mocked blinky object.
        Mockito.when(levelFactory.createGhost()).thenReturn(blinky);

        MapParser mapParser = new MapParser(levelFactory, boardFactory);
        ArrayList<String> map = new ArrayList<>();
        map.add("############");
        map.add("#P        G#");
        map.add("############");


        mapParser.parseMap(map);
        Mockito.verify(levelFactory, Mockito.times(1)).createGhost();
        Mockito.verify(boardFactory, Mockito.times(26)).createWall();
        Mockito.verify(boardFactory, Mockito.times(10)).createGround();
        Mockito.verify(boardFactory, Mockito.times(1)).createBoard(Mockito.any());
        Mockito.verify(levelFactory, Mockito.times(1)).createLevel(
            Mockito.any(), // Board
            Mockito.any(), // List<Ghost>
            Mockito.any()  // List<Square> startPositions
        );
    }

    /**
     * Test for the parseMap method (bad map).
     * Should throw PacmanConfigurationException when map is malformed (inconsistent row width).
     */
    @Test
    public void testParseMapWrong1() {
        PacmanConfigurationException thrown =
            Assertions.assertThrows(PacmanConfigurationException.class, () -> {
                // MockitoAnnotations.initMocks(this) removed
                assertNotNull(boardFactory);
                assertNotNull(levelFactory);

                MapParser mapParser = new MapParser(levelFactory, boardFactory);
                ArrayList<String> map = new ArrayList<>();

                // Map with unequal width (11, 8, 11). This error is caught first in checkMapFormat().
                map.add("###########"); // 11 chars
                map.add("#P  @ G#"); // 8 chars
                map.add("###########"); // 11 chars

                mapParser.parseMap(map);
            });

        // The MapParser is checked for unequal width first, which throws this specific message.
        Assertions.assertEquals("Input text lines are not of equal width.", thrown.getMessage());
    }
}
