package pdf;

import java.util.List;

public class Xref {
    public String generate(List<PDFObject> objects) {
        StringBuilder xref = new StringBuilder();
        xref.append("xref\n");
        xref.append("0 " + (objects.size() + 1) + "\n");
        xref.append("0000000000 65535 f \n");

        for (PDFObject object : objects) {
            String offset = String.format("%010d", object.getOffset());
            String generation = String.format("%05d", object.getGeneration());
            xref.append(offset + " " + generation + " " + "n \n");
        }

        return xref.toString();
    }
}