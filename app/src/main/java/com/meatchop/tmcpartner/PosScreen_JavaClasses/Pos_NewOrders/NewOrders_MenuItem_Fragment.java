package com.meatchop.tmcpartner.PosScreen_JavaClasses.Pos_NewOrders;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.meatchop.tmcpartner.AlertDialogClass;
import com.meatchop.tmcpartner.Constants;
import com.meatchop.tmcpartner.Printer_POJO_Class;
import com.meatchop.tmcpartner.R;
import com.pos.printer.PrinterFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;
import static com.meatchop.tmcpartner.Constants.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewOrders_MenuItem_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewOrders_MenuItem_Fragment extends Fragment {
    private RecyclerView recyclerView;
    ListView listview;
    boolean isdataFetched = false;
    List<Modal_NewOrderItems> Category_List;

    public static List<Modal_NewOrderItems> menuItem;
    public static List<Modal_NewOrderItems> completemenuItem;
    Context mContext;
    public TextView total_item_Rs_text_widget,taxes_and_Charges_rs_text_widget,total_Rs_to_Pay_text_widget;
    Button procced_to_pay_widget,discount;
    EditText mobileNo_Edit_widget;
    String Currenttime,MenuItems,FormattedTime,CurrentDate,formattedDate,CurrentDay,OrderTypefromSpinner;
    String portName = "USB";
    int portSettings=0,totalGstAmount=0;
    double new_total_amount,old_total_Amount=0,sub_total;
    double new_taxes_and_charges_Amount,old_taxes_and_charges_Amount=0;
    double new_to_pay_Amount,old_to_pay_Amount=0;
    public static HashMap<String,Modal_NewOrderItems> cartItem_hashmap = new HashMap();
    public static List<String> cart_Item_List;
    static Adapter_CartItem_Recyclerview adapter_cartItem_recyclerview;
    static Adapter_CartItem_Listview adapter_cartItem_listview;
    TextView discount_Edit_widget,discount_rs_text_widget;
    Button discount_button_widget;
    String discountAmount ="" ;
    String finaltoPayAmount;
    int new_totalAmount_withGst;
    int netTotaL;
    LinearLayout loadingPanel,loadingpanelmask;
    private  boolean isOrderDetailsMethodCalled =false;
    private  boolean isOrderTrackingDetailsMethodCalled =false;
    private  boolean isPaymentDetailsMethodCalled =false;

    boolean isproceedtoPay_Clicked =false, ispaymentMode_Clicked =false,isPrintedSecondTime=false;
    Spinner orderTypeSpinner;


    public NewOrders_MenuItem_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewOrders_MenuItem_Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewOrders_MenuItem_Fragment newInstance(String data) {
        Bundle args = new Bundle();
        args.putString("menuItem", data);

        NewOrders_MenuItem_Fragment fragment = new NewOrders_MenuItem_Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public String getData() {

        return getArguments().getString("menuItem");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity().getWindow().getContext();
        cart_Item_List = new ArrayList<>();
        menuItem = new ArrayList<>();
        Category_List = new ArrayList<>();

        completemenuItem = new ArrayList<>();
        cart_Item_List.clear();
        cartItem_hashmap.clear();


        Log.d(TAG, "starting: ");
    }

    @Override
    public void onStart() {
        super.onStart();

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingpanelmask = view.findViewById(R.id.loadingpanelmask);
        loadingPanel = view.findViewById(R.id.loadingPanel);
        orderTypeSpinner = view.findViewById(R.id.orderTypeSpinner);
        procced_to_pay_widget = view.findViewById(R.id.procced_to_pay_widget);
        mobileNo_Edit_widget = view.findViewById(R.id.Customer_mobileNo_Edit_widget);
        total_item_Rs_text_widget = view.findViewById(R.id.total_amount_text_widget);
        total_Rs_to_Pay_text_widget = view.findViewById(R.id.total_Rs_to_Pay_text_widget);
        taxes_and_Charges_rs_text_widget = view.findViewById(R.id.taxes_and_Charges_rs_text_widget);
        discount_Edit_widget  = view.findViewById(R.id.discount_Edit_widget);
        discount_button_widget = view.findViewById(R.id.discount_widget);
        discount_rs_text_widget = view.findViewById(R.id.discount_rs_text_widget);


        addDatatoOrderTypeSpinner();


        orderTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OrderTypefromSpinner = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected: " + OrderTypefromSpinner,          Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
                OrderTypefromSpinner = "Store Pickup";
            }
        });


        loadingpanelmask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



        discount_button_widget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (cart_Item_List.size() > 0 && cartItem_hashmap.size() > 0) {
                        if ((!total_item_Rs_text_widget.getText().toString().equals("0")) || (!total_Rs_to_Pay_text_widget.getText().toString().equals("0"))||(!total_item_Rs_text_widget.getText().toString().equals("0.00")) || (!total_Rs_to_Pay_text_widget.getText().toString().equals("0.00"))) {

                            discountAmount = discount_Edit_widget.getText().toString();
                            if(!discountAmount.equals("")){
                                double discountAmountdouble = Double.parseDouble(discountAmount);
                                double toPayAmt = Double.parseDouble(finaltoPayAmount);
                                toPayAmt = toPayAmt - discountAmountdouble;
                                int toPayAmountInt = (int) Math.ceil((toPayAmt));


                                total_Rs_to_Pay_text_widget.setText(String.valueOf(toPayAmountInt));
                            }
                        }
                    }
                    else{
                        Toast.makeText(mContext,"Can't Apply discount when Cart is Empty",Toast.LENGTH_SHORT).show();
                    }
                }

                catch(Exception e ){
                        discountAmount = "0";

                        e.printStackTrace();
                    }
                    discount_rs_text_widget.setText(discountAmount);
                }


        });




        procced_to_pay_widget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    if (mobileNo_Edit_widget.getText().toString().length() == 10) {
                        showProgressBar(true);



                        if ((!total_item_Rs_text_widget.getText().toString().equals("0")) && (!total_Rs_to_Pay_text_widget.getText().toString().equals("0"))) {
                            if (checkforBarcodeInCart("empty")) {
                                NewOrders_MenuItem_Fragment.cart_Item_List.remove("empty");

                                NewOrders_MenuItem_Fragment.cartItem_hashmap.remove("empty");
                            }
                            Log.i(TAG, "call adapter cart_Item " + cart_Item_List.size());


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                            try {
                            Dialog dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.select_payment_mode_layout);
                            dialog.setTitle("Select the Payment Mode ");
                            dialog.setCanceledOnTouchOutside(true);

                            Button via_cash = (Button) dialog.findViewById(R.id.via_cash);
                            Button via_card = (Button) dialog.findViewById(R.id.via_card);
                            Button via_upi = (Button) dialog.findViewById(R.id.via_upi);

                            Currenttime = getDate_and_time();
                            Log.d(TAG, "Currenttime: " + Currenttime);

                            long sTime = System.currentTimeMillis();
                            Log.i(TAG, "date and time " + sTime);



                            via_card.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    PlaceOrdersinDatabaseaAndPrintRecipt("CARD",sTime,Currenttime,cart_Item_List);

                                    dialog.cancel();


                                }
                            });


                            via_cash.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    PlaceOrdersinDatabaseaAndPrintRecipt("CASH ON DELIVERY", sTime, Currenttime, cart_Item_List);

                                    dialog.cancel();


                                }
                            });


                            via_upi.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    PlaceOrdersinDatabaseaAndPrintRecipt("UPI", sTime, Currenttime, cart_Item_List);

                                    dialog.cancel();



                                }
                            });


                            dialog.show();
                            showProgressBar(false);

                                   }
                                    catch (WindowManager.BadTokenException e) {
                                        showProgressBar(false);

                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            showProgressBar(false);

                            AlertDialogClass.showDialog(getActivity(), R.string.Cant_place_order);

                        }

                    } else {
                        AlertDialogClass.showDialog(getActivity(), R.string.Enter_the_mobile_no_text);

                    }


            }
        });





    }

    private void addDatatoOrderTypeSpinner() {

        String[] ordertype=getResources().getStringArray(R.array.OrderType);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, ordertype);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        orderTypeSpinner.setAdapter(arrayAdapter);



    }

    private void PlaceOrdersinDatabaseaAndPrintRecipt(String paymentMode, long sTime, String currenttime, List<String> cart_Item_list) {
        showProgressBar(true);
        if (ispaymentMode_Clicked) {
            return;
        }
        else {
            ispaymentMode_Clicked = true;
            if (!isOrderDetailsMethodCalled) {

                PlaceOrder_in_OrderDetails(NewOrders_MenuItem_Fragment.cart_Item_List, paymentMode, sTime);
            }
            if (!isOrderTrackingDetailsMethodCalled) {

                PlaceOrder_in_OrderTrackingDetails(sTime, currenttime);
            }
        }


    }


    private boolean checkforBarcodeInCart(String itemUniquecode) {
        String search = itemUniquecode;
        for(String str: NewOrders_MenuItem_Fragment.cart_Item_List) {
            if(str.trim().contains(search))
                return true;
        }
        return false;
    }

    private  void printRecipt(String userMobile, String tokenno, String itemTotalwithoutGst, String totaltaxAmount, String payableAmount, String orderid, List<String> cart_item_list, HashMap<String, Modal_NewOrderItems> cart_Item_hashmap, String payment_mode, String discountAmountt) {

            Printer_POJO_Class[] Printer_POJO_ClassArray = new Printer_POJO_Class[cart_Item_List.size()];
            double oldSavedAmount = 0;
        String CouponDiscount ="0";
        for (int i = 0; i < cart_item_list.size(); i++) {
                double savedAmount;
                String itemUniqueCode = cart_item_list.get(i);
                Modal_NewOrderItems modal_newOrderItems = cart_Item_hashmap.get(itemUniqueCode);
                String itemName = String.valueOf(modal_newOrderItems.getItemname());
                int indexofbraces = itemName.indexOf("(");
                if (indexofbraces >= 0) {
                    itemName = itemName.substring(0, indexofbraces);

                }
                if (itemName.length() > 21) {
                    itemName = itemName.substring(0, 21);
                    itemName = itemName + "...";
                }
                savedAmount = Double.parseDouble(modal_newOrderItems.getSavedAmount());
                oldSavedAmount = savedAmount + oldSavedAmount;
                String Gst = modal_newOrderItems.getGstAmount();
                String subtotal = modal_newOrderItems.getSubTotal_perItem();
                String quantity = modal_newOrderItems.getQuantity();
                String price = modal_newOrderItems.getItemFinalPrice();
                String weight = modal_newOrderItems.getItemFinalWeight();
                Printer_POJO_ClassArray[i] = new Printer_POJO_Class(quantity, orderid, itemName, weight, price, "0.00", Gst, subtotal);

            }

        Printer_POJO_Class Printer_POJO_ClassArraytotal = new Printer_POJO_Class(itemTotalwithoutGst, discountAmountt, totaltaxAmount, payableAmount, oldSavedAmount);
            PrinterFunctions.PortDiscovery(portName, portSettings);
            PrinterFunctions.OpenCashDrawer(portName, portSettings, 0, 4);

            // PrinterFunctions.OpenPort( portName, portSettings);
            //    PrinterFunctions.CheckStatus( portName, portSettings,2);
            PrinterFunctions.SelectPrintMode(portName, portSettings, 0);
            PrinterFunctions.SetLineSpacing(portName, portSettings, 180);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 0, 0, 2, 1, 0, 1, "The Meat Chop" + "\n");

            PrinterFunctions.SetLineSpacing(portName, portSettings, 60);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 0, 0, 0, 0, 0, 1, "Fresh Meat and SeaFood" + "\n");

            PrinterFunctions.SetLineSpacing(portName, portSettings, 60);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 0, 0, 0, 0, 0, 1, "No 57, Rajendra Prasad Road," + "\n");


            PrinterFunctions.SetLineSpacing(portName, portSettings, 60);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 0, 0, 0, 0, 0, 1, "Hasthinapuram,Chromepet" + "\n");


            PrinterFunctions.SetLineSpacing(portName, portSettings, 60);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 0, 0, 0, 0, 0, 1, "Chennai-600044" + "\n");


            PrinterFunctions.SetLineSpacing(portName, portSettings, 80);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 0, 0, 0, 0, 0, 1, "+914445568499" + "\n");


            PrinterFunctions.SetLineSpacing(portName, portSettings, 80);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 1, 0, 0, 0, 0, 1, "GSTIN :33AAJCC0055D1Z9" + "\n");


            PrinterFunctions.SetLineSpacing(portName, portSettings, 80);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 0, 0, 0, 0, 0, 1, Currenttime + "\n");


            PrinterFunctions.SetLineSpacing(portName, portSettings, 80);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 0, 0, 0, 0, 0, 1, "# " + orderid + "\n");


            PrinterFunctions.SetLineSpacing(portName, portSettings, 40);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 1, 0, 0, 0, 30, 0, "----------------------------------------" + "\n");

            PrinterFunctions.SetLineSpacing(portName, portSettings, 80);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 1, 0, 0, 0, 30, 0, "ITEM NAME * QTY" + "\n");

            PrinterFunctions.SetLineSpacing(portName, portSettings, 70);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 1, 0, 0, 0, 30, 0, "RATE                GST         SUBTOTAL" + "\n");

            PrinterFunctions.SetLineSpacing(portName, portSettings, 60);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 1, 0, 0, 0, 30, 0, "----------------------------------------" + "\n");
            for (int i = 0; i < Printer_POJO_ClassArray.length; i++) {

                PrinterFunctions.SetLineSpacing(portName, portSettings, 80);
                PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
                String itemrate, Gst, subtotal;
                itemrate = "Rs." + Printer_POJO_ClassArray[i].getItemRate();
                Gst = "Rs." + Printer_POJO_ClassArray[i].getGST();
                subtotal = "Rs." + Printer_POJO_ClassArray[i].getSubTotal();
                if (itemrate.length() == 4) {
                    //14spaces
                    itemrate = itemrate + "              ";
                }
                if (itemrate.length() == 5) {
                    //13spaces
                    itemrate = itemrate + "             ";
                }
                if (itemrate.length() == 6) {
                    //12spaces
                    itemrate = itemrate + "            ";
                }
                if (itemrate.length() == 7) {
                    //11spaces
                    itemrate = itemrate + "           ";
                }
                if (itemrate.length() == 8) {
                    //10spaces
                    itemrate = itemrate + "          ";
                }
                if (itemrate.length() == 9) {
                    //9spaces
                    itemrate = itemrate + "         ";
                }
                if (itemrate.length() == 10) {
                    //8spaces
                    itemrate = itemrate + "        ";
                }
                if (itemrate.length() == 11) {
                    //7spaces
                    itemrate = itemrate + "       ";
                }
                if (itemrate.length() == 12) {
                    //6spaces
                    itemrate = itemrate + "      ";
                }
                if (itemrate.length() == 13) {
                    //5spaces
                    itemrate = itemrate + "     ";
                }
                if (itemrate.length() == 14) {
                    //4spaces
                    itemrate = itemrate + "    ";
                }
                if (itemrate.length() == 15) {
                    //3spaces
                    itemrate = itemrate + "   ";
                }
                if (itemrate.length() == 16) {
                    //2spaces
                    itemrate = itemrate + "  ";
                }
                if (itemrate.length() == 17) {
                    //1spaces
                    itemrate = itemrate + " ";
                }
                if (itemrate.length() == 18) {
                    //1spaces
                    itemrate = itemrate + "";
                }


                if (Gst.length() == 7) {
                    //1spaces
                    Gst = Gst + " ";
                }
                if (Gst.length() == 8) {
                    //0space
                    Gst = Gst + "";
                }
                if (Gst.length() == 9) {
                    //no space
                    Gst = Gst;
                }
                if (subtotal.length() == 4) {
                    //5spaces
                    subtotal = "      " + subtotal;
                }
                if (subtotal.length() == 5) {
                    //6spaces
                    subtotal = "      " + subtotal;
                }
                if (subtotal.length() == 6) {
                    //8spaces
                    subtotal = "        " + subtotal;
                }
                if (subtotal.length() == 7) {
                    //7spaces
                    subtotal = "       " + subtotal;
                }
                if (subtotal.length() == 8) {
                    //6spaces
                    subtotal = "      " + subtotal;
                }
                if (subtotal.length() == 9) {
                    //5spaces
                    subtotal = "     " + subtotal;
                }
                if (subtotal.length() == 10) {
                    //4spaces
                    subtotal = "    " + subtotal;
                }
                if (subtotal.length() == 11) {
                    //3spaces
                    subtotal = "   " + subtotal;
                }
                if (subtotal.length() == 12) {
                    //2spaces
                    subtotal = "  " + subtotal;
                }
                if (subtotal.length() == 13) {
                    //1spaces
                    subtotal = " " + subtotal;
                }
                if (subtotal.length() == 14) {
                    //no space
                    subtotal = "" + subtotal;
                }


                PrinterFunctions.PrintText(portName, portSettings, 0, 0, 0, 0, 0, 0, 30, 0, Printer_POJO_ClassArray[i].getItemName() + "  *  " + Printer_POJO_ClassArray[i].getItemWeight() + "(" + Printer_POJO_ClassArray[i].getQuantity() + ")" + "\n");

                PrinterFunctions.SetLineSpacing(portName, portSettings, 80);
                PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
                PrinterFunctions.PrintText(portName, portSettings, 0, 0, 0, 0, 0, 0, 30, 0, itemrate + Gst + subtotal + "\n\n");
            }

            PrinterFunctions.SetLineSpacing(portName, portSettings, 60);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 1, 0, 0, 0, 30, 0, "----------------------------------------" + "\n");

            String totalRate = "Rs." + Printer_POJO_ClassArraytotal.getTotalRate();
            String totalGst = "Rs." + Printer_POJO_ClassArraytotal.getTotalGST();
            String totalSubtotal = "Rs." + finaltoPayAmount;
            if (totalRate.length() == 7) {
                //10spaces
                totalRate = totalRate + "          ";
            }
            if (totalRate.length() == 8) {
                //9spaces
                totalRate = totalRate + "         ";
            }
            if (totalRate.length() == 9) {
                //8spaces
                totalRate = totalRate + "        ";
            }
            if (totalRate.length() == 10) {
                //7spaces
                totalRate = totalRate + "       ";
            }
            if (totalRate.length() == 11) {
                //6spaces
                totalRate = totalRate + "      ";
            }
            if (totalRate.length() == 12) {
                //5spaces
                totalRate = totalRate + "     ";
            }
            if (totalRate.length() == 13) {
                //4spaces
                totalRate = totalRate + "    ";
            }

            if (totalGst.length() == 7) {
                //1spaces
                totalGst = totalGst + " ";
            }
            if (totalGst.length() == 8) {
                //0space
                totalGst = totalGst + "";
            }
            if (totalGst.length() == 9) {
                //no space
                totalGst = totalGst;
            }

            if (totalSubtotal.length() == 6) {
                //8spaces
                totalSubtotal = "        " + totalSubtotal;
            }
            if (totalSubtotal.length() == 7) {
                //7spaces
                totalSubtotal = "       " + totalSubtotal;
            }
            if (totalSubtotal.length() == 8) {
                //6spaces
                totalSubtotal = "      " + totalSubtotal;
            }
            if (totalSubtotal.length() == 9) {
                //5spaces
                totalSubtotal = "     " + totalSubtotal;
            }
            if (totalSubtotal.length() == 10) {
                //4spaces
                totalSubtotal = "    " + totalSubtotal;
            }


            PrinterFunctions.SetLineSpacing(portName, portSettings, 60);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 1, 0, 0, 0, 30, 0, totalRate + totalGst + totalSubtotal + "\n");

            PrinterFunctions.SetLineSpacing(portName, portSettings, 50);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 1, 0, 0, 0, 30, 0, "----------------------------------------" + "\n");
