package pdf;

/**
 * Representation of font object
 *
 */
public class FontObject extends PDFObject {

    public FontObject(String fontAliasName, String fontName) {
        super(null);

        PDFObject fontDef = new PDFObject("Font") {
            @Override
            public void addSpecificAttributes() {
                addAttribute("Subtype", "Type1");
                addAttribute("BaseFont", fontName);
            }
        };
        fontDef.addSpecificAttributes();

        PDFObject fontAlias = new PDFObject(null) {
            @Override
            public void addSpecificAttributes() {
                addAttribute(fontAliasName, fontDef);
            }
        };
        fontAlias.addSpecificAttributes();

        addAttribute("Font", fontAlias);
    }

    @Override
    public void addSpecificAttributes() {

    }

}
