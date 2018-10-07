package com.example.ydd.mylibrary.print.printer;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.couchbase.lite.Document;
import com.gprinter.command.EscCommand;
import com.gprinter.io.BluetoothPort;
import com.gprinter.io.EthernetPort;
import com.gprinter.io.PortManager;

import java.io.IOException;
import java.util.List;

import static com.example.ydd.mylibrary.print.printer.PrinterCouchBase.getAllPrinter;
import static com.example.ydd.mylibrary.print.printer.PrinterCouchBase.getSinglePrint;

public class PrintService {

    Context context;
    public PrintService(Context context){
        this.context = context;
        new PrinterCouchBase(context);
        Config.requestAllPower((Activity) context);
    }
    
    //注册打印机
    private PortManager registeredWifi(String address, String port){
        return new EthernetPort(address, Integer.parseInt(port));
    }
    //注册打印机
    private PortManager registeredBluetooth(String address){
        return  new BluetoothPort(address);
    }

    //打印机是否存在
    public PortManager printerExist(PinterPort pinterPort){
        PortManager mPort = Config.getPrinter(pinterPort.getName());
        //判断Map是否有这个打印机
        if ( mPort == null){
            Document doc = getSinglePrint(pinterPort.getName());
            //判断couchBase是否有这个打印机
            if ( doc == null){
                mPort = savePrinterToMap(pinterPort);
                //存到couchBase
                PrinterCouchBase.setSavePinterPort(pinterPort);

            }else{
                mPort = savePrinterToMap(pinterPort);
            }
        }
        return mPort;
    }

    //判断那个类型的打印机，并注册Map
    private PortManager savePrinterToMap(PinterPort pinterPort){
        PortManager mPort = null;
        if (pinterPort.getTypePrint() == Config.WIFI){
            mPort = registeredWifi(pinterPort.getAddress(),pinterPort.getPort());
        }else if (pinterPort.getTypePrint() == Config.BLUETOOTH){
            mPort = registeredBluetooth(pinterPort.getAddress());
        }
        //存到全局Map
        Config.putPrinter(pinterPort.getName(),mPort);
        return mPort;
    }
    /**
     *  初始化所有打印机
     */
    public List<Document> getAllPrint(){
        List<Document> documentList = getAllPrinter();
        if (documentList.size() > 0){
            for (Document doc : documentList) {
                initPrinter(doc).openPort();
            }
        }
        return documentList;
    }


    /**
     * 初始化指定的打印机
     * @param doc
     */
    public PortManager initPrinter(Document doc){
        if (doc.getInt("printerType") == Config.WIFI){
            return registeredWifi(doc.getString("address"),doc.getString("port"));
        }else if (doc.getInt("printerType") == Config.BLUETOOTH){
            return registeredBluetooth(doc.getString("address"));
        }
        return null;
    }

    public PortManager FindPrinter(String name){
        //判断Map初始化打印机
        PortManager mPort = Config.getPrinter(name);
        if ( mPort == null){
            //查询打印机是否注册
            Document doc = getSinglePrint(name);
            if (doc != null){
                //如果注册打开打印机
                mPort = initPrinter(doc);
                Config.putPrinter(doc.getString("name"),mPort);
            }else{
                return null;
            }
        }
        return mPort;
    }

    /**
     * 把数据分发到指定名称的打印机
     */
    public boolean detchPrinter(String name,EscCommand esc){
        PortManager mPort = FindPrinter(name);
        if (mPort == null){
            Toast.makeText(context,"打印机不存在",Toast.LENGTH_LONG).show();
            return false;
        }else {
            mPort.openPort();
            try {
                mPort.writeDataImmediately(esc.getCommand(), 0, esc.getCommand().size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    //关闭打印机
    public void  closePort(PortManager portManager){
        portManager.closePort();
    }


}
