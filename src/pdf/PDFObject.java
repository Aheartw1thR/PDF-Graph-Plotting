package pdf;


import java.util.HashMap;
import java.util.Map;

/**
 * Abstract Representation of PDF objects. All objects in PDF must extend this.
 *
 */
public abstract class PDFObject {

    private PDFObjectReference reference = new PDFObjectReference();

    private Map<String, Object> attributes = new HashMap<>();

    public PDFObject(String type) {
        super();
        this.attributes.put("Type", type);
    }

    public void addAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    public abstract void addSpecificAttributes();

    public String build() {

        addSpecificAttributes();

        StringBuilder pdfObject = new StringBuilder();
        pdfObject.append(getObjectNumber())
                .append(" ")
                .append(getGeneration())
                .append(" obj\n")
                .append(buildObject())
                .append("endobj\n\n");

        return pdfObject.toString();
    }

    public StringBuilder buildObject() {
        StringBuilder pdfObject = new StringBuilder();
        pdfObject.append("<<");

        for (String key : attributes.keySet()) {
            Object value = attributes.get(key);
            if (value instanceof String) {
                pdfObject.append("/").append(key).append(" ").append(((String) value).contains("[") ? "" : "/")
                        .append(value).append("\n");
            } else if (value instanceof Integer) {
                pdfObject.append("/").append(key).append(" ").append(value).append("\n");
            } else if (value instanceof PDFObject) {
                pdfObject.append("/").append(key).append(" \n").append(((PDFObject) value).buildObject()).append("\n");
            } else if (value instanceof PDFObjectReference[]) {

                pdfObject.append("/").append(key).append(" [");
                for (PDFObjectReference ref : (PDFObjectReference[]) value) {
                    pdfObject.append(ref.getObjectNumber() + " " + ref.getGeneration() + " R ");
                }
                pdfObject.append("]").append("\n");
            } else if (value instanceof PDFObjectReference) {
                pdfObject.append("/").append(key).append(" ")
                        .append(((PDFObjectReference) value).getObjectNumber() + " "
                                + ((PDFObjectReference) value).getGeneration() + " R ").append("\n");
            }
        }
        pdfObject.append(">>\n");

        return pdfObject;
    }

    public void setObjectNumber(int objectNumber) {
        this.reference.setObjectNumber(objectNumber);
    }

    PDFObjectReference getReference() {
        return reference;
    }

    public Object getOffset() {
        return reference.getOffset();
    }

    public Object getObjectNumber() {
      return reference.getObjectNumber();
    }

    public Object getGeneration() {
        return reference.getGeneration();
    }

    public void setOffset(int offset) {
        reference.setOffset(offset);
    }
}
