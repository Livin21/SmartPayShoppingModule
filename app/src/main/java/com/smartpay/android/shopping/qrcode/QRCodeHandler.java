package com.smartpay.android.shopping.qrcode;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

public class QRCodeHandler {
    public static Bitmap generateQRCode(String BILL_CONTENTS, int SCREEN_RESOLUTION) throws WriterException {

        int imageSize;
        //Just random guesses
        if (SCREEN_RESOLUTION > 400){
            imageSize = 700;
        }else if (SCREEN_RESOLUTION > 300 && SCREEN_RESOLUTION < 400)
            imageSize = 500;
        else
            imageSize = 350;

        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(BILL_CONTENTS+"",
                null,
                Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(),
                imageSize);

        return qrCodeEncoder.encodeAsBitmap();
    }
}
