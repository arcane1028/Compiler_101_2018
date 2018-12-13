import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            UcodeGenerator.minigo2ucode("test.go");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



