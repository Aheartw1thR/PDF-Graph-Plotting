package pdf;

/**
 * Abstract Representation of stream object
 *
 */
public abstract class StreamObject extends PDFObject {

    public StreamObject() {
        super(null);
    }

    public abstract String buildStream();

    public void addSpecificAttributes() {
        addAttribute("Length", Integer.valueOf(100));
    }

    @Override
    public StringBuilder buildObject() {
        StringBuilder sb = super.buildObject();
        sb.append("stream\n").append(buildStream()).append("endstream\n");
        return sb;
    }

}
