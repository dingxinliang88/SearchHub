package com.juzi.searchhub.service;

import com.juzi.searchhub.model.entity.Article;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author codejuzi
 */
@SpringBootTest
class ArticleServiceTest {

    @Resource
    private ArticleService articleService;

    @Test
    void testAddArticle() {
        Article article = new Article();
        article.setTitle("ParallelStream的坑");
        article.setContent("Java 8引入了Stream API，使得处理集合数据更加方便和高效。其中，ParallelStream是一个并行的Stream，能够在多个CPU核心上并行处理数据。\n" +
                "\n" +
                "然而，在使用ParallelStream时有一些需要注意的坑：\n" +
                "\n" +
                "1. 数据竞争：由于并行处理数据会涉及到多线程操作，所以可能会出现数据竞争的问题。这可能导致计算结果不正确或者性能下降。要避免这个问题，可以使用synchronized关键字或者ConcurrentHashMap等并发容器。\n" +
                "\n" +
                "2. 并行度设置：默认情况下，ParallelStream会根据CPU核心数动态调整并行度。但是，如果数据量较小，设置过高的并行度反而会降低性能。可以通过System.setProperty(\"java.util.concurrent.ForkJoinPool.common.parallelism\", \"n\")来手动设置并行度，其中n为期望的并行度值。\n" +
                "\n" +
                "3. 管道操作顺序：在执行管道操作时，需要注意操作顺序对最终结果的影响。例如，map操作可以在并行流中并行处理，而reduce操作则必须在所有元素都处理完毕后再进行汇总。因此，在设计管道操作时需要考虑操作的顺序和相互之间的依赖关系。\n" +
                "\n" +
                "4. 状态变量：在并行处理数据时，需要避免使用共享状态变量，因为多个线程同时访问会导致数据竞争问题。可以使用Stream.collect()方法来汇总并行处理的结果，而不是在并行流中进行状态变量的修改。\n" +
                "\n" +
                "5. 任务分配：ParallelStream采用Fork/Join框架来实现任务分配。但如果其中一个任务执行时间过长或者出现异常，整个流的性能可能会受到影响。可以通过设置合适的阈值和错误处理机制来避免这个问题。\n" +
                "\n" +
                "总之，在使用ParallelStream时需要认真考虑数据竞争、并行度设置、管道操作顺序、状态变量和任务分配等因素，以确保得到正确的结果和最优的性能。");
        boolean save = articleService.save(article);
        assertTrue(save);
        System.out.println("articleId = " + article.getId());
    }

}