/*

        PrinterFunctions.SetLineSpacing(portName, portSettings, 50);
        PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
        String SavedAmount = "You just saved Rs."+" on these items"+String.valueOf(Printer_POJO_ClassArraytotal.getOldSavedAmount());

        PrinterFunctions. PrintText(portName,portSettings,0, 0,1,0,0, 0,30,1,SavedAmount+"\n");


 */
        CouponDiscount = "0";

         CouponDiscount = Printer_POJO_ClassArraytotal.getTotaldiscount();

        if(!CouponDiscount.equals("0")) {
            CouponDiscount = "Rs. " + CouponDiscount + ".00";

            if ((!CouponDiscount.equals("Rs.0.0")) && (!CouponDiscount.equals("Rs.0")) && (!CouponDiscount.equals("Rs.0.00")) && (CouponDiscount != (null)) && (!CouponDiscount.equals("")) && (!CouponDiscount.equals("Rs. .00")) && (!CouponDiscount.equals("Rs..00"))) {

                if (CouponDiscount.length() == 4) {
                    //20spaces
                    //NEW TOTAL =4
                    CouponDiscount = "Discount Amount                   " + CouponDiscount;
                }
                if (CouponDiscount.length() == 5) {
                    //21spaces
                    //NEW TOTAL =5
                    CouponDiscount = "Discount Amount                 " + CouponDiscount;
                }
                if (CouponDiscount.length() == 6) {
                    //20spaces
                    //NEW TOTAL =6
                    CouponDiscount = "Discount Amount                " + CouponDiscount;
                }

                if (CouponDiscount.length() == 7) {
                    //19spaces
                    //NEW TOTAL =7
                    CouponDiscount = "Discount Amount               " + CouponDiscount;
                }
                if (CouponDiscount.length() == 8) {
                    //18spaces
                    //NEW TOTAL =8
                    CouponDiscount = " Discount Amount              " + CouponDiscount;
                }
                if (CouponDiscount.length() == 9) {
                    //17spaces
                    //NEW TOTAL =9
                    CouponDiscount = " Discount Amount             " + CouponDiscount;
                }
                if (CouponDiscount.length() == 10) {
                    //16spaces
                    //NEW TOTAL =9
                    CouponDiscount = " Discount Amount            " + CouponDiscount;
                }
                if (CouponDiscount.length() == 11) {
                    //15spaces
                    //NEW TOTAL =9
                    CouponDiscount = "Discount Amount            " + CouponDiscount;
                }
                if (CouponDiscount.length() == 12) {
                    //14spaces
                    //NEW TOTAL =9
                    CouponDiscount = "Discount Amount           " + CouponDiscount;
                }

                if (CouponDiscount.length() == 13) {
                    //13spaces
                    //NEW TOTAL =9
                    CouponDiscount = "Discount Amount           " + CouponDiscount;

                }


                PrinterFunctions.PrintText(portName, portSettings, 0, 0, 1, 0, 0, 0, 0, 1, CouponDiscount + "\n");


                PrinterFunctions.SetLineSpacing(portName, portSettings, 50);
                PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
                PrinterFunctions.PrintText(portName, portSettings, 0, 0, 1, 0, 0, 0, 30, 0, "----------------------------------------" + "\n");

            }
        }

            PrinterFunctions.SetLineSpacing(portName, portSettings, 50);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            String NetTotal = Printer_POJO_ClassArraytotal.getTotalsubtotal();
            try {
                if (NetTotal.contains(".")) {
                    int netTotaLint =Integer.parseInt(NetTotal);
                    int netdiscountAmountint = Integer.parseInt(discountAmountt);
                    netTotaL = netTotaLint - netdiscountAmountint;

                } else {

                    double nettotalDouble = Double.parseDouble(NetTotal);
                    double discountAmountDouble = Double.parseDouble(discountAmountt);
                    netTotaL =( Integer.parseInt(String.valueOf(nettotalDouble))) - (Integer.parseInt(String.valueOf(discountAmountDouble)));

                }
                NetTotal =  String.valueOf(netTotaL);

            }
            catch (Exception e ){
             e.printStackTrace();
                 NetTotal = Printer_POJO_ClassArraytotal.getTotalsubtotal();

            }
        if (NetTotal.length() >6) {

            if (NetTotal.length() == 7) {
                //24spaces
                //NEW TOTAL =9
                NetTotal = " NET TOTAL                       Rs. " + NetTotal;
            }
            if (NetTotal.length() == 8) {
                //23spaces
                //NEW TOTAL =9
                NetTotal = "  NET TOTAL                       Rs. " + NetTotal;
            }
            if (NetTotal.length() == 9) {
                //22spaces
                //NEW TOTAL =9
                NetTotal = "  NET TOTAL                      Rs. " + NetTotal;
            }
            if (NetTotal.length() == 10) {
                //21spaces
                //NEW TOTAL =9
                NetTotal = "  NET TOTAL                    Rs. " + NetTotal;
            }
            if (NetTotal.length() == 11) {
                //20spaces
                //NEW TOTAL =9
                NetTotal = "  NET TOTAL                   Rs. " + NetTotal;
            }
            if (NetTotal.length() == 12) {
                //19spaces
                //NEW TOTAL =9
                NetTotal = "  NET TOTAL                  Rs. " + NetTotal;
            }
        }
        else{
            NetTotal = " NET TOTAL                    Rs.  " + NetTotal;

        }

            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 1, 0, 0, 0, 0, 1, NetTotal + "\n");

            PrinterFunctions.SetLineSpacing(portName, portSettings, 60);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 1, 0, 0, 0, 30, 0, "----------------------------------------" + "\n");


            PrinterFunctions.SetLineSpacing(portName, portSettings, 60);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 1, 0, 0, 0, 30, 0, "Payment Mode: ");


            PrinterFunctions.SetLineSpacing(portName, portSettings, 90);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 0, 0, 0, 0, 30, 0, payment_mode + "\n");


            PrinterFunctions.SetLineSpacing(portName, portSettings, 60);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 1, 0, 0, 0, 30, 0, "MobileNo : ");


            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 0, 0, 0, 0, 0, 0, userMobile + "           "+ "\n");
