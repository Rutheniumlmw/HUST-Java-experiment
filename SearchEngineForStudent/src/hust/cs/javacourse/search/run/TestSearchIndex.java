package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.AbstractIndexBuilder;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;
import hust.cs.javacourse.search.query.impl.IndexSearcher;
import hust.cs.javacourse.search.query.impl.SimpleSorter;
import hust.cs.javacourse.search.util.Config;

import javax.swing.plaf.nimbus.AbstractRegionPainter;
import java.util.Scanner;

import static hust.cs.javacourse.search.query.AbstractIndexSearcher.LogicalCombination.AND;
import static hust.cs.javacourse.search.query.AbstractIndexSearcher.LogicalCombination.OR;

/**
 * 测试搜索
 */
public class TestSearchIndex {
    /**
     *  搜索程序入口
     * @param args ：命令行参数
     */
    public static void main(String[] args){
        Sort simpleSort = new SimpleSorter();
        String indexFile = Config.INDEX_DIR + "index.dat";
        AbstractIndexSearcher searcher = new IndexSearcher();
        searcher.open(indexFile);

        Scanner input = new Scanner(System.in);
        System.out.println("1 or 2 word");
        int op1 = input.nextInt();
        int op2;
        String searcherString1, searcherString2;
        switch (op1){
            case 1:
                searcherString1 = input.next();
                AbstractHit[] hits1 = searcher.search(new Term(searcherString1), simpleSort);
                for (AbstractHit hit:hits1){
                    System.out.println(hit);
                }
                break;
            case 2:
                searcherString1 = input.next();
                searcherString2 = input.next();
                System.out.println("1:AND 2:OR 3:phrase");
                op2 = input.nextInt();
                switch (op2){
                    case 1:
                        AbstractHit[] hits2 = searcher.search(new Term(searcherString1), new Term(searcherString2), simpleSort, AND);
                        for (AbstractHit hit : hits2){
                            System.out.println(hit);
                        }
                        break;
                    case 2:
                        AbstractHit[] hits3 = searcher.search(new Term(searcherString1), new Term(searcherString2), simpleSort, OR);
                        for (AbstractHit hit : hits3){
                            System.out.println(hit);
                        }
                        break;
                    case 3:
                        AbstractHit[] hits4 = ((IndexSearcher)searcher).mysearch(new Term(searcherString1), new Term(searcherString2), simpleSort);
                        for (AbstractHit hit : hits4){
                            System.out.println(hit);
                        }
                        break;
                }
                break;

        }
    }
}
