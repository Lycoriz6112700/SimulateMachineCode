import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class Assembler{
    public static int addr = 0 , PC = 0;
    public static String startAddr ; 
    public static String label[] = new String[65535];
    public static String instr[] = new String[65535];
    public static String a0[] = new String[65535];
    public static String a1[] = new String[65535];
    public static String a2[]= new String[65535];
    public static String field[] = new String[5];
    public static ArrayList<String> labelCheck = new ArrayList<String>(); 
    public static ArrayList<Integer> labelAddr = new ArrayList<>();

    //Binary to Decimal.      
    public static int BitoDec(String bi){
        int dec = Integer.parseInt(bi,2);
        return dec;
    }
    
    //decimal to 3 bits binary.
    public static String DectoBi3(String dec){
        int x = Integer.parseInt(dec);
        String bi = Integer.toBinaryString(x);
        String result3 = String.format("%3s",bi).replaceAll(" ","0");
        return result3;
    }

    //decimal to 16 bits Binary.
    public static String DectoBi16(String dec){
        int x = Integer.parseInt(dec);  
        String bi = Integer.toBinaryString(x);
        String result16 = String.format("%16s",bi).replaceAll(" ","0");
        return result16;
     }

     //16 bits 2's complement.
     public static String TwosComplement(String dec) {
        String fbit16 = "";
        if(Integer.parseInt(dec) >= 0){
            String bit16 = DectoBi16(dec);
            return bit16;
        }else{
            int num = Integer.parseInt(dec)*(-1);
            dec = String.valueOf(num);
            String bit16 = DectoBi16(dec);
            for(int i = 0; i < bit16.length() ; i++ ){
                fbit16 += Flip(bit16.charAt(i));
            }
        int x = BitoDec(fbit16)+1;
        bit16 = DectoBi16(String.valueOf(x));
        return bit16;
        }
    }
    
    //Flip Bits.
    public static char Flip(char c) {
        return (c == '0') ? '1' : '0';
    }
    
    //Check Opcode.        
    public static String Opcode(String instr){
        if(instr.equals("add")) return "000";
        else if(instr.equals("nand")) return instr = "001";
        else if(instr.equals("lw")) return instr = "010";
        else if(instr.equals("sw")) return instr = "011";
        else if(instr.equals("beq")) return instr = "100";
        else if(instr.equals("jalr")) return instr = "101";
        else if(instr.equals("halt")) return instr = "110";
        else if(instr.equals("noop")) return instr = "111";
        else if(instr.equals(".fill")) return "0";
        else{ErrorCode("4", instr); //Error : opcode not found
        return "0"; 
        }
    }
    
    //Check Errors.
    public static void ErrorCode(String code,String e){
        if(code == "1"){
            System.out.println("Error : Label '" + e + "' undefine.");
            System.exit(0);
        }
	    else if(code == "2"){
            System.out.println("Error : Have similar label '" + e + "' in data file.");
            System.exit(0);
        }
	    else if(code == "3"){
            System.out.println("Error : '" + e + "' have more than 16 bits.");
            System.exit(0);
        }
	    else if(code == "4"){
            System.out.println("Error : Using Opcode '" + e + "' other than those specified.");
            System.exit(0);
        }
	    else if(code == "5"){
            System.out.println("Error : Label '" + e + "' more than 6 characters.");
            System.exit(0);
        }
    } 
    
    //func for calculate.
    public static String MacAssembler(int PC, String instr, String op, String a0, String a1, String a2){
        String res = "";
        if(instr.equals("add") || instr.equals("nand")){
            res = op + DectoBi3(a0) + DectoBi3(a1) + "0000000000000" + DectoBi3(a2);
            res = "" + BitoDec(res);
        }else if(instr.equals("lw") || instr.equals("sw") || instr.equals("beq")){
            if(-32768 <= Integer.parseInt(a2) && Integer.parseInt(a2) <= 32767){
                res = op + DectoBi3(a0) + DectoBi3(a1) + TwosComplement(a2);
                res = "" + BitoDec(res);
            }else{
                ErrorCode("3", a2); // Error : have more than 16 bit.
            }
        }else if(instr.equals("jalr")){
            res = op + DectoBi3(a0) + DectoBi3(a1) + TwosComplement("0");
            res = ""+BitoDec(res);
        }else if(instr.equals("halt") || instr.equals("noop")){
            res = op + "0000000000000000000000";
            res = "" + BitoDec(res);
        }else if(instr.equals(".fill")){
            res = a0;
        }
        return res;
    }
    public static void main(String[] args) throws IOException{
        //Read file and store.
        try{
            File assemCode = new File("assemblyCode.txt");
            Scanner reader = new Scanner(assemCode);
            while (reader.hasNextLine()){
                String data = reader.nextLine();
                field = data.split(" ",5);
                if(field.length < 5){
                    for(int x = field.length; x < 5 ; x++){
                    field = Arrays.copyOf(field, field.length + 1);
                    field[field.length - 1] = " "; 
                    }
                } //Fill array field with length 5.
                if(field[0].length() > 6)ErrorCode("5", field[0]);
                if(field[0] != "")labelAddr.add(addr);
                label[addr] = field[0];
                instr[addr] = field[1];
                a0[addr] = field[2];
                a1[addr] = field[3];
                a2[addr] = field[4];
                labelCheck.add(field[0]);
                if(field[0] != "" && Collections.frequency(labelCheck, field[0]) > 1)ErrorCode("2", field[0]); //Error : similar label.
                addr++;
            }
            reader.close();
            }catch (FileNotFoundException e) {
            System.out.println("Read file error.");
            e.printStackTrace();
            }
        
        //Create File.
        try {
            File macFile = new File("MachineLanguage.txt");
            if (macFile.createNewFile()) {
                System.out.println("File created: " + macFile.getName());
            } else {
                System.out.println("File already exists.");
            }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }    
            
        //Write File.
        FileWriter macCoder = new FileWriter("MachineLanguage.txt");
        for(int i = 0; i < addr ; i++){
            String op = Opcode(instr[i]);
            if(instr[i].equals("add") || instr[i].equals("nand") || instr[i].equals("jalr") || instr[i].equals("halt") || instr[i].equals("noop")){
                macCoder.write(MacAssembler(PC, instr[i], op, a0[i], a1[i], a2[i]));
            }else if(instr[i].equals("lw") || instr[i].equals("sw")){
                try{
                    Integer.parseInt(a2[i]);//-Catch- if a2 is label.
                    macCoder.write(MacAssembler(PC, instr[i], op, a0[i], a1[i], a2[i]));
                }catch(Exception e){
                    int state = 1;//error state.
                    for(int x : labelAddr){
                        if(a2[i].equals(label[x])){
                            macCoder.write(MacAssembler(PC, instr[i], op, a0[i], a1[i], String.valueOf(x)));
                            state = 0;
                        }
                    }
                    if(state == 1) ErrorCode("1", a2[i]); //Error : Undefine label
                }
            }else if(instr[i].equals("beq")){
                try{
                    Integer.parseInt(a2[i]); //-Catch- if a2 is label.
                    macCoder.write(MacAssembler(PC, instr[i], op, a0[i], a1[i], a2[i]));
                }catch(Exception e){
                    int state = 1; //error state.
                    for(int x : labelAddr){
                        if(a2[i].equals(label[x])){
                            int offset = x - (PC+1);
                            macCoder.write(MacAssembler(PC, instr[i], op, a0[i], a1[i], String.valueOf(offset)));
                            state = 0;
                        }
                    }
                    if(state == 1) ErrorCode("1", a2[i]); //Error : Undefine label
                }
            }else if(instr[i].equals(".fill")){
                try{
                    new BigInteger(a0[i]);
                    macCoder.write(MacAssembler(PC, instr[i], op, a0[i], a1[i], a2[i]));
                }catch(Exception e){
                    int state = 1; //error state.
                    for(int x : labelAddr){
                        if(a0[i].equals(label[x])){
                            macCoder.write(MacAssembler(PC, instr[i], op, String.valueOf(x), a1[i], a2[i]));
                            state = 0;
                        }
                    }
                     if(state == 1) ErrorCode("1", a0[i]); //Error : Undefine label
                }
            }
            PC++;
            if (i != addr-1)macCoder.write("\n");
        }
        System.out.println("\nSuccessfully wrote to the file.");
        macCoder.close();
    }
}