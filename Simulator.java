import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Simulator{

//Read File From Machine Language--------------------------------
    public static void ReadFile(ArrayList<Long> mem){
        try{
            File FileText = new File("MachineLanguage.txt");
            Scanner myReader = new Scanner(FileText);
            // int index = 0;
            while (myReader.hasNextLine()) {
                mem.add(Long.parseLong(myReader.nextLine()));
                // System.out.println(mem.get(index));
                // index++;
            }
            myReader.close();
        }
        catch (FileNotFoundException e){
            System.out.println("An error occureed.");
            e.printStackTrace();
        }
    }
//--------------------------------------------------------------

// Binary To Decimal -------------------------------------------
    public static Long BinarytoDecimal(String Num){
        Long decimal = Long.parseLong(Num, 2);
        return decimal;
    }
//--------------------------------------------------------------

//Binary 16 bits To Decimal (+,-)-------------------------------
    public static Long BinarytoDecimal16Offset(String Num){
        Long decimal = 0L;
        if(Num.charAt(0) == '1'){
            decimal = 65536-BinarytoDecimal(Num);
            decimal = decimal*(-1);
        }
        else{
            decimal = BinarytoDecimal(Num);
        }
        return decimal;
    }
//--------------------------------------------------------------

//Decimal To Binary 3 bit --------------------------------------
    public static int[] Decimalto3Binary(Long Num){
        int i = 2;
        int[] bin3 = {0,0,0};
        while(Num > 0 && i >= 0){
            bin3[i] = Math.toIntExact(Num%2);
            Num = Num/2;
            i = i-1;
        }
        return bin3;
    }
//--------------------------------------------------------------

//Decimal To Binary 32 bit -------------------------------------
    public static int[] Decimalto32Binary(Long Num){
        int i = 31;
        int[] bin32 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        while(Num > 0 && i >= 0){
            bin32[i] = Math.toIntExact(Num%2);
            Num = Num/2;
            i = i-1;
        }
        return bin32;
}
//--------------------------------------------------------------

//Decimal To Binary 25 bit -------------------------------------
    public static int[] Decimalto25Binary(Long Num){
        int i = 24;
        int[] bin25 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        if(Num < 0){
            Num = Num*(-1);
        }
        while(Num > 0 && i >= 0){
            bin25[i] = Math.toIntExact(Num%2);
            Num = Num/2;
            i = i-1;
        }
        return bin25;
}
//--------------------------------------------------------------

//Decimal To Binary 25 bit (+,-) -------------------------------
    public static int[] complement25(Long Num){
        int[] complements;
        if(Num >= 0){
            complements = Decimalto25Binary(Num);
        }
        else{
            Num = Num * (-1);
            int i = 0;
            complements = Decimalto25Binary(Num);
            while(i < 25){
                if(complements[i] == 0){
                    complements[i] = 1;
                }
                else{
                    complements[i] = 0;
                }
                i = i+1;
            }
            String x = convert(complements);
            Long y = BinarytoDecimal(x);
            y = y+1;
            complements = Decimalto25Binary(y);
        }
        return complements;
    }
//--------------------------------------------------------------

//convert int array to String ----------------------------------
    public static String convert(int[] list){ 
        StringBuilder builder = new StringBuilder();
        for(int num : list){
            builder.append(num);
        }
        return builder.toString();
    }
//--------------------------------------------------------------

//print State --------------------------------------------------
    public static void printState(int pc,ArrayList<Long> mem,Long[] reg){
        int i = 0;
        int j = 0;
        System.out.println("@@@");
        System.out.println("state:");
        System.out.println("          pc " + pc);
        System.out.println("          memory: ");
        while(i < mem.size()){
            System.out.println("              mem[ " + i + " ] " + mem.get(i));
            i = i+1;
        }
        System.out.println("          registers: ");
        while(j < reg.length){
            System.out.println("              reg[ " + j + " ] " + reg[j]);
            j = j+1;
        }
        System.out.println("end state");
        System.out.println("");
    }
//--------------------------------------------------------------
    public static void main(String[] args){
        int pc = 0;
        int inst = 0;
        ArrayList<Long> mem = new ArrayList<Long>();
        Long[] reg = {0L,0L,0L,0L,0L,0L,0L,0L};
        ReadFile(mem);
        printState(pc, mem, reg);
        while(pc < mem.size()){
            inst = inst + 1;
            System.out.println("Instruction count = " + inst);

            String Bit = convert(complement25(mem.get(pc)));
            String opcode = String.valueOf(Bit.charAt(0)) + String.valueOf(Bit.charAt(1)) + String.valueOf(Bit.charAt(2));
            String A = String.valueOf(Bit.charAt(3)) + String.valueOf(Bit.charAt(4)) + String.valueOf(Bit.charAt(5));
            String B = String.valueOf(Bit.charAt(6)) + String.valueOf(Bit.charAt(7)) + String.valueOf(Bit.charAt(8));

            int indexA = Integer.parseInt(String.valueOf(BinarytoDecimal(A)));
            int indexB = Integer.parseInt(String.valueOf(BinarytoDecimal(B)));

            //Debug Loop --------------------------------
            // System.out.println("mem = " + mem.get(pc));
            // System.out.println("pc = " + pc);
            // System.out.println("Bit = " + Bit);
            // System.out.println("opcode = " + opcode);
            // System.out.println("A = " +A);
            // System.out.println("B = " +B);
            // System.out.println("indexA = " + indexA);
            // System.out.println("indexB = " + indexB);
            //-------------------------------------------


            //Add ---------------------------------------
            if(opcode.equals("000")){
                String Strdest = String.valueOf(Bit.charAt(22)) + String.valueOf(Bit.charAt(23)) + String.valueOf(Bit.charAt(24));
                int dest = Integer.parseInt(String.valueOf(BinarytoDecimal(Strdest)));
                reg[dest] = reg[indexA] + reg[indexB];
                pc = pc + 1;
            }
            //-------------------------------------------

            //Nand --------------------------------------
            else if(opcode.equals("001")){
                String Strdest = String.valueOf(Bit.charAt(22)) + String.valueOf(Bit.charAt(23)) + String.valueOf(Bit.charAt(24));
                int dest = Integer.parseInt(String.valueOf(BinarytoDecimal(Strdest)));
                int[] nand = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

                //Compare bit by bit (And)
                int k = 0;
                while(k < nand.length){
                    nand[k] = Decimalto32Binary(reg[indexA])[k] & Decimalto32Binary(reg[indexB])[k];
                    if(nand[k] == 0){
                        nand[k] = 1;
                    }
                    else{
                        nand[k] = 0;
                    }
                    k = k+1;
                }
                reg[dest] = BinarytoDecimal(convert(nand));
                pc = pc+1;
            }
            //-------------------------------------------

            //Load Word ---------------------------------
            else if(opcode.equals("010")){
                String Stroff = String.valueOf(Bit.charAt(9)) + String.valueOf(Bit.charAt(10)) + String.valueOf(Bit.charAt(11)) + 
                                String.valueOf(Bit.charAt(12)) + String.valueOf(Bit.charAt(13)) + String.valueOf(Bit.charAt(14)) + 
                                String.valueOf(Bit.charAt(15)) + String.valueOf(Bit.charAt(16)) + String.valueOf(Bit.charAt(17)) +
                                String.valueOf(Bit.charAt(18)) + String.valueOf(Bit.charAt(19)) + String.valueOf(Bit.charAt(20)) +
                                String.valueOf(Bit.charAt(21)) + String.valueOf(Bit.charAt(22)) + String.valueOf(Bit.charAt(23)) +
                                String.valueOf(Bit.charAt(24)); 
                Long off = BinarytoDecimal16Offset(Stroff);
                reg[indexB] = mem.get(Integer.parseInt(String.valueOf(reg[indexA] + off)));

                pc = pc+1;
            }
            //-------------------------------------------

            //Store Word ---------------------------------
            else if(opcode.equals("011")){
                String Stroff = String.valueOf(Bit.charAt(9)) + String.valueOf(Bit.charAt(10)) + String.valueOf(Bit.charAt(11)) + 
                                String.valueOf(Bit.charAt(12)) + String.valueOf(Bit.charAt(13)) + String.valueOf(Bit.charAt(14)) + 
                                String.valueOf(Bit.charAt(15)) + String.valueOf(Bit.charAt(16)) + String.valueOf(Bit.charAt(17)) +
                                String.valueOf(Bit.charAt(18)) + String.valueOf(Bit.charAt(19)) + String.valueOf(Bit.charAt(20)) +
                                String.valueOf(Bit.charAt(21)) + String.valueOf(Bit.charAt(22)) + String.valueOf(Bit.charAt(23)) +
                                String.valueOf(Bit.charAt(24));
                Long off = BinarytoDecimal16Offset(Stroff);
                if((reg[indexA] + off) >= mem.size()){
                    mem.add(reg[indexB]);
                }
                else{
                    mem.set((Integer.parseInt(String.valueOf(reg[indexA] + off))), reg[indexB]);
                }
                pc = pc + 1;
            }
            //-------------------------------------------

            //Beq ---------------------------------------
            else if(opcode.equals("100")){
                String Stroff = String.valueOf(Bit.charAt(9)) + String.valueOf(Bit.charAt(10)) + String.valueOf(Bit.charAt(11)) + 
                                String.valueOf(Bit.charAt(12)) + String.valueOf(Bit.charAt(13)) + String.valueOf(Bit.charAt(14)) + 
                                String.valueOf(Bit.charAt(15)) + String.valueOf(Bit.charAt(16)) + String.valueOf(Bit.charAt(17)) +
                                String.valueOf(Bit.charAt(18)) + String.valueOf(Bit.charAt(19)) + String.valueOf(Bit.charAt(20)) +
                                String.valueOf(Bit.charAt(21)) + String.valueOf(Bit.charAt(22)) + String.valueOf(Bit.charAt(23)) +
                                String.valueOf(Bit.charAt(24));
                Long off = BinarytoDecimal16Offset(Stroff);
                // System.out.println(off);

                //Jump
                if(reg[indexA].equals(reg[indexB])){
                    pc = pc + 1 + Integer.parseInt(String.valueOf(off));
                }
                else{
                    pc = pc + 1;
                }
               
                printState(pc,mem,reg);

                continue;
            }
            //-------------------------------------------

            //Jalr --------------------------------------
            else if(opcode.equals("101")){
                pc = pc + 1;
                reg[indexB] = Long.parseLong(String.valueOf(pc));
                printState(pc, mem, reg);

                //Jump
                if(indexA != indexB){
                    pc = Integer.parseInt(String.valueOf(reg[indexA]));
                }

                continue;
            }
            //-------------------------------------------

            //Halt --------------------------------------
            else if(opcode.equals("110")){
                pc = pc + 1;
                printState(pc, mem, reg);
                break;
            }
            //-------------------------------------------

            //Noop --------------------------------------
            else if(opcode.equals("111")){
                pc = pc + 1;
            }
            //-------------------------------------------

            printState(pc, mem, reg);
        }
        
    }
}