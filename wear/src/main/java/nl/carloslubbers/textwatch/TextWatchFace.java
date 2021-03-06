package nl.carloslubbers.textwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.DynamicLayout;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.format.Time;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * Created by Carlos on 4/26/2015.
 */
public class TextWatchFace {
    public int FOREGROUND_COLOR = Color.WHITE;
    public int TEXT_SIZE = 20;
    public String LANGUAGE = "en";
    public String darkColor = "#282828";
    public String lightColor = "white";
    final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /** Editable string containing the text to draw with the number of meetings in bold. */
    final Editable mEditable = new SpannableStringBuilder();

    /** Width specified when {@link #mLayout} was created. */
    int mLayoutWidth;

    /** Layout to wrap {@link #mEditable} onto multiple lines. */
    DynamicLayout mLayout;

    /** Paint used to draw text. */
    final TextPaint mTextPaint = new TextPaint();

    private final Time time;

    public static TextWatchFace newInstance(Context context) {

        return new TextWatchFace(new Time());
    }

    TextWatchFace(Time time) {
        this.time = time;
    }

    public void draw(Canvas canvas, Rect bounds) {
        mTextPaint.setColor(FOREGROUND_COLOR);
        mTextPaint.setTextSize(TEXT_SIZE);
        mTextPaint.setTypeface(Typeface.MONOSPACE);
        mTextPaint.setAntiAlias(true);
        // Create or update mLayout if necessary.
        if (mLayout == null || mLayoutWidth != bounds.width()) {
            mLayoutWidth = bounds.width();
            mLayout = new DynamicLayout(mEditable, mTextPaint, mLayoutWidth + 100,
                    Layout.Alignment.ALIGN_CENTER, 1 /* spacingMult */, 2.0f /* spacingAdd */,
                    true /* includePad */);
        }

        // Update the contents of mEditable.
        mEditable.clear();

        time.setToNow();
        canvas.drawColor(Color.BLACK);

        int h12;
        int m5;
        String[][] matrix = getMatrix();
        int[][] status = getStatus();
        Log.v("WatchFace", "setTime();");
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        //int h = watchFace.settings.getInt("h", 0);
        //int m = watchFace.settings.getInt("m", 0);
        //if (m > 60) m = 0;
        //if (h > 12) h = 0;

        h12 = h % 12;
        m5 = (int) Math.floor(m / 5);

        // Reset the status matrix
        for (int i1 = 0; i1 < matrix.length; i1++)
            for (int l1 = 0; l1 < matrix[i1].length; l1++) {
                status[i1][l1] = 0;
            }

        // It is...
        setStatus(0); // it
        setStatus(1); // is

        // Minute dots
        switch (m % 5) {
            case 1:
                status[10][2] = 1;
                break;
            case 2:
                status[10][2] = 1;
                status[10][4] = 1;
                break;
            case 3:
                status[10][2] = 1;
                status[10][4] = 1;
                status[10][6] = 1;
                break;
            case 4:
                status[10][2] = 1;
                status[10][4] = 1;
                status[10][6] = 1;
                status[10][8] = 1;
        }

        // Hour
        if (LANGUAGE.equals("en")) {
            if (m5 >= 7) {
                h12 += 1;
                if (h12 > 11) h12 = 0;
            }
            setStatus(h12 + 2);
        } else if (LANGUAGE.equals("de") || LANGUAGE.equals("nl")) {
            if (m5 >= 4) {
                if (h12 + 3 == 14) {
                    setStatus(2);
                } else {
                    setStatus(h12 + 3);
                }
            } else {
                if (h12 > 11) h12 = 0;
                setStatus(h12 + 2);
            }
        } else if (LANGUAGE.equals("fr")) {

            if (m5 > 6) {
                if (h12 + 3 == 3) {
                    setStatus(15);
                } else {
                    setStatus(14);
                }
                if (h12 + 3 == 14) {
                    setStatus(2);
                } else {
                    setStatus(h12 + 3);
                }
            } else {
                if (h12 + 2 == 3) {
                    setStatus(15);
                } else {
                    setStatus(14);
                }
                if (h12 > 11) h12 = 0;
                setStatus(h12 + 2);
            }


        }

        // Minute
        if (LANGUAGE.equals("de")) {
            switch (m5) {
                case 0:
                    // UHR
                    setStatus(14);
                    break;
                case 1:
                    // FUNF NACH
                    setStatus(15);
                    setStatus(24);
                    break;
                case 2:
                    // ZEHN NACH
                    setStatus(16);
                    setStatus(24);
                    break;
                case 3:
                    // VIERTEL NACH
                    setStatus(17);
                    setStatus(24);
                    break;
                case 4:
                    // ZEHN VOR HALB
                    setStatus(16);
                    setStatus(21);
                    setStatus(20);
                    break;
                case 5:
                    // FUNF VOR HALB
                    setStatus(15);
                    setStatus(21);
                    setStatus(20);
                    break;
                case 6:
                    // HALB
                    setStatus(20);
                    break;
                case 7:
                    // FUNF NACH HALB
                    setStatus(15);
                    setStatus(23);
                    setStatus(20);
                    break;
                case 8:
                    // ZEHN NACH HALB
                    setStatus(16);
                    setStatus(23);
                    setStatus(20);
                    break;
                case 9:
                    // VIERTEL VOR
                    setStatus(17);
                    setStatus(22);
                    break;
                case 10:
                    // ZEHN VOR
                    setStatus(16);
                    setStatus(22);
                    break;
                case 11:
                    // FUNF VOR
                    setStatus(15);
                    setStatus(22);
                    break;

            }
        } else if (LANGUAGE.equals("nl")) {
            switch (m5) {
                case 0:
                    // UUR
                    setStatus(14);
                    break;
                case 1:
                    // VIJF OVER
                    setStatus(15);
                    setStatus(23);
                    break;
                case 2:
                    // TIEN OVER
                    setStatus(16);
                    setStatus(23);
                    break;
                case 3:
                    // KWART OVER
                    setStatus(17);
                    setStatus(23);
                    break;
                case 4:
                    // TIEN VOOR HALF
                    setStatus(16);
                    setStatus(21);
                    setStatus(20);
                    break;
                case 5:
                    // VIJF VOOR HALF
                    setStatus(15);
                    setStatus(21);
                    setStatus(20);
                    break;
                case 6:
                    // HALF
                    setStatus(20);
                    break;
                case 7:
                    // VIJF OVER HALF
                    setStatus(15);
                    setStatus(23);
                    setStatus(20);
                    break;
                case 8:
                    // TIEN OVER HALF
                    setStatus(16);
                    setStatus(23);
                    setStatus(20);
                    break;
                case 9:
                    // KWART VOOR
                    setStatus(17);
                    setStatus(22);
                    break;
                case 10:
                    // TIEN VOOR
                    setStatus(16);
                    setStatus(22);
                    break;
                case 11:
                    // VIJF VOOR
                    setStatus(15);
                    setStatus(22);
                    break;

            }
        } else if (LANGUAGE.equals("en")) {
            switch (m5) {
                case 0:
                    // O' CLOCK
                    setStatus(14);
                    break;
                case 1:
                    // FIVE PAST
                    setStatus(15);
                    setStatus(23);
                    break;
                case 2:
                    // TEN PAST
                    setStatus(16);
                    setStatus(23);
                    break;
                case 3:
                    // A QUARTER PAST
                    setStatus(17);
                    setStatus(18);
                    setStatus(23);
                    break;
                case 4:
                    // TWENTY PAST
                    setStatus(19);
                    setStatus(23);
                    break;
                case 5:
                    // TWENTYFIVE PAST
                    setStatus(20);
                    setStatus(23);
                    break;
                case 6:
                    // HALF PAST
                    setStatus(21);
                    setStatus(23);
                    break;
                case 7:
                    // TWENTYFIVE TO
                    setStatus(20);
                    setStatus(22);
                    break;
                case 8:
                    // TWENTY TO
                    setStatus(19);
                    setStatus(22);
                    break;
                case 9:
                    // A QUARTER TO
                    setStatus(17);
                    setStatus(18);
                    setStatus(22);
                    break;
                case 10:
                    // TEN TO
                    setStatus(16);
                    setStatus(22);
                    break;
                case 11:
                    // FIVE TO
                    setStatus(15);
                    setStatus(22);
                    break;
            }
        } else if (LANGUAGE.equals("fr")) {
            switch (m5) {
                case 0:
                    // HEURE
                    break;
                case 1:
                    // CINQ
                    setStatus(17);
                    break;
                case 2:
                    // DIX
                    setStatus(18);
                    break;
                case 3:
                    // ET QUART
                    setStatus(16);
                    setStatus(19);
                    break;
                case 4:
                    // VINGT
                    setStatus(20);
                    break;
                case 5:
                    // VINGT-CINQ
                    setStatus(21);
                    break;
                case 6:
                    // ET DEMIE
                    setStatus(24);
                    setStatus(22);
                    break;
                case 7:
                    // MOINS VINGT-CINQ
                    setStatus(23);
                    setStatus(21);
                    break;
                case 8:
                    // MOINS VINGT
                    setStatus(23);
                    setStatus(20);
                    break;
                case 9:
                    // MOINS LE QUART
                    setStatus(23);
                    setStatus(25);
                    setStatus(19);
                    break;
                case 10:
                    // MOINS DIX
                    setStatus(23);
                    setStatus(18);
                    break;
                case 11:
                    // MOINS CINQ
                    setStatus(23);
                    setStatus(17);
                    break;

            }
        }

        // Format the text and set it in the view
        String s = "<font color='" + darkColor + "'><br/><br/>";
        for (int j1 = 0; j1 < matrix.length; j1++) {
            int k1 = 0;
            if(j1 % 2 == 0 && j1 != 10) {
                s = (new StringBuilder()).append(s).append(PADDING[j1][0]).toString();
            }
            while (k1 < matrix[j1].length) {
                if (status[j1][k1] == 0) {
                    s = (new StringBuilder()).append(s).append(matrix[j1][k1]).toString();
                } else {
                    s = (new StringBuilder()).append(s).append("</font><font color=").append(lightColor).append(">").append(getMatrix()[j1][k1]).append("</font><font color='").append(darkColor).append("'>").toString();
                }
                k1++;
            }

            if(j1 % 2 != 0) {
                s = (new StringBuilder()).append(s).append(PADDING[j1][1] + "<br/>").toString();
            }
        }
        String text = (new StringBuilder()).append(s).append("</font>").toString();
        Log.v("WatchFace", text);
        mEditable.append(Html.fromHtml(text));
        canvas.translate(-50.0f,0.0f);
        mLayout.draw(canvas);
    }

