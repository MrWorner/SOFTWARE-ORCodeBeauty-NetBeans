/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//http://www.java2s.com/Code/Java/Swing-JFC/CatalogSwing-JFC.htm
//https://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html#TextComponentDemo


package OR3Beauty;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

//$Objects $OBJS $OBJ $Interface $USER $SELOBJ $SELOBJS $RETURN $ERRMSG $BASE $ILANG $XML $Xml $Date $Check $Math $Strings $Gener $NAME $SERVER true false java
//shift + Alt + F
public class OR_Beauty {

    //private JTextPane jTextPane;
    private int needToAddTab = 0;// сколько нужно добавить символов Tab
    private int needToAddTab_comment = 0;// сколько нужно добавить символов Tab (для комментариев)
    private int currentEmptyLine = 0;// текущая пустая линия

    private boolean isCommentedLine_brakets = false;// является ли текущая строка комментарием в /*   */
    private boolean isCommentedLine_classic = false;// является ли текущая строка комментарием //

    //----КОНСТРУКТОР
    public OR_Beauty(JTextPane textPane) {
        //jTextPane = textPane;
        
        
    }

    //----ГЛАВНЫЙ МЕТОД ПО СОЗДАНИЮ КРАСОТЫ КОДА
    public void CleanCode(JTextPane textPane) throws BadLocationException {

        needToAddTab = 0;// сколько нужно добавить символов Tab
        needToAddTab_comment = 0;// сколько нужно добавить символов Tab (для комментариев)
        currentEmptyLine = 0;// текущая пустая линия
        isCommentedLine_brakets = false;// является ли текущая строка комментарием в /*   */
        isCommentedLine_classic = false;// является ли текущая строка комментарием //
             
        //--Вытаскиваем весь текст с компонента JTextPane
        Element root = textPane.getDocument().getDefaultRootElement();
        List<String> FinalListOfLines = new ArrayList<>();

        int currentCaretPos = textPane.getCaretPosition();
        int totalLines = root.getElementCount();// Подсчитываем кол-во строк МОЖНО БЫЛО ПРОСТО: FinalListOfLines.size()
        //--Проходим по каждой строке через разные методы
        for (int i = 0; i < totalLines; i++) {
            Element line = root.getElement(i);
            int startPos = line.getStartOffset();
            int endPos = line.getEndOffset() - line.getStartOffset() - 1;
            String textLine = line.getDocument().getText(startPos, endPos);
            textLine = RemoveTabs(textLine);// Удаляем все символы таба у линии
            textLine = RemoveSpaceBeforeAny(textLine);// Удаляем все начальные пробелы  у линии
            boolean removeLine = RemoveExtraEmptyLines(textLine);// Получаем является ли текущая строка пустой
            if (!removeLine) {// Удалить ли текущую строку так как она пустая подряд?
                textLine = AddRightTabs(textLine);//  Добавляем необходимые символы Таб
                FinalListOfLines.add(textLine);// Запоминаем строку
            }
        }

        String finalText = OR_TextMerger.MergeText(FinalListOfLines);// Слияние всех строк
        textPane.setText(finalText);// Прикрепляем к JTextPane
        textPane.setCaretPosition(currentCaretPos);
        if (needToAddTab > 0) {// Если вдруг неравное количество необх добавлений Таба, то значит где то нехватает #end, код в тексте сломан
            JOptionPane.showMessageDialog(null, "Ooops. Missing #end tag(s): " + needToAddTab);
        }
    }

    private String RemoveTabs(String text) {
        text = text.replaceAll("\t", "");//УДАЛЯЕМ ВСЕ СИМВОЛЫ TAB
        return text;
    }

    private String RemoveSpaceBeforeAny(String text) {

        //_text = _text.replaceAll("\n", "");//УДАЛЯЕМ ВСЕ С НОВОЙ СТРОКИ
        //_text = _text.replaceAll(" ", "");//УДАЛЯЕМ ВСЕ ПРОБЕЛЫ
        String finalText = "";
        boolean removedFirstSpaces = false;
        for (char ch : text.toCharArray()) {
            if (ch != ' ' || removedFirstSpaces) {
                removedFirstSpaces = true;
                finalText = finalText + ch;
            }
        }

        return finalText;
    }

