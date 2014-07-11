package nl.carloslubbers.textwatch;

import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MatrixManager {

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
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };
    private final WatchFace watchFace;
    public String darkColor = "#282828";
    public String lightColor = "white";
    public int backgroundColor = Color.argb(255, 0, 0, 0);
    private String MATRIX_EN[][] = new String[][]{
            {"I", "T", "H", "I", "S", "U", "Q", "J", "S", "P", "G", ""},
            {"A", "C", "Q", "U", "A", "R", "T", "E", "R", "D", "C", "\n"},
            {"T", "W", "E", "N", "T", "Y", "F", "I", "V", "E", "X", ""},
            {"H", "A", "L", "F", "B", "T", "E", "N", "F", "T", "O", "\n"},
            {"P", "A", "S", "T", "E", "R", "U", "N", "I", "N", "E", ""},
            {"O", "N", "E", "S", "I", "X", "T", "H", "R", "E", "E", "\n"},
            {"F", "O", "U", "R", "F", "I", "V", "E", "T", "W", "O", ""},
            {"E", "I", "G", "H", "T", "E", "L", "E", "V", "E", "N", "\n"},
            {"S", "E", "V", "E", "N", "T", "W", "E", "L", "V", "E", ""},
            {"T", "E", "N", "S", "O", "'", "C", "L", "O", "C", "K", "\n"}
    };
    private String MATRIX_DE[][] = new String[][]{
            {"E", "S", "K", "I", "S", "T", "A", "F", "Ü", "N", "F", ""},
            {"Z", "E", "H", "N", "B", "Y", "G", "V", "O", "R", "G", "\n"},
            {"N", "A", "C", "H", "V", "I", "E", "R", "T", "E", "L", ""},
            {"H", "A", "L", "B", "V", "O", "R", "N", "A", "C", "H", "\n"},
            {"E", "I", "N", "S", "L", "M", "E", "Z", "W", "E", "I", ""},
            {"D", "R", "E", "I", "A", "U", "J", "V", "I", "E", "R", "\n"},
            {"F", "Ü", "N", "F", "T", "O", "S", "E", "C", "H", "S", ""},
            {"S", "I", "E", "B", "E", "N", "L", "A", "C", "H", "T", "\n"},
            {"N", "E", "U", "N", "Z", "E", "H", "N", "E", "L", "F", ""},
            {"Z", "W", "Ö", "L", "F", "U", "N", "K", "U", "H", "R", "\n"}
    };

    private String MATRIX_NL[][] = new String[][]{
            {"H", "E", "T", "O", "I", "S", "A", "V", "I", "J", "F", ""},
            {"T", "I", "E", "N", "B", "Y", "V", "O", "O", "R", "G", "\n"},
            {"K", "W", "A", "R", "T", "W", "O", "V", "E", "R", "H", ""},
            {"H", "A", "L", "F", "H", "V", "O", "O", "R", "C", "H", "\n"},
            {"E", "E", "N", "R", "L", "M", "E", "T", "W", "E", "E", ""},
            {"D", "R", "I", "E", "A", "U", "J", "V", "I", "E", "R", "\n"},
            {"V", "I", "J", "F", "Z", "E", "S", "E", "L", "F", "S", ""},
            {"Z", "E", "V", "E", "E", "N", "L", "A", "C", "H", "T", "\n"},
            {"N", "E", "G", "E", "N", "T", "I", "E", "N", "L", "F", ""},
            {"T", "W", "A", "A", "L", "F", "N", "K", "U", "U", "R", "\n"}
    };

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
            {7, 0, 5}, // ZEVEN
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

    private String language = "en";


    public MatrixManager(WatchFace wf) {
        watchFace = wf;
        setLanguage(watchFace.settings.getString("lang", "en"));
    }

    public void updateText(TextView tv) {
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
        if (m > 60) m = 0;
        if (h > 12) h = 0;
        watchFace.editor.putInt("h", h + 1).putInt("m", m + 1).apply();
        h12 = h % 12;
        m5 = (int) Math.floor(m / 5);

        Log.d(WatchFace.TAG, m5 + " " + h12 + " " + h + ":" + m);
        // Reset the status matrix
        for (int i1 = 0; i1 < matrix.length; i1++)
            for (int l1 = 0; l1 < matrix[i1].length; l1++) {
                status[i1][l1] = 0;
            }

        // Switching for the values
        setStatus(0); // it
        setStatus(1); // is

        // Hour
        if (language.equals("en")) {
            if (m5 >= 7) {
                h12 += 1;
                if (h12 > 11) h12 = 0;
            }
            setStatus(h12 + 2); // hour
        } else if (language.equals("de") || language.equals("nl")) {
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
        }

        // Minute
        if (language.equals("de")) {
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
        } else if (language.equals("nl")) {
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
        } else if (language.equals("en")) {
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
                    // HALF
                    setStatus(21);
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
        }

        // Format the text and set it in the view
        String s = "<font color='" + darkColor + "'>";
        for (int j1 = 0; j1 < matrix.length; j1++) {
            int k1 = 0;
            while (k1 < matrix[j1].length) {
                if (status[j1][k1] == 0) {
                    s = (new StringBuilder()).append(s).append(matrix[j1][k1]).toString();
                } else {
                    s = (new StringBuilder()).append(s).append("</font><font color=").append(lightColor).append(">").append(getMatrix()[j1][k1]).append("</font><font color='").append(darkColor).append("'>").toString();
                }
                k1++;
            }
        }
        watchFace.findViewById(R.id.background).setBackgroundColor(backgroundColor);
        tv.setText(Html.fromHtml((new StringBuilder()).append(s).append("</font>").append(watchFace.heightString).toString()));
    }


    public String[][] getMatrix() {
        if (language.equals("en")) {
            return MATRIX_EN;
        } else if (language.equals("de")) {
            return MATRIX_DE;
        } else if (language.equals("nl")) {
            return MATRIX_NL;
        } else {
            return MATRIX_EN;
        }
    }

    public int[][] getValues() {
        if (language.equals("en")) {
            return VALUES_EN;
        } else if (language.equals("de")) {
            return VALUES_DE;
        } else if (language.equals("nl")) {
            return VALUES_NL;
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

    public void setLanguage(String lang) {
        watchFace.editor.putString("lang", lang).apply();
        language = lang;
    }
}