    public String[][] getMatrix() {
        if (LANGUAGE.equals("en")) {
            return MATRIX_EN;
        } else if (LANGUAGE.equals("de")) {
            return MATRIX_DE;
        } else if (LANGUAGE.equals("nl")) {
            return MATRIX_NL;
        } else if (LANGUAGE.equals("fr")) {
            return MATRIX_FR;
        } else {
            return MATRIX_EN;
        }
    }

    public int[][] getValues() {
        if (LANGUAGE.equals("en")) {
            return VALUES_EN;
        } else if (LANGUAGE.equals("de")) {
            return VALUES_DE;
        } else if (LANGUAGE.equals("nl")) {
            return VALUES_NL;
        } else if (LANGUAGE.equals("fr")) {
            return VALUES_FR;
        } else {
            return VALUES_EN;
        }
    }

    public int[][] getStatus() {
        return STATUS;
    }

    private void setStatus(int line) {
        int[][] values = getValues();
        for (int i = values[line][1]; i <= values[line][2]; i++) {
            getStatus()[values[line][0]][i] = 1;
        }
    }

    private String MATRIX_EN[][] = new String[][]{
            {"I", "T", "H", "I", "S", "U", "Q", "J", "S", "P", "G", ""},
            {"A", "C", "Q", "U", "A", "R", "T", "E", "R", "D", "C", ""},
            {"T", "W", "E", "N", "T", "Y", "F", "I", "V", "E", "X", ""},
            {"H", "A", "L", "F", "B", "T", "E", "N", "F", "T", "O", ""},
            {"P", "A", "S", "T", "E", "R", "U", "N", "I", "N", "E", ""},
            {"O", "N", "E", "S", "I", "X", "T", "H", "R", "E", "E", ""},
            {"F", "O", "U", "R", "F", "I", "V", "E", "T", "W", "O", ""},
            {"E", "I", "G", "H", "T", "E", "L", "E", "V", "E", "N", ""},
            {"S", "E", "V", "E", "N", "T", "W", "E", "L", "V", "E", ""},
            {"T", "E", "N", "S", "O", "'", "C", "L", "O", "C", "K", ""},
            {" ", " ", "•", " ", "•", " ", "•", " ", "•", "", "", ""}
    };
    private String MATRIX_DE[][] = new String[][]{
            {"E", "S", "K", "I", "S", "T", "A", "F", "Ü", "N", "F", ""},
            {"Z", "E", "H", "N", "B", "Y", "G", "V", "O", "R", "G", ""},
            {"N", "A", "C", "H", "V", "I", "E", "R", "T", "E", "L", ""},
            {"H", "A", "L", "B", "V", "O", "R", "N", "A", "C", "H", ""},
            {"E", "I", "N", "S", "L", "M", "E", "Z", "W", "E", "I", ""},
            {"D", "R", "E", "I", "A", "U", "J", "V", "I", "E", "R", ""},
            {"F", "Ü", "N", "F", "T", "O", "S", "E", "C", "H", "S", ""},
            {"S", "I", "E", "B", "E", "N", "L", "A", "C", "H", "T", ""},
            {"N", "E", "U", "N", "Z", "E", "H", "N", "E", "L", "F", ""},
            {"Z", "W", "Ö", "L", "F", "U", "N", "K", "U", "H", "R", ""},
            {"", "", "•", " ", "•", " ", "•", " ", "•", "", "", ""}};

