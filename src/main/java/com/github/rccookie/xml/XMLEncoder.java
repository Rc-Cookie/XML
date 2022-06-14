package com.github.rccookie.xml;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Utility class for encoding and decoding strings in an xml document.
 */
public final class XMLEncoder {

    private XMLEncoder() {
        throw new UnsupportedOperationException();
    }

    /**
     * Default predicate to decide whether a character is displayable: Character
     * is in ASCII charset.
     */
    private static final Predicate<Character> DEFAULT_XML_DISPLAYABLE = c -> c < 128;
    /**
     * Maps the name of a named escape sequence to the character it represents.
     */
    private static final Map<String, Character> DECODE_LOOKUP;
    /**
     * Maps a character to the name of its named escape sequence, if it exists.
     */
    private static final Map<Character, String> ENCODE_LOOKUP;
    // Initialize lookups
    static {
        Map<String,Character> decodeLookup = new HashMap<>(359);
        decodeLookup.put("Tab", (char) 9);
        decodeLookup.put("NewLine", (char) 10);
//        decodeLookup.put("nbsp", (char) 32);
        decodeLookup.put("quot", (char) 34);
        decodeLookup.put("amp", (char) 38);
        decodeLookup.put("lt", (char) 60);
        decodeLookup.put("gt", (char) 62);
        decodeLookup.put("nbsp", (char) 160);
        decodeLookup.put("iexcl", (char) 161);
        decodeLookup.put("cent", (char) 162);
        decodeLookup.put("pound", (char) 163);
        decodeLookup.put("curren", (char) 164);
        decodeLookup.put("yen", (char) 165);
        decodeLookup.put("brvbar", (char) 166);
        decodeLookup.put("sect", (char) 167);
        decodeLookup.put("uml", (char) 168);
        decodeLookup.put("copy", (char) 169);
        decodeLookup.put("ordf", (char) 170);
        decodeLookup.put("laquo", (char) 171);
        decodeLookup.put("not", (char) 172);
        decodeLookup.put("shy", (char) 173);
        decodeLookup.put("reg", (char) 174);
        decodeLookup.put("macr", (char) 175);
        decodeLookup.put("deg", (char) 176);
        decodeLookup.put("plusmn", (char) 177);
        decodeLookup.put("sup2", (char) 178);
        decodeLookup.put("sup3", (char) 179);
        decodeLookup.put("acute", (char) 180);
        decodeLookup.put("micro", (char) 181);
        decodeLookup.put("para", (char) 182);
        decodeLookup.put("dot", (char) 182);
        decodeLookup.put("cedil", (char) 184);
        decodeLookup.put("sup1", (char) 185);
        decodeLookup.put("ordm", (char) 186);
        decodeLookup.put("raquo", (char) 187);
        decodeLookup.put("frac14", (char) 188);
        decodeLookup.put("frac12", (char) 189);
        decodeLookup.put("frac34", (char) 190);
        decodeLookup.put("iquest", (char) 191);
        decodeLookup.put("Agrave", (char) 192);
        decodeLookup.put("Aacute", (char) 193);
        decodeLookup.put("Acirc", (char) 194);
        decodeLookup.put("Atilde", (char) 195);
        decodeLookup.put("Auml", (char) 196);
        decodeLookup.put("Aring", (char) 197);
        decodeLookup.put("AElig", (char) 198);
        decodeLookup.put("Ccedil", (char) 199);
        decodeLookup.put("Egrave", (char) 200);
        decodeLookup.put("Eacute", (char) 201);
        decodeLookup.put("Ecirc", (char) 202);
        decodeLookup.put("Euml", (char) 203);
        decodeLookup.put("Igrave", (char) 204);
        decodeLookup.put("Iacute", (char) 205);
        decodeLookup.put("Icirc", (char) 206);
        decodeLookup.put("Iuml", (char) 207);
        decodeLookup.put("ETH", (char) 208);
        decodeLookup.put("Ntilde", (char) 209);
        decodeLookup.put("Ograve", (char) 210);
        decodeLookup.put("Oacute", (char) 211);
        decodeLookup.put("Ocirc", (char) 212);
        decodeLookup.put("Otilde", (char) 213);
        decodeLookup.put("Ouml", (char) 214);
        decodeLookup.put("times", (char) 215);
        decodeLookup.put("Oslash", (char) 216);
        decodeLookup.put("Ugrave", (char) 217);
        decodeLookup.put("Uacute", (char) 218);
        decodeLookup.put("Ucirc", (char) 219);
        decodeLookup.put("Uuml", (char) 220);
        decodeLookup.put("Yacute", (char) 221);
        decodeLookup.put("THORN", (char) 222);
        decodeLookup.put("szlig", (char) 223);
        decodeLookup.put("agrave", (char) 224);
        decodeLookup.put("aacute", (char) 225);
        decodeLookup.put("acirc", (char) 226);
        decodeLookup.put("atilde", (char) 227);
        decodeLookup.put("auml", (char) 228);
        decodeLookup.put("aring", (char) 229);
        decodeLookup.put("aelig", (char) 230);
        decodeLookup.put("ccedil", (char) 231);
        decodeLookup.put("egrave", (char) 232);
        decodeLookup.put("eacute", (char) 233);
        decodeLookup.put("ecirc", (char) 234);
        decodeLookup.put("euml", (char) 235);
        decodeLookup.put("igrave", (char) 236);
        decodeLookup.put("iacute", (char) 237);
        decodeLookup.put("icirc", (char) 238);
        decodeLookup.put("iuml", (char) 239);
        decodeLookup.put("eth", (char) 240);
        decodeLookup.put("ntilde", (char) 241);
        decodeLookup.put("ograve", (char) 242);
        decodeLookup.put("oacute", (char) 243);
        decodeLookup.put("ocirc", (char) 244);
        decodeLookup.put("otilde", (char) 245);
        decodeLookup.put("ouml", (char) 246);
        decodeLookup.put("divide", (char) 247);
        decodeLookup.put("oslash", (char) 248);
        decodeLookup.put("ugrave", (char) 249);
        decodeLookup.put("uacute", (char) 250);
        decodeLookup.put("ucirc", (char) 251);
        decodeLookup.put("uuml", (char) 252);
        decodeLookup.put("yacute", (char) 253);
        decodeLookup.put("thorn", (char) 254);
        decodeLookup.put("yuml", (char) 255);
        decodeLookup.put("Amacr", (char) 256);
        decodeLookup.put("amacr", (char) 257);
        decodeLookup.put("Abreve", (char) 258);
        decodeLookup.put("abreve", (char) 259);
        decodeLookup.put("Aogon", (char) 260);
        decodeLookup.put("aogon", (char) 261);
        decodeLookup.put("Cacute", (char) 262);
        decodeLookup.put("cacute", (char) 263);
        decodeLookup.put("Ccirc", (char) 264);
        decodeLookup.put("ccirc", (char) 265);
        decodeLookup.put("Cdot", (char) 266);
        decodeLookup.put("cdot", (char) 267);
        decodeLookup.put("Ccaron", (char) 268);
        decodeLookup.put("ccaron", (char) 269);
        decodeLookup.put("Dcaron", (char) 270);
        decodeLookup.put("dcaron", (char) 271);
        decodeLookup.put("Dstrok", (char) 272);
        decodeLookup.put("dstrok", (char) 273);
        decodeLookup.put("Emacr", (char) 274);
        decodeLookup.put("emacr", (char) 275);
        decodeLookup.put("Ebreve", (char) 276);
        decodeLookup.put("ebreve", (char) 277);
        decodeLookup.put("Edot", (char) 278);
        decodeLookup.put("edot", (char) 279);
        decodeLookup.put("Eogon", (char) 280);
        decodeLookup.put("eogon", (char) 281);
        decodeLookup.put("Ecaron", (char) 282);
        decodeLookup.put("ecaron", (char) 283);
        decodeLookup.put("Gcirc", (char) 284);
        decodeLookup.put("gcirc", (char) 285);
        decodeLookup.put("Gbreve", (char) 286);
        decodeLookup.put("gbreve", (char) 287);
        decodeLookup.put("Gdot", (char) 288);
        decodeLookup.put("gdot", (char) 289);
        decodeLookup.put("Gcedil", (char) 290);
        decodeLookup.put("gcedil", (char) 291);
        decodeLookup.put("Hcirc", (char) 292);
        decodeLookup.put("hcirc", (char) 293);
        decodeLookup.put("Hstrok", (char) 294);
        decodeLookup.put("hstrok", (char) 295);
        decodeLookup.put("Itilde", (char) 296);
        decodeLookup.put("itilde", (char) 297);
        decodeLookup.put("Imacr", (char) 298);
        decodeLookup.put("imacr", (char) 299);
        decodeLookup.put("Ibreve", (char) 300);
        decodeLookup.put("ibreve", (char) 301);
        decodeLookup.put("Iogon", (char) 302);
        decodeLookup.put("iogon", (char) 303);
        decodeLookup.put("Idot", (char) 304);
        decodeLookup.put("imath", (char) 305);
        decodeLookup.put("IJlig", (char) 306);
        decodeLookup.put("ijlig", (char) 307);
        decodeLookup.put("Jcirc", (char) 308);
        decodeLookup.put("jcirc", (char) 309);
        decodeLookup.put("Kcedil", (char) 310);
        decodeLookup.put("kcedil", (char) 311);
        decodeLookup.put("kgreen", (char) 312);
        decodeLookup.put("Lacute", (char) 313);
        decodeLookup.put("lacute", (char) 314);
        decodeLookup.put("Lcedil", (char) 315);
        decodeLookup.put("lcedil", (char) 316);
        decodeLookup.put("Lcaron", (char) 317);
        decodeLookup.put("lcaron", (char) 318);
        decodeLookup.put("Lmidot", (char) 319);
        decodeLookup.put("lmidot", (char) 320);
        decodeLookup.put("Lstrok", (char) 321);
        decodeLookup.put("lstrok", (char) 322);
        decodeLookup.put("Nacute", (char) 323);
        decodeLookup.put("nacute", (char) 324);
        decodeLookup.put("Ncedil", (char) 325);
        decodeLookup.put("ncedil", (char) 326);
        decodeLookup.put("Ncaron", (char) 327);
        decodeLookup.put("ncaron", (char) 328);
        decodeLookup.put("napos", (char) 329);
        decodeLookup.put("ENG", (char) 330);
        decodeLookup.put("eng", (char) 331);
        decodeLookup.put("Omacr", (char) 332);
        decodeLookup.put("omacr", (char) 333);
        decodeLookup.put("Obreve", (char) 334);
        decodeLookup.put("obreve", (char) 335);
        decodeLookup.put("Odblac", (char) 336);
        decodeLookup.put("odblac", (char) 337);
        decodeLookup.put("OElig", (char) 338);
        decodeLookup.put("oelig", (char) 339);
        decodeLookup.put("Racute", (char) 340);
        decodeLookup.put("racute", (char) 341);
        decodeLookup.put("Rcedil", (char) 342);
        decodeLookup.put("rcedil", (char) 343);
        decodeLookup.put("Rcaron", (char) 344);
        decodeLookup.put("rcaron", (char) 345);
        decodeLookup.put("Sacute", (char) 346);
        decodeLookup.put("sacute", (char) 347);
        decodeLookup.put("Scirc", (char) 348);
        decodeLookup.put("scirc", (char) 349);
        decodeLookup.put("Scedil", (char) 350);
        decodeLookup.put("scedil", (char) 351);
        decodeLookup.put("Scaron", (char) 352);
        decodeLookup.put("scaron", (char) 353);
        decodeLookup.put("Tcedil", (char) 354);
        decodeLookup.put("tcedil", (char) 355);
        decodeLookup.put("Tcaron", (char) 356);
        decodeLookup.put("tcaron", (char) 357);
        decodeLookup.put("Tstrok", (char) 358);
        decodeLookup.put("tstrok", (char) 359);
        decodeLookup.put("Utilde", (char) 360);
        decodeLookup.put("utilde", (char) 361);
        decodeLookup.put("Umacr", (char) 362);
        decodeLookup.put("umacr", (char) 363);
        decodeLookup.put("Ubreve", (char) 364);
        decodeLookup.put("ubreve", (char) 365);
        decodeLookup.put("Uring", (char) 366);
        decodeLookup.put("uring", (char) 367);
        decodeLookup.put("Udblac", (char) 368);
        decodeLookup.put("udblac", (char) 369);
        decodeLookup.put("Uogon", (char) 370);
        decodeLookup.put("uogon", (char) 371);
        decodeLookup.put("Wcirc", (char) 372);
        decodeLookup.put("wcirc", (char) 373);
        decodeLookup.put("Ycirc", (char) 374);
        decodeLookup.put("ycirc", (char) 375);
        decodeLookup.put("Yuml", (char) 376);
        decodeLookup.put("fnof", (char) 402);
        decodeLookup.put("circ", (char) 710);
        decodeLookup.put("tilde", (char) 732);
        decodeLookup.put("Alpha", (char) 913);
        decodeLookup.put("Beta", (char) 914);
        decodeLookup.put("Gamma", (char) 915);
        decodeLookup.put("Delta", (char) 916);
        decodeLookup.put("Epsilon", (char) 917);
        decodeLookup.put("Zeta", (char) 918);
        decodeLookup.put("Eta", (char) 919);
        decodeLookup.put("Theta", (char) 920);
        decodeLookup.put("Iota", (char) 921);
        decodeLookup.put("Kappa", (char) 922);
        decodeLookup.put("Lambda", (char) 923);
        decodeLookup.put("Mu", (char) 924);
        decodeLookup.put("Nu", (char) 925);
        decodeLookup.put("Xi", (char) 926);
        decodeLookup.put("Omicron", (char) 927);
        decodeLookup.put("Pi", (char) 928);
        decodeLookup.put("Rho", (char) 929);
        decodeLookup.put("Sigma", (char) 931);
        decodeLookup.put("Tau", (char) 932);
        decodeLookup.put("Upsilon", (char) 933);
        decodeLookup.put("Phi", (char) 934);
        decodeLookup.put("Chi", (char) 935);
        decodeLookup.put("Psi", (char) 936);
        decodeLookup.put("Omega", (char) 937);
        decodeLookup.put("alpha", (char) 945);
        decodeLookup.put("beta", (char) 946);
        decodeLookup.put("gamma", (char) 947);
        decodeLookup.put("delta", (char) 948);
        decodeLookup.put("epsilon", (char) 949);
        decodeLookup.put("zeta", (char) 950);
        decodeLookup.put("eta", (char) 951);
        decodeLookup.put("theta", (char) 952);
        decodeLookup.put("iota", (char) 953);
        decodeLookup.put("kappa", (char) 954);
        decodeLookup.put("lambda", (char) 955);
        decodeLookup.put("mu", (char) 956);
        decodeLookup.put("nu", (char) 957);
        decodeLookup.put("xi", (char) 958);
        decodeLookup.put("omicron", (char) 959);
        decodeLookup.put("pi", (char) 960);
        decodeLookup.put("rho", (char) 961);
        decodeLookup.put("sigmaf", (char) 962);
        decodeLookup.put("sigma", (char) 963);
        decodeLookup.put("tau", (char) 964);
        decodeLookup.put("upsilon", (char) 965);
        decodeLookup.put("phi", (char) 966);
        decodeLookup.put("chi", (char) 967);
        decodeLookup.put("psi", (char) 968);
        decodeLookup.put("omega", (char) 969);
        decodeLookup.put("thetasym", (char) 977);
        decodeLookup.put("upsih", (char) 978);
        decodeLookup.put("piv", (char) 982);
        decodeLookup.put("ensp", (char) 8194);
        decodeLookup.put("emsp", (char) 8195);
        decodeLookup.put("thinsp", (char) 8201);
        decodeLookup.put("zwnj", (char) 8204);
        decodeLookup.put("zwj", (char) 8205);
        decodeLookup.put("lrm", (char) 8206);
        decodeLookup.put("rlm", (char) 8207);
        decodeLookup.put("ndash", (char) 8211);
        decodeLookup.put("mdash", (char) 8212);
        decodeLookup.put("lsquo", (char) 8216);
        decodeLookup.put("rsquo", (char) 8217);
        decodeLookup.put("sbquo", (char) 8218);
        decodeLookup.put("ldquo", (char) 8220);
        decodeLookup.put("rdquo", (char) 8221);
        decodeLookup.put("bdquo", (char) 8222);
        decodeLookup.put("dagger", (char) 8224);
        decodeLookup.put("Dagger", (char) 8225);
        decodeLookup.put("bull", (char) 8226);
        decodeLookup.put("hellip", (char) 8230);
        decodeLookup.put("permil", (char) 8240);
        decodeLookup.put("prime", (char) 8242);
        decodeLookup.put("Prime", (char) 8243);
        decodeLookup.put("lsaquo", (char) 8249);
        decodeLookup.put("rsaquo", (char) 8250);
        decodeLookup.put("oline", (char) 8254);
        decodeLookup.put("euro", (char) 8364);
        decodeLookup.put("trade", (char) 8482);
        decodeLookup.put("larr", (char) 8592);
        decodeLookup.put("uarr", (char) 8593);
        decodeLookup.put("rarr", (char) 8594);
        decodeLookup.put("darr", (char) 8595);
        decodeLookup.put("harr", (char) 8596);
        decodeLookup.put("crarr", (char) 8629);
        decodeLookup.put("forall", (char) 8704);
        decodeLookup.put("part", (char) 8706);
        decodeLookup.put("exist", (char) 8707);
        decodeLookup.put("empty", (char) 8709);
        decodeLookup.put("nabla", (char) 8711);
        decodeLookup.put("isin", (char) 8712);
        decodeLookup.put("notin", (char) 8713);
        decodeLookup.put("ni", (char) 8715);
        decodeLookup.put("prod", (char) 8719);
        decodeLookup.put("sum", (char) 8721);
        decodeLookup.put("minus", (char) 8722);
        decodeLookup.put("lowast", (char) 8727);
        decodeLookup.put("radic", (char) 8730);
        decodeLookup.put("prop", (char) 8733);
        decodeLookup.put("infin", (char) 8734);
        decodeLookup.put("ang", (char) 8736);
        decodeLookup.put("and", (char) 8743);
        decodeLookup.put("or", (char) 8744);
        decodeLookup.put("cap", (char) 8745);
        decodeLookup.put("cup", (char) 8746);
        decodeLookup.put("int", (char) 8747);
        decodeLookup.put("there4", (char) 8756);
        decodeLookup.put("sim", (char) 8764);
        decodeLookup.put("cong", (char) 8773);
        decodeLookup.put("asymp", (char) 8776);
        decodeLookup.put("ne", (char) 8800);
        decodeLookup.put("equiv", (char) 8801);
        decodeLookup.put("le", (char) 8804);
        decodeLookup.put("ge", (char) 8805);
        decodeLookup.put("sub", (char) 8834);
        decodeLookup.put("sup", (char) 8835);
        decodeLookup.put("nsub", (char) 8836);
        decodeLookup.put("sube", (char) 8838);
        decodeLookup.put("supe", (char) 8839);
        decodeLookup.put("oplus", (char) 8853);
        decodeLookup.put("otimes", (char) 8855);
        decodeLookup.put("perp", (char) 8869);
        decodeLookup.put("sdot", (char) 8901);
        decodeLookup.put("lceil", (char) 8968);
        decodeLookup.put("rceil", (char) 8969);
        decodeLookup.put("lfloor", (char) 8970);
        decodeLookup.put("rfloor", (char) 8971);
        decodeLookup.put("loz", (char) 9674);
        decodeLookup.put("spades", (char) 9824);
        decodeLookup.put("clubs", (char) 9827);
        decodeLookup.put("hearts", (char) 9829);
        decodeLookup.put("diams", (char) 9830);
        DECODE_LOOKUP = Collections.unmodifiableMap(decodeLookup);

        Map<Character, String> encodeLookup = new HashMap<>();
        decodeLookup.forEach((k,v) -> encodeLookup.put(v,k));
        ENCODE_LOOKUP = Collections.unmodifiableMap(encodeLookup);
    }

