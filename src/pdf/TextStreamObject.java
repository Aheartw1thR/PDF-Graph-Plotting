package pdf;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representation of text stream object
 *
 */
public class TextStreamObject extends StreamObject {

    private static final String BEGIN_TEXT = "BT";
    private static final String END_TEXT = "ET";
    private static final String TEXT_FONT = "Tf";
    private static final String TEXT_OFFSET = "Td";
    private static final String SHOW_TEXT = "Tj";

    private List<String> texts = new ArrayList<>();

    public TextStreamObject(String fontAlias, int fontSize, int xPos, int yPos, String text) {
        add(fontAlias, fontSize, xPos, yPos, text);

    }

    public void add(String fontAlias, int fontSize, int xPos, int yPos, String text) {
        this.texts.add(" \n " + BEGIN_TEXT + " \n  /" + fontAlias + " " + fontSize + " " + TEXT_FONT + " \n " + xPos
                + " " + yPos + " " + TEXT_OFFSET + "\n (" + text + ") " + SHOW_TEXT + "\n" + END_TEXT + "\n");
    }

    @Override
    public String buildStream() {
        return texts.stream().collect(Collectors.joining());
    }
}