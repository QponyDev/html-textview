package org.sufficientlysecure.htmltextview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.text.Layout;
import android.text.ParcelableSpan;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;

/**
 * Created by michal.luszczuk on 20.03.2017.
 */

public class BulletSpan implements LeadingMarginSpan, ParcelableSpan {

    public static final int BULLET_SPAN = 1010;

    public static final int STANDARD_GAP_WIDTH = 2;
    public static final Creator<BulletSpan> CREATOR = new Creator<BulletSpan>() {
        @Override
        public BulletSpan createFromParcel(Parcel source) {
            return new BulletSpan(source);
        }

        @Override
        public BulletSpan[] newArray(int size) {
            return new BulletSpan[size];
        }
    };

    private static final int BULLET_RADIUS = 3;
    private static Path sBulletPath = null;
    private final int mGapWidth;
    private final boolean mWantColor;
    private final int mColor;

    public BulletSpan() {
        mGapWidth = STANDARD_GAP_WIDTH;
        mWantColor = false;
        mColor = 0;
    }

    public BulletSpan(int gapWidth) {
        mGapWidth = gapWidth;
        mWantColor = false;
        mColor = 0;
    }

    public BulletSpan(int gapWidth, int color) {
        mGapWidth = gapWidth;
        mWantColor = true;
        mColor = color;
    }

    protected BulletSpan(Parcel in) {
        this.mGapWidth = in.readInt();
        this.mWantColor = in.readByte() != 0;
        this.mColor = in.readInt();
    }

    @Override
    public int getLeadingMargin(boolean first) {
        return 2 * BULLET_RADIUS + mGapWidth;
    }

    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom,
                                  CharSequence text, int start, int end, boolean first, Layout l) {
        if (((Spanned) text).getSpanStart(this) == start) {
            Paint.Style style = p.getStyle();
            int oldcolor = 0;

            if (mWantColor) {
                oldcolor = p.getColor();
                p.setColor(mColor);
            }

            p.setStyle(Paint.Style.FILL);

            if (c.isHardwareAccelerated()) {
                if (sBulletPath == null) {
                    sBulletPath = new Path();
                    // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
                    sBulletPath.addCircle(0.0f, 0.0f, 1.2f * BULLET_RADIUS, Path.Direction.CW);
                }

                c.save();
                c.translate(x + dir * BULLET_RADIUS, (top + baseline) / 2.0f);
                c.drawPath(sBulletPath, p);
                c.restore();
            } else {
                c.drawCircle(x + dir * BULLET_RADIUS, (top + baseline) / 2.0f, BULLET_RADIUS, p);
            }

            if (mWantColor) {
                p.setColor(oldcolor);
            }

            p.setStyle(style);
        }
    }

    public int getSpanTypeIdInternal() {
        return BULLET_SPAN;
    }

    @Override
    public int getSpanTypeId() {
        return BULLET_SPAN;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mGapWidth);
        dest.writeByte(this.mWantColor ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mColor);
    }
}