    /**
     * Finds and replaces all valid escape sequences with their respective character
     * and returns the decoded string. Named, decimal and hexadecimal escape sequences
     * are supported.
     *
     * @param str The string to decode
     * @return The decoded string
     */
    public static String decode(String str) {
        StringBuilder out = new StringBuilder(str.length());
        char c;
        int end;
        for(int i=0; i<str.length(); i++) {
            c = str.charAt(i);
            if(c == '&' && (end = str.indexOf(';', i+1)) <= i+10 && end != -1) {
                c = str.charAt(i+1);
                if(c == '#') {
                    c = str.charAt(i+2);
                    try {
                        if(c == 'x')
                            out.append((char) Integer.parseInt(str.substring(i+3, end), 16));
                        else out.append((char) Integer.parseInt(str.substring(i+2, end)));
                    } catch(NumberFormatException e) {
                        out.append('&');
                        continue;
                    }
                }
                else {
                    Character d = DECODE_LOOKUP.get(str.substring(i+1, end));
                    if(d == null) {
                        out.append('&');
                        continue;
                    }
                    out.append(d);
                }
                i = end;
            }
            else out.append(c);
        }
        return out.toString();
    }

    /**
     * Encodes the given string to a valid xml string for ASCII charset.
     *
     * @param str The string to encode
     * @return The encoded string
     */
    public static String encode(String str) {
        return encode(str, (Charset) null);
    }

