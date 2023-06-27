package com.poupock.feussom.aladabusiness.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.poupock.feussom.aladabusiness.R;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.ui.adapter.PrintAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Methods {

    private static final int REQUEST_STORAGE_PERMISSION_CODE = 0X5565;
    public static final int REQUEST_LOCATION_PERMISSION_CODE = 0x5560;
    private static final String TAG = Methods.class.getSimpleName();

    public static void hideKeyBoard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * helper to retrieve the path of an image URI
     */
    public static String getPath(Uri uri, Activity activity) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }

    public static String saveImage(Bitmap bitmap, Context context) {
        String fullPath = null;
        String folderName = context.getString(R.string.app_name).replace(" ","_");
        try{
            if (android.os.Build.VERSION.SDK_INT >= 29) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName);
                values.put(MediaStore.Images.Media.IS_PENDING, true);
                // RELATIVE_PATH and IS_PENDING are introduced in API 29.

                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    saveImageToStream(bitmap, context.getContentResolver().openOutputStream(uri));
                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    context.getContentResolver().update(uri, values, null, null);
                    return getRealPathFromURI(uri, context.getContentResolver());
                }

            } else {
                File directory = new File(Environment.getExternalStorageDirectory().toString() +"/" + folderName);
                // getExternalStorageDirectory is deprecated in API 29

                if (!directory.exists()) {
                    directory.mkdirs();
                }
                String fileName = "IMG_ALADA_" + System.currentTimeMillis() + ".png";
                File file = new File(directory, fileName);
                saveImageToStream(bitmap, new FileOutputStream(file));
                ContentValues values = setImageContentDetails();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                // .DATA is deprecated in API 29
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                fullPath = file.getAbsolutePath();

            }
        }catch (FileNotFoundException ex){
            Log.i(TAG, "Unable to find the file : "+ex.toString());
        }catch (NullPointerException ex){
            Log.e(TAG, "A null object found : "+ex.toString());
        }

        return fullPath;
    }

    public static String getRealPathFromURI(Uri uri, ContentResolver resolver) {
        String path = "";
        if (resolver != null) {
            Cursor cursor = resolver.query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    private static ContentValues  setImageContentDetails() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values;
    }

    private static void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Uri storedImageAndGetUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage,
            "ALADA-Business/IMG_ALADA_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }

    public static boolean runtimeWritePermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity.getApplicationContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION_CODE);
            }
            return true;
        } else return false;
    }

    public static boolean runtimeLocationPermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity.getApplicationContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION_CODE);
            }
            return true;
        } else return false;
    }

    public static AlertDialog setProgressDialog(Context context) {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(context);
        tvText.setText(R.string.loading);
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setView(ll);

        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
        return dialog;
    }

    public static String getCurrentTimeStamp() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return (sdf.format(new Date()));
    }

    public static String getDBCurrentTimeStamp() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return (sdf.format(new Date()));
    }

    public static TextWatcher createTextWatcher(TextInputLayout inputLayout){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    public static String generateCode(Context context) {
        return "ORD_"+User.currentUser(context).id+ new Date().getTime()+"";
    }

    @NonNull
    public static List<MenuItem> filterMenuItems(List<MenuItem> menuItems, String text) {
        List<MenuItem> menuItemList = new ArrayList<>();
        if(menuItems != null && text != null){
            for (int i=0; i<menuItems.size(); i++){
                if(menuItems.get(i).getName().trim().toLowerCase().contains(text.toLowerCase()))
                    menuItemList.add(menuItems.get(i));
            }
        }
        return menuItemList;
    }

    public static int processStatus(int status) {
        if(status == Constant.STATUS_OPEN) return R.string.opened;
        else if(status == Constant.STATUS_CLOSED) return R.string.closed;
        else if(status == Constant.STATUS_SUCCESS) return R.string.success;
        else if(status == Constant.STATUS_ON_GOING) return R.string.on_going;
        else if(status == Constant.STATUS_PAYMENT_PENDING) return R.string.payment_pending;
        else  return R.string.unknown;
    }

    public static String formatTime(String created_at) {
        if (created_at != null){
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss yyyy-MMM-dd");
            try {
                return sdf1.format(Objects.requireNonNull(sdf.parse(created_at)));

            } catch (ParseException e) {
                e.printStackTrace();
                return "";
            }
        }
        else {
            return "";
        }

    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,15}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        if (emailStr.contains("@")) {
            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
            return matcher.find();
        }else return verifyPhone(emailStr);
    }

    public static boolean verifyPhone(String trim) {
        return trim.matches("^[+]?[0-9]{10,13}$");
    }

    public static boolean verifyPassword(String trim) {
        if (trim.length() == 4){
            try{
                int num = Integer.parseInt(trim);
                if (num >= 0)  return true;
            }catch (NumberFormatException ex){
                return false;
            }
        }
        return false;
    }

    public static void generateFCMToken(String tag, Context context) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(tag, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        // Log and toast
                        String msg = context.getString(R.string.msg_token_fmt)+" " +token;
                        Log.d(tag, msg);
                        User.storeFCMToken(token, context);
//                            Toast.makeText(AuthActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    @SuppressLint("SimpleDateFormat")
    public void createPDF(List<OrderItem> orderItems, Order order, Context context) throws FileNotFoundException, DocumentException {

        PdfPCell cell;
        Image imgReportLogo;
        BaseColor tableHeadColor = WebColors.getRGBColor("#000000");
        //Create document file
        float PADDING = 1;
        boolean b = false;
//        Document document = new Document(new RectangleReadOnly(114, 213));
        Document document = new Document(new RectangleReadOnly(114, 213));
        try {
//            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a");
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_HHmm");
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() +
                    "/alada-business/Tickets/";
            File dir = new File(path);
            if (!dir.exists())
                b = dir.mkdirs();

            File file = new File(dir, "ALD_"+ dateFormat.format(Calendar.getInstance().getTime()) +".pdf");
            FileOutputStream outputStream = new FileOutputStream(file);

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            //Open the document
            document.open();
            document.addCreationDate();
            document.addAuthor(context.getString(R.string.app_name));
            document.addCreator(context.getString(R.string.app_name));

            BaseFont poppins = BaseFont.createFont("res/font/poppins_regular.ttf", "UTF-8", BaseFont.EMBEDDED);
            Font poppinsFont = new Font(poppins, 2.0f, Font.NORMAL, BaseColor.BLACK);
            Font poppinsWhFont = new Font(poppins, 2.0f, Font.NORMAL, BaseColor.WHITE);
            Font poppinsBFont = new Font(poppins, 2.0f, Font.BOLD, BaseColor.BLACK);


            try {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                Bitmap bitmap = BitmapFactory.decodeFile(User.getPath(context), options);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitmapLogo = stream.toByteArray();

                imgReportLogo = Image.getInstance(bitmapLogo);
                PdfPTable headTable = new PdfPTable(2);
                cell = new PdfPCell();
                cell.setColspan(2);
                cell.addElement(imgReportLogo);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(PADDING);
                headTable.addCell(cell);

                cell = new PdfPCell();
                cell.setColspan(2);
                cell.addElement(buildCellElt(AppDataBase.getInstance(context).businessDao().getAllBusinesses().get(0).getName(),
                        poppinsBFont, Element.ALIGN_CENTER));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(PADDING);
                headTable.addCell(cell);

                cell = new PdfPCell();
                cell.addElement(buildCellElt(context.getString(R.string.ordered_on),
                        poppinsFont, Element.ALIGN_LEFT));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(PADDING);
                headTable.addCell(cell);

                cell = new PdfPCell();
                cell.addElement(buildCellElt(order.getCreated_at(),
                        poppinsFont, Element.ALIGN_RIGHT));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(PADDING);
                headTable.addCell(cell);

                cell = new PdfPCell();
                cell.setColspan(2);
                cell.addElement(buildCellElt(order.getCode(),
                        poppinsFont, Element.ALIGN_CENTER));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(PADDING);
                headTable.addCell(cell);

                document.add(headTable);

                PdfPTable table = new PdfPTable(3);
                float[] columnWidth = new float[]{60, 20, 30};
                table.setWidthPercentage(100f);
                table.setWidths(columnWidth);

                cell = new PdfPCell();
                cell.addElement(buildCellElt(context.getString(R.string.items)
                        , poppinsWhFont, Element.ALIGN_LEFT));
                cell.setBackgroundColor(tableHeadColor);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(PADDING);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.addElement(buildCellElt(context.getString(R.string.qty)
                        , poppinsWhFont, Element.ALIGN_LEFT));
                cell.setBackgroundColor(tableHeadColor);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(PADDING);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);


                cell = new PdfPCell();
                cell.addElement(buildCellElt(context.getString(R.string.price)
                        , poppinsWhFont, Element.ALIGN_LEFT));
                cell.setBackgroundColor(tableHeadColor);
                cell.setBorder(Rectangle.NO_BORDER);;
                cell.setPadding(PADDING);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setColspan(3);

                for (int i = 0; i < orderItems.size(); i++) {
                    cell = new PdfPCell();
                    cell.addElement(buildCellElt(
                            AppDataBase.getInstance(context).menuItemDao().
                                    getSpecificMenuItem(orderItems.get(i).getMenu_item_id()).getName()
                            , poppinsFont, Element.ALIGN_LEFT));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setPadding(PADDING);
                    table.addCell(cell);

                    cell = new PdfPCell();
                    cell.addElement(buildCellElt(orderItems.get(i).getQuantity() +"", poppinsFont, Element.ALIGN_RIGHT));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setPadding(PADDING);
                    table.addCell(cell);

                    cell = new PdfPCell();
                    cell.addElement(buildCellElt((orderItems.get(i).getQuantity() * orderItems.get(i).getPrice())+"", poppinsFont, Element.ALIGN_RIGHT));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setPadding(PADDING);
                    table.addCell(cell);
                }

                cell = new PdfPCell();
                cell.addElement(buildCellElt(context.getString(R.string.total), poppinsFont, Element.ALIGN_LEFT));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(PADDING);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setColspan(2);
                float total = 0;
                for (int i=0; i <orderItems.size(); i++){
                    total = (float) (total + (orderItems.get(i).getPrice() * orderItems.get(i).getQuantity()));
                }
                cell.addElement(buildCellElt(total+" "+context.getString(R.string.currency_cfa), poppinsBFont, Element.ALIGN_RIGHT));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(PADDING);
                table.addCell(cell);

                document.add(table);
                addLineSeperator(document);
                addNewItem(document, context.getString(R.string.thank_u_for_visit), Element.ALIGN_CENTER, poppinsBFont);
                addLineSeperator(document);
                Toast.makeText(context, context.getString(R.string.ticket_generated) +" "+ dateFormat.format(Calendar.getInstance().getTime()) , Toast.LENGTH_LONG).show();
                doPrint(file, context);
            } catch (DocumentException de) {
                Log.e("PDFCreator", "DocumentException:" + de);
            } catch (IOException e) {
                Log.e("PDFCreator", "ioException:" + e);
            } finally {
                document.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Element buildCellElt(String s, Font f, int align){
        Chunk chunk = new Chunk(s, f);
        Paragraph paragraph =  new Paragraph(chunk);
        paragraph.setAlignment(align);
        return paragraph;
    }

    private void addNewItem(Document document, String text, int alignment, Font font) throws DocumentException {
        Chunk chunk = new Chunk(text, font);
        Paragraph paragraph =  new Paragraph(chunk);
        paragraph.setAlignment(alignment);
        document.add(paragraph);
    }

    private void addLineSeperator(Document document) throws DocumentException{
        LineSeparator lineSeparator = new LineSeparator();
        addLineSpace(document);
        lineSeparator.setLineColor(new BaseColor(0,0,0, 70));
        document.add(new Chunk(lineSeparator));
        addLineSpace(document);
    }

    private void addLineSpace(Document document) throws DocumentException {
        document.add(new Paragraph(""));
    }

    private void addNewItemLeftAndRight(Document document, String leftText, String rightText, Font leftFont, Font rightFont) throws DocumentException{
        Chunk leftChunk = new Chunk(leftText, leftFont);
        Chunk rightChunk = new Chunk(rightText, rightFont);
        Paragraph paragraph = new Paragraph(leftChunk);
        paragraph.add(new VerticalPositionMark());
        paragraph.add(rightChunk);
        document.add(paragraph);
    }
    private void doPrint(File file, Context context){
        PrintManager printManager=(PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        try {
            PrintAdapter printAdapter = new PrintAdapter(context, file.getAbsolutePath());
            printManager.print("Document", printAdapter, new PrintAttributes.Builder().build());

        } catch (Exception e){
            Log.e(TAG, "The error is : "+e.toString());
        }
    }
}

