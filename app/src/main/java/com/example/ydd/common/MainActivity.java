package com.example.ydd.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ydd.mylibrary.print.config.PrinterChangeListener;
import com.example.ydd.mylibrary.print.config.WorkRunnable;
import com.example.ydd.mylibrary.print.printer.Config;
import com.example.ydd.mylibrary.print.printer.PinterPort;
import com.example.ydd.mylibrary.print.printer.PrintService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.LabelCommand;
import com.gprinter.io.EthernetPort;
import com.gprinter.io.PortManager;

import java.io.IOException;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        printService = new PrintService(this);
        pinterPort = new PinterPort();
        pinterPort.setName("WIFI");
        pinterPort.setTypePrint(Config.WIFI);
        pinterPort.setAddress("192.168.2.248");
        pinterPort.setPort("9100");
        printService.printerExist(pinterPort);
        Log.e("Common", "----"+printService.printerExist(pinterPort).openPort());

//        //打印机的状态，命令都在广播处理
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(PRINTER_URL);
//        LocalReceiver localReceiver = new LocalReceiver();
//        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, intentFilter);
//
//
//
//        //初始化监听监听器,全局单例模式
//        printerChangeListener = PrinterChangeListener.getInstance(getApplicationContext(),new ConcurrentHashMap());
//
//
//        //------------------------- 测试数据部分------------------------
//        ethernetPort1 = new EthernetPort("192.168.2.248", 9100);
//        Log.e("DOAING", ethernetPort1.openPort() + " 第一个打开");
//
//
//
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
//        findViewById(R.id.printa_bt).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //开启周期监听，当前设置2000毫秒一个监听
//                printerChangeListener.openPeriodPrinterListener(2000L);
//
//            }
//        });
//
//        findViewById(R.id.printb_bt).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //发起单次的查询命令
//                printerChangeListener.openOncePrinterListener();
//
//            }
//        });
//
//        findViewById(R.id.printc_bt).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //释放指定打印机的监听线程
//                if(printerChangeListener.freeWorkPool("101")){
//
//                    Toast.makeText(MainActivity.this,"移除了101的监听",Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//        findViewById(R.id.printd_bt).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                printerChangeListener.freeWorkPool("201");
//
//            }
//        });
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
}