    private String AddRightTabs(String text) {

        int i = text.indexOf("//");//получаем позицию символа //
        boolean i_EXISTS = i > -1;

        //BEGIN ВЫРЕЗАЕМ КОММЕНТАРИИ, ЧТОБЫ ИЗБАВИТЬСЯ ОТ ПРОВЕРОК
        String cuttedText = "";
        if (i_EXISTS) {
            String[] arrOfStr = text.split("//");
            if (arrOfStr.length != 0) {

                text = arrOfStr[0];
                arrOfStr[0] = "//";
                boolean first = true;
                for (String a : arrOfStr) {
                    if (a != "//") {

                        if (first) {
                            cuttedText = cuttedText + a;
                            first = false;
                        } else {
                            cuttedText = cuttedText + "//" + a;
                        }

                    } else {
                        cuttedText = cuttedText + a;
                    }

                }
            }
        }
        //END ВЫРЕЗАЕМ КОММЕНТАРИИ, ЧТОБЫ ИЗБАВИТЬСЯ ОТ ПРОВЕРОК

        //BEGIN ДЛЯ ЗАКОМЕНТИРОВАННОЙ СТРОКИ С ПОМОЩЬЮ '//'
        if (text.length() == 0) {

            if (cuttedText.length() > 0) { //если есть только комментарии комментарии
                //---------------System.out.println("_cuttedText = " + _cuttedText + " needToAddTab_comment = " + needToAddTab_comment);
                text = cuttedText;
                if (!isCommentedLine_classic) {//если режим НЕ включен, то включаем
                    isCommentedLine_classic = true;
                    isCommentedLine_brakets = true;
                }

            } else//если нет комментарии
            {
                if (isCommentedLine_classic) {//если режим включен, то отключаем
                    isCommentedLine_classic = false;
                    isCommentedLine_brakets = false;
                    needToAddTab_comment = 0;
                }
            }

        } else {
            if (isCommentedLine_classic) {//если режим включен, то отключаем
                isCommentedLine_classic = false;
                isCommentedLine_brakets = false;
                needToAddTab_comment = 0;
            }
        }
        //END ДЛЯ ЗАКОМЕНТИРОВАННОЙ СТРОКИ С ПОМОЩЬЮ '//'

        //-------ВОЗМОЖНО ПОТРЕБУЕТСЯ НЕМНОГО ИЗМЕНИТЬ ЛОГИКУ ЕСЛИ ВДРУГ НА ОДНОЙ ЛИНИИ БУДУТ КОММЕНТЫ напр: /* */ /* */
        int i_begin = text.indexOf("/*");//получаем позицию символа {*
        int i_end = text.indexOf("*/");//получаем позицию символа {*
        boolean i_begin_EXISTS = i_begin > -1;
        boolean i_end_EXISTS = i_end > -1;

        if (i_begin_EXISTS) {
            isCommentedLine_brakets = true;
        } else {
            if (i_end_EXISTS) {
                isCommentedLine_brakets = false;
                needToAddTab_comment = needToAddTab;
            }
        }

        int x_if = text.indexOf("#if");//получаем позицию команды #if
        int x_while = text.indexOf("#while");//получаем позицию команды #while        
        int x_foreach = text.indexOf("#foreach");//получаем позицию команды #foreach
        int x_end = text.indexOf("#end");//24.01.2020 NEW получаем позицию команды #end |  кто то помжет писать код в одну строку: #if($isZayv && $isZayv == 1) #set($personZayv = $zapFace) #end

        boolean x_if_EXISTS = x_if > -1;
        boolean x_while_EXISTS = x_while > -1;
        boolean x_foreach_EXISTS = x_foreach > -1;
        boolean x_end_EXISTS = x_end > -1;

        if (x_if_EXISTS || x_while_EXISTS || x_foreach_EXISTS) //проверяем, есть ли одна из команд, которая открывает блок
        {
            boolean isOpening = true;
            if (x_end_EXISTS) {//Проверяем есть ли блок закрытия, если вдруг будет, то возможно придется игнорировать само открытие
                if ((x_end > x_if && x_if_EXISTS) || (x_end > x_while && x_while_EXISTS) || (x_end > x_foreach && x_foreach_EXISTS)) {
                    isOpening = false;//код написан в одну строку с открытием и закрытием, пример: #if($isZayv && $isZayv == 1) #set($personZayv = $zapFace) #end. Значит игнорируем строку
                }
            }
            if (isOpening) {
                text = OpeningBlock(text);
            }
        } else {//если нет команды, которая открывает блок, то проверяем - не имеется ли команда закрытия блока (#end)

            //int x_end = _text.indexOf("#end");//ОТКЛЮЧЕН 24.01.2020 получаем позицию команды #end 
            int x_else = text.indexOf("#else");//получаем позицию команды #else
            int x_elseif = text.indexOf("#elseif");//получаем позицию команды #elseif

            //boolean x_end_ALIVE = x_end > -1;//ОТКЛЮЧЕН 24.01.2020
            boolean x_else_EXISTS = x_else > -1;
            boolean x_elseif_EXISTS = x_elseif > -1;

            if (x_end_EXISTS || x_else_EXISTS || x_elseif_EXISTS) {//если есть команда закрытие блока
                text = ClosingBlock(text, x_end_EXISTS);
            } else {//если нет команд открытия или закрытия блока, то просто для обычного текста
                text = SimpleText(text);
            }
        }

        //BEGIN ЕСЛИ БЫЛИ КОММЕНТАРИИ (//), ТО ВОЗВРАЩАЕМ В ТЕКСТ
        if (i_EXISTS) {
            if (!isCommentedLine_classic) {
                text = text + cuttedText;
            }
        }
        //END ЕСЛИ БЫЛИ КОММЕНТАРИИ (//), ТО ВОЗВРАЩАЕМ В ТЕКСТ

        //System.err.println("STARTED| i = " + i + " x_foreach = " + x_foreach);
        return text;
    }

