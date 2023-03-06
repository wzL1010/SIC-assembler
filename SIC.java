/**
 * 2022/5/2
 * SIC assembler
 */

 import java.io.*;
 import java.util.Locale;
 import java.util.Scanner;
 import java.util.ArrayList;
 import java.lang.System;
 
 public class App {
 
   public static String zero_format(int location, int count) {
     String tmp = "";
     String hex_str = Integer.toHexString(location);
     for (int i = 0; i < count - hex_str.length(); i++) {
       tmp += "0";
     }
     tmp += hex_str;
     return tmp;
   }
 
   public static void main(String[] args) throws Exception {
     //建立OP表
     String[] op_TAB = {"ADD", "ADDF", "ADDR", "AND", "CLEAR", "COMP", "COMPF", "COMPR", "DIV", "DIVF", "DIVR",
         "FIX", "FLOAT", "HIO", "J", "JEQ", "JGT", "JLT", "JSUB", "LDA", "LDB", "LDCH", "LDF", "LDL", "LDS",
         "LDT", "LDX", "LPS", "MUL", "MULF", "MULR", "NORM", "OR", "RD", "RMO", "RSUB", "SHIFTL", "SHIFTR",
         "SIO", "SSK", "STA", "STB", "STCH", "STF", "STI", "STL", "STS", "STSW", "STT", "STX", "SUB", "SUBF",
         "SUBR", "SVC", "TD", "TIO", "TIX", "TIXR", "WD"};
     //建立OP CODE表
     String[] opCode = {"18", "58", "90", "40", "B4", "28", "88", "A0", "24", "64", "9C", "C4", "C0", "F4", "3C",
         "30", "34", "38", "48", "00", "68", "50", "70", "08", "6C", "74", "04", "E0", "20", "60", "98", "C8",
         "44", "D8", "AC", "4C", "A4", "A8", "F0", "EC", "0C", "78", "54", "80", "D4", "14", "7C", "E8", "84",
         "10", "1C", "5C", "94", "B0", "E0", "F8", "2C", "B8", "DC"};
     //讀入輸入的SIC程式碼
 
     String fileName ="";
     if (args.length != 1){
       System.out.println("pls input file name");
       Scanner sc = new Scanner(System.in);
       fileName = sc.next();
       sc.close();
       //return;
     }else {
       fileName=args[0];
     }
     FileReader fr = new FileReader(fileName);
     BufferedReader br = new BufferedReader(fr);
     Scanner scn = new Scanner(br);
     //建立arraylist來儲存輸入的資料
     ArrayList<String> input = new ArrayList<>();
     ArrayList<String> input_opcode = new ArrayList<>();
     ArrayList<String> label = new ArrayList<>();
     ArrayList<Integer> location = new ArrayList<>();
     ArrayList<String> arg = new ArrayList<>();
     ArrayList<String> object_code = new ArrayList<>();
 
     //boolean commit = false;
     //boolean has_error = false;
     int index = 0;    //紀錄pass1讀到第幾個資料
     int index_pass2 = 0;    //紀錄pass2讀到第幾個資料
     int start_address = 0;  //紀錄要從哪個位址開始
     int address = 0;    //紀錄目前的loc加到哪了
     //System.out.println(Integer.toHexString(0x9+0x3));
     boolean commit = false;
     while (scn.hasNext()) {
       String temp = scn.next();
       //處理特定格式的註解 模擬平時多行註解格式 開頭需加/* 然後以*/結尾  EX:/* xxxxxxxxx */
       if (temp.startsWith("/*")) {
         if (commit == false) {
           commit = true;
           //System.out.println("commit T");
           if (temp.endsWith("*/")) {
             commit = false;
             //System.out.println("same line commit F");
             continue;
           }
         }
         continue;
       }
       if (commit == true) {
         if (temp.endsWith("*/")) {
           commit = false;
           //System.out.println("commit F");
         }
         continue;
       }
       input.add(temp.toUpperCase(Locale.ROOT)); //將輸入存到arraylist
 
     }
 
     while (index < input.size()) { //run frist pass and save location mapping
       boolean islabel = true;
       //判斷是否為opcode
       for (String s : op_TAB) {
         if (input.get(index).equals(s)) {
           islabel = false;
         }
       }
       //如果是opcode
       if (!islabel || input.get(index).equals("START") || input.get(index).equals("END") || input.get(index).equals("RESW") || input.get(index).equals("RESB") || input.get(index).equals("BYTE") || input.get(index).equals("WORD")) { //compare op_TAB
         input_opcode.add(input.get(index));
         location.add(address);
         switch (input.get(index)) {//if it is op code
           case "START":
             String temp = input.get(++index);
             start_address = Integer.valueOf(temp, 16);// hex to oct
             arg.add(temp);
             location.set(0, start_address); //更改原本的loc(因為一開始存入arraylist時還沒讀到START後面的值 所以初始為0)
             address = start_address;  //loc要從哪開始加
             //System.out.println(address);
             break;
           case "END":
             temp = input.get(++index);
             arg.add(temp);
             break;
           case "RESW":
             temp = input.get(++index);
             arg.add(temp);
             address += Integer.parseInt(temp) * 3;
             break;
           case "RESB":
             temp = input.get(++index);
             arg.add(temp);
             address += Integer.parseInt(temp);
             break;
           case "BYTE":
             temp = input.get(++index);
             arg.add(temp);
             if (temp.charAt(0) == 'C') {  //C
               address += input.get(index).length() - 3;
             } else if (temp.charAt(0) == 'X') { //X
               address += 1;
             }
             break;
           case "WORD":
             temp = input.get(++index);
             arg.add(temp);
             address += 3;
             break;
           default:
             if (input.get(index).compareTo("RSUB") != 0) { // isn`t RSUB
               temp = input.get(++index);
               arg.add(temp);
             } else {
               arg.add("   ");
             }
             address += 3;
             break;
         }
         if (label.size() < input_opcode.size()) {
           label.add("   ");
         }
         index++;
         //System.out.println(Integer.toHexString(address));
       } else {//if it is label
         for(String s:label){
           if(input.get(index).equals(s)){
             System.out.println("Label " +"'"+s+"'"+" duplicate");
             //has_error = true;
             return ;
           }
         }
         label.add(input.get(index));
         index++;
       }
     }
 
     //System.out.println(input_opcode.size()+" "+label.size().toString +" "+arg.size().toString);
 
 
     //pass2
     //System.out.printf("Loc     Label       OP          operand     ObjectCode\t\r\n");
     while (index_pass2 < input_opcode.size()) {
       if (input_opcode.get(index_pass2).equals("START") || input_opcode.get(index_pass2).equals("END") || input_opcode.get(index_pass2).equals("RESW") || input_opcode.get(index_pass2).equals("RESB")) {
         object_code.add("  ");
         index_pass2++;
       } else {
         String objcode = "";
         switch (input_opcode.get(index_pass2)) {
           case "RSUB":
             object_code.add("4C0000");
             break;
           case "WORD":
             //前面補0
             int add_zero = 6 - arg.get(index_pass2).length();
             for (int i = 0; i < add_zero; i++) {
               objcode += "0";
             }
             objcode += Integer.toHexString(Integer.parseInt(arg.get(index_pass2)));
             object_code.add(objcode);
             break;
           case "BYTE":
             int char_length = arg.get(index_pass2).length() - 1;
             if (arg.get(index_pass2).charAt(0) == 'C') {  //C
               for (int i = 2; i < char_length; i++) {
                 int temp = arg.get(index_pass2).charAt(i);
                 objcode += String.valueOf(Integer.toHexString(temp));
               }
             } else if (arg.get(index_pass2).charAt(0) == 'X') { //x
               for (int i = 2; i < char_length; i++) {
                 objcode += arg.get(index_pass2).charAt(i);
               }
             }
             object_code.add(objcode);
             break;
           default:
             int compare_i = 0;
             int compare_j = 0;
             String pre_str = "";
             String pos_str = "";
             for (String s : op_TAB) {
               if (input_opcode.get(index_pass2).equals(s)) {
                 pre_str = opCode[compare_i];
                 break;
               } else {
                 compare_i++;
               }
             }
             String arg_label = "";
             //用","來判別是否使用其他暫存器
             int i = arg.get(index_pass2).indexOf(",");
             if (arg.get(index_pass2).contains(",")) {
               for (int j = 0; j < i; j++) {
                 arg_label += arg.get(index_pass2).charAt(j);
               }
             } else {
               arg_label = arg.get(index_pass2);
             }
             int loc = 0;
             for (String x : label) {
               if (arg_label.equals(x)) {
                 loc = location.get(compare_j);
                 break;
               } else {
                 compare_j++;
               }
             }
             //用到x暫存器要+8000
             if (arg.get(index_pass2).charAt(i + 1) == 'X' && i != -1) {
               loc += 32768;
             }
             pos_str = Integer.toHexString(loc);
             objcode = pre_str + pos_str;
             object_code.add(objcode);
             break;
         }
         index_pass2++;
       }
     }
     PrintWriter listWriter = new PrintWriter("SIC_Listing.txt");
     for (int i = 0; i < input_opcode.size(); i++) {
    /* //處理loc不是從1000開始計算時前面補0的情況
     String loc = "";
     if (Integer.toHexString(location.get(i)).length() < 4) {
       int loc_add_zero = 4 - Integer.toHexString(location.get(i)).length();
       for (int j = 0; j < loc_add_zero; j++) {
         loc += "0";
       }
       loc += Integer.toHexString(location.get(i));
     } else {
       loc += Integer.toHexString(location.get(i));
     }*/
       // System.out.println(Integer.toHexString(location.get(i))+"    "+label.get(i)+"    "+input_opcode.get(i)+"    "+arg.get(i)+"    "+object_code.get(i));
 
       //輸出結果
       String loc = zero_format(location.get(i), 4);
       System.out.printf("%s\t%-8s\t%-8s\t%-10s\t%s\t\r\n", loc.toUpperCase(Locale.ROOT), label.get(i), input_opcode.get(i), arg.get(i), object_code.get(i).toUpperCase(Locale.ROOT));
       listWriter.printf("%s\t%-8s\t%-8s\t%-10s\t%s\t\r\n", loc.toUpperCase(Locale.ROOT), label.get(i), input_opcode.get(i), arg.get(i), object_code.get(i).toUpperCase(Locale.ROOT));
     }
     listWriter.close();
 
 
     PrintWriter objWriter = new PrintWriter("SIC_Object.txt");
     int preloc_index = 0;
     boolean obj_null = false;
     int obj_null_loc = 0;
     int count = 0;
     int T_record_length = 0;
     String T_record = "";
     String T_record_content = "";
     for (int k = 0; k < input_opcode.size(); k++) {
       if (k == 0) {
         //印出H_record
         System.out.println("H^" + label.get(k) + " ^" + zero_format(location.get(k), 6).toUpperCase(Locale.ROOT) + "^" + zero_format((location.get(location.size() - 1) - location.get(k)), 6).toUpperCase(Locale.ROOT));
         objWriter.println("H^" + label.get(k) + " ^" + zero_format(location.get(k), 6).toUpperCase(Locale.ROOT) + "^" + zero_format((location.get(location.size() - 1) - location.get(k)), 6).toUpperCase(Locale.ROOT));
       } else if (input_opcode.get(k).equals("END")) {
         int end_loc = 0;
         for (int i = 0; i < label.size(); i++) {
           if (label.get(i).equals(arg.get(k))) {
             end_loc = i;
           }
         }
         if (count != 10) {
           if (obj_null_loc != 0) {
             T_record_length = obj_null_loc - location.get(preloc_index);
           } else {
             T_record_length = location.get(k) - location.get(preloc_index);
           }
           //印出最後的T_record
           System.out.println(T_record + zero_format(T_record_length, 2).toUpperCase(Locale.ROOT) + T_record_content);
           objWriter.println(T_record + zero_format(T_record_length, 2).toUpperCase(Locale.ROOT) + T_record_content);
         }
         //印出E_record
         System.out.println("E^" + zero_format(location.get(end_loc), 6));
         objWriter.println("E^" + zero_format(location.get(end_loc), 6));
       } else {
         //印出T_record
         if (count == 10) {
           if (obj_null_loc != 0) {
             T_record_length = obj_null_loc - location.get(preloc_index);
           } else {
             T_record_length = location.get(preloc_index + 10) - location.get(preloc_index);
           }
           System.out.println(T_record + zero_format(T_record_length, 2).toUpperCase(Locale.ROOT) + T_record_content);
           objWriter.println(T_record + zero_format(T_record_length, 2).toUpperCase(Locale.ROOT) + T_record_content);
 
           //System.out.print("\n");
           count = 0;
           obj_null = false;
           obj_null_loc = 0;
           T_record = "";
           T_record_content = "";
         }
         if (count == 0) {
           //紀錄T_record loc開始的位址
           preloc_index = k;
           /*if ((k + 10) < input_opcode.size()) {
             T_record_length = location.get(k + 10) - location.get(k);
           } else {
             T_record_length = location.get(input_opcode.size() - 1) - location.get(k);
           }*/
           T_record += "T^" + zero_format(location.get(k), 6).toUpperCase(Locale.ROOT) + "^";
           //System.out.print(T_record);
         }//print 10 obj count ++
         if (object_code.get(k).equals("  ")) {
           T_record_content += object_code.get(k);
           //System.out.print(object_code.get(k));
           if (obj_null == false) {
             obj_null = true;
             obj_null_loc = location.get(k);
           }
         } else {
           //System.out.print("^" + object_code.get(k).toUpperCase(Locale.ROOT));
           T_record_content += "^" + object_code.get(k).toUpperCase(Locale.ROOT);
         }
         count++;
       }
 
     }
     objWriter.close();
     scn.close();
 
   }
 
 }