    private String MATRIX_NL[][] = new String[][]{
            {"H", "E", "T", "O", "I", "S", "A", "V", "I", "J", "F", ""},
            {"T", "I", "E", "N", "B", "Y", "V", "O", "O", "R", "G", ""},
            {"K", "W", "A", "R", "T", "W", "O", "V", "E", "R", "H", ""},
            {"H", "A", "L", "F", "H", "V", "O", "O", "R", "C", "H", ""},
            {"E", "E", "N", "R", "L", "M", "E", "T", "W", "E", "E", ""},
            {"D", "R", "I", "E", "A", "U", "J", "V", "I", "E", "R", ""},
            {"V", "I", "J", "F", "Z", "E", "S", "E", "L", "F", "S", ""},
            {"Z", "E", "V", "E", "N", "A", "L", "A", "C", "H", "T", ""},
            {"N", "E", "G", "E", "N", "T", "I", "E", "N", "L", "F", ""},
            {"T", "W", "A", "A", "L", "F", "N", "K", "U", "U", "R", ""},
            {"", "", "•", " ", "•", " ", "•", " ", "•", "", "", ""}};

    private String MATRIX_FR[][] = new String[][]{
            {"I", "L", "N", "E", "S", "T", "O", "U", "N", "E", "R", ""},
            {"D", "E", "U", "X", "N", "U", "T", "R", "O", "I", "S", ""},
            {"Q", "U", "A", "T", "R", "E", "D", "O", "U", "Z", "E", ""},
            {"C", "I", "N", "Q", "S", "I", "X", "S", "E", "P", "T", ""},
            {"H", "U", "I", "T", "N", "E", "U", "F", "D", "I", "X", ""},
            {"O", "N", "Z", "E", "R", "H", "E", "U", "R", "E", "S", ""},
            {"M", "O", "I", "N", "S", "O", "L", "E", "D", "I", "X", ""},
            {"E", "T", "R", "Q", "U", "A", "R", "T", "R", "E", "D", ""},
            {"V", "I", "N", "G", "T", "-", "C", "I", "N", "Q", "U", ""},
            {"E", "T", "S", "D", "E", "M", "I", "E", "P", "A", "N", ""},
            {"", "", "•", " ", "•", " ", "•", " ", "•", "", "", ""}};
    private int VALUES_EN[][] = new int[][]{
            {0, 0, 1}, // IT
            {0, 3, 4}, // IS
            {8, 5, 10}, // TWELVE
            {5, 0, 2}, // ONE
            {6, 8, 10}, // TWO
            {5, 6, 10}, // THREE
            {6, 0, 3}, // FOUR
            {6, 4, 7}, // FIVE
            {5, 3, 5}, // SIX
            {8, 0, 4}, // SEVEN
            {7, 0, 4}, // EIGHT
            {4, 7, 10}, // NINE
            {9, 0, 2}, // TEN
            {7, 5, 10}, // ELEVEN

            {9, 4, 10}, // o'clock 14
            {2, 6, 9}, // FIVE 15
            {3, 5, 7}, // TEN 16
            {1, 0, 0}, // A 17
            {1, 2, 8}, // QUARTER 18
            {2, 0, 5}, // TWENTY 19
            {2, 0, 9}, // TWENTY FIVE 20
            {3, 0, 3}, // HALF 21
            {3, 9, 10}, // TO 22
            {4, 0, 3}, // PAST 23
    };
    private int VALUES_DE[][] = new int[][]{
            {0, 0, 1}, // ES
            {0, 3, 5}, // IST
            {9, 0, 4}, // ZWÖLF
            {4, 0, 3}, // EIN
            {4, 7, 10}, // ZWEI
            {5, 0, 3}, // DREI
            {5, 7, 10}, // VIER
            {6, 0, 3}, // FÜNF
            {6, 6, 10}, // SECHS
            {7, 0, 5}, // SIEBEN
            {7, 7, 10}, // ACHT
            {8, 0, 3}, // NEUN
            {8, 4, 7}, // ZEHN
            {8, 8, 10}, // ELF

            {9, 8, 10}, // uhr (14)
            {0, 7, 10}, // FÜNF (15)
            {1, 0, 3}, // ZEHN (16)
            {2, 4, 10}, // VIERTEL (17)
            {0, 0, 0}, // 20
            {0, 0, 0}, // 25
            {3, 0, 3}, // HALB (20)
            {1, 7, 9}, // VOR-1 (21)
            {3, 4, 6}, // VOR-2 (22)
            {2, 0, 3}, // NACH-1 (23)
            {3, 7, 10}, // NACH-2 (24)
    };
    private int VALUES_NL[][] = new int[][]{
            {0, 0, 2}, // HET
            {0, 4, 5}, // IS
            {9, 0, 5}, // TWAALF
            {4, 0, 2}, // EEN
            {4, 7, 10}, // TWEE
            {5, 0, 3}, // DRIE
            {5, 7, 10}, // VIER
            {6, 0, 3}, // VIJF
            {6, 4, 6}, // ZES
            {7, 0, 4}, // ZEVEN
            {7, 7, 10}, // ACHT
            {8, 0, 4}, // NEGEN
            {8, 5, 8}, // TIEN
            {6, 7, 9}, // ELF

            {9, 8, 10}, // UUR (14)
            {0, 7, 10}, // VIJF (15)
            {1, 0, 3}, // TIEN (16)
            {2, 0, 4}, // KWART (17)
            {0, 0, 0}, // 20
            {0, 0, 0}, // 25
            {3, 0, 3}, // HALF (20)
            {1, 6, 9}, // VOOR-1 (21)
            {3, 5, 8}, // VOOR-2 (22)
            {2, 6, 9}, // OVER-1 (23)
            {3, 7, 10}, // OVER-2 (24)
    };

