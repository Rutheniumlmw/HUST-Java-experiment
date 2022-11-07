package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.index.impl.TermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * AbstractTermTupleScanner的具体实现类
 */
public class TermTupleScanner extends AbstractTermTupleScanner {

    private int pos = 0;
    private Queue<TermTuple> termTupleQueue = new LinkedList<>();
    private StringSplitter stringSplitter = null;

    /**
     * 构造函数
     * @param input：指定输入流对象，应该关联到一个文本文件
     */
    public TermTupleScanner(BufferedReader input){
       super(input);
       stringSplitter = new StringSplitter();
       stringSplitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
   }

    /**
     * 获得下一个三元组
     *
     * @return :下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() {
       if(termTupleQueue.isEmpty()) {
           try {
               String line = input.readLine();
               while (line != null && line.equals(""))
                   line = input.readLine();
               if (line != null) {
                   List<String> term = stringSplitter.splitByRegex(line);
                   for (String i : term) {
                       termTupleQueue.add(new TermTuple(new Term(i), pos++));
                   }
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
       return termTupleQueue.poll();
    }
}
