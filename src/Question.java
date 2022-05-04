import java.io.InputStream;
import java.util.Scanner;

public class Question {
    private final String[] element = new String[6];
    private byte answer;
    //Создание вопроса, вариантов ответа, правильного варианта и пояснения
    private void setQuestion (String q){
        element[0] = "<html><center>" + q;
    }
    private void setOptionA (String a){
        element[1] = a;
    }
    private void setOptionB (String b){
        element[2] = b;
    }
    private void setOptionC (String c){
        element[3] = c;
    }
    private void setOptionD (String d){
        element[4] = d;
    }
    private void setExplanation (String e){
        element[5] = "<html><center>" + e;
    }
    private void setAnswer (String ans){
        answer = Byte.parseByte(ans);
    }
    //Возврат значений
    public final String getQuestion () { return element[0]; }
    public final String getOptionA () { return element[1]; }
    public final String getOptionB () { return element[2]; }
    public final String getOptionC () { return element[3]; }
    public final String getOptionD () { return element[4]; }
    public final String getExplanation () { return element[5]; }
    public final byte getAnswer () { return answer; }
    //Создать вопрос и варианты ответа
    public Question(byte number, byte n){
        InputStream text = getClass().getResourceAsStream("questions/" + number + "/" + n + ".txt");
        Scanner scan = new Scanner(text, "UTF-8");
        setQuestion(scan.nextLine());
        setOptionA(scan.nextLine());
        setOptionB(scan.nextLine());
        setOptionC(scan.nextLine());
        setOptionD(scan.nextLine());
        setAnswer(scan.nextLine());
        setExplanation(scan.nextLine());
        scan.close();
    }
}
