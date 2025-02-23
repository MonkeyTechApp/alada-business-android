package com.poupock.feussom.aladabusiness.print;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.print.sdk.Barcode;
import com.android.print.sdk.PrinterConstants;
import com.android.print.sdk.PrinterConstants.Command;
import com.android.print.sdk.PrinterInstance;
import com.poupock.feussom.aladabusiness.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PrintUtils {

    public static void printText(Resources resources, PrinterInstance mPrinter) {
        mPrinter.init();

        mPrinter.printText(resources.getString(R.string.str_text));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);


        mPrinter.setFont(0, 0, 0, 0);
        mPrinter.setPrinter(Command.ALIGN, 0);
        mPrinter.printText(resources.getString(R.string.str_text_left));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);// 换2行


        mPrinter.setPrinter(Command.ALIGN, 1);
        mPrinter.printText(resources.getString(R.string.str_text_center));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);// 换2行

        mPrinter.setPrinter(Command.ALIGN, 2);
        mPrinter.printText(resources.getString(R.string.str_text_right));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3); // 换3行

        mPrinter.setPrinter(Command.ALIGN, 0);
        mPrinter.setFont(0, 0, 1, 0);
        mPrinter.printText(resources.getString(R.string.str_text_strong));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2); // 换2行

        mPrinter.setFont(0, 0, 0, 1);
        mPrinter.sendByteData(new byte[]{(byte) 0x1C, (byte) 0x21, (byte) 0x80});
        mPrinter.printText(resources.getString(R.string.str_text_underline));
        mPrinter.sendByteData(new byte[]{(byte) 0x1C, (byte) 0x21, (byte) 0x00});
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2); // 换2行

        mPrinter.setFont(0, 0, 0, 0);
        mPrinter.printText(resources.getString(R.string.str_text_height));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        for (int i = 0; i < 4; i++) {
            mPrinter.setFont(i, i, 0, 0);
            mPrinter.printText((i + 1) + resources.getString(R.string.times));

        }
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 1);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

        for (int i = 0; i < 4; i++) {
            mPrinter.setFont(i, i, 0, 0);
            mPrinter.printText(resources.getString(R.string.bigger) + (i + 1) + resources.getString(R.string.bigger1));
            mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);
        }

        mPrinter.setFont(0, 0, 0, 0);
        mPrinter.setPrintModel(true, false, false, false);
        mPrinter.printText("文字加粗演示\n");

        mPrinter.setPrintModel(true, true, false, false);
        mPrinter.printText("文字加粗倍高演示\n");

        mPrinter.setPrintModel(true, false, true, false);
        mPrinter.printText("文字加粗倍宽演示\n");

        mPrinter.setPrintModel(true, true, true, false);
        mPrinter.printText("文字加粗倍高倍宽演示\n");

        mPrinter.setPrintModel(false, false, false, true);
        mPrinter.printText("文字下划线演示\n");


        mPrinter.setFont(0, 0, 0, 0);
        mPrinter.setPrinter(Command.ALIGN, 0);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 3);

    }


    public static void printImage(Resources resources, PrinterInstance mPrinter) {
        mPrinter.init();
        mPrinter.setFont(0, 0, 0, 0);
        mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_LEFT);
        mPrinter.printText(resources.getString(R.string.str_image));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        Bitmap bitmap1 = null;
        try {
            bitmap1 = BitmapFactory.decodeStream(resources.getAssets().open("android.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        mPrinter.printImage(bitmap1);
        mPrinter.printText("\n\n\n\n");                     //换4行

        try {
            bitmap1 = BitmapFactory.decodeStream(resources.getAssets().open("support.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        mPrinter.setPrinter(Command.ALIGN, Command.ALIGN_CENTER);   //设置打印居中
        mPrinter.printText(resources.getString(R.string.str_image));
        mPrinter.printImage(bitmap1);
        mPrinter.printText("\n\n\n\n");                     //换4行
    }


    public static void printBarcode(Resources resources, PrinterInstance mPrinter) {

        mPrinter.init();
        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.CODE39" + resources.getString(R.string.str_show));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        Barcode barcode0 = new Barcode(PrinterConstants.BarcodeType.CODE39, 2, 150, 2, "123456");
        mPrinter.printBarCode(barcode0);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        mPrinter.printText(resources.getString(R.string.print) + " BarcodeType.CODABAR" + resources.getString(R.string.str_show));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        Barcode barcode1 = new Barcode(PrinterConstants.BarcodeType.CODABAR, 2, 150, 2, "123456");
        mPrinter.printBarCode(barcode1);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.ITF" + resources.getString(R.string.str_show));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        Barcode barcode2 = new Barcode(PrinterConstants.BarcodeType.ITF, 2, 150, 2, "123456");
        mPrinter.printBarCode(barcode2);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.CODE93" + resources.getString(R.string.str_show));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        Barcode barcode3 = new Barcode(PrinterConstants.BarcodeType.CODE93, 2, 150, 2, "123456");
        mPrinter.printBarCode(barcode3);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        mPrinter.printText(resources.getString(R.string.print) + " BarcodeType.CODE128" + resources.getString(R.string.str_show));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        Barcode barcode4 = new Barcode(PrinterConstants.BarcodeType.CODE128, 2, 150, 2, "123456");
        mPrinter.printBarCode(barcode4);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        mPrinter.printText(resources.getString(R.string.print) + " BarcodeType.UPC_A" + resources.getString(R.string.str_show));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        Barcode barcode5 = new Barcode(PrinterConstants.BarcodeType.UPC_A, 2, 63, 2, "000000000000");
        mPrinter.printBarCode(barcode5);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.UPC_E" + resources.getString(R.string.str_show));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        Barcode barcode6 = new Barcode(PrinterConstants.BarcodeType.UPC_E, 2, 63, 2, "000000000000");
        mPrinter.printBarCode(barcode6);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.JAN13" + resources.getString(R.string.str_show));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        Barcode barcode7 = new Barcode(PrinterConstants.BarcodeType.JAN13, 2, 63, 2, "000000000000");
        mPrinter.printBarCode(barcode7);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);

        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.JAN8" + resources.getString(R.string.str_show));
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
        Barcode barcode8 = new Barcode(PrinterConstants.BarcodeType.JAN8, 2, 63, 2, "0000000");
        mPrinter.printBarCode(barcode8);
        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);


//        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.PDF417" + resources.getString(R.string.str_show));
//        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
//        Barcode barcode9 = new Barcode(PrinterConstants.BarcodeType.PDF417, 2, 3, 6, "123456");
//        mPrinter.printBarCode(barcode9);
//        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
//
//        mPrinter.printText(resources.getString(R.string.print) + " BarcodeType.QRCODE" + resources.getString(R.string.str_show));
//        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
//        Barcode barcode10 = new Barcode(PrinterConstants.BarcodeType.QRCODE, 2, 3, 6, "123456");
//        mPrinter.printBarCode(barcode10);
//        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
//
//        mPrinter.printText(resources.getString(R.string.print) + "BarcodeType.DATAMATRIX" + resources.getString(R.string.str_show));
//        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
//        Barcode barcode11 = new Barcode(PrinterConstants.BarcodeType.DATAMATRIX, 2, 3, 6, "123456");
//        mPrinter.printBarCode(barcode11);
//        mPrinter.setPrinter(Command.PRINT_AND_WAKE_PAPER_BY_LINE, 2);
    }


    public static void printBigData(Resources resources, PrinterInstance mPrinter) {
        try {
            InputStream is = resources.getAssets().open("58-big-data-test.bin");
            int length = is.available();
            byte[] fileByte = new byte[length];
            is.read(fileByte);
            mPrinter.init();
            mPrinter.sendByteData(fileByte);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void printUpdate(Resources resources, PrinterInstance mPrinter, String filePath) {
        try {

            FileInputStream fis = new FileInputStream(new File(filePath));
            //InputStream is = resources.getAssets().open("PT8761-HT-BAT-9170.bin");
            int length = fis.available();
            byte[] fileByte = new byte[length];
            fis.read(fileByte);
            mPrinter.init();
            mPrinter.updatePrint(fileByte);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
