package app.xml;

import app.xml.exception.XmlExpectedEndOfFileException;
import app.xml.exception.XmlNoMoreNodesException;
import app.xml.exception.XmlParseException;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;


/**
 * Реализация xml-парсера, считывающий узлы ({@link app.xml.Node}) из файла по требованию.
 * Такая реализация позволяет работать с файлами,
 * размер которых превышает несколько гигабайт и не помещаются в оперативную память.
 * Для достижения этой возможности парсер считывает символы из файла
 * лишь до появления открывающего тэга({@link Element}) и возвращает узел дерева, тело которого ещё не прочитано.
 * Также парсер "забывает" все узлы дерева, закрывающие тэги которых были прочитаны.
 *
 * В случае возникновения ошибки во время работы парсера и
 * продолжения работы с ним будет возникать первая возникшая ошибка.
 */
public class XmlPartParser implements XmlParser{

    /**
     * Xml-файл.
     */
    private final File file;

    /**
     * Буффер для считываемого файла.
     */
    private final BufferedReader buffIn;

    /**
     * Отдельный парсер для компонентов тэга({@link Element}) узла: имени, атрибутов.
     */
    private XmlElementParser elementParser;

    /**
     * Последовательность открытых узлов(текущая ветвь дерева).
     */
    private NodePath nodePath = new NodePath();
    /**
     * Флаг: true - найден ли корневой узел дерева.
     * Он необходим для определения наличия нескольких корневых узлов в файле и их отсутствия(пустой файл).
     */
    private boolean rootElementIsFound = false;

    /**
     * Содержит ссылку на следующий узел в дереве, который необходимо вернуть.
     * Он необходим для проверки наличия следующего узла в дереве.
     */
    private Node nextNode = null;

    /**
     * Текущая позиция в файле. Необходим для указания местоположения ошибки в файле.
     */
    //TODO add cursor
    private CursorPosition cursor = new CursorPosition();

    /**
     * При возникновении ошибки во время парскинга, необходимо сохранить экзепляр ошибки,
     * чтобы снова его выкидывать в случае вызова методов парсера.
     */
    private IOException thrownException = null;

    /**
     * Сохраняет значения полей, открывает поток на чтение из файла и
     * считывает открывающий элемент корневого узла.
     * @param file - xml-файл
     * @param elementParser - парсер для тэгов
     * @throws app.xml.exception.XmlParseException - если файл пустой либо нарушена структура xml-файла
     * @throws IOException - если возникла ошибка во время чтения файла
     */
    public XmlPartParser(File file, XmlElementParser elementParser) throws IOException{
        this.file = file;
        this.elementParser = elementParser;

        FileInputStream fileInput = new FileInputStream(file);
        FileChannel channel =fileInput.getChannel();
        buffIn = new BufferedReader(Channels.newReader(channel, StandardCharsets.UTF_8));

        nextNode = findNextNode();
        rootElementIsFound = true;
    }

    @Override
    public Node getNextNode() throws IOException{
        if(thrownException != null){
            throw thrownException;
        }
        if(!hasNextNode()){
            thrownException = new XmlNoMoreNodesException("There is no more nodes in the file.", cursor);
            throw thrownException;
        }

        Node currentNode = nextNode;
        try{
            nextNode = findNextNode();
        } catch (XmlExpectedEndOfFileException e){
            nextNode = null;
        }
        return currentNode;

    }

    @Override
    public boolean hasNextNode() throws IOException {
        if(thrownException instanceof XmlNoMoreNodesException
                || thrownException instanceof XmlExpectedEndOfFileException){
            return false;
        }
        if(thrownException != null){
            throw thrownException;
        }
        return nextNode != null;
    }

    private Node findNextNode() throws IOException{
        Element element = getNextElement();
        if(rootElementIsFound && nodePath.isEmpty()){
            thrownException = new XmlParseException("Multiply root elements.", cursor);
            throw thrownException;
        }
        if(checkNodeClose(element)){
            nodePath.getTailNode().setStatus(NodeStatus.CLOSED);
            nodePath = nodePath.removeLast();
            return findNextNode();
        }

        Node newNode = new Node(nodePath.getTailNode(), element, NodeStatus.OPENED);
        nodePath = nodePath.addNode(newNode);
        return newNode;
    }

    private Element getNextElement() throws IOException {
        readCharsBeforeNextElement();
        String element = readElement();
        return elementParser.parseElement(element);
    }

    private void readCharsBeforeNextElement() throws IOException {
        int c;
        while((c = readChar()) != '<'){
            char ch = (char) c;

            if(nodePath.isEmpty() && rootElementIsFound && c == -1){
                //TODO add cursor
                throw new XmlExpectedEndOfFileException(null);
            }

            if(nodePath.isEmpty() && !Character.isWhitespace(ch)){
                thrownException = new XmlParseException("Unexpected symbol occurs.", cursor);
                throw thrownException;
            }

            if(!nodePath.isEmpty()
                    && (!nodePath.getTailNode().isBodyEmpty()
                    || !Character.isWhitespace(ch))){
                nodePath.appendIntoBody(ch);
            }
        }
    }

    private String readElement() throws IOException {
        StringBuilder element = new StringBuilder();
        int c;
        while((c = readChar()) != '>'){
            if(c == -1){
                thrownException = new XmlParseException("Unexpected file end.", cursor);
                throw thrownException;
            }
            char ch = (char) c;
            if(ch == '<'){
                thrownException = new XmlParseException("Double open tag.", cursor);
                throw thrownException;
            }
            element.append(ch);
        }
        return element.toString();
    }


    private int readChar() throws IOException {
        int c = buffIn.read();
        if(c == -1 && !rootElementIsFound){
            thrownException = new XmlParseException("File is empty.", cursor);
            throw thrownException;
        }
        if(c == -1 && !nodePath.isEmpty()){
            thrownException = new XmlParseException("Xml file closed before end.", cursor);
            throw thrownException;
        }

        return c;
    }

    private boolean checkNodeClose(Element element) throws IOException {
        if(element.getType() != ElementType.CLOSE){
            return false;
        }
        String elementName = element.getName().trim();
        if(nodePath.isEmpty() ||
                !elementName.equals(nodePath.getTailNode().getName())){
            thrownException = new XmlParseException(
                    "Close element name does not coincide with the current node one.",
                    cursor
            );
            throw thrownException;
        }
        return true;
    }

    @Override
    public void close() throws IOException {
        buffIn.close();
    }


}
