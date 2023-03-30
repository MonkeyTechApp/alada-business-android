package com.poupock.feussom.aladabusiness.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
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
import com.poupock.feussom.aladabusiness.callback.DialogCallback;
import com.poupock.feussom.aladabusiness.callback.ListItemClickCallback;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.DialogListBinding;
import com.poupock.feussom.aladabusiness.databinding.OrderDetailFragmentBinding;
import com.poupock.feussom.aladabusiness.ui.adapter.GuestTableAdapter;
import com.poupock.feussom.aladabusiness.ui.adapter.OrderAdapter;
import com.poupock.feussom.aladabusiness.ui.adapter.OrderItemAdapter;
import com.poupock.feussom.aladabusiness.ui.adapter.PrintAdapter;
import com.poupock.feussom.aladabusiness.ui.fragment.order.OrderViewModel;
import com.poupock.feussom.aladabusiness.util.Constant;
import com.poupock.feussom.aladabusiness.util.GuestTable;
import com.poupock.feussom.aladabusiness.util.Order;
import com.poupock.feussom.aladabusiness.util.OrderItem;
import com.squareup.picasso.Picasso;

import org.intellij.lang.annotations.JdkConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class OrderDetailDialogFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = OrderDetailDialogFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Bitmap bitmap;
    private boolean boolean_save;

    public static OrderDetailDialogFragment newInstance(String param1, String param2) {
        OrderDetailDialogFragment fragment = new OrderDetailDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /** The system calls this to get the DialogFragment's layout, regardless
     of whether it's being displayed as a dialog or an embedded fragment. */

    OrderDetailFragmentBinding binding;
    OrderViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        binding = OrderDetailFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /** The system calls this only when creating the layout in a dialog. */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        Log.i(TAG,"The view created");

        binding.txtCode.setText(viewModel.getOrderMutableLiveData().getValue().getCode());
        binding.txtTable.setText(AppDataBase.getInstance(requireContext()).guestTableDao().getSpecificGuestTable(viewModel.getOrderMutableLiveData().getValue().getGuest_table_id()).getTitle());
        binding.txtTime.setText(viewModel.getOrderMutableLiveData().getValue().getCreated_at());
        binding.txtTotal.setText(viewModel.getOrderMutableLiveData().getValue().getTotal()+" CFA");

        binding.listDetails.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listDetails.setAdapter(new OrderItemAdapter(requireContext(),
                viewModel.getOrderMutableLiveData().getValue().extractAllOrderedItems(),
                new DialogCallback() {
                    @Override
                    public void onActionClicked(Object o, int action) {

                    }
                }));

        binding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap = loadBitmapFromView(binding.mainLay, binding.mainLay.getWidth(), binding.mainLay.getHeight());
                try {
                    createPDF(viewModel.getOrderMutableLiveData().getValue().extractAllOrderedItems());
//                    generatePDF(viewModel.getOrderMutableLiveData().getValue());
                } catch (FileNotFoundException | DocumentException e) {
                    Log.e(TAG, "The execption : "+e.toString());
                }
            }
        });
    }


    private void createPdf(float width , float hight) {
        WindowManager wm = (WindowManager) requireActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        float hight = displaymetrics.heightPixels;
//        float width = displaymetrics.widthPixels;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();
        canvas.drawPaint(paint);


        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);


        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/PdfTest/";
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();

        File filePath = new File(dir, "Test.pdf");

        try {
            document.writeTo(new FileOutputStream(filePath));
//            btn_generate.setText("Check PDF");
            boolean_save=true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
    }

    Bitmap bmp, scaledbmp;

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void doPrint(File file){
        PrintManager printManager=(PrintManager) requireActivity().getSystemService(Context.PRINT_SERVICE);
        try {
            PrintAdapter printAdapter = new PrintAdapter(requireActivity(), file.getAbsolutePath());
            printManager.print("Document", printAdapter, new PrintAttributes.Builder().build());

        } catch (Exception e){
            Log.e(TAG, "The error is : "+e.toString());
        }
    }

    private void generatePDF(Order order){
        // creating an object variable
        // for our PDF document.
        bmp = drawableToBitmap(getResources().getDrawable(R.mipmap.ic_launcher, requireActivity().getTheme()));
        scaledbmp = Bitmap.createScaledBitmap(bmp, 30, 30, false);
        PdfDocument pdfDocument = new PdfDocument();

        // two variables for paint "paint" is used
        // for drawing shapes and we will use "title"
        // for adding text in our PDF file.
        Paint paint = new Paint();
        Paint title = new Paint();

        // we are adding page info to our PDF file
        // in which we will be passing our pageWidth,
        // pageHeight and number of pages and after that
        // we are calling it to create our PDF.
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(213  , 114, 1).create();

        // below line is used for setting
        // start page for our PDF file.
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        // creating a variable for canvas
        // from our page of PDF.
        Canvas canvas = myPage.getCanvas();

        // below line is used to draw our image on our PDF file.
        // the first parameter of our drawbitmap method is
        // our bitmap
        // second parameter is position from left
        // third parameter is position from top and last
        // one is our variable for paint.
        canvas.drawBitmap(scaledbmp, 59, 10, paint);

        // below line is used for adding typeface for
        // our text which we will be adding in our PDF file.
        title.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));

        // below line is used for setting text size
        // which we will be displaying in our PDF file.
        title.setTextSize(8);

        // below line is sued for setting color
        // of our text inside our PDF file.
        title.setColor(ContextCompat.getColor(requireContext(), R.color.black));
        title.setTextAlign(Paint.Align.CENTER);

        // below line is used to draw text in our PDF file.
        // the first parameter is our text, second parameter
        // is position from start, third parameter is position from top
        // and then we are passing our variable of paint which is title.
        canvas.drawText("\n "
                +AppDataBase.getInstance(requireContext()).businessDao().getAllBusinesses().get(0).getName(), 10, 100, title);
        canvas.drawText("\n "+getString(R.string.code_command)+" : "+
                order.getCode(), 10, 80, title);

        // similarly we are creating another text and in this
        // we are aligning this text to center of our PDF file.
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(requireContext(), R.color.black));
        title.setTextSize(15);

        // below line is used for setting
        // our text to center of PDF.
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("This is sample document which we have created.", 10, 560, title);
        // after adding all attributes to our
        // PDF file we will be finishing our page.
        pdfDocument.finishPage(myPage);

        // below line is used to set the name of
        // our PDF file and its path.

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/PdfTest/";
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();

        File file = new File(dir, "GFG_"+ UUID.randomUUID().toString()+".pdf");

        try {
            // after creating a file name we will
            // write our PDF file to that location.
            pdfDocument.writeTo(new FileOutputStream(file));

            // below line is to print toast message
            // on completion of PDF generation.
            Toast.makeText(requireActivity(), R.string.ticket_generated, Toast.LENGTH_SHORT).show();
            doPrint(file);
        } catch (IOException e) {
            // below line is used
            // to handle error
            e.printStackTrace();
        }
        // after storing our pdf to that
        // location we are closing our PDF file.
        pdfDocument.close();
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    private PdfPCell cell;
    private Image imgReportLogo;

    BaseColor headColor = WebColors.getRGBColor("#DEDEDE");
    BaseColor tableHeadColor = WebColors.getRGBColor("#F5ABAB");

    public void createOldPDF(List<OrderItem> orderItems) throws FileNotFoundException, DocumentException {

        //Create document file
        Document document = new Document();
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a");
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_HHmm");
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/PdfTest/";
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            File file = new File(dir, "GFG_"+ dateFormat.format(Calendar.getInstance().getTime()) +".pdf");
            FileOutputStream outputStream = new FileOutputStream(file);

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            //Open the document
            document.open();

            document.setPageSize(PageSize.A8);
            document.addCreationDate();
            document.addAuthor(getString(R.string.app_name));
            document.addCreator(getString(R.string.app_name));

            Font titleFont = new Font(Font.FontFamily.COURIER, 22.0f, Font.BOLD, BaseColor.BLACK);

            addNewItem(document, AppDataBase.getInstance(requireContext()).businessDao().getAllBusinesses().get(0).getName(),
                    Element.ALIGN_CENTER, titleFont);

            //Create Header table
            PdfPTable header = new PdfPTable(3);
            header.setWidthPercentage(100);
            float[] fl = new float[]{20, 45, 35};
            header.setWidths(fl);
            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);

            //Set Logo in Header Cell
