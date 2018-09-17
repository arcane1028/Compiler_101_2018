import java.io.*;
import java.util.StringTokenizer;

/*
 * Ubuntu 18.04, oracle jdk 8, intellij로 작성했습니다.
 * */

public class Main {

    private static class Hoo {
        private String mainBuffer = ""; // 입력한 값이 저장되는 변수
        // TODO 파일 경로
        private String openPath = "input.hoo"; // 프로젝트 바로아래 경로
        private String savePath = "output.c"; // 프로젝트 바로아래 경로

        private void run() {
            try {
                // 파일 읽기 쓰기
                File openFile = new File(openPath);
                File saveFile = new File(savePath);

                BufferedReader bufferedReader = new BufferedReader(new FileReader(openFile));
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(saveFile));
                // 사용변수
                String line;
                StringTokenizer lineToken;
                StringTokenizer backToken;

                // C 기본 입력
                bufferedWriter.write("#include <stdio.h>\n\n");
                bufferedWriter.write("int main(){\n\n");

                // 한줄씩 읽은뒤 실행
                while ((line = bufferedReader.readLine()) != null) {

                    // ":" 문자 기준으로 나눔
                    lineToken = new StringTokenizer(line, ":");
                    // 토큰 개수가 2개보다 많은 경우 오류
                    if (lineToken.countTokens() < 1 || lineToken.countTokens() > 2) {
                        System.out.println("lineToken error : " + line);
                        continue;
                    }
                    // 첫번째 토큰 처리
                    processFirstToken(lineToken.nextToken());
                    // 두번째 토큰 처리
                    if (lineToken.hasMoreTokens()) {
                        // "(", ")", " " 세가지 문자 기준으로 나눔
                        backToken = new StringTokenizer(lineToken.nextToken(), "() ");
                        // 두개보다 많은 경우 오류
                        if (backToken.countTokens() > 2) {
                            System.out.println("backToken error : ");
                            continue;
                        }
                        boolean isFirst = true;
                        // 토큰이 있을때까지 반복
                        while (backToken.hasMoreTokens()) {
                            // 토큰을 가져옴
                            String command = backToken.nextToken();

                            if (command.equals("ignore")) {
                                mainBuffer = ""; // 저장된 문자열 삭제
                                break;
                            } else if (command.equals("print")) {
                                bufferedWriter.write("\tprintf(\"%s\\n\", \"" + mainBuffer + "\");\n");
                                mainBuffer = ""; // 출력후 삭제
                                break;
                            } else if (isNumber(command) && isFirst) { // 숫자인 경우
                                String temp = "";
                                int count = Integer.parseInt(command);
                                for (int i = 0; i < count; i++) {
                                    temp = temp.concat(mainBuffer); // 저장된 문자열을 숫자만큼 반복
                                }
                                mainBuffer = temp; // 저장
                            } else { // 이 외의 경우 오류
                                System.out.println("back command error : " + command);
                                break;
                            }
                            isFirst = false;
                        }
                    }
                }
                // C 기본 입력
                bufferedWriter.write("\n\treturn 0;\n}\n");
                bufferedWriter.close();
                bufferedReader.close();

            } catch (FileNotFoundException e) {
                System.out.println("file");
            } catch (IOException io) {
                System.out.println("io");
            }
        }

        // 문자열과 옵션 처리
        private void processFirstToken(String first) {
            // "[", "]", " " 세가지 문자 기준으로 나눔
            StringTokenizer frontToken = new StringTokenizer(first, "[] ");
            String firstCommand = "";

            // 토큰 개수 오류
            if (frontToken.countTokens() < 1 || frontToken.countTokens() > 2) {
                System.out.println("frontToken error : " + first);
                return;
            }
            // 기존의 문자열에 연결
            mainBuffer = mainBuffer.concat(frontToken.nextToken());
            // 옵션이 있는 경우
            if (frontToken.hasMoreTokens()) {
                firstCommand = frontToken.nextToken();

                if (firstCommand.equals("U")) {
                    mainBuffer = mainBuffer.toUpperCase(); // 대분자로 변환
                } else if (firstCommand.equals("L")) {
                    mainBuffer = mainBuffer.toLowerCase(); // 소문자로 변환
                } else if (firstCommand.charAt(1) == '/' && firstCommand.length() == 3) {
                    mainBuffer = mainBuffer.replace(firstCommand.charAt(2), firstCommand.charAt(0));// 문자 치환
                } else {
                    System.out.println("first command error : " + firstCommand); //오류
                }
            }
        }

        // 숫자인지 검사하는 함수
        private boolean isNumber(String str) {
            char temp;
            boolean result = true;

            for (int i = 0; i < str.length(); i++) {
                temp = str.charAt(i);
                if (temp < 48 || temp > 57) { // 아스키 코드 검사
                    return false;
                }
            }
            return result;
        }
    }

    public static void main(String[] args) {

        Hoo hoo = new Hoo(); // 객체 생성
        hoo.run(); // 실행
    }
}