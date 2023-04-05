package com.poupock.feussom.aladabusiness.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.discovery.DeviceInfo;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.google.android.material.snackbar.Snackbar;
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
import com.poupock.feussom.aladabusiness.callback.PrinterSelectedCallback;
import com.poupock.feussom.aladabusiness.database.AppDataBase;
import com.poupock.feussom.aladabusiness.databinding.DialogListBinding;
import com.poupock.feussom.aladabusiness.databinding.OrderDetailFragmentBinding;
import com.poupock.feussom.aladabusiness.ui.adapter.GuestTableAdapter;
import com.poupock.feussom.aladabusiness.ui.adapter.OrderAdapter;
import com.poupock.feussom.aladabusiness.ui.adapter.OrderItemAdapter;
import com.poupock.feussom.aladabusiness.ui.adapter.PrintAdapter;
import com.poupock.feussom.aladabusiness.ui.fragment.order.OrderViewModel;
import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.Constant;
import com.poupock.feussom.aladabusiness.util.GuestTable;
import com.poupock.feussom.aladabusiness.util.Order;
import com.poupock.feussom.aladabusiness.util.OrderItem;
import com.poupock.feussom.aladabusiness.util.ShowMsg;
import com.poupock.feussom.aladabusiness.util.SpnModelsItem;
import com.poupock.feussom.aladabusiness.util.User;
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

public class OrderDetailDialogFragment extends DialogFragment implements ReceiveListener {

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
    private PdfPCell cell;
    private Image imgReportLogo;

    BaseColor tableHeadColor = WebColors.getRGBColor("#000000");
    private Printer printer = null;
    private static final int DISCONNECT_INTERVAL = 500;

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


        initializeObject();