//            Drawable logo = getResources().getDrawable(R.mipmap.ic_launcher);
            Bitmap bitmap = drawableToBitmap(getResources().getDrawable(R.mipmap.ic_launcher, requireActivity().getTheme()));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapLogo = stream.toByteArray();
            try {
                imgReportLogo = Image.getInstance(bitmapLogo);
                imgReportLogo.setAbsolutePosition(330f, 642f);

                cell.addElement(imgReportLogo);
                header.addCell(cell);

                cell = new PdfPCell();
                cell.setBorder(Rectangle.NO_BORDER);

                // Heading
                //BaseFont font = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

                //Creating Chunk
                Chunk titleChunk = new Chunk(AppDataBase.getInstance(requireContext()).businessDao().getAllBusinesses().get(0).getName(),
                        titleFont);
                //Paragraph
                Paragraph titleParagraph = new Paragraph(titleChunk);

                cell.addElement(titleParagraph);
                cell.addElement(new Paragraph(getString(R.string.order)));
                cell.addElement(new Paragraph(getString(R.string.ordered_on)+" : " + format.format(Calendar.getInstance().getTime())));
                header.addCell(cell);

//                cell = new PdfPCell(new Paragraph(""));
//                cell.setBorder(Rectangle.NO_BORDER);
//                header.addCell(cell);

                PdfPTable pTable = new PdfPTable(1);
                pTable.setWidthPercentage(100);
                cell = new PdfPCell();
                cell.setColspan(1);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.addElement(header);
                pTable.addCell(cell);

                PdfPTable table = new PdfPTable(3);
                float[] columnWidth = new float[]{60, 20, 30};
                table.setWidths(columnWidth);

                cell = new PdfPCell();
                cell.setBackgroundColor(headColor);
                cell.setColspan(3);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.addElement(pTable);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(" "));
                cell.setColspan(3);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setColspan(3);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setBackgroundColor(tableHeadColor);

                cell = new PdfPCell(new Phrase(getString(R.string.items)));
                cell.setBackgroundColor(tableHeadColor);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(getString(R.string.quantity)));
                cell.setBackgroundColor(tableHeadColor);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(getString(R.string.price)));
                cell.setBackgroundColor(tableHeadColor);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.setColspan(3);

                for (int i = 0; i < orderItems.size(); i++) {
                    cell = new PdfPCell(new Phrase(AppDataBase.getInstance(requireContext()).menuItemDao().
                            getSpecificMenuItem(orderItems.get(i).getMenu_item_id()).getName()));
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase(orderItems.get(i).getQuantity()+""));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase((orderItems.get(i).getPrice()*orderItems.get(i).getQuantity())+""));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);
                }

                PdfPTable ftable = new PdfPTable(3);
                ftable.setWidthPercentage(100);
                float[] columnWidtha = new float[]{50, 20, 30};
                ftable.setWidths(columnWidtha);
                cell = new PdfPCell();
                cell.setColspan(3);
                cell.setBackgroundColor(tableHeadColor);
                cell = new PdfPCell(new Phrase("Total Number"));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setBackgroundColor(tableHeadColor);
                ftable.addCell(cell);

                cell = new PdfPCell(new Phrase(""));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setBackgroundColor(tableHeadColor);
                ftable.addCell(cell);

                cell = new PdfPCell(new Phrase(""));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setBackgroundColor(tableHeadColor);
                ftable.addCell(cell);

                cell = new PdfPCell(new Paragraph("Footer"));
                cell.setColspan(3);
                cell.setBorder(Rectangle.NO_BORDER);
                ftable.addCell(cell);

                cell = new PdfPCell();
                cell.setColspan(3);
                cell.addElement(ftable);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                document.add(table);

                Toast.makeText(requireActivity(), getString(R.string.ticket_generated) +" "+ dateFormat.format(Calendar.getInstance().getTime()) + ".pdf successfully generated at DOWNLOADS folder", Toast.LENGTH_LONG).show();
                doPrint(file);
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

    public void createPDF(List<OrderItem> orderItems) throws FileNotFoundException, DocumentException {

        //Create document file
        float PADDING = 1;
        Document document = new Document(new RectangleReadOnly(114, 213));
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a");
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_HHmm");
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/PdfTest/";
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            File file = new File(dir, "ALD_"+ dateFormat.format(Calendar.getInstance().getTime()) +".pdf");
            FileOutputStream outputStream = new FileOutputStream(file);

            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            //Open the document
            document.open();

//            document.setPageSize(new RectangleReadOnly(114, 213));
//            document.setPageSize(new RectangleReadOnly(114, 213));
            document.addCreationDate();
            document.addAuthor(getString(R.string.app_name));
            document.addCreator(getString(R.string.app_name));

            BaseFont poppins = BaseFont.createFont("res/font/poppins_regular.ttf", "UTF-8", BaseFont.EMBEDDED);
            Font poppinsFont = new Font(poppins, 2.0f, Font.NORMAL, BaseColor.BLACK);
            Font poppinsBFont = new Font(poppins, 2.0f, Font.BOLD, BaseColor.BLACK);

            //Set Logo in Header Cell
//            Drawable logo = getResources().getDrawable(R.mipmap.ic_launcher);

            try {
//                URL url = new URL("https://alada.poupock.com/img/dinner.png");

//                Bitmap bitmap = Picasso.get().load("https://alada.poupock.com/img/dinner.png").get();
                Bitmap bitmap = drawableToBitmap(getResources().getDrawable(R.mipmap.ic_launcher, requireActivity().getTheme()));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitmapLogo = stream.toByteArray();
//                PdfPTable tTable = new PdfPTable(1);
//                float[] columnTWidth = new float[]{60};
//                tTable.setWidthPercentage(100f);
//                tTable.setWidths(columnTWidth);
                imgReportLogo = Image.getInstance(bitmapLogo);
//                imgReportLogo.setAbsolutePosition(330f, 642f);
//                document.add(imgReportLogo);
//                cell = new PdfPCell();
//                cell.addElement(imgReportLogo);
//
//                cell.setBorder(Rectangle.NO_BORDER);
//                cell.setPadding(PADDING);
//                tTable.addCell(cell);
//
//                document.add(tTable);

//                addImage(document, imgReportLogo, Element.ALIGN_CENTER, titleFont);
                PdfPTable headTable = new PdfPTable(2);
                cell = new PdfPCell();
                cell.setColspan(2);
                cell.addElement(imgReportLogo);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(PADDING);
                headTable.addCell(cell);
                document.add(headTable);

                addNewItem(document, AppDataBase.getInstance(requireContext()).businessDao().getAllBusinesses().get(0).getName(),
                        Element.ALIGN_CENTER, poppinsBFont);
                addLineSpace(document);
                addLineSpace(document);
                addNewItem(document, getString(R.string.ordered_on)+" : " + format.format(Calendar.getInstance().getTime()),
                        Element.ALIGN_LEFT, poppinsFont);
                addLineSpace(document);
                addLineSeperator(document);

                PdfPTable table = new PdfPTable(3);
                float[] columnWidth = new float[]{60, 20, 30};
                table.setWidthPercentage(100f);
                table.setWidths(columnWidth);

//                cell = new PdfPCell(new Phrase(getString(R.string.items)));
                cell = new PdfPCell();
                cell.addElement(buildCellElt(getString(R.string.items)
                        , poppinsFont, Element.ALIGN_LEFT));
                cell.setBackgroundColor(tableHeadColor);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(PADDING);
                table.addCell(cell);

//                cell = new PdfPCell(new Phrase(getString(R.string.quantity)));
                cell = new PdfPCell();
                cell.addElement(buildCellElt(getString(R.string.qty)
                        , poppinsFont, Element.ALIGN_LEFT));
                cell.setBackgroundColor(tableHeadColor);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(PADDING);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);


                cell = new PdfPCell();
                cell.addElement(buildCellElt(getString(R.string.price)
                        , poppinsFont, Element.ALIGN_LEFT));
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
                            AppDataBase.getInstance(requireContext()).menuItemDao().
                                    getSpecificMenuItem(orderItems.get(i).getMenu_item_id()).getName()
                            , poppinsFont, Element.ALIGN_LEFT));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setPadding(PADDING);
                    table.addCell(cell);

