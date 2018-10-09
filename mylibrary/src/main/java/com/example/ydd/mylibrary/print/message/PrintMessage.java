package com.example.ydd.mylibrary.print.message;

import com.example.ydd.mylibrary.units.MyBigDecimal;
import com.gprinter.command.EscCommand;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import static com.gprinter.command.EscCommand.ENABLE.OFF;
import static com.gprinter.command.EscCommand.ENABLE.ON;
import static com.gprinter.command.EscCommand.FONT.FONTA;

public class PrintMessage {

    private PrintData printData;

    public PrintMessage(PrintData printData){
        this.printData = printData;
    }
    /**
     * 生成order发送票据
     */
    public EscCommand orderSendReceiptWithResponse(String clientname,int wType) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        String date = df.format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");// 设置日期格式
        String endtime = sdf.format(new Date());
        EscCommand esc = new EscCommand();
        // 打印标题居中
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        // 设置字体宽高增倍
        esc.addSelectPrintModes(FONTA, OFF, ON, ON, OFF); // 设置为倍高倍宽
        esc.addText(clientname + "\n");// 打印文字

        //打印并换行
        esc.addPrintAndLineFeed();
        // 打印文字

        esc.addSelectPrintModes(FONTA, OFF, OFF, OFF, OFF);// 取消倍高倍宽
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐
        String serNum = printData.getSerNum();
        String areaName = printData.getAreaName();
        String tableName = printData.getTableName();
        int currentPersions = printData.getCurrentPersons();
        List<Dish> dishList = printData.getDishList();
        if(wType==2)//  80型宽度
        {

            esc.addText("流水号:" + serNum + "\n");//流水号生成机制开发

            esc.addText("房间:" + areaName + "   " + "桌位：" + tableName + "\n");// 打印文字

            esc.addText("人数:" + currentPersions + "\n");//流水号生成机制开发

            esc.addText("时间:" + date + " " + endtime + "\n"); // 时间


            esc.addText("------------------------------------------\n");//42个空格，21个汉字
            esc.addTurnEmphasizedModeOnOrOff(ON);
            esc.addSelectCharacterFont(FONTA);
            esc.addText("菜品名称                  数量         \n"); // 菜品名称(8)10   8 数量(4) 8
            esc.addTurnEmphasizedModeOnOrOff(OFF);
            esc.addText("\n");

            for (int i = 0; i < dishList.size(); i++) {

                float num = 1; // 数量 默认为1
                esc.addSelectCharacterFont(FONTA);
                Dish dish = dishList.get(i);
                num = dish.getCount();
                String dishesName = "";
                esc.addSelectPrintModes(FONTA,OFF,ON,OFF,OFF);
                if (dish.getGoodsType() == 0) {
                    dishesName = dish.getName();
                }else if (dish.getGoodsType() == 1) {
                    dishesName = dish.getName()+"(退)";
                }else if (dish.getGoodsType() == 2) {
                    dishesName = dish.getName()+"(赠)";
                }
                if ( dish.getGoodsAlter() == 1){
                    dishesName = "(改)  "+dishesName;
                }
                esc.addText(dishesName);
                String temp = dish.getTasteName();
                if (temp == null || "".equals(temp)) {
                    try {
                        for (int j = 0; j < (32 - dishesName.getBytes("gbk").length); j++)
                            esc.addText(" ");
                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();
                    }
                } else {
                    esc.addText("(" + temp + ")");

                    try {

                        for (int j = 0; j < (26 - dishesName.getBytes("gbk").length

                                - temp.getBytes("gbk").length - 2); j++)

                            esc.addText(" ");

                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();

                    }

                }
                esc.addText("" + num+"\n");
                if (dish.isPromotion()){

                    for (String str: dish.getPromotionDishList()){

                        esc.addText(str+"\n");
                    }

                }
                if (dish.getDescription() != null){
                    esc.addText("菜品备注：  " + dish.getDescription()+"\n");
                }
                //换行
                esc.addPrintAndLineFeed();

            }
            if (printData.getDescription() != null){
                esc.addText("订单备注：  " + printData.getDescription()+"\n\n");
            }
            esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);
            if (printData.getEmployeeName() != null) {
                esc.addText("操作员:  " + printData.getEmployeeName() +"\n\n\n");
            }
            esc.addPrintAndLineFeed();


