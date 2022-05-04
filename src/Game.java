import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Game extends JFrame{
    private final Dimension sizeScreen = Toolkit.getDefaultToolkit().getScreenSize();//Размеры экрана
    private final JLabel labelBackgroundMain = new JLabel();//Главный фон
    private final JLabel labelBackgroundAmounts = new JLabel();//Фон сумм справа
    private final JLabel labelCurrentAmount = new JLabel();//Фон текущей суммы
    private final JLabel labelPrize = new JLabel();//Выигрыш
    private final JLabel[] labelSum = {
            new JLabel("500"),
            new JLabel("1 000"),
            new JLabel("2 000"),
            new JLabel("3 000"),
            new JLabel("5 000"),
            new JLabel("10 000"),
            new JLabel("15 000"),
            new JLabel("25 000"),
            new JLabel("50 000"),
            new JLabel("100 000"),
            new JLabel("200 000"),
            new JLabel("400 000"),
            new JLabel("800 000"),
            new JLabel("1 500 000"),
            new JLabel("3 000 000")
    };//Суммы
    private final JLabel[] labelSumNumber = new JLabel[15];//Номер вопроса
    private final JLabel labelQuestion = new JLabel();//Вопрос
    private final JButton[] buttonOption = new JButton[4];//Кнопки вариантов ответа
    private final JLabel[] labelOption = new JLabel[4];//Надписи A B C D
    private final JButton[] buttonHint = new JButton[5];//Подсказки
    private final String[] hintTitle = {"Dibrov", "50.50", "x2", "call", "replace"};//Имена ресурсов подсказок
    private final JLabel[] labelCentralHint = new JLabel[2];//Отображение в центре использования подсказки "Помощь ведущего", "Право на ошибку" и "Замена вопроса"
    private final JLabel labelDialog = new JLabel();//Надпись диалога с игроком
    private final JLabel labelTimer = new JLabel();//Таймер звонка другу
    private final JButton buttonUpdate = new JButton("Продолжить");//Кнопка продолжения игры
    private final JButton buttonMenu = new JButton("Меню");//Меню
    private final JButton[] buttonFromMenu = new JButton[4];//Кнопки из меню
    private final JButton[] buttonDecision = new JButton[2];//Кнопки "Да" и "Нет"
    private byte F1, F2, F3;//Номера неверных вариантов ответа, исключенных "50:50" и "Правом на ошибку"
    private boolean isFiftyFiftyUsed = false;//Используется ли "50:50" на текущем вопросе?
    private boolean isRightMistakeUsed = false;//Используется ли "Право на ошибку" на текущем вопросе?
    private byte number = 1;//Номер текущего вопроса
    private byte n;//Индек вопроса. Нужен для выбора нового вопроса, если используется "Замена вопроса"
    private byte trueAnswer;//Правильный вариант ответа (цифра)
    private byte currentAnswer;//Текущий ответ
    private String explanation;//Пояснение к ответу
    private byte checkpoint;//Номер вопроса с несгораемой суммой
    private boolean hintAllow = false;//Можно ли использовать подсказку
    private boolean answerAllow = false;//Можно ли давать ответ
    private final MouseListener amount = new Amount();//Слушатель для выбора несгораемой суммы
    //МЕТОДЫ
    //Установи иконку для кнопки
    private void setImageButton(JButton button, String url, int width, int height) {
        button.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(Game.class.getResource("images/" + url + ".png")).getScaledInstance(width, height, Image.SCALE_SMOOTH)));
    }
    private void setImageButton(JButton button, String url) {
        setImageButton(button, url, button.getWidth(), button.getHeight());
    }
    //Установи иконку для отключенной кнопки
    private void setImageButtonInactive(JButton button, String url, int width, int height) {
        button.setDisabledIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(Game.class.getResource("images/" + url + ".png")).getScaledInstance(width, height, Image.SCALE_SMOOTH)));
    }
    private void setImageButtonInactive(JButton button, String url) {
        setImageButtonInactive(button, url, button.getWidth(), button.getHeight());
    }
    //Установи иконку для надписи
    private void setImageLabel(JLabel label, String url, int width, int height) {
        label.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(Game.class.getResource("images/" + url + ".png")).getScaledInstance(width, height, Image.SCALE_SMOOTH)));
    }
    private void setImageLabel(JLabel label, String url) {
        setImageLabel(label, url, label.getWidth(), label.getHeight());
    }
    //Блокировка последней неиспользованной подсказки
    private byte lastHint = 0;//Аргумент функции, определяющей последнюю неиспользованную подсказку.
    private byte counterHint = 0;//Счётчик подсказок
    private void lockLastHint(int delta) {
        lastHint += delta;
        counterHint++;
        if(counterHint == 4){
            lastHint = (byte) (14 - lastHint);
            setImageButtonInactive(buttonHint[lastHint], "hint." + hintTitle[lastHint] + ".locked", (int) (buttonHint[lastHint].getWidth() * 0.9f), (int) (buttonHint[lastHint].getHeight() * 0.9f));
            buttonHint[lastHint].setEnabled(false);
        }
    }
    //Верни команды вариантам ответа
    private void returnCommandsOptions(){
        for(byte i = 0; i < buttonOption.length; i++)
            buttonOption[i].setActionCommand(String.valueOf(i));
    }
    //Очисти надпись пояснения и верни логотип
    private void clearLabelDialog(){
        labelDialog.setText(null);
        setImageLabel(labelDialog, "logo", (int) (labelDialog.getHeight() * 0.85f), (int) (labelDialog.getHeight() * 0.85f));
    }
    //Выбор несгораемой суммы
    private void selectCheckpoint(){
        switch ((int) (Math.random() * 5)){
            case 0:
                labelDialog.setText("<html><center>Какую сумму вы назовёте несгораемой?</html>");
                break;
            case 1:
                labelDialog.setText("<html><center>Какая сумма для Вас наиболее предпочтительна как несгораемая?</html>");
                break;
            case 2:
                labelDialog.setText("<html><center>Какова будет несгораемая сумма?</html>");
                break;
            case 3:
                labelDialog.setText("<html><center>Какую сумму будем считать несгораемой?</html>");
                break;
            default:
                labelDialog.setText("<html><center>Какая сумма Вас устроит в качестве несгораемой?</html>");
        }
        buttonUpdate.setVisible(false);
        labelSum[14].setForeground(Color.ORANGE);
        labelSumNumber[14].setForeground(Color.ORANGE);
        labelCurrentAmount.setLocation(labelCurrentAmount.getX(), -labelCurrentAmount.getHeight());
        for(byte i = 0; i < labelSum.length; i++){
            labelSum[i].addMouseListener(amount);
            labelSumNumber[i].setIcon(null);
        }
    }
    //Создай вопрос
    private void createQuestion(){
        Question q = new Question(number, n);
        labelQuestion.setText(q.getQuestion());
        buttonOption[0].setText(q.getOptionA());
        buttonOption[1].setText(q.getOptionB());
        buttonOption[2].setText(q.getOptionC());
        buttonOption[3].setText(q.getOptionD());
        trueAnswer = q.getAnswer();
        explanation = q.getExplanation();
        buttonMenu.setVisible(true);
        answerAllow = true;
        hintAllow = true;
    }
    //Исправь последствия подсказок "50:50" и "Право на ошибку"
    private void fixConsequencesHints(){
        if(isFiftyFiftyUsed){
            isFiftyFiftyUsed = false;
            buttonOption[F1].setEnabled(true);
            buttonOption[F2].setEnabled(true);
        }
        if(isRightMistakeUsed){
            isRightMistakeUsed = false;
            buttonOption[F3].setEnabled(true);
            buttonOption[F3].setForeground(Color.WHITE);
            setImageButtonInactive(buttonOption[F3], "answer.blue");
            labelOption[F3].setForeground(Color.ORANGE);
            buttonFromMenu[2].setEnabled(true);
        }
    }
    //Меню видно
    private void menuVisible(boolean isVisible){
        for (JButton fromMenu : buttonFromMenu) fromMenu.setVisible(isVisible);
    }
    //Контекстное меню видно
    private void contextMenuVisible(boolean isVisible){
        if(!isVisible)
            labelDialog.setText(null);
        for (JButton jButton : buttonDecision) jButton.setVisible(isVisible);
    }
    //Фон виден
    private void backgroundVisible(boolean isVisible, boolean iconIsVisible){
        if(isVisible && iconIsVisible)
            clearLabelDialog();
        else
            labelDialog.setIcon(null);
        labelQuestion.setVisible(isVisible);
        for(byte i = 0; i < buttonOption.length; i++){
            buttonOption[i].setVisible(isVisible);
            labelOption[i].setVisible(isVisible);
        }
        for (JLabel jLabel : labelCentralHint) jLabel.setVisible(isVisible);
    }
    //Меню при завершении игры
    private void menuEndGame(boolean isShow){
        buttonDecision[0].setText(isShow ? "Новая игра" : "Да");
        buttonDecision[1].setText(isShow ? "Выйти из игры" : "Нет");
        buttonDecision[0].setActionCommand(isShow ? "restart" : null);
        buttonDecision[1].setActionCommand(isShow ? "exit" : null);
        contextMenuVisible(isShow);
    }
    //АНИМАЦИЯ
    //Вспомогательные методы анимации
    //Положение вопроса и вариантов
    private void locationQuestion(int x){
        labelQuestion.setLocation(x, labelQuestion.getY());
        for(byte i = 0; i < buttonOption.length; i++)
            buttonOption[i].setLocation(labelQuestion.getX() + labelQuestion.getWidth() / 2 - buttonOption[i].getWidth() + buttonOption[i].getWidth() * (i % 2), buttonOption[i].getY());
    }
    //Появление вопроса
    private void showQuestion() throws InterruptedException{
        final int k = labelQuestion.getWidth() / 10;
        while (Math.abs(labelQuestion.getX()) > k) {
            labelQuestion.setLocation(labelQuestion.getX() + k, labelQuestion.getY());
            for (byte i = 0; i < 4; i++)
                buttonOption[i].setLocation(buttonOption[i].getX() + k, buttonOption[i].getY());
            Thread.sleep(45);
        }
        locationQuestion(0);
        for (byte i = 0; i < 4; i++) {
            labelOption[i].setVisible(true);
        }
        n = (byte) (Math.random() * 15 + 1);
        createQuestion();
    }
    //Переход с вопроса на выигрыш
    private void transition(boolean win) throws InterruptedException{
        try{
            for (JLabel jLabel : labelCentralHint) jLabel.setIcon(null);
            final int k = labelQuestion.getWidth()/10;
            while(Math.abs(labelPrize.getX()) > k){
                labelPrize.setLocation(labelPrize.getX() + k, labelPrize.getY());
                labelQuestion.setLocation(labelQuestion.getX() + k, labelQuestion.getY());
                for (JButton jButton : buttonOption) jButton.setLocation(jButton.getX() + k, jButton.getY());
                Thread.sleep(45);
            }
            labelPrize.setLocation(0, labelPrize.getY());
            locationQuestion((int) (-labelQuestion.getWidth() * 1.5f));
            if(win)
                labelPrize.setText(number != 15 ? labelSum[number - 1].getText() : "МИЛЛИОНЕР!");
            else
                labelPrize.setText(number <= checkpoint ? "0" : labelSum[checkpoint - 1].getText());
        }
        catch (ArrayIndexOutOfBoundsException ignored) {
            labelPrize.setText("0");
        }
    }
    //Покажи правильный ответ
    private void showAnswer(boolean isCorrect, boolean delay) throws InterruptedException{
        labelOption[trueAnswer].setForeground(Color.BLACK);
        if(isCorrect)
            buttonOption[trueAnswer].setForeground(Color.WHITE);
        for(byte i = 0; i < 3; i++){
            setImageButton(buttonOption[trueAnswer], "answer." + (isCorrect ? "orange" : "blue"));
            Thread.sleep(150);
            setImageButton(buttonOption[trueAnswer], "answer.green");
            Thread.sleep(150);
        }
        if(delay || !isCorrect)
            Thread.sleep(3000);
        fixConsequencesHints();
        setImageButton(buttonOption[trueAnswer], "answer.blue");
        labelOption[trueAnswer].setForeground(Color.ORANGE);
        if(!isCorrect){
            setImageButton(buttonOption[currentAnswer], "answer.blue");
            labelOption[currentAnswer].setForeground(Color.ORANGE);
            buttonOption[currentAnswer].setForeground(Color.WHITE);
        }
        labelQuestion.setText(null);
        for(byte i = 0; i < buttonOption.length; i++){
            buttonOption[i].setText(null);
            labelOption[i].setVisible(false);
        }
    }
    //Покажи подсказку
    private void showHint(JButton button, String image){
        Runnable showHint = () -> {
            try {
                for(float i = 0.1f; i <= 1.0f; i += 0.2f){
                    setImageButton(button, image, (int) (button.getWidth() * i), (int) (button.getHeight() * i));
                    Thread.sleep(40);
                }
                setImageButton(button, image, (int) (button.getWidth() * 0.9f), (int) (button.getHeight() * 0.9f));
            } catch (InterruptedException ignored) {}
        };
        Thread thread = new Thread(showHint);
        thread.start();
    }
    //Покажи или скрой подсказку в центре
    private void setIconLabelCentralHint(int index, String image, boolean isShow){
        Runnable show = () -> {
            try {
                float step = isShow ? 0.15f : -0.15f;
                for(float i = isShow ? 0.1f : 1f; i <= 1.1f && i >= 0.1f; i += step){
                    setImageLabel(labelCentralHint[index], image, (int) (labelCentralHint[index].getWidth() * i), (int) (labelCentralHint[index].getHeight() * i));
                    Thread.sleep(80);
                }
                if(isShow)
                    setImageLabel(labelCentralHint[index], image);
                else
                    labelCentralHint[index].setIcon(null);
            } catch (InterruptedException ignored) {}
        };
        Thread thread = new Thread(show);
        thread.start();
    }
    //Заставка
    private void screeSaver() throws InterruptedException{
        for (float i = 0.13f; i <= 0.8f; i += 0.045f){
            setImageLabel(labelDialog, "logo", (int) (sizeScreen.height * i), (int) (sizeScreen.height * i));
            Thread.sleep(30);
        }
    }
    //Анимация начала игры
    Runnable startGame = () -> {
        try {
            Sound.playSound("game.start");
            Thread.sleep(5000);
            Sound.playBackgroundSound("question.reflections", number);
            showQuestion();
        } catch (InterruptedException ignored) {}
    };
    //Метод анимации
    private void showAnimation(Runnable ani) {
        Thread thread = new Thread(ani);
        thread.start();
    }
    //КОНСТРУКТОР
    public Game() {
        super("Кто хочет стать миллионером?");
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/logo.png")));
        setSize(sizeScreen);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = getContentPane();
        //Слои. Устанавливает приоритет положения компонента при наложении с другим компонентом.
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, sizeScreen.width, sizeScreen.height);
        container.add(layeredPane);
        //Локальные переменные координат и размеров объектов
        //Вопрос
        final int yQuestion = (int) (sizeScreen.height / 1.57f);
        final int widthQuestion = (int) (sizeScreen.width * 0.7f);
        final int heightQuestion = (int) (widthQuestion / 9.31f);
        //Фон текущей суммы
        final int widthCurrentAmount = (int) (sizeScreen.width * 0.25f);
        //Подсказки
        final int widthHint = widthCurrentAmount / 3;
        final int heightHint = (int) (widthHint / 1.78f);
        //Суммы
        final int widthSum = (int) (widthCurrentAmount * 0.89f);
        final int heightSum = (int) (widthCurrentAmount / 9.63f);
        final int xSum = (int) (widthQuestion + sizeScreen.width * 0.025f);
        final int ySum = heightSum + 2 * heightHint;
        //Вариаты ответа
        final int widthOption = (int) (widthQuestion / 2.21f);
        final int heightOption = (int) (widthOption / 7.86f);
        final int deltaYOption = (int) (heightOption / 8.43f);//Разница по высоте между вариантами ответа
        final int xOption = widthQuestion / 2 - widthOption;
        final int yOption = yQuestion + heightQuestion + deltaYOption;
        //Фоны
        labelBackgroundMain.setBounds(0, 0, sizeScreen.width, sizeScreen.height);
        labelBackgroundAmounts.setBounds(sizeScreen.width, 0, sizeScreen.width - widthQuestion, sizeScreen.height);
        setImageLabel(labelBackgroundMain, "background");
        setImageLabel(labelBackgroundAmounts, "background.amounts");
        layeredPane.add(labelBackgroundMain, Integer.valueOf(-1));
        layeredPane.add(labelBackgroundAmounts, Integer.valueOf(3));
        //Шрифты
        int sizeFont = (int) (0.018f * sizeScreen.width);//Размер шрифта
        Font mainFont = new Font("", Font.PLAIN, sizeFont);//Обыный шрифт
        Font boldFont = new Font("", Font.BOLD, sizeFont);//Шрифт сумм
        //Суммы справа
        for (int i = labelSum.length - 1; i > -1; i--) {
            labelSumNumber[i] = new JLabel((i < 9 ? "  " : "") + (i + 1));
            labelSum[i].setBounds(xSum, ySum + heightSum * (14 - i), widthSum, heightSum);
            labelSumNumber[i].setBounds((int) (xSum + widthCurrentAmount * 0.09f), ySum + heightSum * (14 - i), (int) (widthCurrentAmount * 0.21f), heightSum);
            labelSum[i].setFont(boldFont);
            labelSumNumber[i].setFont(boldFont);
            labelSum[i].setForeground(Color.ORANGE);
            labelSumNumber[i].setForeground(Color.ORANGE);
            labelSum[i].setHorizontalAlignment(JLabel.RIGHT);
            labelSumNumber[i].setHorizontalTextPosition(JLabel.LEFT);
            labelSumNumber[i].setIconTextGap((int) (widthCurrentAmount * 0.06f));
            labelSum[i].setVisible(false);
            labelSumNumber[i].setVisible(false);
            layeredPane.add(labelSum[i], Integer.valueOf(5));
            layeredPane.add(labelSumNumber[i], Integer.valueOf(5));
        }
        //Текущая сумма справа
        labelCurrentAmount.setBounds(xSum, -heightSum, widthCurrentAmount, heightSum);
        setImageLabel(labelCurrentAmount, "current.amount");
        layeredPane.add(labelCurrentAmount, Integer.valueOf(4));
        //Текущая сумма в центре
        labelPrize.setBounds((int) (-widthQuestion * 1.5f), yQuestion + heightQuestion / 2, widthQuestion, heightQuestion);
        setImageLabel(labelPrize, "question");
        labelPrize.setHorizontalTextPosition(JLabel.CENTER);
        labelPrize.setFont(new Font("", Font.PLAIN, 3 * sizeFont));
        labelPrize.setForeground(Color.WHITE);
        layeredPane.add(labelPrize, Integer.valueOf(0));
        //Вопрос
        labelQuestion.setBounds((int) (-widthQuestion * 1.5f), yQuestion, widthQuestion, heightQuestion);
        labelQuestion.setHorizontalTextPosition(JLabel.CENTER);
        setImageLabel(labelQuestion, "question");
        labelQuestion.setFont(mainFont);
        labelQuestion.setForeground(Color.WHITE);
        layeredPane.add(labelQuestion, Integer.valueOf(0));
        //Кнопки вариантов ответа и надписи A, B, C, D
        for(byte i = 0; i < buttonOption.length; i++) {
            buttonOption[i] = new JButton();
            labelOption[i] = new JLabel((char) ('A' + i) + ":");
            buttonOption[i].setBounds(labelQuestion.getX() + labelQuestion.getWidth() / 2 - widthOption + widthOption * (i % 2), yOption + (heightOption + deltaYOption) * (i - i % 2) / 2, widthOption, heightOption);
            labelOption[i].setBounds(xOption + widthOption * (i % 2), buttonOption[i].getY(), (int) (widthOption / 6.9f), heightOption);
            labelOption[i].setHorizontalAlignment(JLabel.RIGHT);
            buttonOption[i].setHorizontalTextPosition(JLabel.CENTER);
            buttonOption[i].setFont(mainFont);
            labelOption[i].setFont(mainFont);
            buttonOption[i].setForeground(Color.WHITE);
            labelOption[i].setForeground(Color.ORANGE);
            buttonOption[i].setFocusPainted(false);
            buttonOption[i].setBorderPainted(false);
            buttonOption[i].setContentAreaFilled(false);
            labelOption[i].setVisible(false);
            buttonOption[i].addActionListener(new Answer());
            buttonOption[i].setActionCommand(String.valueOf(i));
            layeredPane.add(buttonOption[i], Integer.valueOf(0));
            layeredPane.add(labelOption[i], Integer.valueOf(1));
            setImageButton(buttonOption[i], "answer.blue");
            setImageButtonInactive(buttonOption[i], "answer.blue");
            buttonOption[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        //Подсказки
        for(byte i = 0; i < buttonHint.length; i++){
            buttonHint[i] = new JButton();
            buttonHint[i].setFocusPainted(false);//Отключение рамок при нажатии
            buttonHint[i].setBorderPainted(false);//Отключение рамки при наведении курсора
            buttonHint[i].setContentAreaFilled(false);//Сделать кнопки прозрачными, сохранив отображение иконок и текста
            buttonHint[i].setBounds((int) (xSum + widthHint * (i < 3 ? i : i - 2.5f)), heightSum / 2 + (i > 2 ? heightHint : 0), widthHint, heightHint);//Координаты и размеры
            layeredPane.add(buttonHint[i], Integer.valueOf(4));//Слои
            setImageButtonInactive(buttonHint[i], "hint." + hintTitle[i] + ".used", (int) (widthHint * 0.9f), (int) (heightHint * 0.9f));//Иконки при неактивной кнопке
            buttonHint[i].setRolloverIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/hint." + hintTitle[i] + ".selected.png")).getScaledInstance((int) (widthHint * 0.9f), (int) (heightHint * 0.9f), Image.SCALE_SMOOTH)));//Иконка при наведении курсора
        }
        //Вызов класса при нажании
        buttonHint[0].addActionListener(new HelpDibrov());
        buttonHint[1].addActionListener(new FiftyFifty());
        buttonHint[2].addActionListener(new RightMistake());
        buttonHint[3].addActionListener(new CallFriend());
        buttonHint[4].addActionListener(new ReplaceQuestion());
        //Надписи использования "Право на ошибку", "Замены вопроса" и "Помощи ведущего"
        for(byte i = 0; i < labelCentralHint.length; i++){
            labelCentralHint[i] = new JLabel();
            labelCentralHint[i].setBounds((int) (widthQuestion / 2 - (heightOption + deltaYOption) * 0.89f), yQuestion + heightQuestion + deltaYOption + heightOption / 2, (int) ((heightOption + deltaYOption) * 1.78f), heightOption + deltaYOption);
            labelCentralHint[i].setHorizontalAlignment(SwingConstants.CENTER);
            labelCentralHint[i].setVerticalAlignment(SwingConstants.CENTER);
            layeredPane.add(labelCentralHint[i], Integer.valueOf(i + 2));
        }
        //Таймер звонка другу
        labelTimer.setBounds(labelQuestion.getWidth(), 0, (int) (0.26f * sizeScreen.height), (int) (0.26f * sizeScreen.height));
        labelTimer.setHorizontalTextPosition(SwingConstants.CENTER);
        labelTimer.setHorizontalAlignment(SwingConstants.CENTER);
        labelTimer.setFont(new Font("", Font.PLAIN, 4 * sizeFont));
        labelTimer.setForeground(Color.WHITE);
        layeredPane.add(labelTimer, Integer.valueOf(2));
        labelTimer.setVisible(false);
        setImageLabel(labelTimer, "timer");
        //Пояснение к ответу
        labelDialog.setBounds(0, 0, sizeScreen.width, yQuestion - 2 * heightHint - heightOption);
        labelDialog.setHorizontalAlignment(JLabel.CENTER);
        labelDialog.setFont(boldFont);
        labelDialog.setForeground(Color.WHITE);
        layeredPane.add(labelDialog, Integer.valueOf(0));
        //Кнопка "Продолжить"
        buttonUpdate.setBounds((sizeScreen.width - widthOption) / 2, sizeScreen.height / 2 - deltaYOption - heightOption, widthOption, heightOption);
        buttonUpdate.setFocusPainted(false);
        buttonUpdate.setBorderPainted(false);
        buttonUpdate.setContentAreaFilled(false);
        buttonUpdate.setFont(mainFont);
        buttonUpdate.setForeground(Color.WHITE);
        setImageButton(buttonUpdate, "answer.blue");
        buttonUpdate.setRolloverIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/answer.green.png")).getScaledInstance(widthOption, heightOption, Image.SCALE_SMOOTH)));
        buttonUpdate.setHorizontalTextPosition(JLabel.CENTER);
        buttonUpdate.addActionListener(new Update());
        buttonUpdate.setActionCommand("rules");
        buttonUpdate.setVisible(false);
        layeredPane.add(buttonUpdate, Integer.valueOf(1));
        //Кнопка меню
        buttonMenu.setBounds(labelCurrentAmount.getX(), (int) (sizeScreen.height - labelCurrentAmount.getHeight() * 1.5f), widthCurrentAmount, heightSum);
        buttonMenu.setFocusPainted(false);
        buttonMenu.setBorderPainted(false);
        buttonMenu.setContentAreaFilled(false);
        buttonMenu.setFont(mainFont);
        buttonMenu.setForeground(Color.WHITE);
        buttonMenu.setHorizontalTextPosition(SwingConstants.CENTER);
        setImageButton(buttonMenu, "answer.blue");
        buttonMenu.setRolloverIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/answer.green.png")).getScaledInstance(buttonMenu.getWidth(), buttonMenu.getHeight(), Image.SCALE_SMOOTH)));
        buttonMenu.addActionListener(new Menu());
        layeredPane.add(buttonMenu, Integer.valueOf(4));
        buttonMenu.setVisible(false);
        //Кнопки из меню
        for(byte i = 0; i < buttonFromMenu.length; i++){
            buttonFromMenu[i] = new JButton();
            buttonFromMenu[i].setBounds((sizeScreen.width - labelBackgroundAmounts.getWidth() - widthOption) / 2, (sizeScreen.height + (2 * i - 3) * deltaYOption) / 2 + (i - 2) * heightOption, widthOption, heightOption);
            buttonFromMenu[i].setFocusPainted(false);
            buttonFromMenu[i].setBorderPainted(false);
            buttonFromMenu[i].setContentAreaFilled(false);
            buttonFromMenu[i].setFont(mainFont);
            buttonFromMenu[i].setForeground(Color.WHITE);
            setImageButton(buttonFromMenu[i], "answer.blue");
            buttonFromMenu[i].setRolloverIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/answer.green.png")).getScaledInstance(widthOption, heightOption, Image.SCALE_SMOOTH)));
            buttonFromMenu[i].setHorizontalTextPosition(JLabel.CENTER);
            buttonFromMenu[i].addActionListener(new Menu());
            layeredPane.add(buttonFromMenu[i], Integer.valueOf(1));
            buttonFromMenu[i].setActionCommand(String.valueOf(i));
            buttonFromMenu[i].setVisible(false);
        }
        buttonFromMenu[0].setText("Вернуться в игру");
        buttonFromMenu[1].setText("Новая игра");
        buttonFromMenu[2].setText("Забрать деньги");
        buttonFromMenu[3].setText("Выйти из игры");
        setImageButtonInactive(buttonFromMenu[2], "answer.gray");
        //Кнопки "Да" и "Нет". Они же кнопки начала игры
        for(byte i = 0; i < buttonDecision.length; i++){
            buttonDecision[i] = new JButton();
            buttonDecision[i].setBounds((sizeScreen.width - widthOption) / 2, buttonUpdate.getY() + i * (heightOption + deltaYOption), widthOption, heightOption);
            buttonDecision[i].setFocusPainted(false);
            buttonDecision[i].setBorderPainted(false);
            buttonDecision[i].setContentAreaFilled(false);
            buttonDecision[i].setFont(mainFont);
            buttonDecision[i].setForeground(Color.WHITE);
            setImageButton(buttonDecision[i], "answer.blue");
            buttonDecision[i].setRolloverIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/answer.green.png")).getScaledInstance(buttonDecision[i].getWidth(), buttonDecision[i].getHeight(), Image.SCALE_SMOOTH)));
            buttonDecision[i].setHorizontalTextPosition(JLabel.CENTER);
            buttonDecision[i].addActionListener(new PlayerDecision());
            layeredPane.add(buttonDecision[i], Integer.valueOf(1));
        }
        buttonDecision[0].setText("Начать игру");
        buttonDecision[1].setText("Выйти из игры");
        buttonDecision[0].setActionCommand("begin");
        buttonDecision[1].setActionCommand("exit");
        //Последний шаг
        setExtendedState(JFrame.MAXIMIZED_BOTH);//JFrame во весь экран
        setUndecorated(true);//Скрыть панель задач
        setVisible(true);
    }
    //Выбор несгораемой суммы
    class Amount implements MouseListener {
        public void mouseClicked(MouseEvent e) {
            for (JLabel jLabel : labelSum) jLabel.removeMouseListener(amount);
            byte indexSelectedAmount = (byte) (14 - (e.getComponent().getY() - labelSum[14].getY()) / labelSum[14].getHeight());
            for(byte i = 0; i < indexSelectedAmount; i++)
                setImageLabel(labelSumNumber[i], "icon.amount", (int) (labelCurrentAmount.getHeight() * 0.44f), (int) (labelCurrentAmount.getHeight() * 0.32f));
            labelCurrentAmount.setLocation(labelCurrentAmount.getX(), labelSum[indexSelectedAmount].getY());
            Sound.playSound("game.rules.amount");
            buttonUpdate.setActionCommand("start");
            buttonUpdate.setVisible(true);
            labelDialog.setText("<html><center>" + labelSum[indexSelectedAmount].getText() +
                    " рублей — несгораемая сумма.<br>" +
                    "И для Вас начинается игра «Кто хочет стать миллионером?»!!!</html>");
            checkpoint = (byte) (indexSelectedAmount + 1);
            labelSum[indexSelectedAmount].setForeground(Color.WHITE);
            labelSumNumber[indexSelectedAmount].setForeground(Color.WHITE);
            labelSum[14].setForeground(Color.WHITE);
            labelSumNumber[14].setForeground(Color.WHITE);
        }
        public void mouseEntered(MouseEvent e) {
            labelCurrentAmount.setLocation(labelCurrentAmount.getX(), e.getComponent().getY());
        }
        public void mouseExited(MouseEvent e) {
            labelCurrentAmount.setLocation(labelCurrentAmount.getX(), -labelCurrentAmount.getHeight());
        }
        public void mousePressed(MouseEvent e){}
        public void mouseReleased(MouseEvent e){}
    }
    //Дан ответ
    class Answer implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(answerAllow) {
                byte choice = Byte.parseByte(e.getActionCommand());
                switch (choice / 4) {
                    //Обычный ответ
                    case 0:
                        answerAllow = false;
                        hintAllow = false;
                        buttonMenu.setVisible(false);
                        if (number > 5)
                            Sound.playSound("answer.accepted");
                        Sound.playBackgroundSound("answer.drum.roll", number);
                        currentAnswer = choice;
                        setImageButton(buttonOption[currentAnswer], "answer.orange");
                        buttonOption[currentAnswer].setForeground(Color.BLACK);
                        labelOption[currentAnswer].setForeground(Color.WHITE);
                        labelDialog.setIcon(null);
                        if (currentAnswer != trueAnswer)
                            explanation += "<br>Правильный ответ: «" + labelOption[trueAnswer].getText() + " " + buttonOption[trueAnswer].getText() + "».";
                        labelDialog.setText(explanation);
                        buttonUpdate.setVisible(true);
                        break;
                    //Ответ с подсказкой "Право на ошибку"
                    case 1:
                        answerAllow = false;
                        buttonMenu.setVisible(false);
                        currentAnswer = (byte) (choice - 4);
                        setImageButton(buttonOption[currentAnswer], "answer.orange");
                        buttonOption[currentAnswer].setForeground(Color.BLACK);
                        labelOption[currentAnswer].setForeground(Color.WHITE);
                        if (number > 5) {
                            Sound.playSound("answer.accepted");
                            Sound.playBackgroundSound("answer.drum.roll", number);
                        }
                        returnCommandsOptions();
                        if (currentAnswer != trueAnswer) {
                            Runnable secondAttempt = () -> {
                                try {
                                    F3 = currentAnswer;
                                    Thread.sleep(3000);
                                    setImageButtonInactive(buttonOption[currentAnswer], "answer.gray");
                                    buttonOption[F3].setEnabled(false);
                                    setImageButton(buttonOption[currentAnswer], "answer.blue");
                                    if (number != 15)
                                        Sound.falseAnswer(number);
                                    else
                                        Sound.falseAnswer((byte) 14);
                                    if (isFiftyFiftyUsed) {
                                        Thread.sleep(3000);
                                        setImageButton(buttonOption[trueAnswer], "answer.orange");
                                        buttonOption[trueAnswer].setForeground(Color.BLACK);
                                        labelOption[trueAnswer].setForeground(Color.WHITE);
                                        if (number > 5)
                                            Sound.playSound("answer.accepted");
                                        Sound.playBackgroundSound("answer.drum.roll", number);
                                        currentAnswer = trueAnswer;
                                        labelDialog.setIcon(null);
                                        labelDialog.setText(explanation);
                                        buttonUpdate.setVisible(true);
                                    } else {
                                        Sound.playBackgroundSound("hint.x2", number);
                                        answerAllow = true;
                                        buttonMenu.setVisible(true);
                                    }
                                } catch (InterruptedException ignored) {}
                            };
                            showAnimation(secondAttempt);
                        } else {
                            isRightMistakeUsed = false;
                            buttonFromMenu[2].setEnabled(true);
                            setImageButton(buttonOption[trueAnswer], "answer.orange");
                            labelDialog.setIcon(null);
                            labelDialog.setText(explanation);
                            buttonUpdate.setVisible(true);
                        }
                        break;
                    //Ответ с подсказкой "Замена вопроса"
                    case 2:
                        answerAllow = false;
                        currentAnswer = (byte) (choice - 8);
                        setImageButton(buttonOption[currentAnswer], "answer.orange");
                        buttonOption[currentAnswer].setForeground(Color.BLACK);
                        labelOption[currentAnswer].setForeground(Color.WHITE);
                        if (currentAnswer == trueAnswer)
                            explanation += "<br>Это был бы правильный ответ, ";
                        else
                            explanation += "<br>Правильный ответ: «" + labelOption[trueAnswer].getText() + " " + buttonOption[trueAnswer].getText() + "», ";
                        switch ((int) (Math.random()*3)){
                            case 0:
                                explanation += "но мы меняем вопрос.";
                                break;
                            case 1:
                                explanation += "и теперь " + number + "-й вопрос выглядит следующим образом.";
                                break;
                            default:
                                explanation += "однако извольте видеть новый " + number + "-й вопрос.";
                        }
                        labelDialog.setIcon(null);
                        labelDialog.setText(explanation);
                        buttonUpdate.setVisible(true);
                        break;
                    //Ответ после взятия денег
                    case 3:
                        answerAllow = false;
                        currentAnswer = (byte) (choice - 12);
                        setImageButton(buttonOption[currentAnswer], "answer.orange");
                        buttonOption[currentAnswer].setForeground(Color.BLACK);
                        labelOption[currentAnswer].setForeground(Color.WHITE);
                        if (currentAnswer == trueAnswer)
                            explanation += "<br>Это был бы правильный ответ и Вы бы выиграли " + labelSum[number - 1].getText() + " рублей!!!";
                        else
                            explanation += "<br>Правильный ответ: «" + labelOption[trueAnswer].getText() + " " + buttonOption[trueAnswer].getText() + "».<br>Вы вовремя остановились.";
                        labelDialog.setIcon(null);
                        labelDialog.setText(explanation);
                        buttonUpdate.setVisible(true);
                        returnCommandsOptions();
                        break;
                }
            }
        }
    }
    //Кнопка "Продолжить"
    class Update implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()){
                case "next":
                    Sound.stopBackgroundSound();
                    buttonUpdate.setVisible(false);
                    clearLabelDialog();
                    if (currentAnswer == trueAnswer && number != 15) {
                        Runnable nextQuestion = () -> {
                            try {
                                Sound.stopBackgroundSound();
                                Sound.trueAnswer(number, checkpoint);
                                showAnswer(true, false);
                                labelCurrentAmount.setLocation(labelCurrentAmount.getX(), labelSum[number - 1].getY());
                                if(number != 1)
                                    setImageLabel(labelSumNumber[number - 2], "icon.amount", (int) (labelCurrentAmount.getHeight() * 0.44f), (int) (labelCurrentAmount.getHeight() * 0.32f));
                                Thread.sleep(200);
                                transition(true);
                                if(number == 5 && checkpoint != 5){
                                    Sound.playSound("answer.true.5.ending");
                                    Thread.sleep(5000);
                                }
                                Thread.sleep(number != checkpoint ? (number + 4) / 5 * 2000 : 9000);
                                labelPrize.setText(null);
                                final int d = labelPrize.getWidth() / 10;
                                while(labelPrize.getX() < labelPrize.getWidth()){
                                    labelPrize.setLocation(labelPrize.getX() + d, labelPrize.getY());
                                    Thread.sleep(45);
                                }
                                labelPrize.setLocation((int) (-labelPrize.getWidth() * 1.5f), labelPrize.getY());
                                number++;
                                if(number == 15){
                                    labelDialog.setIcon(null);
                                    labelDialog.setText(
                                            "<html><center>А теперь мы с Вами подошли к кульминационному моменту. Лишь немногие достигали наивысшую планку игры «Кто хочет стать миллионером?», а правильно отвечали на последний вопрос единицы. Последний рубеж!" +
                                                    "<br>15-й вопрос на " + labelSum[labelSum.length - 1].getText() + " рублей. " +
                                                    (counterHint == 4 ? "Подсказок нет." : (4 - counterHint) + " подсказк" + (counterHint == 3 ? "а" : "и") + " на выбор из " + (5 - counterHint) + ".") +
                                                    "<br>" + labelSum[labelSum.length - 2].getText() + " можно забрать.</html>"
                                    );
                                    buttonUpdate.setActionCommand("question15");
                                    buttonUpdate.setVisible(true);
                                    return;
                                }
                                if(number > 5){
                                    Sound.playSound("question.next");
                                    Thread.sleep(5500);
                                }
                                Sound.playBackgroundSound("question.reflections", number);
                                showQuestion();
                            }catch (InterruptedException ignored) {}
                        };
                        showAnimation(nextQuestion);
                    }
                    else if(currentAnswer != trueAnswer) {
                        Runnable stopGame = () -> {
                            try {
                                Sound.falseAnswer(number);
                                showAnswer(false, true);
                                byte end;
                                if(number > checkpoint){
                                    labelCurrentAmount.setLocation(labelCurrentAmount.getX(), labelSum[checkpoint - 1].getY());
                                    end = (byte) (checkpoint - 1);
                                }
                                else{
                                    labelCurrentAmount.setLocation(labelCurrentAmount.getX(), -labelCurrentAmount.getHeight());
                                    end = 0;
                                }
                                for(int i = number - 2; i >= end; i--)
                                    labelSumNumber[i].setIcon(null);
                                Thread.sleep(200);
                                transition(false);
                                menuEndGame(true);
                            }catch (InterruptedException ignored) {}
                        };
                        showAnimation(stopGame);
                    }
                    else {
                        Runnable winner = () -> {
                            try {
                                Sound.playSound("game.winner");
                                showAnswer(true, true);
                                transition(true);
                                labelCurrentAmount.setLocation(labelCurrentAmount.getX(), labelSum[labelSum.length - 1].getY());
                                setImageLabel(labelSumNumber[labelSum.length - 2], "icon.amount", (int) (labelCurrentAmount.getHeight() * 0.44f), (int) (labelCurrentAmount.getHeight() * 0.32f));
                                Thread.sleep(19000);
                                menuEndGame(true);
                            }catch (InterruptedException ignored) {}
                        };
                        showAnimation(winner);
                    }
                    break;
                case "rules":
                    buttonUpdate.setVisible(false);
                    for(byte i = 0; i < buttonDecision.length; i++)
                        buttonDecision[i].setLocation(buttonFromMenu[i + 1].getLocation());
                    buttonUpdate.setLocation(buttonDecision[0].getLocation());
                    Runnable begin = () -> {
                        try {
                            labelDialog.setText(null);
                            int oldHeight = labelDialog.getHeight();
                            labelDialog.setSize(sizeScreen.width, sizeScreen.height);
                            Sound.playSound("game.begin");
                            screeSaver();
                            Thread.sleep(8000);
                            labelDialog.setIcon(null);
                            labelDialog.setSize(labelQuestion.getWidth(), oldHeight);
                            Sound.playBackgroundSound("game.rules", (byte) 15);
                            final int k = labelBackgroundAmounts.getWidth() / 10;
                            while (Math.abs(labelQuestion.getWidth() - labelBackgroundAmounts.getX()) > k){
                                labelBackgroundAmounts.setLocation(labelBackgroundAmounts.getX() - k, 0);
                                Thread.sleep(45);
                            }
                            labelBackgroundAmounts.setLocation(labelQuestion.getWidth(), 0);
                            labelSum[14].setForeground(Color.WHITE);
                            labelSumNumber[14].setForeground(Color.WHITE);
                            for(byte i = 0; i < labelSum.length; i++){
                                labelSum[i].setVisible(true);
                                labelSumNumber[i].setVisible(true);
                            }
                            labelDialog.setText("<html><center>Вам необходимо правильно ответить на 15 вопросов из различных областей знаний. " +
                                    "Каждый вопрос имеет 4 варианта ответа, из которых только один является верным.</html>");
                            Thread.sleep(1000);
                            for(byte i = 0; i < labelSumNumber.length; i++){
                                try {
                                    labelCurrentAmount.setLocation(labelCurrentAmount.getX(), labelSum[i].getY());
                                    setImageLabel(labelSumNumber[i - 1], "icon.amount", (int) (labelCurrentAmount.getHeight() * 0.44f), (int) (labelCurrentAmount.getHeight() * 0.32f));
                                }
                                catch (ArrayIndexOutOfBoundsException ignored){}
                                finally { Thread.sleep(300); }
                            }
                            buttonUpdate.setVisible(true);
                        } catch (InterruptedException ignored) {}
                    };
                    showAnimation(begin);
                    buttonUpdate.setActionCommand("hint1");
                    break;
                case "hint1":
                    labelDialog.setText("<html><center>У Вас есть 5 подсказок:<br>" +
                            "<br>«50:50» — убирает два неверных варианта ответа.</html>");
                    showHint(buttonHint[1], "hint." + hintTitle[1]);
                    Sound.playSound("game.rules.hint1");
                    buttonUpdate.setActionCommand("hint2");
                    break;
                case "hint2":
                    labelDialog.setText("<html><center>«Право на ошибку» — позволяет дать второй вариант ответа, если первый оказался неверным.</html>");
                    showHint(buttonHint[2], "hint." + hintTitle[2]);
                    Sound.playSound("game.rules.hint2");
                    buttonUpdate.setActionCommand("hint3");
                    break;
                case "hint3":
                    labelDialog.setText("<html><center>«Звонок другу» — даёт возможность посоветоваться с другом по телефону.</html>");
                    showHint(buttonHint[3], "hint." + hintTitle[3]);
                    Sound.playSound("game.rules.hint3");
                    buttonUpdate.setActionCommand("hint4");
                    break;
                case "hint4":
                    labelDialog.setText("<html><center>«Замена вопроса» — меняет вопрос на другой.</html>");
                    showHint(buttonHint[4], "hint." + hintTitle[4]);
                    Sound.playSound("game.rules.hint4");
                    buttonUpdate.setActionCommand("hint5");
                    break;
                case "hint5":
                    labelDialog.setText("<html><center>«Помощь ведущего» — позволяет взять подсказку у самого ведущего — Дмитрия Диброва.</html>");
                    showHint(buttonHint[0], "hint." + hintTitle[0]);
                    Sound.playSound("game.rules.hint1");
                    buttonUpdate.setActionCommand("endRules");
                    break;
                case "endRules":
                    labelDialog.setText("<html><center>Но разрешается использовать только 4, а какие, решаете Вы во время игры." +
                            "<br>До тех пор, пока Вы не дали ответ, можете забрать выигранные деньги.</html>");
                    buttonUpdate.setActionCommand("checkpoint");
                    break;
                case "checkpoint":
                    selectCheckpoint();
                    break;
                case "start":
                    buttonUpdate.setVisible(false);
                    buttonUpdate.setActionCommand("next");
                    clearLabelDialog();
                    labelCurrentAmount.setLocation(labelCurrentAmount.getX(), -labelCurrentAmount.getHeight());
                    for(byte i = 0; i < checkpoint - 1; i++)
                        labelSumNumber[i].setIcon(null);
                    Sound.stopBackgroundSound();
                    showAnimation(startGame);
                    break;
                case "newQuestion":
                    clearLabelDialog();
                    buttonUpdate.setVisible(false);
                    buttonUpdate.setActionCommand("next");
                    Runnable newQuestion = () -> {
                        try {
                            showAnswer(currentAnswer == trueAnswer, true);
                            setIconLabelCentralHint(0, "hint.replace", false);
                            Sound.restartBackgroundSound();
                            final int k = labelQuestion.getWidth() / 10;
                            while(labelQuestion.getX() < labelQuestion.getWidth()){
                                labelQuestion.setLocation(labelQuestion.getX() + k, labelQuestion.getY());
                                for (JButton jButton : buttonOption) jButton.setLocation(jButton.getX() + k, jButton.getY());
                                Thread.sleep(45);
                            }
                            locationQuestion(-labelQuestion.getWidth());
                            while(Math.abs(labelQuestion.getX()) > k){
                                labelQuestion.setLocation(labelQuestion.getX() + k, labelQuestion.getY());
                                for (JButton jButton : buttonOption) jButton.setLocation(jButton.getX() + k, jButton.getY());
                                Thread.sleep(45);
                            }
                            locationQuestion(0);
                            for(byte i = 0; i < buttonOption.length; i++){
                                labelOption[i].setVisible(true);
                                buttonOption[i].setActionCommand(String.valueOf(i));
                            }
                            setIconLabelCentralHint(0,"hint.replace", true);
                            byte oldN = n;
                            do {
                                n = (byte) (Math.random() * 15 + 1);
                            }while(oldN == n);
                            createQuestion();
                        }catch (InterruptedException ignored) {}
                    };
                    showAnimation(newQuestion);
                    break;
                case "DibrovHelped":
                    clearLabelDialog();
                    buttonUpdate.setVisible(false);
                    buttonUpdate.setActionCommand("next");
                    Sound.playSound("hint.Dibrov.end");
                    Sound.playBackgroundSound("question.reflections", number);
                    setIconLabelCentralHint(1, "hint.Dibrov", false);
                    buttonMenu.setVisible(true);
                    answerAllow = true;
                    hintAllow = true;
                    break;
                case "callHelped":
                    buttonUpdate.setVisible(false);
                    Sound.stopSound();
                    Sound.playSound("hint.call.end");
                    answerAllow = true;
                    buttonUpdate.setActionCommand("next");
                    break;
                case "take":
                    clearLabelDialog();
                    buttonUpdate.setVisible(false);
                    buttonUpdate.setActionCommand("end");
                    Runnable lastAnswer = () -> {
                        try {
                            Sound.stopBackgroundSound();
                            Sound.playSound("game.takeMoney");
                            Thread.sleep(7000);
                            labelDialog.setIcon(null);
                            labelDialog.setText("<html><center>Какой вариант ответа Вы бы выбрали?");
                            answerAllow = true;
                        }catch (InterruptedException ignored) {}
                    };
                    showAnimation(lastAnswer);
                    break;
                case "end":
                    clearLabelDialog();
                    buttonUpdate.setVisible(false);
                    Runnable prize = () -> {
                        try {
                            showAnswer(currentAnswer == trueAnswer, true);
                            number--;
                            transition(true);
                            menuEndGame(true);
                        }
                        catch (InterruptedException ignored) {}
                    };
                    showAnimation(prize);
                    break;
                case "question15":
                    buttonUpdate.setVisible(false);
                    buttonUpdate.setActionCommand("next");
                    clearLabelDialog();
                    showAnimation(startGame);
                    break;
            }
        }
    }
    //Подсказка "50:50"
    class FiftyFifty implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (hintAllow) {
                buttonHint[1].setEnabled(false);
                lockLastHint(2);
                isFiftyFiftyUsed = true;
                do {
                    F1 = (byte) (Math.random() * 4);
                    F2 = (byte) (Math.random() * 4);
                }
                while (F1 == trueAnswer || F2 == trueAnswer || F1 == F2);
                buttonOption[F1].setText(null);
                buttonOption[F2].setText(null);
                buttonOption[F1].setEnabled(false);
                buttonOption[F2].setEnabled(false);
                Sound.playSound("hint.50.50");
            }
        }
    }
    //Подсказка "Право на ошибку"
    class RightMistake implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(hintAllow){
                hintAllow = false;
                buttonHint[2].setEnabled(false);
                isRightMistakeUsed = true;
                lockLastHint(3);
                buttonFromMenu[2].setEnabled(false);
                for(byte i = 0; i < buttonOption.length; i++)
                    buttonOption[i].setActionCommand(String.valueOf(i + 4));
                Sound.playSound("game.rules.hint4");
                Sound.playBackgroundSound("hint.x2", number);
                setIconLabelCentralHint(1, "hint.x2", true);
            }
        }
    }
    //Подсказка "Звонок другу"
    class CallFriend implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(hintAllow){
                hintAllow = false;
                answerAllow = false;
                buttonHint[3].setEnabled(false);
                buttonMenu.setVisible(false);
                lockLastHint(4);
                labelDialog.setIcon(null);
                Sound.playBackgroundSound("hint.call.dialing", (byte) 15);
                Runnable call = () -> {
                    try {
                        labelDialog.setVerticalAlignment(JLabel.TOP);
                        Thread.sleep(2000);
                        Sound.playSound("hint.call.beeps");
                        Thread.sleep(5000);
                        String message = "<html>- Здравствуйте. Это Дмитрий Дибров с программы<br>«Кто хочет стать миллионером?».";
                        labelDialog.setText(message);
                        Thread.sleep(2000);
                        message += "<br>- Здравствуйте, очень приятно.";
                        labelDialog.setText(message);
                        Thread.sleep(2000);
                        message += "<br>- Взаимно. Ваш друг нуждается в вашей помощи.<br>Не могли бы Вы ответить на вопрос и<br>помочь выиграть " + labelSum[number - 1].getText() + " рублей?";
                        labelDialog.setText(message);
                        Thread.sleep(4000);
                        message += "<br>- Да, конечно. Я постараюсь.";
                        labelDialog.setText(message);
                        Thread.sleep(3000);
                        message += "<br>- У вас 30 секунд!";
                        labelDialog.setText(message);
                        Thread.sleep(2000);
                        labelTimer.setText("30");
                        labelTimer.setVisible(true);
                        for(int i = 0; i <= 5; i++){
                            labelTimer.setLocation(labelTimer.getX() - labelTimer.getWidth() / 6, 0);
                            Thread.sleep(40);
                        }
                        labelDialog.setVerticalAlignment(JLabel.CENTER);
                        labelDialog.setSize(labelDialog.getWidth() - labelTimer.getWidth(), labelDialog.getHeight());
                        if((number - 1) / 3 * -0.2375f + 1 >= Math.random() || isFiftyFiftyUsed){
                            if(!isFiftyFiftyUsed)
                                labelDialog.setText("<html>- " + labelQuestion.getText().replaceAll("<html><center>", "").replaceAll("<br>", " ") +
                                    "<br>- Хм... Я думаю это «" + labelOption[trueAnswer].getText() + " " + buttonOption[trueAnswer].getText() + "»." +
                                    "<br>- Уверен?<br>- Да.<br>- Спасибо.");
                            else
                                labelDialog.setText("<html>- " + labelQuestion.getText().replaceAll("<html><center>", "").replaceAll("<br>", " ") +
                                        "<br>- Ну «" + labelOption[6 - F1 - F2 - trueAnswer].getText() + " " + buttonOption[6 - F1 - F2 - trueAnswer].getText() + "» однозначно неверно." +
                                        "<br>- То есть ты говоришь «" + labelOption[trueAnswer].getText() + " " + buttonOption[trueAnswer].getText() + "»?" +
                                        "<br>- Да, именно он должен быть правильным.<br>- Спасибо.");
                        }else {
                            F1 = (byte) (Math.random() * 4);
                            labelDialog.setText("<html>- " + labelQuestion.getText().replaceAll("<html><center>", "").replaceAll("<br>", " ") +
                                    "<br>- Я плохо разбираюсь в этой теме.<br>- Хотя бы попробуй предположить.<br>- Ну пускай будет «" + labelOption[F1].getText() + " " + buttonOption[F1].getText() + "»." +
                                    "<br>- Уверен?<br>- Нет.<br>- Спасибо за помощь.");
                        }
                        buttonUpdate.setActionCommand("callHelped");
                        buttonUpdate.setVisible(true);
                        Sound.stopBackgroundSound();
                        Sound.playSound("hint.call.timer");
                        byte counter = 30;
                        while (!answerAllow && counter != 0){
                            labelTimer.setText(String.valueOf(counter));
                            Thread.sleep(1000);
                            counter--;
                        }
                        if(!answerAllow) {
                            labelTimer.setText("0");
                            buttonUpdate.setVisible(false);
                            buttonUpdate.setActionCommand("next");
                        }
                        labelDialog.setSize(labelQuestion.getWidth(), labelDialog.getHeight());
                        clearLabelDialog();
                        buttonMenu.setVisible(true);
                        answerAllow = true;
                        hintAllow = true;
                        Sound.playBackgroundSound("question.reflections", number);
                        Thread.sleep(1000);
                        for(int i = 0; i <= 5; i++){
                            labelTimer.setLocation(labelTimer.getX() + labelTimer.getWidth() / 6, 0);
                            Thread.sleep(40);
                        }
                        labelTimer.setLocation(labelQuestion.getWidth(), 0);
                        labelTimer.setVisible(false);
                    }catch (InterruptedException ignored) {}
                };
                showAnimation(call);
            }
        }
    }
    //Подсказка "Замена вопроса"
    class ReplaceQuestion implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(hintAllow){
                buttonHint[4].setEnabled(false);
                buttonMenu.setVisible(false);
                hintAllow = false;
                lockLastHint(5);
                labelDialog.setIcon(null);
                for(byte i = 0; i < buttonOption.length; i++)
                    buttonOption[i].setActionCommand(String.valueOf(i+8));
                switch ((int) (Math.random()*3)){
                    case 0:
                        labelDialog.setText("<html><center>И всё же скажите, Вы бы что ответили?</html>");
                        break;
                    case 1:
                        labelDialog.setText("<html><center>Перед тем, как мы узнаем правильный ответ,<br>какой вариант ответа Вы бы всё же выбрали?</html>");
                        break;
                    default:
                        labelDialog.setText("<html><center>Прежде чем мы поменяем вопрос, Вы к чему больше склонялись?</html>");
                }
                Sound.playSound("game.rules.hint4");
                setIconLabelCentralHint(0,"hint.replace", true);
                buttonUpdate.setActionCommand("newQuestion");
            }
        }
    }
    //Подсказка "Помощь Диброва"
    class HelpDibrov implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(hintAllow){
                buttonHint[0].setEnabled(false);
                buttonMenu.setVisible(false);
                hintAllow = false;
                answerAllow = false;
                lockLastHint(1);
                Sound.playSound("game.rules.hint4");
                Sound.playBackgroundSound("hint.Dibrov", (byte) 15);
                setIconLabelCentralHint(1,"hint.Dibrov", true);
                labelDialog.setIcon(null);
                if (number > 5 && !isFiftyFiftyUsed)
                    F1 = (byte) (Math.random() * 4);
                if(!isFiftyFiftyUsed && number != 15){
                    if(Math.random() < (number - 1) / 5 * -0.3f + 1.1f)
                        switch ((number - 1) / 5){
                            case 0:
                                labelDialog.setText("<html><center>Вопрос очень простой и ответ на него, разумеется, очевидный.<br>" +
                                        "«" + labelOption[trueAnswer].getText() + " " + buttonOption[trueAnswer].getText() + "» — правильный ответ.</html>");
                                break;
                            case 1:
                                labelDialog.setText("<html><center>Я бы ответил «" + labelOption[trueAnswer].getText() + " " + buttonOption[trueAnswer].getText() + "»." +
                                        "<br>Мне так кажется — я могу ошибаться.</html>");
                                break;
                            default:
                                labelDialog.setText("<html><center>Чем выше цена вопроса, тем коварнее становится игра.<br>" +
                                        "На " + labelSum[number - 1].getText() + " ответ может оказаться самым парадоксальным.<br>Я бы выбрал вариант «" +
                                        labelOption[trueAnswer].getText() + " " + buttonOption[trueAnswer].getText() + "».</html>");
                        }
                    else
                        labelDialog.setText("<html><center>Я могу только вместе с Вами поразмышлять.<br>Я считаю, что это вариант «" +
                                labelOption[F1].getText() + " " + buttonOption[F1].getText() + "»,<br>но это неточно — если ошибаюсь, прошу не обижаться.</html>");
                }
                else
                    if(isFiftyFiftyUsed && number != 15)
                        labelDialog.setText("<html><center>Из двух вариантов, конечно, выбирать легче.<br>" +
                                "Я больше склоняюсь к варианту «"+labelOption[trueAnswer].getText() + " " + buttonOption[trueAnswer].getText() + "».</html>");
                        else {
                            String ans15 = "<html><center>Увидеть 15-й вопрос — особая привилегия.<br>Поскольку вопрос на " + labelSum[labelSum.length - 1].getText() + ", я не гарантирую, что отвечу правильно." +
                                    "<br>Мне кажется, что это вариант ";
                            if (Math.random() < 0.5f)
                                ans15 += "«" + labelOption[trueAnswer].getText() + " " + buttonOption[trueAnswer].getText() + "».";
                            else
                                if(!isFiftyFiftyUsed)
                                    ans15 += "«" + labelOption[F1].getText() + " " + buttonOption[F1].getText() + "».";
                                else
                                    ans15 += "«" + labelOption[6 - F1 - F2 - trueAnswer].getText() + " " + buttonOption[6 - F1 - F2 - trueAnswer].getText() + "».";
                            ans15 += "<br>Но решение остаётся за Вами.</html>";
                            labelDialog.setText(ans15);
                        }
                buttonUpdate.setActionCommand("DibrovHelped");
                buttonUpdate.setVisible(true);
            }
        }
    }
    //Меню
    class Menu implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()){
                case "0":
                    hintAllow = !isRightMistakeUsed;
                    backgroundVisible(true, true);
                    buttonMenu.setVisible(true);
                    menuVisible(false);
                    break;
                case "1":
                    buttonDecision[0].setActionCommand("restart");
                    labelDialog.setText("Вы действительно хотите начать новую игру?");
                    menuVisible(false);
                    contextMenuVisible(true);
                    break;
                case "2":
                    buttonDecision[0].setActionCommand("takeMoney");
                    labelDialog.setText("Вы действительно хотите забрать деньги?");
                    menuVisible(false);
                    contextMenuVisible(true);
                    break;
                case "3":
                    buttonDecision[0].setActionCommand("exit");
                    labelDialog.setText("Вы действительно хотите выйти из игры?");
                    menuVisible(false);
                    contextMenuVisible(true);
                    break;
                default:
                    hintAllow = false;
                    backgroundVisible(false, false);
                    buttonMenu.setVisible(false);
                    menuVisible(true);
            }
        }
    }
    //Решение игрока
    class PlayerDecision implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e){
            switch (e.getActionCommand()){
                case "begin":
                    menuEndGame(false);
                    buttonDecision[0].setLocation(buttonUpdate.getLocation());
                    buttonDecision[1].setLocation(buttonUpdate.getX(), buttonUpdate.getY() + buttonUpdate.getHeight() + (int) (buttonUpdate.getHeight() / 8.43f));
                    labelDialog.setText("<html><center>Здравствуйте, дорогие друзья!!! " +
                            "В эфире Первого канала интеллектуальное шоу<br>«Кто хочет стать миллионером?» " +
                            "и я, её ведущий, Дмитрий Дибров!!!");
                    buttonUpdate.setVisible(true);
                    break;
                case "restart":
                    Sound.stopBackgroundSound();
                    menuEndGame(false);
                    if(labelPrize.getX() == 0){
                        labelPrize.setText(null);
                        labelPrize.setLocation((int) (-labelPrize.getWidth() * 1.5f), labelPrize.getY());
                    }
                    else {
                        backgroundVisible(true, false);
                        returnCommandsOptions();
                        fixConsequencesHints();
                        labelQuestion.setText(null);
                        for(byte i = 0; i < buttonOption.length; i++){
                            buttonOption[i].setText(null);
                            labelOption[i].setVisible(false);
                        }
                        locationQuestion((int) (-labelQuestion.getWidth() * 1.5f));
                        for (JLabel jLabel : labelCentralHint) jLabel.setIcon(null);
                    }
                    labelSum[14].setForeground(Color.ORANGE);
                    labelSumNumber[14].setForeground(Color.ORANGE);
                    labelSum[checkpoint - 1].setForeground(Color.ORANGE);
                    labelSumNumber[checkpoint - 1].setForeground(Color.ORANGE);
                    if(labelCurrentAmount.getY() > 0){
                        byte end = (byte) (14 - (labelCurrentAmount.getY() - labelSum[14].getY()) / labelSum[14].getHeight());
                        for(byte i = 0; i < end; i++)
                            labelSumNumber[i].setIcon(null);
                        labelCurrentAmount.setLocation(labelCurrentAmount.getX(), -labelCurrentAmount.getHeight());
                    }
                    if(counterHint == 4)
                        setImageButtonInactive(buttonHint[lastHint], "hint." + hintTitle[lastHint] + ".used", (int) (buttonHint[lastHint].getWidth() * 0.9f), (int) (buttonHint[lastHint].getHeight() * 0.9f));
                    for (JButton jButton : buttonHint) {
                        jButton.setVisible(false);
                        jButton.setEnabled(true);
                    }
                    lastHint = 0; counterHint = 0;
                    labelDialog.setIcon(null);
                    number = 1;
                    labelBackgroundAmounts.setLocation(sizeScreen.width, 0);
                    for(byte i = 0; i < labelSum.length; i++){
                        labelSum[i].setVisible(false);
                        labelSumNumber[i].setVisible(false);
                    }
                    Runnable newGame = () -> {
                        try {
                            final int oldHeight = labelDialog.getHeight();
                            labelDialog.setSize(sizeScreen.width, sizeScreen.height);
                            Sound.playSound("game.new");
                            screeSaver();
                            Thread.sleep(4000);
                            labelDialog.setIcon(null);
                            labelDialog.setSize(labelQuestion.getWidth(), oldHeight);
                            Sound.playBackgroundSound("game.rules", (byte) 15);
                            final int k = labelBackgroundAmounts.getWidth()/10;
                            while (Math.abs(labelQuestion.getWidth() - labelBackgroundAmounts.getX()) > k){
                                labelBackgroundAmounts.setLocation(labelBackgroundAmounts.getX() - k, 0);
                                Thread.sleep(45);
                            }
                            labelBackgroundAmounts.setLocation(labelQuestion.getWidth(), 0);
                            for(byte i = 0; i < labelSum.length; i++){
                                labelSum[i].setVisible(true);
                                labelSumNumber[i].setVisible(true);
                            }
                            for (JButton jButton : buttonHint) jButton.setVisible(true);
                            selectCheckpoint();
                        } catch (InterruptedException ignored) {}
                    };
                    showAnimation(newGame);
                    break;
                case "takeMoney":
                    contextMenuVisible(false);
                    backgroundVisible(true, false);
                    answerAllow = false;
                    hintAllow = false;
                    try {
                        labelDialog.setText("<html><center>Игрок берёт деньги.<br>Выигрыш нашего гостя составил " + labelSum[number - 2].getText() + " рублей!!!");
                    } catch(ArrayIndexOutOfBoundsException ignored){
                        labelDialog.setText("<html><center>Завершать игру на 1-м вопросе... Ваше право.<br>Игрок берёт деньги.<br>Выигрыш нашего гостя составил 0 рублей!!!");
                    }
                    buttonUpdate.setActionCommand("take");
                    buttonUpdate.setVisible(true);
                    for(byte i = 0; i < 4; i++)
                        buttonOption[i].setActionCommand(String.valueOf(i + 12));
                    break;
                case "exit":
                    System.exit(0);
                    break;
                default:
                    //Нажата кнопка "Нет"
                    contextMenuVisible(false);
                    menuVisible(true);
            }
        }
    }
}