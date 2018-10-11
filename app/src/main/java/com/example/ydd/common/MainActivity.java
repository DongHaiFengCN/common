package com.example.ydd.common;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Document;
import com.example.ydd.mylibrary.print.config.PrinterChangeListener;
import com.example.ydd.mylibrary.print.config.WorkRunnable;
import com.example.ydd.mylibrary.print.message.Dish;
import com.example.ydd.mylibrary.print.message.PrintData;
import com.example.ydd.mylibrary.print.message.PrintMessage;
import com.example.ydd.mylibrary.print.printer.Config;
import com.example.ydd.mylibrary.print.printer.PinterPort;
import com.example.ydd.mylibrary.print.printer.PrintService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;
import com.gprinter.io.EthernetPort;
import com.gprinter.io.PortManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.ydd.mylibrary.print.config.PrinterChangeListener.ESC_STATE_COVER_OPEN;
import static com.example.ydd.mylibrary.print.config.PrinterChangeListener.ESC_STATE_ERR_OCCURS;
import static com.example.ydd.mylibrary.print.config.PrinterChangeListener.ESC_STATE_PAPER_ERR;
import static com.example.ydd.mylibrary.print.config.PrinterChangeListener.PRINTER_URL;
import static com.example.ydd.mylibrary.print.config.PrinterChangeListener.READ_BUFFER_ARRAY;
import static com.example.ydd.mylibrary.print.config.PrinterChangeListener.READ_DATA;
import static com.example.ydd.mylibrary.print.config.PrinterChangeListener.READ_NAME;
import static com.example.ydd.mylibrary.print.config.PrinterChangeListener.READ_PERIOD;
import static com.example.ydd.mylibrary.print.config.PrinterChangeListener.concurrentMap;

public class MainActivity extends AppCompatActivity {

    private EthernetPort ethernetPort1;
    private EthernetPort ethernetPort2;
    private PrinterChangeListener printerChangeListener;
    private PrintService printService;
    private PinterPort pinterPort;
    private PrintMessage printMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestAllPower();
        //打印机的状态，命令都在广播处理
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PRINTER_URL);
        LocalReceiver localReceiver = new LocalReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, intentFilter);



        //初始化监听监听器,全局单例模式
        printerChangeListener = PrinterChangeListener.getInstance(getApplicationContext(),new ConcurrentHashMap());


        //------------------------- 测试数据部分------------------------
//        ethernetPort1 = new EthernetPort("192.168.2.248", 9100);
//        Log.e("DOAING", ethernetPort1.openPort() + " 第一个打开");
        printService = new PrintService(MainActivity.this);



//        //每添加一个打印机或者启动一个打印机就去添加一个对应的打印机的监听线程
//        printerChangeListener.addPoolThread(new WorkRunnable(getApplicationContext(), ethernetPort1, "101"));
//
//
//        ethernetPort2 = new EthernetPort("192.168.2.201", 9100);
//        Log.e("DOAING", ethernetPort2.openPort() + " 第二个打开");
//
//        printerChangeListener.addPoolThread(new WorkRunnable(getApplicationContext(), ethernetPort2, "201"));
//
//
//        //动态的添加新打印机新数据 测试 实际开发中concurrentMap替换成缓存打印机的哪个map
//        concurrentMap.put("101", ethernetPort1);
//
//        concurrentMap.put("201", ethernetPort2);
//        //------------------------- 测试数据------------------------
//
//
        findViewById(R.id.printa_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //开启周期监听，当前设置2000毫秒一个监听
                //printerChangeListener.openPeriodPrinterListener(2000L);
                establishPrintData();

                printService.detchPrinter("吃饭",printMessage.orderSendReceiptWithResponse(2));

            }
        });
//
        findViewById(R.id.printb_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //发起单次的查询命令
                //printerChangeListener.openOncePrinterListener();
                printService.deletePrint("吃饭");

            }
        });
//
        findViewById(R.id.printc_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //释放指定打印机的监听线程
//                if(printerChangeListener.freeWorkPool("101")){
//
//                    Toast.makeText(MainActivity.this,"移除了101的监听",Toast.LENGTH_SHORT).show();
//                }
                pinterPort = new PinterPort();
                pinterPort.setPort("9100");
                pinterPort.setAddress("192.168.2.248");
                pinterPort.setTypePrint(Config.WIFI);
                pinterPort.setName("吃饭");
                printService.printerExist(pinterPort);


            }
        });
        findViewById(R.id.printd_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //printerChangeListener.freeWorkPool("201");
                System.exit(0);

            }
        });