            byte len = 0x01;

            esc.addCutAndFeedPaper(len);

        }else //58型打印机
        {

            esc.addText("流水号:" + serNum + "\n");//流水号生成机制开发

            esc.addText("房间:" + areaName + "   " + "桌位：" + tableName + "\n");// 打印文字

            esc.addText("人数:" + currentPersions + "\n");//流水号生成机制开发

            esc.addText("时间:" + date + " " + endtime + "\n"); // 时间

            esc.addText("--------------------------------\n"); //32横线==16个汉字

            esc.addText("菜品名称                数量    \n"); // 菜品名称+16个空格即占12个汉字长度；  数量+4个空格即占4个汉字长度 )

            esc.addText("\n");



            esc.addSetHorAndVerMotionUnits((byte)8, (byte) 0);//设置移动单位



            for (int i = 0; i < dishList.size(); i++)

            {
                Dish dish = dishList.get(i);
                String dishesName = "";
                if (dish.getGoodsType() == 0) {
                    dishesName = dish.getName();
                }else if (dish.getGoodsType() == 1) {
                    dishesName =  dish.getName()+"(退)";
                }else if (dish.getGoodsType() == 2) {
                    dishesName =  dish.getName()+"(赠)";
                }
                String temp = dish.getTasteName();

                float num = dish.getCount();

                esc.addSetAbsolutePrintPosition((short) 0);
                if (temp == null || "".equals(temp))//无口味
                {
                    esc.addText(dishesName);
                }

                else//有口味

                {

                    esc.addText(dishesName+temp);

                }
                esc.addSetAbsolutePrintPosition((short) 13);
                esc.addText("" + num+"\n");

                if (dish.isPromotion()){

                    for (String str: dish.getPromotionDishList()){
                        esc.addText(str+"\n");
                    }
                }
                if (dish.getDescription() != null){
                    esc.addText("菜品备注：  " + dish.getDescription()+"\n");
                }
                //换行
                esc.addPrintAndLineFeed();
            }
            if (printData.getDescription() != null){
                esc.addText("订单备注：  " + printData.getDescription()+"\n\n");
            }
            esc.addText("--------------------------------\n");
            esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);
            if (printData.getEmployeeName() != null) {
                esc.addText("操作员:  " + printData.getEmployeeName() +"\n\n\n");
            }
            esc.addPrintAndLineFeed();

        }
       return esc;
    }

    /**
     * 重新发送打印模板
     * @param clientname
     * @param widthType
     * @return
     */
    public EscCommand resendPrint(String clientname,int widthType){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        String date = df.format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");// 设置日期格式
        String endtime = sdf.format(new Date());
        EscCommand esc = new EscCommand();
        // 打印标题居中
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        // 设置字体宽高增倍
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF); // 设置为倍高倍宽
        esc.addText(clientname + "\n");// 打印文字

        //打印并换行
        esc.addPrintAndLineFeed();
        // 打印文字

        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽

        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐
        String serNum = printData.getSerNum();
        String areaName = printData.getAreaName();
        String tableName = printData.getTableName();
        int currentPersons = printData.getCurrentPersons();
        List<Dish> dishList = printData.getDishList();
        if(widthType==2)//  80型宽度
        {

            esc.addText("流水号:" + serNum + "\n");//流水号生成机制开发

            esc.addText("房间:" + areaName + "   " + "桌位：" + tableName + "\n");// 打印文字

            esc.addText("人数:" + currentPersons + "\n");//流水号生成机制开发

            esc.addText("时间:" + date + " " + endtime + "\n"); // 时间


            esc.addText("------------------------------------------\n");//42个空格，21个汉字

            esc.addText("菜品名称                  数量         \n"); // 菜品名称(8)10   8 数量(4) 8

            esc.addText("\n");
            for (int i = 0; i < dishList.size(); i++) {
                Dish goodsDic = dishList.get(i);
                float num = 1; // 数量 默认为1

                num = goodsDic.getCount();
                String dishesName = "";
                if (goodsDic.getGoodsType() == 0) {
                    dishesName = goodsDic.getName();
                }else if (goodsDic.getGoodsType() == 1) {
                    dishesName = goodsDic.getName() +"(退)";
                }else if (goodsDic.getGoodsType() == 2) {
                    dishesName = goodsDic.getName() +"(赠)";
                }
                esc.addSelectPrintModes(FONTA,OFF,ON,OFF,OFF);
                esc.addText(dishesName);

                String temp="";
                if (goodsDic.getTasteName() != null) {
                    temp = goodsDic.getTasteName();
                }
                if (temp == null || "".equals(temp)) {

                    try {

                        for (int j = 0; j < (26 - goodsDic.getName().getBytes("gbk").length); j++)

                            esc.addText(" ");

                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();

                    }

                } else {

                    esc.addText("(" + temp + ")");

                    try {

                        for (int j = 0; j < (26 - goodsDic.getName().getBytes("gbk").length

                                - temp.getBytes("gbk").length - 2); j++)

                            esc.addText(" ");

                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();

                    }

                }
                esc.addText("" + num+"");
                esc.addPrintAndLineFeed();
                if (goodsDic.getPromotionDishList().size() != 0){
                    for (String str: goodsDic.getPromotionDishList()){
                        esc.addText(str+"\n");
                    }

                }

                if (goodsDic.getDescription() != null){
                    esc.addText("菜品备注：  " + goodsDic.getDescription()+"\n");
                }
                esc.addPrintAndLineFeed();
            }
            if (printData != null){
                if (printData.getDescription() != null){
                    esc.addText("订单备注：  " + printData.getDescription() +"\n\n");
                }
            }
            esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);
            if (printData.getEmployeeName() != null) {
                esc.addText("操作员:  " + printData.getEmployeeName()+"\n\n\n");
            }
            esc.addPrintAndLineFeed();


            byte len = 0x01;

            esc.addCutAndFeedPaper(len);



        }

        else //58型打印机
        {

            esc.addText("流水号:" + serNum + "\n");//流水号生成机制开发

            esc.addText("房间:" + areaName + "   " + "桌位：" + tableName + "\n");// 打印文字

            esc.addText("人数:" + currentPersons + "\n");//流水号生成机制开发

            esc.addText("时间:" + date + " " + endtime + "\n"); // 时间

            esc.addText("--------------------------------\n"); //32横线==16个汉字

            esc.addText("菜品名称                数量    \n"); // 菜品名称+16个空格即占12个汉字长度；  数量+4个空格即占4个汉字长度 )

            esc.addText("\n");



            esc.addSetHorAndVerMotionUnits((byte)8, (byte) 0);//设置移动单位



            for (int i = 0; i < dishList.size(); i++)

            {
                Dish goodsDic = dishList.get(i);
                String dishesName = goodsDic.getName();

                float num = goodsDic.getCount();
                String temp ="";
                if (goodsDic.getTasteName() != null){
                    temp = goodsDic.getTasteName();
                }
                esc.addSetAbsolutePrintPosition((short) 0);
                if (goodsDic.getGoodsType() == 0) {
                    dishesName = goodsDic.getName();
                }else if (goodsDic.getGoodsType() == 1) {
                    dishesName = goodsDic.getName()+"(退)";
                }else if (goodsDic.getGoodsType() == 2) {
                    dishesName = goodsDic.getName()+"(赠)";
                }
                esc.addText(dishesName+"\n");
                if (goodsDic.getTasteName() != null) {
                    temp = goodsDic.getName();
                }
                if (temp == null || "".equals(temp)) {

                    try {

                        for (int j = 0; j < (26 - goodsDic.getName().getBytes("gbk").length); j++)

                            esc.addText(" ");

                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();

                    }

                } else {

                    esc.addText("(" + temp + ")");

                    try {

                        for (int j = 0; j < (26 - goodsDic.getName().getBytes("gbk").length

                                - temp.getBytes("gbk").length - 2); j++)

                            esc.addText(" ");

                    } catch (UnsupportedEncodingException e) {

                        e.printStackTrace();

                    }

                }
                esc.addText("" + num+"");
                esc.addSetAbsolutePrintPosition((short) 13);
                esc.addText("" + num+"\n");
                if (goodsDic.getPromotionDishList().size() != 0){

                    for (String str: goodsDic.getPromotionDishList()){
                        esc.addText(str+"\n");
                        esc.addSetAbsolutePrintPosition((short) 13);
                    }

                }

                if (goodsDic.getDescription() != null){
                    esc.addText("菜品备注：  " + goodsDic.getDescription()+"\n");
                }
                //换行
                esc.addPrintAndLineFeed();
            }
            StringBuffer stringBuffer = new StringBuffer();
            if (printData.getDescription() != null){
                stringBuffer.append(printData.getDescription()+"\n");
            }
            esc.addText("订单备注：  " + stringBuffer.toString()+"\n\n");
            esc.addText("--------------------------------\n");
            esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);
            if (printData.getEmployeeName() != null) {
                esc.addText("操作员:  " + printData.getEmployeeName() +"\n\n\n");
            }
            esc.addPrintAndLineFeed();

        }
        // 加入查询打印机状态，打印完成后，此时会接收到GpCom.ACTION_DEVICE_STATUS广播
        esc.addQueryPrinterStatus();

        return esc;
    }

    /**
     * 结账打印小票
     */
    public EscCommand payPrint(String clientname,int widthType){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        String date = df.format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");// 设置日期格式
        String endtime = sdf.format(new Date());
        EscCommand esc = new EscCommand();
        // 打印标题居中
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        // 设置字体宽高增倍
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF); // 设置为倍高倍宽
        esc.addText("前台打印机" + "\n");// 打印文字
        //打印并换行
        esc.addPrintAndLineFeed();
        // 打印文字
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF
                , EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐

        String serNum = printData.getSerNum();
        String areaName = printData.getAreaName();
        String tableName = printData.getTableName();
        int currentPersons = printData.getCurrentPersons();
        List<Dish> dishList = printData.getDishList();
        if (widthType ==2){
            esc.addText("流水号:" + serNum + "\n");//流水号生成机制开发

            esc.addText("房间:" + areaName + "   " + "桌位：" + tableName + "\n");// 打印文字

            esc.addText("人数:" + currentPersons + "\n");//流水号生成机制开发

            esc.addText("时间:" + date + " " + endtime + "\n"); // 时间


            esc.addText("--------------------------------------------\n");//42个空格，21个汉字
            esc.addText("菜品名称           单价       数量      金额 \n"); // 菜品名称(14) 单价(6) 数量(5) 金额(7)
            esc.addText("\n");
            esc.addSetHorAndVerMotionUnits((byte) 11, (byte) 0);//设置移动单位
            for (int i = 0; i < dishList.size(); i++) {

                Dish dishDoc = dishList.get(i);
                String dishesName="";
                if (dishDoc.getGoodsType() == 0) {
                    dishesName = dishDoc.getName();
                }else if (dishDoc.getGoodsType() == 1) {
                    dishesName =  dishDoc.getName()+"(退)";
                }else if (dishDoc.getGoodsType() == 2) {
                    dishesName =  dishDoc.getName()+"(赠)";
                }
                float num =  dishDoc.getCount();
                float total = dishDoc.getPrice();
                String temp;
                if (dishDoc.getTasteName() != null) {
                    temp = dishDoc.getTasteName();
                }else{
                    temp = "";
                }
                int length = 0;
                esc.addSetAbsolutePrintPosition((short) 0);
                printDish80(temp,length,dishesName,esc,num,total);
                //换行

                esc.addPrintAndLineFeed();

            }
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            esc.addText("--------------------------------------------\n");
            esc.addText("合计:" + printData.getPayment().getTotalPrice() + "\n");//
            esc.addText("--------------------------------------------\n");
            if (printData.getPayment().getDiscountsList().size() > 0) {
                for (Discounts pdd : printData.getPayment().getDiscountsList()) {
                    if (pdd.getType() == 1) {
                        esc.addText("打折:" + MyBigDecimal.sub(1, MyBigDecimal.div(pdd.getSubtotal(), printData.getPayment().getTotalPrice(), 1), 1) + "\n");//
                    } else if (pdd.getType() == 2) {
                        esc.addText("满减:" + pdd.getSubtotal() + "\n");//
                    } else {
                        esc.addText("优惠:" +  pdd.getDiscounts() + "\n");//
                    }
                    esc.addText("--------------------------------------------\n");
                }
            }

            if (printData.getPayment().getWipeZero() != 0.0) {
                float wipe = printData.getPayment().getWipeZero();
                esc.addText("抹零:" + wipe + "\n");// 打印文字
                esc.addText("--------------------------------------------\n");
            }
            esc.addText("应收:" + printData.getPayment().getReceivable() + "\n");//
            if (printData.getPayment().isPay()) {
                esc.addText("--------------------------------------------\n");
                StringBuffer stringBuffer = new StringBuffer("");
                List<Discounts> payDetailList = printData.getPayment().getPayDetailList();
                if (payDetailList != null && !payDetailList.isEmpty()) {


                    for (int i = 0; i < payDetailList.size(); i++) {

                        Discounts p = payDetailList.get(i);

                        switch (p.getType()) {

                            case 1:
                                stringBuffer.append("实收现金 " + p.getSubtotal());
                                break;
                            case 2:
                                stringBuffer.append("实收银行卡 " + p.getSubtotal());
                                break;
                            case 3:
                                stringBuffer.append("实收微信 " + p.getSubtotal());
                                break;
                            case 4:
                                stringBuffer.append("实收支付宝 " + p.getSubtotal());
                                 break;
                            case 5:
                                stringBuffer.append("实收美团 " + p.getSubtotal());
                                break;
                            case 6:
                                stringBuffer.append("实收会员卡 " + p.getSubtotal());
                                break;
                            case 11:
                                stringBuffer.append("实收团购 " + p.getSubtotal());
                                break;
                            default:
                                break;
                        }
                    }
                }

                esc.addText(stringBuffer.toString() + "\n");


                esc.addText("\n\n");
                if (printData.getEmployeeName() != null) {
                    esc.addText("操作员:  " + printData.getEmployeeName());
                }
            }

            esc.addPrintAndLineFeed();
            esc.addText("--------------------------------------------\n\n\n");

            esc.addPrintAndLineFeed();
            // 加入查询打印机状态，打印完成后，此时会接收到GpCom.ACTION_DEVICE_STATUS广播

            esc.addQueryPrinterStatus();



            byte len = 0x01;

            esc.addCutAndFeedPaper(len);
        }else {

            esc.addText("流水号:" + serNum + "\n");//流水号生成机制开发

            esc.addText("房间:" + areaName + "   " + "桌位：" + tableName + "\n");// 打印文字

            esc.addText("人数:" + currentPersons + "\n");//流水号生成机制开发

            esc.addText("时间:" + date + " " + endtime  + "\n"); // 时间

            esc.addText("--------------------------------\n");
            esc.addText("菜品名称    单价   数量   金额 \n"); // 菜品名称(14) 单价(6) 数量(5) 金额(7)
            esc.addText("\n");
            esc.addSetHorAndVerMotionUnits((byte) 8, (byte) 0);//设置移动单位
            for (int i = 0; i < dishList.size(); i++) {

                Dish dishDoc = dishList.get(i);
                String dishesName="";
                if (dishDoc.getGoodsType() == 0) {
                    dishesName = dishDoc.getName();
                }else if (dishDoc.getGoodsType() == 1) {
                    dishesName =  dishDoc.getName()+"(退)";
                }else if (dishDoc.getGoodsType() == 2) {
                    dishesName =  dishDoc.getName()+"(赠)";
                }
                float num =  dishDoc.getCount();
                float total = dishDoc.getPrice();
                String temp;
                if (dishDoc.getTasteName() != null) {
                    temp = dishDoc.getTasteName();
                }else{
                    temp = "";
                }
                int length = 0;
                esc.addSetAbsolutePrintPosition((short) 0);
                printDish58(temp,length,dishesName,esc,num,total);
                //换行

                esc.addPrintAndLineFeed();

            }
            esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
            esc.addText("--------------------------------\n");
            esc.addText("合计:" + printData.getPayment().getTotalPrice() + "\n");//
            esc.addText("--------------------------------\n");
            List<Discounts> promotionDiscountDetailList = printData.getPayment().getDiscountsList();
            if (promotionDiscountDetailList.size() > 0) {
                for (Discounts pdd : promotionDiscountDetailList) {
                    if (pdd.getType() == 1) {
                        esc.addText("打折:" + MyBigDecimal.sub(1, MyBigDecimal.div(pdd.getSubtotal(),
                                printData.getPayment().getTotalPrice(), 2), 2) + "\n");//
                    } else if (pdd.getType() == 2) {
                        esc.addText("满减:" + pdd.getSubtotal() + "\n");//
                    } else {
                        esc.addText("优惠:" + pdd.getDiscounts() + "\n");//
                    }
                    esc.addText("--------------------------------\n");
                }
            }

            if (printData.getPayment().getWipeZero() != 0.0) {
                float wipe = printData.getPayment().getWipeZero();
                esc.addText("抹零:" + wipe + "\n");// 打印文字
                esc.addText("--------------------------------\n");
            }
            esc.addText("应收:" + printData.getPayment().getReceivable() + "\n");//
            if (printData.getPayment().isPay()) {
                esc.addText("--------------------------------\n");
                StringBuffer stringBuffer = new StringBuffer("");
                List<Discounts> payDetailList = printData.getPayment().getPayDetailList();
                if (payDetailList != null && !payDetailList.isEmpty()) {


                    for (int i = 0; i < payDetailList.size(); i++) {

                        Discounts p = payDetailList.get(i);

                        switch (p.getType()) {

                            case 1:
                                stringBuffer.append("实收现金 " + p.getSubtotal());
                                break;
                            case 2:
                                stringBuffer.append("实收银行卡 " + p.getSubtotal());
                                break;
                            case 3:
                                stringBuffer.append("实收微信 " + p.getSubtotal());
                                break;
                            case 4:
                                stringBuffer.append("实收支付宝 " + p.getSubtotal());
                                break;
                            case 5:
                                stringBuffer.append("实收美团 " + p.getSubtotal());
                                break;
                            case 6:
                                stringBuffer.append("实收会员卡 " + p.getSubtotal());
                                break;
                            case 11:
                                stringBuffer.append("实收团购 " + p.getSubtotal());
                                break;
                            default:
                                break;
                        }
                    }
                }

                esc.addText(stringBuffer.toString() + "\n");

            }
            if (printData.getEmployeeName() != null) {
                esc.addText("操作员:  " + printData.getEmployeeName());
            }

            esc.addPrintAndLineFeed();
            esc.addText("--------------------------------\n\n\n");

            esc.addPrintAndLineFeed();
            // 加入查询打印机状态，打印完成后，此时会接收到GpCom.ACTION_DEVICE_STATUS广播

            esc.addQueryPrinterStatus();

        }

        return esc;
    }
    //80打印机 打印菜品
    private void printDish80(String temp,int length,String dishesName, EscCommand esc,float num,float total){
        if (temp == null || "".equals(temp))//无口味
        {
            length = dishesName.length();
            if (length > 8) {
                esc.addText(dishesName + "\n");
            } else {
                esc.addText(dishesName);
            }
            try {

                for (int j = 0; j < (19 - dishesName.getBytes("gbk").length); j++)

                    esc.addText(" ");

            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();

            }
        } else//有口味
        {
            length = (dishesName + "(" + temp + ")").length();
            if (length > 8) {
                esc.addText(dishesName + "(" + temp + ")\n");
            } else {
                esc.addText(dishesName + "(" + temp + ")");
            }
            try {

                for (int j = 0; j < (19 - dishesName.getBytes("gbk").length

                        - temp.getBytes("gbk").length - 2); j++)

                    esc.addText(" ");

            } catch (UnsupportedEncodingException e) {



                e.printStackTrace();

            }
        }

        if (length > 8) {
            for (int j = 0; j < 19; j++) {
                esc.addText(" ");
            }
            esc.addText(""+total);
            try {

                for (int j = 0; j < (11 - (""+total).getBytes("gbk").length); j++)

                    esc.addText(" ");

            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();

            }
            esc.addText("" + num);
            try {
                for (int j = 0; j < (10 - (""+num).getBytes("gbk").length); j++)
                    esc.addText(" ");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            esc.addText("" + MyBigDecimal.mul(total, num, 1) + "\n");
        } else {
            esc.addText("" + total);
            try {

                for (int j = 0; j < (11 - (""+total).getBytes("gbk").length); j++)

                    esc.addText(" ");

            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();

            }
            esc.addText("" + num);
            try {

                for (int j = 0; j < (10 - (""+num).getBytes("gbk").length); j++)

                    esc.addText(" ");

            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();

            }
            esc.addText("" + MyBigDecimal.mul(total, num, 1) + "\n");
        }
    }
    //58打印机 打印菜品
    private void printDish58(String temp,int length,String dishesName, EscCommand esc,float num,float total){
        if (temp == null || "".equals(temp))//无口味
        {
            length = dishesName.length();
            if (length > 5) {
                esc.addText(dishesName + "\n");
            } else {
                esc.addText(dishesName);
            }
            try {

                for (int j = 0; j < (12 - dishesName.getBytes("gbk").length); j++)

                    esc.addText(" ");

            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();

            }
        } else//有口味
        {
            length = (dishesName + "(" + temp + ")").length();
            if (length > 5) {
                esc.addText(dishesName + "(" + temp + ")\n");
            } else {
                esc.addText(dishesName + "(" + temp + ")");
            }
            try {

                for (int j = 0; j < (12 - dishesName.getBytes("gbk").length

                        - temp.getBytes("gbk").length - 2); j++)

                    esc.addText(" ");

            } catch (UnsupportedEncodingException e) {


                e.printStackTrace();

            }
        }

        if (length > 5) {
            for (int j = 0; j < 12; j++) {
                esc.addText(" ");
            }
            esc.addText("" + total);
            try {

                for (int j = 0; j < (7 - ("" + total).getBytes("gbk").length); j++)

                    esc.addText(" ");

            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();

            }
            esc.addText("" + num);
            try {
                for (int j = 0; j < (7 - ("" + num).getBytes("gbk").length); j++)
                    esc.addText(" ");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            esc.addText("" + MyBigDecimal.mul(total, num, 1) + "\n");
        } else {
            esc.addText("" + total);
            try {

                for (int j = 0; j < (7 - ("" + total).getBytes("gbk").length); j++)

                    esc.addText(" ");

            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();

            }
            esc.addText("" + num);
            try {

                for (int j = 0; j < (7 - ("" + num).getBytes("gbk").length); j++)

                    esc.addText(" ");

            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();

            }
            esc.addText("" + MyBigDecimal.mul(total, num, 1) + "\n");
        }
    }
}