    private int VALUES_FR[][] = new int[][]{
            {0, 0, 1}, // IL
            {0, 3, 5}, // EST
            {2, 6, 10}, // DOUZE
            {0, 7, 9}, // UNE
            {1, 0, 3}, // DEUX
            {1, 6, 10}, // TROIS
            {2, 0, 5}, // QUATRE
            {3, 0, 3}, // CINQ
            {3, 4, 6}, // SIX
            {3, 7, 10}, // SEPT
            {4, 0, 3}, // HUIT
            {4, 4, 7}, // NEUF
            {4, 8, 10}, // DIX
            {5, 0, 3}, // ONZE

            {5, 5, 10}, // HEURES (14)
            {5, 5, 9}, // HEURE 15
            {7, 0, 1}, // ET-1 16
            {8, 6, 9}, // CINQ 17
            {6, 8, 10}, // DIX 18
            {7, 3, 7}, // QUART 19
            {8, 0, 4}, // VINGT 20
            {8, 0, 9}, // VINGT-CINQ 21
            {9, 3, 7}, // DEMIE 22
            {6, 0, 4}, // MOINS 23
            {9, 0, 1}, // ET-2 24
            {6, 6, 7}, // LE 25
    };

    private static int STATUS[][] = new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    private String PADDING[][] = new String[][]{
            {"CDDH", ""},
            {"", "UIXF"},
            {"HJKI", ""},
            {"", "OUEH"},
            {"IIUE", ""},
            {"", "OJDR"},
            {"BWZP", ""},
            {"", "SKML"},
            {"JBWN", ""},
            {"", "HPXZ"},
            {"", ""}
    };
}