//        findViewById(R.id.printe_bt).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //终止所有打印机的线程监听
//                printerChangeListener.shutdownAllPool();
//
//            }
//        });
//
//        findViewById(R.id.printf_bt).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //查看指定打印机状态
//                printerChangeListener.openAppointPrinterListener("101");
//
//            }
//        });
    }

    private void establishPrintData(){

        PrintData printData = new PrintData();

        List<Dish> dishList = new ArrayList<>();
        for (int i = 0 ; i < 2 ; i++){
            dishList.add(transformDish());
        }
        printData.setDishList(dishList);
        printData.setEmployeeName("管理员");
        printData.setCurrentPersons(2);
        printData.setTableName("001");
        printData.setSerNum("001");
        printData.setAreaName("大厅");
        printMessage = new PrintMessage(printData);
    }

    private Dish transformDish(){
        Dish dish = new Dish();
        dish.setName("土豆丝");
        dish.setGoodsType(0);
        dish.setCount(1);
        dish.setGoodsAlter(0);
        dish.setPromotion(false);
        dish.setDescription("");
        return dish;
    }

    /**
     * 发送票据
     */
    EscCommand sendReceiptWithResponse() {
        EscCommand esc = new EscCommand();
        esc.addCutPaper();
        esc.addInitializePrinter();
        esc.addPrintAndFeedLines((byte) 3);
        // 设置打印居中
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        // 设置为倍高倍宽
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);
        // 打印文字
        esc.addText("Sample\n");
        esc.addPrintAndLineFeed();

        /* 打印文字 */
        // 取消倍高倍宽
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
        // 设置打印左对齐
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        // 打印文字
        esc.addText("Print text\n");
        // 打印文字
        esc.addText("Welcome to use SMARNET printer!\n");

        /* 打印繁体中文 需要打印机支持繁体字库 */
        String message = "佳博智匯票據打印機\n";
        esc.addText(message, "GB2312");
        esc.addPrintAndLineFeed();

        /* 绝对位置 具体详细信息请查看GP58编程手册 */
        esc.addText("智汇");
        esc.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
        esc.addSetAbsolutePrintPosition((short) 6);
        esc.addText("网络");
        //esc.addCutPaper();
        esc.addSetAbsolutePrintPosition((short) 10);
        esc.addText("设备");
        esc.addPrintAndLineFeed();

        /* 打印图片 */
        for(int i = 0 ; i < 5 ; i++){
            esc.addCutPaper();
            esc.addText(i+"\n");
        }
        // 打印文字
        esc.addText("Print bitmap!\n");

        /* 打印一维条码 */
        // 打印文字
        esc.addText("Print code128\n");
        esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);
        // 设置条码可识别字符位置在条码下方
        // 设置条码高度为60点
        esc.addSetBarcodeHeight((byte) 60);
        // 设置条码单元宽度为1
        esc.addSetBarcodeWidth((byte) 1);
        // 打印Code128码
        esc.addCODE128(esc.genCodeB("SMARNET"));
        esc.addPrintAndLineFeed();

        /*
         * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
         */
        // 打印文字
        esc.addText("Print QRcode\n");
        // 设置纠错等级
        esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31);
        // 设置qrcode模块大小
        esc.addSelectSizeOfModuleForQRCode((byte) 3);
        // 设置qrcode内容
        esc.addStoreQRCodeData("www.smarnet.cc");
        esc.addPrintQRCode();// 打印QRCode
        esc.addPrintAndLineFeed();

        // 设置打印左对齐
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        //打印文字
        esc.addText("Completed!\r\n");

        // 开钱箱
        esc.addGeneratePlus(LabelCommand.FOOT.F5, (byte) 255, (byte) 255);
        esc.addPrintAndFeedLines((byte) 8);
        // 加入查询打印机状态，打印完成后，此时会接收到GpCom.ACTION_DEVICE_STATUS广播
        esc.addQueryPrinterStatus();
        // 发送数据
        return esc;
    }

    /**
     *
     * 负责接收printerchangerListener 发出的查询广播命令，与workrunable的打印机具体返回值数据命令
     */
    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {


            Bundle bundle = intent.getBundleExtra(READ_DATA);

            boolean isPeriod = intent.getBooleanExtra(READ_PERIOD,false);


            if (bundle != null) {


                //可以通过打印机名称来处理对应打印机的业务逻辑
                String name = bundle.getString(READ_NAME);


                byte[] buffer = bundle.getByteArray(READ_BUFFER_ARRAY);

                if ((buffer[0] & ESC_STATE_PAPER_ERR) > 0) {

                    Log.e("DOAING", name + "缺纸了");

                }
                if ((buffer[0] & ESC_STATE_COVER_OPEN) > 0) {

                    Log.e("DOAING", name + "没扣好盖子");

                }
                if ((buffer[0] & ESC_STATE_ERR_OCCURS) > 0) {

                    Log.e("DOAING", name + "打印错误");

                }
            }


            //发送打印机查询命令
            if (isPeriod) {

                printerChangeListener.executePrinterStatusCommand();

            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        ethernetPort1.closePort();
        ethernetPort2.closePort();

        //  printerReader.isRun = false;


        Log.e("DOAING", "onDestroy");
    }


    /**
     * 判断是实时状态（10 04 02）还是查询状态（1D 72 01）
     */
    private int judgeResponseType(byte r) {
        byte FLAG = 0x10;
        return (byte) ((r & FLAG) >> 4);
    }


    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }
}