    /**
     * Encodes the given string to a valid xml string for the given charset.
     *
     * @param str The string to encode
     * @param charset The charset that defines what characters are displayable
     * @return The encoded string
     */
    public static String encode(String str, Charset charset) {
        StringBuilder out = new StringBuilder();
        encode(str, out, charset);
        return out.toString();
    }

    /**
     * Encodes the given string to a valid xml string for ASCII charset and
     * writes the output into the given StringBuilder.
     *
     * @param str The string to encode
     * @param out The string builder to write into
     */
    public static void encode(String str, StringBuilder out) {
        encode(str, out, (Charset) null);
    }

    /**
     * Encodes the given string to a valid xml string for the given charset
     * and writes the output into the given StringBuilder.
     *
     * @param str The string to encode
     * @param out The string builder to write into
     * @param charset The charset that defines what characters are displayable
     */
    public static void encode(String str, StringBuilder out, Charset charset) {
        encode(str, out, charset != null ? charset.newEncoder()::canEncode : DEFAULT_XML_DISPLAYABLE);
    }

    /**
     * Encodes the given string to a valid xml string for the given display
     * predicate and writes the output into the given StringBuilder.
     *
     * @param str The string to encode
     * @param out The string builder to write into
     * @param displayable The predicate that defines what characters are displayable
     */
    private static void encode(String str, StringBuilder out, Predicate<Character> displayable) {
        for(int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if(c == '\'') out.append("&apos;");
            else if(c == '"') out.append("&quot;");
            else if(c == '<') out.append("&lt;");
            else if(c == '>') out.append("&gt;");
            else if(c == '&') out.append("&amp;");
            else if(c < 10 || c == 11 || c == 12 || (c > 13 && c < 32) || !displayable.test(c))
                out.append('&').append(ENCODE_LOOKUP.getOrDefault(c, "#" + (int)c)).append(';');
            else out.append(c);
        }
    }
}
