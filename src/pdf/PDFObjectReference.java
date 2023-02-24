package pdf;

/**
 * Representation of reference to any PDF object.
 *
 */
public class PDFObjectReference {
    private int objectNumber;

    private int generation = 0; // Hardcode as it remains same always
    private int offset = 0;

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    int getObjectNumber() {
        return objectNumber;
    }

    int getGeneration() {
        return generation;
    }

    void setObjectNumber(int objectNumber) {
        this.objectNumber = objectNumber;
    }

}