    private String OpeningBlock(String text) {

        for (int j = 0; j < needToAddTab; j++) {
            text = "\t" + text;
        }

        if (isCommentedLine_brakets) {
            for (int j = 0; j < needToAddTab_comment; j++) {
                text = "\t" + text;
            }
        }
        
        System.out.println("BEFORE OpeningBlock _text = " + text + " needToAddTab_comment = " + needToAddTab_comment);
        if (isCommentedLine_brakets) {
            needToAddTab_comment++;
            System.out.println("OpeningBlock _text = " + text + " needToAddTab_comment = " + needToAddTab_comment);
        } else {
            needToAddTab++;
        }

        return text;
    }

    private String ClosingBlock(String text, boolean isEndTag) {
        if (!isCommentedLine_brakets) {
            needToAddTab--;
        }

        for (int j = 0; j < needToAddTab; j++) {
            text = "\t" + text;
        }

        if (isCommentedLine_brakets) {

            if (!(needToAddTab_comment < 1)) {//Чтобы в комментарии END не выезжал из глубины  без открытого тега
                needToAddTab_comment--;
            }

            for (int j = 0; j < needToAddTab_comment; j++) {
                text = "\t" + text;
            }
        }

        if (!isEndTag) {//не #END
            if (!isCommentedLine_brakets) {
                needToAddTab++;//возвращаем углубление
            } else {
                needToAddTab_comment++;
            }
        }

        return text;
    }

    private String SimpleText(String text) {

        for (int j = 0; j < needToAddTab; j++) {
            text = "\t" + text;
        }
        if (isCommentedLine_brakets) {
            for (int j = 0; j < needToAddTab_comment; j++) {
                text = "\t" + text;
            }
        }

        return text;
    }

    private boolean RemoveExtraEmptyLines(String text) {
        boolean remove = false;

        if (text.length() == 0 || text == " ") {
            currentEmptyLine++;
            if (currentEmptyLine > 1) {
                remove = true;
                currentEmptyLine--;
            }
        } else {
            currentEmptyLine = 0;
        }

        return remove;
    }
}