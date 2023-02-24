package pdf;

/**
 * Representation of page object.
 *
 */
public class PageObject extends PDFObject {

    private StreamObject content;

    public PageObject() {
        super("Page");
    }

    public void addContent(StreamObject streamObject) {
        content = streamObject;
    }

    @Override
    public void addSpecificAttributes() {
        addAttribute("Contents", content.getReference());
    }

    StreamObject getContent() {
        return content;
    }

}
