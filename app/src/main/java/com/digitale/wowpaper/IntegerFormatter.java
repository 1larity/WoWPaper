package com.digitale.wowpaper;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Integer number format for use with charts
 * Created by Rich on 25/03/2016.
 */
public class IntegerFormatter  implements ValueFormatter {

        private DecimalFormat mFormat;

        public IntegerFormatter() {
            mFormat = new DecimalFormat("###");
            mFormat.setMinimumFractionDigits(0);
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return mFormat.format(value);
        }
    }

