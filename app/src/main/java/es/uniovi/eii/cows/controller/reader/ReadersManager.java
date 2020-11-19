package es.uniovi.eii.cows.controller.reader;

import android.util.Log;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import es.uniovi.eii.cows.data.FirebaseHelper;
import es.uniovi.eii.cows.model.NewsItem;

/**
 * This class pulls controls all the readers and their results
 */
public class ReadersManager {

    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static final int CORE_POOL_SIZE = 8;
    private static final int MAXIMUM_POOL_SIZE = 8;

    private ThreadPoolExecutor readersThreadPool;

    private List<NewsReader> readers;

    private static ReadersManager instance = new ReadersManager();               // Singleton

    private ReadersManager() {
        readers = ReadersFactory.getInstance().getReaders();
        initThreads();
    }

    private void initThreads() {
        BlockingQueue<Runnable> readersQueue = new LinkedBlockingQueue<>();
        readersThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, readersQueue);
    }

    /**
     * @return  Instance of the manager
     */
    public static ReadersManager getInstance() {
        return instance;
    }

    /**
     * Starts all the readers in different Threads
     */
    public void run() {
        readers.forEach(r -> readersThreadPool.execute(r));
    }

    /**
     * Restarts the threads
     */
    public void rerun() {
        initThreads();
        run();
    }

    /**
     * @return  Pulled and parsed news when finished
     */
    public List<NewsItem> getNews() {
        readersThreadPool.shutdown();
        try {
            readersThreadPool.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<NewsItem> copy = readers.stream().map(NewsReader::getNews).flatMap(Collection::stream)
                .sorted().collect(Collectors.toList());

        storeNewsItems(copy);

        return readers.stream().map(NewsReader::getNews).flatMap(Collection::stream)
                .sorted().collect(Collectors.toList());
    }

    /**
     * Close readers streams
     */
    public void shutdown() {
        readers.forEach(NewsReader::stop);
    }

    /**
     * Stores the specified list of news items into the database
     * @param newsItems
     */
    public void storeNewsItems(List<NewsItem> newsItems){
        newsItems.forEach(newsItem -> FirebaseHelper.getInstance().addNewsItem(newsItem));
    }
}