//                    cell = new PdfPCell(new Phrase(orderItems.get(i).getQuantity()+""));
                    cell = new PdfPCell();
                    cell.addElement(buildCellElt(orderItems.get(i).getQuantity() +"", poppinsFont, Element.ALIGN_RIGHT));
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setPadding(PADDING);
                    table.addCell(cell);

//                    cell = new PdfPCell(new Phrase((orderItems.get(i).getPrice()*orderItems.get(i).getQuantity())+""));
                    cell = new PdfPCell();
                    cell.addElement(buildCellElt((orderItems.get(i).getQuantity() * orderItems.get(i).getPrice())+"", poppinsFont, Element.ALIGN_RIGHT));
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setPadding(PADDING);
                    table.addCell(cell);
                }

                cell = new PdfPCell();
                cell.addElement(buildCellElt(getString(R.string.total), poppinsFont, Element.ALIGN_LEFT));
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
                cell.addElement(buildCellElt(total+" "+getString(R.string.currency_cfa), poppinsBFont, Element.ALIGN_RIGHT));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(PADDING);
                table.addCell(cell);

                document.add(table);
                addLineSeperator(document);
                addNewItem(document, getString(R.string.thank_u_for_visit), Element.ALIGN_CENTER, poppinsBFont);
                addLineSpace(document);
                addLineSeperator(document);
                document.close();
                document.resetPageCount();
                Toast.makeText(requireActivity(), getString(R.string.ticket_generated) +" "+ dateFormat.format(Calendar.getInstance().getTime()) + ".pdf successfully generated at DOWNLOADS folder", Toast.LENGTH_LONG).show();
                doPrint(file);
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
        paragraph.setPaddingTop(16f);
        document.add(paragraph);
    }

    private void addImage(Document document, Image image, int alignment, Font font) throws DocumentException {
        Chunk chunk = new Chunk("", font);
        Paragraph paragraph =  new Paragraph(chunk);
        paragraph.add(image);
        paragraph.setAlignment(alignment);
        document.add(paragraph);
    }

    private void addLineSeperator(Document document) throws DocumentException{
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0,0,0, 70));
        addLineSpace(document);
        document.add(new Chunk(lineSeparator));
        addLineSpace(document);
    }

    private void addLineSpace(Document document) throws DocumentException {
        document.add(new Paragraph(""));
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
}