/*
            PrinterFunctions.SetLineSpacing(portName, portSettings, 120);
            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 1, 0, 0, 0, 0, 30, "ID : ");


            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 0, 0, 0, 0, 0, 50, tokenno + "\n");


 */


            PrinterFunctions.SelectCharacterFont(portName, portSettings, 0);
            PrinterFunctions.PrintText(portName, portSettings, 0, 0, 1, 0, 0, 0, 0, 1,  "\n"+"Thank you for choosing us !!!  " + "\n");


            PrinterFunctions.PreformCut(portName, portSettings, 1);
            //  PrinterFunctions.PrintSampleReceipt(portName,portSettings);
            Log.i("tag", "printer Log    " + PrinterFunctions.PortDiscovery(portName, portSettings));

            Log.i("tag", "printer Log    " + PrinterFunctions.OpenPort(portName, portSettings));

            Log.i("tag", "printer Log    " + PrinterFunctions.CheckStatus(portName, portSettings, 2));

               if(!isPrintedSecondTime) {
                   showProgressBar(false);

                   openPrintAgainDialog(userMobile, tokenno, itemTotalwithoutGst, totaltaxAmount, payableAmount, orderid, cart_Item_List, cart_Item_hashmap, payment_mode);

                }


                else{
                    cart_Item_List.clear();
                    cart_Item_hashmap.clear();
                    cart_item_list.clear();
                   cartItem_hashmap.clear();
                    ispaymentMode_Clicked=false;
                    isOrderDetailsMethodCalled = false;

                    isPaymentDetailsMethodCalled=false;
                    isOrderTrackingDetailsMethodCalled=false;
                   new_to_pay_Amount = 0;
                    old_taxes_and_charges_Amount = 0;
                    old_total_Amount = 0;
                    createEmptyRowInListView("empty");
                    CallAdapter();
                    discountAmount ="0";

                   CouponDiscount = "0";
                    discount_Edit_widget .setText("0");
                    finaltoPayAmount = "0";
                    discount_rs_text_widget .setText(discountAmount);

                    total_item_Rs_text_widget.setText(String.valueOf(old_total_Amount));
                    taxes_and_Charges_rs_text_widget.setText(String.valueOf((old_taxes_and_charges_Amount)));
                    total_Rs_to_Pay_text_widget.setText(String.valueOf(new_to_pay_Amount));

                    mobileNo_Edit_widget.setText("");
                    isPrintedSecondTime=false;
                   showProgressBar(false);

               }
    }

    private void openPrintAgainDialog(String userMobile, String tokenno, String itemTotalwithoutGst, String totaltaxAmount, String payableAmount, String orderid, List<String> cart_Item_List, HashMap<String, Modal_NewOrderItems> cartItem_hashmap, String payment_mode) {



        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.print_again);
                    dialog.setTitle("Do you Want to Print Again !!!! ");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);

                    Button printAgain = (Button) dialog.findViewById(R.id.printAgain);


                    printAgain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                          isPrintedSecondTime=true;
                            showProgressBar(true);


                            printRecipt(userMobile, tokenno, itemTotalwithoutGst, totaltaxAmount, payableAmount, orderid, cart_Item_List, cartItem_hashmap, payment_mode, discountAmount);
                            dialog.cancel();
                        }
                    });


                    dialog.show();
                }
                catch (WindowManager.BadTokenException e) {
                    e.printStackTrace();
                }
            }
        });


      /*  new TMCAlertDialogClass(mContext, R.string.app_name, R.string.Exit_Instruction,
                R.string.Yes_Text, R.string.No_Text,
                new TMCAlertDialogClass.AlertListener() {
                    @Override
                    public void onYes() {
                        printRecipt(userMobile, tokenno, itemTotalwithoutGst, totaltaxAmount, payableAmount, orderid, cart_Item_List, cartItem_hashmap, payment_mode);

                    }

                    @Override
                    public void onNo() {
                        cart_Item_List.clear();
                        cartItem_hashmap.clear();

                        new_to_pay_Amount = 0;
                        old_taxes_and_charges_Amount = 0;
                        old_total_Amount = 0;
                        createEmptyRowInListView("empty");
                        isproceedtoPay_Clicked = false;
                        ispaymentMode_Clicked = false;
                        CallAdapter();

                        total_item_Rs_text_widget.setText(String.valueOf(old_total_Amount));
                        taxes_and_Charges_rs_text_widget.setText(String.valueOf((old_taxes_and_charges_Amount)));
                        total_Rs_to_Pay_text_widget.setText(String.valueOf(new_to_pay_Amount));

                        mobileNo_Edit_widget.setText("");
                    }
                });

       */
    }

    void CallAdapter() {
        Log.e(TAG, "AdapterCalled  ");


      //  adapter_cartItem_listview= new Adapter_CartItem_Listview(mContext,cartItem_hashmap, MenuItems,NewOrders_MenuItem_Fragment.this);
      //  listview.setAdapter(adapter_cartItem_listview);
        adapter_cartItem_recyclerview = new Adapter_CartItem_Recyclerview(mContext,cartItem_hashmap, MenuItems,NewOrders_MenuItem_Fragment.this);
        adapter_cartItem_recyclerview.setHandler(newHandler());
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        int last_index=NewOrders_MenuItem_Fragment.cartItem_hashmap.size()-1;

        recyclerView.setAdapter(adapter_cartItem_recyclerview);
        recyclerView.scrollToPosition(last_index);



    }


    void showProgressBar(boolean show) {
        if(show) {
            loadingPanel.setVisibility(View.VISIBLE);
            loadingpanelmask.setVisibility(View.VISIBLE);

        }
        else {
            loadingpanelmask.setVisibility(View.GONE);
            loadingPanel.setVisibility(View.GONE);


        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.neworders_menu_item_fragment, container, false);
        rootView.setTag("RecyclerViewFragment");
        Log.d(TAG, "onCreateView: ");
        listview = rootView.findViewById(R.id.listview);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        MenuItems=getData();

        Log.i(TAG, "call adapter cart_Item " + getData());

        completemenuItem= getMenuItemfromString(MenuItems);
        createEmptyRowInListView("empty");

        CallAdapter();

        return rootView;


    }

    private List<Modal_NewOrderItems> getMenuItemfromString(String menulist) {
        List<Modal_NewOrderItems>MenuList=new ArrayList<>();
        String ItemName = "";
        if(!menulist.isEmpty()) {

            try {
                //converting jsonSTRING into array
                JSONObject jsonObject = new JSONObject(menulist);
                JSONArray JArray = jsonObject.getJSONArray("content");
                Log.d(Constants.TAG, "convertingJsonStringintoArray Response: " + JArray);
                int i1 = 0;
                int arrayLength = JArray.length();
                Log.d("Constants.TAG", "convertingJsonStringintoArray Response: " + arrayLength);


                for (; i1 < (arrayLength); i1++) {

                    try {
                        JSONObject json = JArray.getJSONObject(i1);
                        Modal_NewOrderItems newOrdersPojoClass = new Modal_NewOrderItems();
                        if(json.has("itemname")){
                            ItemName =  String.valueOf(json.get("itemname"));

                            newOrdersPojoClass.itemname = String.valueOf(json.get("itemname"));

                        }
                        else{
                            newOrdersPojoClass.itemname = "Item Name is Missing";
                            Toast.makeText(mContext,"TMC itemname Json is Missing",Toast.LENGTH_LONG).show();

                        }
                        if(json.has("tmcpriceperkg")){
                            try {
                                String tmcpriceperkg = String.valueOf(json.get("tmcpriceperkg"));
                                double doubleAmount = Double.parseDouble(tmcpriceperkg);
                                int intAmount = (int) Math.ceil(doubleAmount);

                                Log.i("Tag", "doubleAmount" + String.valueOf(intAmount));
                                newOrdersPojoClass.tmcpriceperkg = String.valueOf(json.get("tmcpriceperkg"));
                            }catch (Exception e ){
                                Toast.makeText(mContext,"Can't Convert  PriceperKg for "+ItemName+"in Menu Item",Toast.LENGTH_LONG).show();

                            }


                        }
                        else{
                            newOrdersPojoClass.tmcpriceperkg = "0";
                            Toast.makeText(mContext,"TMC PriceperKg Json is Missing",Toast.LENGTH_LONG).show();

                        }
                        if(json.has("grossweight")){
                            newOrdersPojoClass.grossweight = String.valueOf(json.get("grossweight"));

                        }
                        else{
                            newOrdersPojoClass.grossweight = "0";
                            Toast.makeText(mContext,"TMC grossweight Json is Missing",Toast.LENGTH_LONG).show();

                        }
                        if(json.has("netweight")){
                            newOrdersPojoClass.netweight = String.valueOf(json.get("netweight"));

                        }
                        else{
                            newOrdersPojoClass.netweight = "0";
                            Toast.makeText(mContext,"TMC netweight Json is Missing",Toast.LENGTH_LONG).show();

                        }
                        if(json.has("itemuniquecode")){
                            newOrdersPojoClass.itemuniquecode = String.valueOf(json.get("itemuniquecode"));

                        }
                        else{
                            Toast.makeText(mContext,"TMC itemuniquecode Json is Missing",Toast.LENGTH_LONG).show();

                            newOrdersPojoClass.itemuniquecode = "No Item Unique code for this item";
                        }
                        if(json.has("tmcprice")){
                            try {
                                String tmcprice = String.valueOf(json.get("tmcprice"));
                                double doubleAmount = Double.parseDouble(tmcprice);
                                int intAmount = (int) Math.ceil(doubleAmount);

                                Log.i("Tag", "doubleAmount" + String.valueOf(intAmount));
                                newOrdersPojoClass.tmcprice = String.valueOf(json.get("tmcprice"));
                            }catch (Exception e ){
                                Toast.makeText(mContext,"Can't Convert  tmcPrice for "+ItemName+"in Menu Item",Toast.LENGTH_LONG).show();

                            }


                        }
                        else{
                            newOrdersPojoClass.tmcpriceperkg = "0";
                            Toast.makeText(mContext,"TMC price Json is Missing",Toast.LENGTH_LONG).show();
                            Toast.makeText(mContext,"TMC tmcpriceperkg Json is Missing",Toast.LENGTH_LONG).show();

                        }
                        if(json.has("key")){
                            newOrdersPojoClass.menuItemId= String.valueOf(json.get("key"));

                        }
                        else{
                            newOrdersPojoClass.key = "Key for this menu is missing";
                            Toast.makeText(mContext,"TMC menuItemId Json is Missing",Toast.LENGTH_LONG).show();

                        }
                        if(json.has("portionsize")){
                            newOrdersPojoClass.portionsize = String.valueOf(json.get("portionsize"));

                        }
                        else{
                            newOrdersPojoClass.portionsize = "";
                            Toast.makeText(mContext,"TMC portionsize Json is Missing",Toast.LENGTH_LONG).show();

                        }
                        if(json.has("pricetypeforpos")){
                            newOrdersPojoClass.pricetypeforpos = String.valueOf(json.get("pricetypeforpos"));

                        }
                        else{
                            newOrdersPojoClass.pricetypeforpos = "0";
                            Toast.makeText(mContext,"TMC pricetypeforpos Json is Missing",Toast.LENGTH_LONG).show();

                        }

                        if(json.has("barcode")){
                            newOrdersPojoClass.barcode= String.valueOf(json.get("barcode"));

                        }
                        else{
                            newOrdersPojoClass.barcode = "barcode for this menu is missing";
                            Toast.makeText(mContext,"TMC barcode Json is Missing",Toast.LENGTH_LONG).show();

                        }
                        if(json.has("gstpercentage")){
                            newOrdersPojoClass.gstpercentage = String.valueOf(json.get("gstpercentage"));

                        }
                        else{
                            newOrdersPojoClass.gstpercentage = "";
                            Toast.makeText(mContext,"TMC gstpercentage Json is Missing",Toast.LENGTH_LONG).show();

                        }
                        if(json.has("applieddiscountpercentage")){
                            newOrdersPojoClass.discountpercentage = String.valueOf(json.get("applieddiscountpercentage"));

                        }
                        else{
                            newOrdersPojoClass.discountpercentage = "0";
                            Toast.makeText(mContext,"TMC applieddiscountpercentage Json is Missing",Toast.LENGTH_LONG).show();

                        }
                        if(json.has("tmcsubctgykey")){
                            newOrdersPojoClass.tmcsubctgykey = String.valueOf(json.get("tmcsubctgykey"));

                        }
                        else{
                            newOrdersPojoClass.tmcsubctgykey = "0";
                            Toast.makeText(mContext,"TMC tmcsubctgykey Json is Missing",Toast.LENGTH_LONG).show();
                            Log.i("Tag", "TMC tmcsubctgykey Json is Missing"+ String.valueOf(newOrdersPojoClass.getTmcsubctgykey()));

                        }
                        newOrdersPojoClass.quantity = "";
                        Log.d(TAG, "itemname of addMenuListAdaptertoListView: " + newOrdersPojoClass.portionsize);
                        MenuList.add(newOrdersPojoClass);

                        Log.d(Constants.TAG, "convertingJsonStringintoArray menuListFull: " + MenuList);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(Constants.TAG, "e: " + e.getLocalizedMessage());
                        Log.d(Constants.TAG, "e: " + e.getMessage());
                        Log.d(Constants.TAG, "e: " + e.toString());

                    }


                }

                Log.d(Constants.TAG, "convertingJsonStringintoArray menuListFull: " + MenuList);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return MenuList;
    }


    public void add_amount_ForBillDetails() {
DecimalFormat decimalFormat = new DecimalFormat("0.00");
        for(String Key :cart_Item_List){
            Modal_NewOrderItems newOrderItems = cartItem_hashmap.get(Key);

            //find total amount with out GST
            double new_total_amountfromArray = 0, discountpercentageDecimal=0, discountPercentage=0,newsavedAmount=0,taxes_and_chargesfromArray=0;
            if (newOrderItems != null) {
              try {
                  String itemFinalPrice_string = String.valueOf(newOrderItems.getItemFinalPrice());
                  new_total_amountfromArray = Double.parseDouble(itemFinalPrice_string);
              }
              catch (Exception e){
                  e.printStackTrace();
              }
                String discountPercentage_string;
              try {
                   discountPercentage_string = String.valueOf(newOrderItems.getDiscountpercentage());

                   discountPercentage = Double.parseDouble(discountPercentage_string);

              }
              catch (Exception e ){
                  e.printStackTrace();
              }


                Log.i(TAG, "add_amount_ForBillDetails new_total_amountfromArray" + new_total_amountfromArray);

              try {
                   discountpercentageDecimal = (100 - discountPercentage) / 100;

              }
              catch (Exception e){
                  e.printStackTrace();
              }
              try {
                   newsavedAmount = new_total_amountfromArray / discountpercentageDecimal;

              }
              catch (Exception e){
                  e.printStackTrace();

              }

              try{
                  newOrderItems.setSavedAmount(String.valueOf(decimalFormat.format(newsavedAmount)));

              }
              catch (Exception e ){
                  e.printStackTrace();

              }


                try{
                    new_total_amount = new_total_amountfromArray;
                    old_total_Amount = old_total_Amount + new_total_amount;
                    Log.i(TAG, "add_amount_ForBillDetails new_total_amount" + new_total_amount);
                    Log.i(TAG, "add_amount_ForBillDetails old_total_Amount" + old_total_Amount);


                }
                catch (Exception e ){
                    e.printStackTrace();

                }


                try{
                     taxes_and_chargesfromArray = Double.parseDouble(newOrderItems.getGstpercentage());
                    Log.i(TAG, "add_amount_ForBillDetails taxes_and_chargesfromadapter" + taxes_and_chargesfromArray);

                    taxes_and_chargesfromArray = ((taxes_and_chargesfromArray * new_total_amountfromArray) / 100);

                }
                catch (Exception e ){
                    e.printStackTrace();

                }



                try{
                    newOrderItems.setGstAmount(String.valueOf(decimalFormat.format(taxes_and_chargesfromArray)));

                    Log.i(TAG, "add_amount_ForBillDetails taxes_and_charges " + taxes_and_chargesfromArray);
                    Log.i(TAG, "add_amount_ForBillDetails new_total_amountfromadapter" + new_total_amountfromArray);
                    Log.i(TAG, "add_amount_ForBillDetails old_taxes_and_charges_Amount" + old_taxes_and_charges_Amount);
                    new_taxes_and_charges_Amount = taxes_and_chargesfromArray;
                    old_taxes_and_charges_Amount = old_taxes_and_charges_Amount + new_taxes_and_charges_Amount;

                }
                catch (Exception e ){
                    e.printStackTrace();

                }


                try {
                    //find subtotal
                    double subTotal_perItem = new_total_amount + new_taxes_and_charges_Amount;


                    newOrderItems.setSubTotal_perItem(String.valueOf(decimalFormat.format(subTotal_perItem)));


                    //find total payable Amount
                    new_to_pay_Amount = (old_total_Amount + old_taxes_and_charges_Amount);

                }
                catch (Exception e){
                    e.printStackTrace();

                }


                //find total GST amount

                try{
                    double subTotal_perItem = new_total_amount + new_taxes_and_charges_Amount;


                    newOrderItems.setSubTotal_perItem(String.valueOf(decimalFormat.format(subTotal_perItem)));


                    //find total payable Amount
                    new_to_pay_Amount = (old_total_Amount + old_taxes_and_charges_Amount);

                }catch (Exception e){
                    e.printStackTrace();
                }

                //find subtotal
             }
        }





        try{
            total_item_Rs_text_widget.setText(decimalFormat.format(old_total_Amount));
            taxes_and_Charges_rs_text_widget.setText(decimalFormat.format(old_taxes_and_charges_Amount));
             new_totalAmount_withGst = (int) Math.ceil(new_to_pay_Amount);
                finaltoPayAmount = String.valueOf(new_totalAmount_withGst)+".00";
            total_Rs_to_Pay_text_widget.setText(String.valueOf(new_totalAmount_withGst)+".00");

        }catch (Exception e){
            e.printStackTrace();
        }

        old_total_Amount=0;
        old_taxes_and_charges_Amount=0;
        new_to_pay_Amount=0;


    }


    void createEmptyRowInListView(String empty) {
        Modal_NewOrderItems newOrdersPojoClass = new Modal_NewOrderItems();
        newOrdersPojoClass.itemname = "";
        newOrdersPojoClass.tmcpriceperkg = "";
        newOrdersPojoClass.grossweight = "";
        newOrdersPojoClass.netweight = "";
        newOrdersPojoClass.tmcprice = "";
        newOrdersPojoClass.gstpercentage = "";
        newOrdersPojoClass.portionsize = "";
        newOrdersPojoClass.pricetypeforpos = "";
        newOrdersPojoClass.itemFinalWeight="";
        newOrdersPojoClass.pricePerItem ="";
        newOrdersPojoClass.quantity="";
        newOrdersPojoClass.itemFinalPrice = "0";
        newOrdersPojoClass.gstpercentage = "0";
        newOrdersPojoClass.discountpercentage = "0";

        newOrdersPojoClass.itemuniquecode=empty;
        cart_Item_List.add(empty);
        cartItem_hashmap.put(empty,newOrdersPojoClass);
    }

    private Handler newHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String data = bundle.getString("CartItem");

                if (data.equalsIgnoreCase("addNewItem")) {

                }

                if (data.equalsIgnoreCase("addBillDetails")) {
                    //   createBillDetails(cart_Item_List);

                }
                return false;
            }
        };
        return new Handler(callback);
    }

    public String getDate_and_time()
    {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => Sat, 9 Jan 2021 13:12:24 " + c);

        SimpleDateFormat day = new SimpleDateFormat("EEE");
        CurrentDay = day.format(c);

        SimpleDateFormat df = new SimpleDateFormat("d MMM yyyy");
       String CurrentDatee = df.format(c);
        CurrentDate = CurrentDay+", "+CurrentDatee;


        SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm:ss");
        FormattedTime = dfTime.format(c);
        formattedDate = CurrentDay+", "+CurrentDatee+" "+FormattedTime;
        return formattedDate;
    }



    private void PlaceOrder_in_OrderDetails(List<String> cart_Item_List, String Payment_mode, long sTime) {
      if(isOrderDetailsMethodCalled){
          return;
      }

        isOrderDetailsMethodCalled = true;
        String newOrderId = String.valueOf(sTime);
            SharedPreferences sh
                    = mContext.getSharedPreferences("VendorLoginData",
                    MODE_PRIVATE);


            String merchantorderid = "";
            String couponid = "";
            String CouponDiscountAmount = discount_Edit_widget.getText().toString();
            String DeliveryAmount = "";

            String orderid = String.valueOf(sTime);
            String orderplacedTime = Currenttime;
            String tokenno = "";
            String userid = "";
            String ordertype = "POSORDER";
            String deliverytype = "STOREPICKUP";
            String slotname = "EXPRESSDELIVERY";
            String slotdate = "";
            String orderPlacedDate = CurrentDate;

            String slottimerange = "";
            String UserMobile = "+91" + mobileNo_Edit_widget.getText().toString();
            String vendorkey = sh.getString("VendorKey", "vendor_1");
            String vendorName = sh.getString("VendorName", "TMCHasthinapuram");
            String itemTotalwithoutGst = total_item_Rs_text_widget.getText().toString();
            String payableAmount = total_Rs_to_Pay_text_widget.getText().toString();
            String taxAmount = taxes_and_Charges_rs_text_widget.getText().toString();
                PlaceOrder_in_PaymentTransactionDetails(sTime, Payment_mode, payableAmount, UserMobile);

            JSONArray itemDespArray = new JSONArray();

            for (int i = 0; i < cart_Item_List.size(); i++) {
                String itemUniqueCode = cart_Item_List.get(i);
                Modal_NewOrderItems modal_newOrderItems = cartItem_hashmap.get(itemUniqueCode);
                String itemName = String.valueOf(modal_newOrderItems.getItemname());
                String price = modal_newOrderItems.getItemPrice_quantityBased();
                String weight = modal_newOrderItems.getItemFinalWeight();
                String quantity = modal_newOrderItems.getQuantity();
                String GstAmount = modal_newOrderItems.getGstAmount();
                String menuItemId = modal_newOrderItems.getMenuItemId();
                String netweight = modal_newOrderItems.getNetweight();
                String portionsize = modal_newOrderItems.getPortionsize();
                String grossweight = modal_newOrderItems.getGrossweight();
                String subCtgyKey = modal_newOrderItems.getTmcsubctgykey();

                PlaceOrder_in_OrderItemDetails(itemName, weight, quantity, price, "", GstAmount, vendorkey, Currenttime, sTime, vendorkey, vendorName);


                JSONObject itemdespObject = new JSONObject();
                try {
                    itemdespObject.put("menuitemid", menuItemId);
                    itemdespObject.put("itemname", itemName);
                    itemdespObject.put("tmcprice", Double.parseDouble(price));
                    itemdespObject.put("quantity", Integer.parseInt(quantity));
                    itemdespObject.put("checkouturl", "");
                    itemdespObject.put("gstamount", Double.parseDouble(GstAmount));
                    itemdespObject.put("netweight", netweight);
                    itemdespObject.put("portionsize", portionsize);
                    itemdespObject.put("tmcsubctgykey", subCtgyKey);
                    try {
                        if (weight.equals("") || weight == (null)) {
                            itemdespObject.put("grossweight", grossweight);
                            itemdespObject.put("netweight", grossweight);

                            itemdespObject.put("weightingrams", grossweight);

                        } else {
                            itemdespObject.put("grossweight", weight);
                            itemdespObject.put("netweight", weight);
                            itemdespObject.put("weightingrams", weight);

                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                itemDespArray.put(itemdespObject);


            }


        if((CouponDiscountAmount .equals("0"))||(CouponDiscountAmount.equals("0.00"))){
            CouponDiscountAmount = "";
        }
        String StoreCoupon = "";
        if((!CouponDiscountAmount.equals("0"))&&(!CouponDiscountAmount.equals(""))){
            StoreCoupon = "storeCoupon";
        }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("coupondiscount", CouponDiscountAmount);
                jsonObject.put("deliveryamount", 0);
                jsonObject.put("couponkey", StoreCoupon);

                jsonObject.put("ordertype", ordertype);
                jsonObject.put("gstamount", Double.parseDouble(taxAmount));

                jsonObject.put("deliverytype", deliverytype);
                jsonObject.put("slotname", slotname);
                jsonObject.put("slotdate", "");
                jsonObject.put("slottimerange", "");

                jsonObject.put("orderid", orderid);
                jsonObject.put("orderplacedtime", orderplacedTime);
                jsonObject.put("tokenno", (tokenno));
                jsonObject.put("userid", userid);

                jsonObject.put("usermobile", UserMobile);
                jsonObject.put("vendorkey", vendorkey);
                jsonObject.put("vendorname", vendorName);
                jsonObject.put("payableamount", Double.parseDouble(payableAmount));

                jsonObject.put("taxamount", taxAmount);
                jsonObject.put("paymentmode", Payment_mode);
                jsonObject.put("itemdesp", itemDespArray);
                jsonObject.put("couponid", couponid);

                jsonObject.put("orderplaceddate", orderPlacedDate);

                jsonObject.put("merchantorderid", merchantorderid);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(Constants.TAG, "Request Payload: " + jsonObject);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.api_addOrderDetailsInOrderDetailsTable,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {

                    Log.d(Constants.TAG, "Response: " + response);
                    try {
                        String message = response.getString("message");
                        if (message.equals("success")) {
                            // StartTwice startTwice =new StartTwice(UserMobile,tokenno,itemTotalwithoutGst,taxAmount,payableAmount,orderid,cart_Item_List,cartItem_hashmap,Payment_mode);
                            // startTwice.main();
                            printRecipt(UserMobile, tokenno, itemTotalwithoutGst, taxAmount, payableAmount, orderid, cart_Item_List, cartItem_hashmap, Payment_mode,discountAmount);

                        }
                        else{

                            isOrderDetailsMethodCalled = false;
                            showProgressBar(false);
                            Toast.makeText(mContext,"OrderDetails is not updated in DB",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        showProgressBar(false);
                        isOrderDetailsMethodCalled = false;

                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    Log.d(Constants.TAG, "Error: " + error.getLocalizedMessage());
                    Log.d(Constants.TAG, "Error: " + error.getMessage());
                    Log.d(Constants.TAG, "Error: " + error.toString());
                    showProgressBar(false);
                    isOrderDetailsMethodCalled = false;

                    error.printStackTrace();
                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");

                    return params;
                }
            };
            // Make the request


        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(40000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(mContext).add(jsonObjectRequest);






    }

    private void PlaceOrder_in_OrderItemDetails(String itemnamee, String itemweightt,
                                                String quantityy, String itemamountt,
                                                String discountamountt,
                                                String gstamountt, String vendorkeyy, String currenttime,
                                                long sTime, String vendorkey, String vendorName){



            String orderid = String.valueOf(sTime);

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("orderid", orderid);
                jsonObject.put("itemname", itemnamee);
                jsonObject.put("quantity", quantityy);
                jsonObject.put("netWeight", itemweightt);
                jsonObject.put("discountamount", discountamountt);
                jsonObject.put("gstamount", gstamountt);
                jsonObject.put("vendorid", vendorkeyy);
                jsonObject.put("orderplacedtime", currenttime);
                jsonObject.put("tmcprice", itemamountt);
                jsonObject.put("vendorkey", vendorkey);
                jsonObject.put("vendorname", vendorName);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.d(Constants.TAG, "Request Payload: " + jsonObject);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.api_addOrderDetailsInOrderItemDetailsTable,
                    jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(@NonNull JSONObject response) {

                    Log.d(Constants.TAG, "Response for PlaceOrder_in_OrderItemDetails: " + response);
                    try {
                        String message = response.getString("message");
                        if (message.equals("success")) {
                            //   printRecipt(taxAmount,payableAmount,orderid,cart_Item_List);
                        }
                        else{
                            Log.d(Constants.TAG, "Failed  while PlaceOrder_in_OrderItemDetails: " + response);

                        }
                    } catch (JSONException e) {
                        Log.d(Constants.TAG, "Failed  while PlaceOrder_in_OrderItemDetails: " + response);

                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(@NonNull VolleyError error) {
                    Log.d(Constants.TAG, "Error: " + error.getLocalizedMessage());
                    Log.d(Constants.TAG, "Error: " + error.getMessage());
                    Log.d(Constants.TAG, "Error: " + error.toString());
                    Log.d(Constants.TAG, "Failed  while PlaceOrder_in_OrderItemDetails: " + error);

                    error.printStackTrace();
                }
            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");

                    return params;
                }
            };
            // Make the request
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(40000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(mContext).add(jsonObjectRequest);



    }


    private void PlaceOrder_in_OrderTrackingDetails(long sTime,String Currenttiime) {

        if(isOrderTrackingDetailsMethodCalled){
            return;
        }

        isOrderTrackingDetailsMethodCalled = true;

        String orderid = String.valueOf(sTime);
        String orderplacedDate_time = getDate_and_time();
        Log.d(Constants.TAG, "orderplacedDate_time: " + orderplacedDate_time);
        Log.d(Constants.TAG, "orderplacedDate_time: " + getDate_and_time());
        Log.d(Constants.TAG, "orderplacedDate_time: " + Currenttiime);
        Log.d(Constants.TAG, "orderplacedDate_time: " + Currenttime);

        SharedPreferences sh
                = mContext.getSharedPreferences("VendorLoginData",
                MODE_PRIVATE);
        String vendorkey = sh.getString("VendorKey","vendor_1");

        JSONObject  orderTrackingTablejsonObject = new JSONObject();
        try {
            orderTrackingTablejsonObject.put("orderdeliverytime",Currenttime);

            orderTrackingTablejsonObject.put("orderplacedtime",Currenttime);
            orderTrackingTablejsonObject.put("orderid",orderid);
            orderTrackingTablejsonObject.put("vendorkey",vendorkey);
            orderTrackingTablejsonObject.put("orderstatus","DELIVERED");

        }


        catch (JSONException e) {
            e.printStackTrace();

        }


        Log.d(Constants.TAG, "orderplacedDate_time Payload  : " + orderTrackingTablejsonObject);
        Log.d(Constants.TAG, "orderplacedDate_time: " + orderplacedDate_time);
        Log.d(Constants.TAG, "orderplacedDate_time: " + getDate_and_time());
        Log.d(Constants.TAG, "orderplacedDate_time: " + Currenttiime);
        Log.d(Constants.TAG, "orderplacedDate_time: " + Currenttime);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.api_addOrderDetailsInOrderTrackingDetailsTable,
                orderTrackingTablejsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {

                Log.d(Constants.TAG, "Response for PlaceOrder_in_OrderItemDetails: " + response);
                try {
                    String message = response.getString("message");
                    if(message .equals( "success")){
                        // printRecipt(taxAmount,payableAmount,orderid,cart_Item_List);
                    }
                    else{
                        isOrderTrackingDetailsMethodCalled = false;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    isOrderTrackingDetailsMethodCalled = false;

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(@NonNull VolleyError error) {
                Log.d(Constants.TAG, "Error: " + error.getLocalizedMessage());
                Log.d(Constants.TAG, "Error: " + error.getMessage());
                Log.d(Constants.TAG, "Error: " + error.toString());
                isOrderTrackingDetailsMethodCalled = false;

                error.printStackTrace();
            }
        }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");

                return params;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(40000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Make the request
        Volley.newRequestQueue(mContext).add(jsonObjectRequest);





    }



    private void PlaceOrder_in_PaymentTransactionDetails(long sTime, String paymentmode, String transactionAmount, String userMobile) {
       if(isPaymentDetailsMethodCalled){
           return;
       }


        isPaymentDetailsMethodCalled = true;

        String orderid = String.valueOf(sTime);

        JSONObject  jsonObject = new JSONObject();
        try {
            jsonObject.put("orderid", orderid);
            jsonObject.put("mobileno", userMobile);
            jsonObject.put("merchantorderid", "");
            jsonObject.put("paymentmode", paymentmode);
            jsonObject.put("paymenttype", "");
            jsonObject.put("transactiontime", Currenttime);
            jsonObject.put("transactionamount", transactionAmount);
            jsonObject.put("status", "SUCCESS");
            jsonObject.put("merchantpaymentid", "");
            jsonObject.put("desp", "");



        }


        catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(Constants.TAG, "Request Payload: " + jsonObject);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.api_addOrderDetailsInPaymentDetailsTable,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(@NonNull JSONObject response) {

                Log.d(Constants.TAG, "Response for PlaceOrder_in_OrderItemDetails: " + response);
                try {
                    String message = response.getString("message");
                    if(message .equals( "success")){
                        // printRecipt(taxAmount,payableAmount,orderid,cart_Item_List);
                    }
                    else{
                        isPaymentDetailsMethodCalled = false;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    isPaymentDetailsMethodCalled = false;

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(@NonNull VolleyError error) {

                Log.d(Constants.TAG, "Error: " + error.getLocalizedMessage());
                Log.d(Constants.TAG, "Error: " + error.getMessage());
                Log.d(Constants.TAG, "Error: " + error.toString());
                isPaymentDetailsMethodCalled = false;

                error.printStackTrace();
            }
        }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");

                return params;
            }
        };
        // Make the request
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(40000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(mContext).add(jsonObjectRequest);


    }


}



















/*
 public class StartTwice extends Thread {
        String userMobile;  String tokenno;
         String itemTotalwithoutGst; String taxAmount;
          String payableAmount; String orderid;
          List<String> cart_item_list;  HashMap<String, Modal_NewOrderItems> cartItem_hashmap;
           String payment_mode;
        public StartTwice(String userMobile, String tokenno, String itemTotalwithoutGst, String taxAmount, String payableAmount, String orderid, List<String> cart_item_list, HashMap<String, Modal_NewOrderItems> cartItem_hashmap, String payment_mode) {
        this.userMobile  =userMobile;
        this.tokenno = tokenno;
        this.itemTotalwithoutGst = itemTotalwithoutGst;
        this.taxAmount = taxAmount;
        this.payableAmount = payableAmount;
        this.orderid = orderid;
        this.cart_item_list = cart_item_list;
            this.payment_mode = payment_mode;
            this.cartItem_hashmap = cartItem_hashmap;



        }

        public void run() {
            System.out.println("running...");
             printRecipt(userMobile, tokenno, itemTotalwithoutGst, taxAmount, payableAmount, orderid, cart_Item_List, cartItem_hashmap, payment_mode);

        }

        public  void main() {

            StartTwice start1 = new StartTwice( userMobile,  tokenno,  itemTotalwithoutGst,  taxAmount,  payableAmount,  orderid,  cart_item_list,  cartItem_hashmap,  payment_mode);
            System.out.println("Thread id: " + start1.getId());
            start1.start();

             start1 = new StartTwice( userMobile,  tokenno,  itemTotalwithoutGst,  taxAmount,  payableAmount,  orderid,  cart_item_list,  cartItem_hashmap,  payment_mode);
            System.out.println("Thread id: " + start1.getId());
            start1.start();
        }



    }


 */

































































































//add amount for bill details
/*
        for(int i = 0; i<cart_Item_List.size();i++) {

            Modal_NewOrderItems modal_newOrderItems = cart_Item_List.get(i);
            String pricetype_of_pos = String.valueOf(modal_newOrderItems.getPricetypeforpos());

            if(pricetype_of_pos.equals("tmcprice")) {
                int new_total_amountfromArray = Integer.parseInt(modal_newOrderItems.getPricePerItem());
                Log.i(TAG, "add_amount_ForBillDetails new_total_amountfromArray" + new_total_amountfromArray);

                new_total_amount = new_total_amountfromArray;
                old_total_Amount = old_total_Amount + new_total_amount;
                Log.i(TAG, "add_amount_ForBillDetails new_total_amount" + new_total_amount);
                Log.i(TAG, "add_amount_ForBillDetails old_total_Amount" + old_total_Amount);


                int taxes_and_chargesfromArray = Integer.parseInt(modal_newOrderItems.getGstpercentage());
                Log.i(TAG, "add_amount_ForBillDetails taxes_and_chargesfromadapter" + taxes_and_chargesfromArray);

                taxes_and_chargesfromArray = ((taxes_and_chargesfromArray * new_total_amountfromArray) / 100);


                modal_newOrderItems.setGstAmount(String.valueOf(taxes_and_chargesfromArray));
                Log.i(TAG, "add_amount_ForBillDetails taxes_and_charges " + taxes_and_chargesfromArray);
                Log.i(TAG, "add_amount_ForBillDetails new_total_amountfromadapter" + new_total_amountfromArray);
                Log.i(TAG, "add_amount_ForBillDetails old_taxes_and_charges_Amount" + old_taxes_and_charges_Amount);
                new_taxes_and_charges_Amount = taxes_and_chargesfromArray;
                int subTotal_perItem=new_total_amount+new_taxes_and_charges_Amount;
                modal_newOrderItems.setSubTotal_perItem(String.valueOf(subTotal_perItem));
                old_taxes_and_charges_Amount = old_taxes_and_charges_Amount + new_taxes_and_charges_Amount;
                Log.i(TAG, "add_amount_ForBillDetails new_taxes_and_charges_Amount" + new_taxes_and_charges_Amount);
                Log.i(TAG, "add_amount_ForBillDetails old_taxes_and_charges_Amount" + old_taxes_and_charges_Amount);


            }
            if (pricetype_of_pos.equals("tmcpriceperkg")) {
                int new_total_amountfromArray = Integer.parseInt(modal_newOrderItems.getPricePerItem());
                Log.i(TAG, "add_amount_ForBillDetails new_total_amountfromArray" + new_total_amountfromArray);

                new_total_amount = new_total_amountfromArray;
                old_total_Amount = old_total_Amount + new_total_amount;
                Log.i(TAG, "add_amount_ForBillDetails new_total_amount" + new_total_amount);
                Log.i(TAG, "add_amount_ForBillDetails old_total_Amount" + old_total_Amount);


                int taxes_and_chargesfromArray = Integer.parseInt(modal_newOrderItems.getGstpercentage());
                Log.i(TAG, "add_amount_ForBillDetails taxes_and_chargesfromadapter" + taxes_and_chargesfromArray);

                taxes_and_chargesfromArray = ((taxes_and_chargesfromArray * new_total_amountfromArray) / 100);
                Log.i(TAG, "add_amount_ForBillDetails taxes_and_charges " + taxes_and_chargesfromArray);
                Log.i(TAG, "add_amount_ForBillDetails new_total_amountfromadapter" + new_total_amountfromArray);
                Log.i(TAG, "add_amount_ForBillDetails old_taxes_and_charges_Amount" + old_taxes_and_charges_Amount);
                new_taxes_and_charges_Amount = taxes_and_chargesfromArray;
                int subTotal_perItem=new_total_amount+new_taxes_and_charges_Amount;
                modal_newOrderItems.setSubTotal_perItem(String.valueOf(subTotal_perItem));

                old_taxes_and_charges_Amount = old_taxes_and_charges_Amount + new_taxes_and_charges_Amount;
                Log.i(TAG, "add_amount_ForBillDetails new_taxes_and_charges_Amount" + new_taxes_and_charges_Amount);
                Log.i(TAG, "add_amount_ForBillDetails old_taxes_and_charges_Amount" + old_taxes_and_charges_Amount);


            }



            modal_newOrderItems.setTotalGstAmount(String.valueOf(old_taxes_and_charges_Amount));

            modal_newOrderItems.setTotal_pricePerItem(String.valueOf(old_total_Amount));


                    new_to_pay_Amount = old_total_Amount + old_taxes_and_charges_Amount;
            modal_newOrderItems.setTotal_of_subTotal_perItem(String.valueOf(new_to_pay_Amount));
            total_item_Rs_text_widget.setText(String.valueOf(old_total_Amount));
            taxes_and_Charges_rs_text_widget.setText(String.valueOf((old_taxes_and_charges_Amount)));
            total_Rs_to_Pay_text_widget.setText(String.valueOf(new_to_pay_Amount));
        }
        old_total_Amount=0;
        old_taxes_and_charges_Amount=0;
        new_to_pay_Amount=0;

 */








/*
    public void getMenuItemUsingBarCode(String barcode,int position) {


            Log.e(TAG, "Got barcode isBarcodeEntered getMenuItemUsingBarCode" + isdataFetched);
            isdataFetched = true;

            if (barcode.length() == 13) {
                Log.e(TAG, "Got barcode isBarcodeEntered getMenuItemUsingBarCode" + isdataFetched);
                String itemWeight;
                Log.e(TAG, "1 barcode " + barcode);
                Log.e(TAG, "Got barcode isBarcodeEntered getMenuItemUsingBarCode" + isdataFetched);

                for (int i = 0; i < NewOrders_MenuItem_Fragment.completemenuItem.size(); i++) {

                    Modal_NewOrderItems modal_newOrderItems = NewOrders_MenuItem_Fragment.completemenuItem.get(i);

                    if ((String.valueOf(modal_newOrderItems.getItemuniquecode())).equals(barcode)) {


                        Modal_NewOrderItems newItem_newOrdersPojoClass = new Modal_NewOrderItems();
                        newItem_newOrdersPojoClass.itemname = modal_newOrderItems.getItemname();
                        newItem_newOrdersPojoClass.grossweight = modal_newOrderItems.getGrossweight();
                        newItem_newOrdersPojoClass.netweight = modal_newOrderItems.getNetweight();
                        newItem_newOrdersPojoClass.tmcprice = modal_newOrderItems.getTmcprice();
                        newItem_newOrdersPojoClass.tmcpriceperkg = modal_newOrderItems.getTmcpriceperkg();

                        newItem_newOrdersPojoClass.gstpercentage = modal_newOrderItems.getGstpercentage();
                        newItem_newOrdersPojoClass.portionsize = modal_newOrderItems.getPortionsize();
                        newItem_newOrdersPojoClass.pricetypeforpos = modal_newOrderItems.getPricetypeforpos();
                        newItem_newOrdersPojoClass.itemuniquecode = modal_newOrderItems.getItemuniquecode();
                        newItem_newOrdersPojoClass.pricePerItem = modal_newOrderItems.getTmcprice();
                        newItem_newOrdersPojoClass.itemPrice_quantityBased = modal_newOrderItems.getTmcprice();
                        newItem_newOrdersPojoClass.quantity = "1";
                        newItem_newOrdersPojoClass.subTotal_perItem = "";
                        newItem_newOrdersPojoClass.total_of_subTotal_perItem = "";
                        newItem_newOrdersPojoClass.totalGstAmount = "";
                        newItem_newOrdersPojoClass.itemFinalPrice = "";

                        if (modal_newOrderItems.getGrossweight().equals("") && modal_newOrderItems.getNetweight().equals("")) {
                            Log.e(Constants.TAG, "getPortionsize " + (String.format(" %s", modal_newOrderItems.getPortionsize())));
                            newItem_newOrdersPojoClass.itemFinalWeight = (modal_newOrderItems.getPortionsize());

                            //     itemWeightTextview_widget.setText(String.valueOf(modal_newOrderItems.getPortionsize()));
                            add_amount_ForBillDetails();
                            itemWeight = String.valueOf(modal_newOrderItems.getPortionsize());
                        } else if (modal_newOrderItems.getNetweight().equals("")) {

                            Log.e(Constants.TAG, "getGrossweight " + (String.format(" %s", modal_newOrderItems.getGrossweight())));

                            newItem_newOrdersPojoClass.itemFinalWeight = (modal_newOrderItems.getGrossweight());
                            //   itemWeightTextview_widget.setText(String.valueOf(modal_newOrderItems.getGrossweight()));
                            add_amount_ForBillDetails();
                            itemWeight = String.valueOf(modal_newOrderItems.getGrossweight());


                        } else if (modal_newOrderItems.getGrossweight().equals("")) {
                            Log.e(Constants.TAG, "getNetweight " + (String.format(" %s", modal_newOrderItems.getNetweight())));
                            newItem_newOrdersPojoClass.itemFinalWeight = (modal_newOrderItems.getNetweight());

                            //     itemWeightTextview_widget.setText(String.valueOf(modal_newOrderItems.getNetweight()));
                            add_amount_ForBillDetails();
                            itemWeight = String.valueOf(modal_newOrderItems.getNetweight());


                        } else {
                            Log.e(Constants.TAG, "getGrossweight " + (String.format(" %s", modal_newOrderItems.getGrossweight())));
                            //   itemWeightTextview_widget.setText(String.valueOf(modal_newOrderItems.getGrossweight()));

                            newItem_newOrdersPojoClass.itemFinalWeight = (modal_newOrderItems.getGrossweight());
                            add_amount_ForBillDetails();
                            itemWeight = String.valueOf(modal_newOrderItems.getGrossweight());


                        }

                        addItemIntheCart(newItem_newOrdersPojoClass, itemWeight, barcode);


                    }


                }
            }
            if (barcode.length() == 14) {
                int item_total;
                String itemuniquecode = barcode.substring(0, 9);
                String itemWeight = barcode.substring(9, 14);
                Log.e(TAG, "1 barcode uniquecode" + itemuniquecode);
                Log.e(TAG, "1 barcode itemweight" + itemWeight);

                for (int i = 0; i < NewOrders_MenuItem_Fragment.completemenuItem.size(); i++) {

                    Modal_NewOrderItems modal_newOrderItems = NewOrders_MenuItem_Fragment.completemenuItem.get(i);

                    if (String.valueOf(modal_newOrderItems.getItemuniquecode()).equals(itemuniquecode)) {

                        Modal_NewOrderItems newItem_newOrdersPojoClass = new Modal_NewOrderItems();
                        newItem_newOrdersPojoClass.itemname = modal_newOrderItems.getItemname();
                        newItem_newOrdersPojoClass.tmcpriceperkg = modal_newOrderItems.getTmcpriceperkg();
                        newItem_newOrdersPojoClass.grossweight = modal_newOrderItems.getGrossweight();
                        newItem_newOrdersPojoClass.netweight = modal_newOrderItems.getNetweight();
                        newItem_newOrdersPojoClass.tmcprice = modal_newOrderItems.getTmcprice();
                        newItem_newOrdersPojoClass.gstpercentage = modal_newOrderItems.getGstpercentage();
                        newItem_newOrdersPojoClass.portionsize = modal_newOrderItems.getPortionsize();
                        newItem_newOrdersPojoClass.pricetypeforpos = modal_newOrderItems.getPricetypeforpos();
                        newItem_newOrdersPojoClass.itemuniquecode = (modal_newOrderItems.getItemuniquecode());
                        newItem_newOrdersPojoClass.itemPrice_quantityBased = modal_newOrderItems.getTmcpriceperkg();

                        newItem_newOrdersPojoClass.quantity = "1";
                        newItem_newOrdersPojoClass.subTotal_perItem = "";
                        newItem_newOrdersPojoClass.total_of_subTotal_perItem = "";
                        newItem_newOrdersPojoClass.totalGstAmount = "";
                        newItem_newOrdersPojoClass.itemFinalPrice = "";


/*
                    if (String.valueOf(modal_newOrderItems.getPricetypeforpos()).equals("tmcpriceperkg")) {
                        int priceperKg=Integer.parseInt(modal_newOrderItems.getTmcpriceperkg());
                        int weight = Integer.parseInt(itemWeight);
                        if (weight < 1000) {
                            item_total = (priceperKg * weight);
                            Log.e("TAG", "adapter 9 item_total price_per_kg" + priceperKg);

                            Log.e("TAG", "adapter 9 item_total weight" + weight);

                            Log.e("TAG", "adapter 9 item_total " + priceperKg * weight);

                            item_total = item_total / 1000;
                            Log.e("TAG", "adapter 9 item_total " + item_total);

                            Log.e("TAg", "weight2" + weight);
                            cart_Item_List.get(position).setPricePerItem(String.valueOf(item_total));
                            cart_Item_List.get(position).setItemFinalWeight(String.valueOf(weight));

                           // itemPrice_Widget.setText(String.valueOf(item_total));
                            add_amount_ForBillDetails();

                            NewOrders_MenuItem_Fragment.adapter_cartItem_recyclerview.notifyDataSetChanged();

                        }

                        if (weight == 1000) {

                         //   itemPrice_Widget.setText(String.valueOf(priceperKg));
                            Log.e("TAG", "Cart adapter price_per_kg +" + priceperKg);
                            Log.e("TAG", "adapter 10" + itemInCart.get(getAdapterPosition()).getItemname());
                            Log.e("TAG", "adapter 10" + itemInCart.get(getAdapterPosition()).getItemFinalWeight());
                            Log.e("TAG", "adapter 10" + itemInCart.get(getAdapterPosition()).getItemFinalPrice());
                            Log.e("TAG", "adapter 10" + itemInCart.get(getAdapterPosition()).getTmcpriceperkg());

                            itemInCart.get(getAdapterPosition()).setPricePerItem(String.valueOf(priceperKg));
                            itemInCart.get(getAdapterPosition()).setItemFinalWeight(String.valueOf(weight));

                            Log.e("TAG", "Cart adapter price_per_kg +" + priceperKg);
                            Log.e("TAG", "adapter 10.1" + itemInCart.get(getAdapterPosition()).getItemname());
                            Log.e("TAG", "adapter 10.1" + itemInCart.get(getAdapterPosition()).getItemFinalWeight());
                            Log.e("TAG", "adapter 10.1" + itemInCart.get(getAdapterPosition()).getItemFinalPrice());
                            Log.e("TAG", "adapter 10.1" + itemInCart.get(getAdapterPosition()).getTmcpriceperkg());


                            newOrders_menuItem_fragment.add_amount_ForBillDetails();
                            NewOrders_MenuItem_Fragment.adapter_cartItem_recyclerview.notifyDataSetChanged();

                        }

                        if (weight > 1000) {
                            Log.e("TAG", "Cart adapter price_per_kg +" + priceperKg);
                            Log.e("TAG", "adapter 11" + itemInCart.get(getAdapterPosition()).getItemname());
                            Log.e("TAG", "adapter 11" + itemInCart.get(getAdapterPosition()).getItemFinalWeight());
                            Log.e("TAG", "adapter 11" + itemInCart.get(getAdapterPosition()).getItemFinalPrice());
                            Log.e("TAG", "adapter 11" + itemInCart.get(getAdapterPosition()).getTmcpriceperkg());

                            Log.e("TAg", "weight3" + weight);

                            int itemquantity = weight - 1000;
                            Log.e("TAg", "weight itemquantity" + itemquantity);

                            item_total = (price_per_kg * itemquantity) / 1000;


                            Log.e("TAg", "weight item_total" + item_total);

                            itemInCart.get(getAdapterPosition()).setPricePerItem(String.valueOf(priceperKg + item_total));
                            itemInCart.get(getAdapterPosition()).setItemFinalWeight(String.valueOf(weight));
                            Log.e("TAG", "Cart adapter price_per_kg +" + priceperKg);
                            Log.e("TAG", "adapter 11.1" + itemInCart.get(getAdapterPosition()).getItemname());
                            Log.e("TAG", "adapter 11.1" + itemInCart.get(getAdapterPosition()).getItemFinalWeight());
                            Log.e("TAG", "adapter 11.1" + itemInCart.get(getAdapterPosition()).getItemFinalPrice());
                            Log.e("TAG", "adapter 11.1" + itemInCart.get(getAdapterPosition()).getTmcpriceperkg());

                            itemPrice_Widget.setText(String.valueOf(priceperKg + item_total));
                            Log.e("TAg", "weight item_total+price" + item_total + priceperKg);
                            newOrders_menuItem_fragment.add_amount_ForBillDetails();
                            NewOrders_MenuItem_Fragment.adapter_cartItem_recyclerview.notifyDataSetChanged();

                        }



                        // newItem_newOrdersPojoClass.pricePerItem = modal_newOrderItems.getTmcpriceperkg();
                        Log.e("TAG", "adapter 12" + itemInCart.get(getAdapterPosition()).getItemname());
                        Log.e("TAG", "adapter 12" + itemInCart.get(getAdapterPosition()).getItemFinalWeight());
                        Log.e("TAG", "adapter 12" + itemInCart.get(getAdapterPosition()).getPricePerItem());
                        Log.e("TAG", "adapter 12" + itemInCart.get(getAdapterPosition()).getTmcpriceperkg());

                    }



                        if (String.valueOf(modal_newOrderItems.getPricetypeforpos()).equals("tmcprice")) {
                            newItem_newOrdersPojoClass.pricePerItem = modal_newOrderItems.getTmcprice();

                        }

                        Log.e(TAG, "Got barcode getMenuItemUsingBarCode" + isdataFetched);

                        newItem_newOrdersPojoClass.itemFinalWeight = itemWeight;
                        addItemIntheCart(newItem_newOrdersPojoClass, itemWeight, itemuniquecode);


                    }


                }

            }

    }


    private void addItemIntheCart(Modal_NewOrderItems newItem_newOrdersPojoClass, String itemWeight,String itemUniquecode) {
      isdataFetched=true;
        String old_ItemUniqueCode = "";
        int repeatedItemIndex=-1;
        if(cart_Item_List.size()>1){
            for (int i =0; i<cart_Item_List.size();i++) {
                Modal_NewOrderItems modal_newOrderItems = cart_Item_List.get(i);

                 old_ItemUniqueCode = modal_newOrderItems.getItemuniquecode().toString();
                if(old_ItemUniqueCode.equals(itemUniquecode)){
               /* Log.e(TAG, "newItem_uniqueCode  "+old_ItemUniqueCode);
                Log.e(TAG, "itemUniquecode "+itemUniquecode);
                quantity  = Integer.parseInt(modal_newOrderItems.getQuantity());
                quantity=quantity+1;

                modal_newOrderItems.setQuantity(String.valueOf(quantity));
                adapter_cartItem_recyclerview.notifyDataSetChanged();



                    repeatedItemIndex=i;
                    deleteRepeatedIndex();
                    break;

                }
            else{
                addItemsNormallyIntheCart(newItem_newOrdersPojoClass,itemWeight);
                    break;

                }



        }
            if(repeatedItemIndex!=-1){
                int quantity =0;
                int itemPrice =0;
                Modal_NewOrderItems modal_newOrderItems = cart_Item_List.get(repeatedItemIndex);

                quantity  = Integer.parseInt(modal_newOrderItems.getQuantity());
                quantity=quantity+1;
                modal_newOrderItems.setQuantity(String.valueOf(quantity));
                itemPrice=Integer.parseInt(newItem_newOrdersPojoClass.getItemPrice_quantityBased());
                modal_newOrderItems.setSubTotal_perItem(String.valueOf(itemPrice));

                modal_newOrderItems.setQuantity(String.valueOf(quantity));
                adapter_cartItem_recyclerview.notifyDataSetChanged();

            }



        }else{
            Log.e(TAG, "2nd else "+itemUniquecode);

            addItemsNormallyIntheCart(newItem_newOrdersPojoClass,itemWeight);

        }
    }



    private void deleteRepeatedIndex() {
        int last_ItemInCart = cart_Item_List.size() - 1;
        cart_Item_List.remove(last_ItemInCart);
        adapter_cartItem_recyclerview.notifyDataSetChanged();
    }

    public  void addItemsNormallyIntheCart(Modal_NewOrderItems newItem_newOrdersPojoClass, String itemWeight) {
        Log.e(TAG, "Got barcode addItemIntheCart"+isdataFetched);
        int last_ItemInCart = NewOrders_MenuItem_Fragment.cart_Item_List.size() - 1;
        Log.e(TAG, "barcode uniquecode last_ItemInCart" + last_ItemInCart);
        Log.e(TAG, "barcode uniquecode itemWeight" + itemWeight);
        Log.e(TAG, "barcode uniquecode getItemFinalPrice" + newItem_newOrdersPojoClass.getPricePerItem());
        Log.e(TAG, "barcode uniquecode getItemFinalWeight" + newItem_newOrdersPojoClass.getItemFinalWeight());

        Modal_NewOrderItems modal_newOrderItems = NewOrders_MenuItem_Fragment.cart_Item_List.get(last_ItemInCart);
        if (String.valueOf(modal_newOrderItems.getItemname()).equals("")) {
            Log.e(TAG, "barcode in if  " + modal_newOrderItems.getItemname());

            modal_newOrderItems.setItemname(newItem_newOrdersPojoClass.getItemname());
            modal_newOrderItems.setTmcpriceperkg(newItem_newOrdersPojoClass.getTmcpriceperkg());
            modal_newOrderItems.setGrossweight(newItem_newOrdersPojoClass.getGrossweight());
            modal_newOrderItems.setNetweight(newItem_newOrdersPojoClass.getNetweight());
            modal_newOrderItems.setTmcprice(newItem_newOrdersPojoClass.getTmcprice());
            modal_newOrderItems.setGstpercentage(newItem_newOrdersPojoClass.getGstpercentage());
            modal_newOrderItems.setPortionsize(newItem_newOrdersPojoClass.getPortionsize());
            modal_newOrderItems.setPricetypeforpos(newItem_newOrdersPojoClass.getPricetypeforpos());
            modal_newOrderItems.setPricePerItem(newItem_newOrdersPojoClass.getPricePerItem());
            modal_newOrderItems.setQuantity(newItem_newOrdersPojoClass.getQuantity());
            modal_newOrderItems.setItemuniquecode(newItem_newOrdersPojoClass.getItemuniquecode());
            modal_newOrderItems.setItemFinalWeight(newItem_newOrdersPojoClass.getItemFinalWeight());
            newItem_newOrdersPojoClass.setSubTotal_perItem(newItem_newOrdersPojoClass.getSubTotal_perItem());
            newItem_newOrdersPojoClass.setTotal_of_subTotal_perItem(newItem_newOrdersPojoClass.getTotal_of_subTotal_perItem());
            newItem_newOrdersPojoClass.setGstAmount(newItem_newOrdersPojoClass.getGstAmount());
            newItem_newOrdersPojoClass.setItemPrice_quantityBased(newItem_newOrdersPojoClass.getItemPrice_quantityBased());


            Log.e(TAG, "barcode in if before cart_Item_List.size()" + NewOrders_MenuItem_Fragment.cart_Item_List.size());

            // NewOrders_MenuItem_Fragment.cart_Item_List.add(last_ItemInCart,modal_newOrderItems);
            Log.e(TAG, "barcode in if after cart_Item_List.size()" + NewOrders_MenuItem_Fragment.cart_Item_List.size());
            //    newOrders_menuItem_fragment.add_amount_ForBillDetails();
            // cart_Item_List.add(last_ItemInCart,modal_newOrderItems);
            Log.i(TAG, "barcode in if after cart_Item_List.size()" + NewOrders_MenuItem_Fragment.cart_Item_List.size());
            Log.e(TAG, "Got barcode addItemIntheCart" +
                    "" +
                    ""+isdataFetched);

            NewOrders_MenuItem_Fragment.adapter_cartItem_recyclerview.notifyDataSetChanged();

        }
    }


 */