        binding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (User.getPrinterModelTarget(requireContext()) == null){
                    DialogFragment epsonDiscoverDialog = EpsonPrintersListDialogFragment.newInstance(1,
                            new PrinterSelectedCallback() {
                                @Override
                                public void onItemClickListener(DeviceInfo o, boolean isLong) {
                                    Log.i(TAG, "Printer selected!!");
                                    User.storePrinterModelTarget(o.getTarget(), requireContext());
                                }
                            });
                    epsonDiscoverDialog.show(getParentFragmentManager(), EpsonPrintersListDialogFragment.class.getSimpleName());
                }
                else {
                    if (!runPrintReceiptSequence()) {
                        updateButtonState(true);
                    }else{
                        Log.i(TAG, "Printing sequence FALSE");
                    }
                }
//                try {
//                    createPDF(viewModel.getOrderMutableLiveData().getValue().extractAllOrderedItems() ,
//                            viewModel.getOrderMutableLiveData().getValue());
////                    generatePDF(viewModel.getOrderMutableLiveData().getValue());
//                } catch (FileNotFoundException | DocumentException e) {
//                    Log.e(TAG, "The execption : "+e.toString());
//                }
            }
        });
    }

    private boolean runPrintReceiptSequence() {

        if (!createReceiptData(viewModel.getOrderMutableLiveData().getValue(),
                viewModel.getOrderMutableLiveData().getValue().extractAllOrderedItems())) {
            return false;
        }

        if (!printData()) {
            return false;
        }

        return true;
    }

    private boolean createReceiptData(Order order, List<OrderItem> orderItems) {
        String method = "";
//        Bitmap logoData = BitmapFactory.decodeResource(getResources(), R.drawable.store);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        Bitmap logoData = BitmapFactory.decodeFile(User.getPath(requireContext()), options);
        StringBuilder textData = new StringBuilder();
        final int barcodeWidth = 2;
        final int barcodeHeight = 100;

        if (printer == null) {
            return false;
        }

        try {

//            if(mDrawer.isChecked()) {
//                method = "addPulse";
//                printer.addPulse(Printer.PARAM_DEFAULT,
//                        Printer.PARAM_DEFAULT);
//            }

            method = "addTextAlign";
            printer.addTextAlign(Printer.ALIGN_CENTER);

            method = "addImage";
            printer.addImage(logoData, 0, 0,
                    logoData.getWidth(),
                    logoData.getHeight(),
                    Printer.COLOR_1,
                    Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT,
                    Printer.COMPRESS_AUTO);

            method = "addFeedLine";
            printer.addFeedLine(1);
            Business business = AppDataBase.getInstance(requireContext()).businessDao().getAllBusinesses().get(0);
            List<User> users = AppDataBase.getInstance(requireContext()).userDao().getAllUsers();
            String role = User.currentUser(requireContext()).getName();
            for(int i=0; i<users.size(); i++){
                if (users.get(i).getEmail().equals(User.currentUser(requireContext()).getEmail())){
//                binding.txtRole.setText();
                    role = User.currentUser(requireContext()).getEmail()+" - "+
                            requireContext().getResources().getStringArray(R.array.role_array)[Integer.parseInt(users.get(i).getRole_id())];
                    break;
                }
            }
            textData.append("").append(business.getName().toUpperCase()).append(" ").append(business.getPhone()).append("\n");
            textData.append(role.toUpperCase()).append("\n");
            textData.append("\n");
            textData.append(order.getCreated_at()).append("\n");
            textData.append(order.getCode()).append("\n");
            textData.append("------------------------------\n");
            method = "addText";
            printer.addText(textData.toString());
            textData.delete(0, textData.length());

            float total = 0;
            for (int i = 0; i < orderItems.size(); i++) {
                total = (float) (total + (orderItems.get(i).getPrice() * orderItems.get(i).getQuantity()));
                textData.append(orderItems.get(i).getQuantity()).append(" ")
                        .append(AppDataBase.getInstance(requireContext()).menuItemDao().
                            getSpecificMenuItem(orderItems.get(i).getMenu_item_id()).getName())
                        .append(" ")
                        .append((orderItems.get(i).getQuantity() * orderItems.get(i).getPrice()))
                        .append(getString(R.string.currency_cfa))
                        .append("\n");

            }

            textData.append("------------------------------\n");
            method = "addText";
            printer.addText(textData.toString());
            textData.delete(0, textData.length());

//            textData.append("TOTAL  ").append(total).append(getString(R.string.currency_cfa)).append("\n");
//            textData.append("TAX                      14.43\n");
            method = "addText";
            printer.addText(textData.toString());
            textData.delete(0, textData.length());

            method = "addTextSize";
            printer.addTextSize(2, 2);
            method = "addText";
            printer.addText("TOTAL "+total+" "+ getString(R.string.currency_cfa)+"\n");
            method = "addTextSize";
            printer.addTextSize(1, 1);
            method = "addFeedLine";
            printer.addFeedLine(1);

//            textData.append("CASH                    200.00\n");
//            textData.append("CHANGE                   25.19\n");
            textData.append("------------------------------\n");
            method = "addText";
            printer.addText(textData.toString());
            textData.delete(0, textData.length());

//            textData.append("Purchased item total number\n");
            textData.append(getString(R.string.thank_u_for_visit)).append("!\n");
//            textData.append("With Preferred Saving Card\n");
            method = "addText";
            printer.addText(textData.toString());
            textData.delete(0, textData.length());
            method = "addFeedLine";
            printer.addFeedLine(2);

            method = "addBarcode";
            printer.addBarcode(order.getId()+"",
                    Printer.BARCODE_CODE39,
                    Printer.HRI_BELOW,
                    Printer.FONT_A,
                    barcodeWidth,
                    barcodeHeight);

            method = "addCut";
            printer.addCut(Printer.CUT_FEED);
        }
        catch (Exception e) {
            printer.clearCommandBuffer();
            ShowMsg.showException(e, method, requireContext());
            return false;
        }

        textData = null;

        return true;
    }

    private boolean printData() {
        if (printer == null) {
            return false;
        }

        if (!connectPrinter()) {
            printer.clearCommandBuffer();
            return false;
        }

        try {
            printer.sendData(Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            printer.clearCommandBuffer();
            ShowMsg.showException(e, "sendData", requireContext());
            try {
                printer.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }

        return true;
    }

    private boolean initializeObject() {
        try {

            printer = new Printer(Printer.TM_M30,
                    Printer.MODEL_ANK,
                    requireContext());
        }
        catch (Exception e) {
            ShowMsg.showException(e, "Printer", requireContext());
            return false;
        }

        printer.setReceiveEventListener(this);

        return true;
    }

    private void finalizeObject() {
        if (printer == null) {
            return;
        }

        printer.setReceiveEventListener(null);

        printer = null;
    }

    private boolean connectPrinter() {
        if (printer == null) {
            return false;
        }

        try {
            printer.connect(User.getPrinterModelTarget(requireContext()), Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "connect", requireContext());
            return false;
        }

        return true;
    }

    private void disconnectPrinter() {
        if (printer == null) {
            return;
        }

        while (true) {
            try {
                printer.disconnect();
                break;
            } catch (final Exception e) {
                if (e instanceof Epos2Exception) {
                    //Note: If printer is processing such as printing and so on, the disconnect API returns ERR_PROCESSING.
                    if (((Epos2Exception) e).getErrorStatus() == Epos2Exception.ERR_PROCESSING) {
                        try {
                            Thread.sleep(DISCONNECT_INTERVAL);
                        } catch (Exception ex) {
                        }
                    }else{
                        requireActivity().runOnUiThread(new Runnable() {
                            public synchronized void run() {
                                ShowMsg.showException(e, "disconnect", requireContext());
                            }
                        });
                        break;
                    }
                }else{
                    requireActivity().runOnUiThread(new Runnable() {
                        public synchronized void run() {
                            ShowMsg.showException(e, "disconnect", requireContext());
                        }
                    });
                    break;
                }
            }
        }

        printer.clearCommandBuffer();
    }

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

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    @SuppressLint("SimpleDateFormat")
    public void createPDF(List<OrderItem> orderItems, Order order) throws FileNotFoundException, DocumentException {

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
            document.addAuthor(getString(R.string.app_name));
            document.addCreator(getString(R.string.app_name));

            BaseFont poppins = BaseFont.createFont("res/font/poppins_regular.ttf", "UTF-8", BaseFont.EMBEDDED);
            Font poppinsFont = new Font(poppins, 2.0f, Font.NORMAL, BaseColor.BLACK);
            Font poppinsWhFont = new Font(poppins, 2.0f, Font.NORMAL, BaseColor.WHITE);
            Font poppinsBFont = new Font(poppins, 2.0f, Font.BOLD, BaseColor.BLACK);


            try {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                Bitmap bitmap = BitmapFactory.decodeFile(User.getPath(requireContext()), options);

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
                cell.addElement(buildCellElt(AppDataBase.getInstance(requireContext()).businessDao().getAllBusinesses().get(0).getName(),
                        poppinsBFont, Element.ALIGN_CENTER));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(PADDING);
                headTable.addCell(cell);

                cell = new PdfPCell();
                cell.addElement(buildCellElt(getString(R.string.ordered_on),
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
                cell.addElement(buildCellElt(getString(R.string.items)
                        , poppinsWhFont, Element.ALIGN_LEFT));
                cell.setBackgroundColor(tableHeadColor);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(PADDING);
                table.addCell(cell);

                cell = new PdfPCell();
                cell.addElement(buildCellElt(getString(R.string.qty)
                        , poppinsWhFont, Element.ALIGN_LEFT));
                cell.setBackgroundColor(tableHeadColor);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(PADDING);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cell);


                cell = new PdfPCell();
                cell.addElement(buildCellElt(getString(R.string.price)
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
                            AppDataBase.getInstance(requireContext()).menuItemDao().
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
                addLineSeperator(document);
                Toast.makeText(requireActivity(), getString(R.string.ticket_generated) +" "+ dateFormat.format(Calendar.getInstance().getTime()) , Toast.LENGTH_LONG).show();
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

    @Override
    public void onPtrReceive(Printer printer, final int code, final PrinterStatusInfo status, final String printJobId) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public synchronized void run() {
                ShowMsg.showResult(code, makeErrorMessage(status), requireContext());

                displayPrinterWarnings(status);

                updateButtonState(true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }

    private void updateButtonState(boolean state) {
        binding.btnPay.setEnabled(state);
    }

    private void displayPrinterWarnings(PrinterStatusInfo status) {
        String warningsMsg = "";

        if (status == null) {
            return;
        }

        if (status.getPaper() == Printer.PAPER_NEAR_END) {
            warningsMsg += getString(R.string.handlingmsg_warn_receipt_near_end);
        }

        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_1) {
            warningsMsg += getString(R.string.handlingmsg_warn_battery_near_end);
        }

        if (!warningsMsg.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle(R.string.printer_warning);
            builder.setMessage(warningsMsg)
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dismiss();
                        }
                    }).show();
        }
    }


    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += getString(R.string.handlingmsg_err_autocutter);
            msg += getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += getString(R.string.handlingmsg_err_overheat);
                msg += getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += getString(R.string.handlingmsg_err_battery_real_end);
        }
        if (status.getRemovalWaiting() == Printer.REMOVAL_WAIT_PAPER) {
            msg += getString(R.string.handlingmsg_err_wait_removal);
        }
        if(status.getUnrecoverError() == Printer.HIGH_VOLTAGE_ERR ||
                status.getUnrecoverError() == Printer.LOW_VOLTAGE_ERR) {
            msg += getString(R.string.handlingmsg_err_voltage);
        }

        return msg;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        finalizeObject();
    }
}
