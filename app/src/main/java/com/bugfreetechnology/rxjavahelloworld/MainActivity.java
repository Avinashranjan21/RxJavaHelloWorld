package com.bugfreetechnology.rxjavahelloworld;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.hello_world_salute)
    TextView helloWorldSalute;
    @BindView(R.id.stock_updates_recycler_view)
    RecyclerView recyclerView;
    private StockDataAdapter stockDataAdapter;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Observable.just("Hello Please use this app responsibly!")
                .subscribe(s->helloWorldSalute.setText(s));

        Observable.just("Hello from RetroLambda").subscribe(e -> Log.d(TAG, "Hello " + e));

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        stockDataAdapter = new StockDataAdapter();
        recyclerView.setAdapter(stockDataAdapter);

        Observable.just(new StockUpdate("GOOGLE", 12.43, new Date()),
                new StockUpdate("APPLE", 645.1, new Date()),
                new StockUpdate("TWITTER", 1.43, new Date()))
                .subscribe(stockSymbol -> stockDataAdapter.add(stockSymbol));

        
        
//        Checking for Scheduler thread
        
        
//         All the call made on Main Thread
        Observable.just("First item", "Second item")
                .doOnNext(e -> Log.d(TAG, "on-next:"    + Thread.currentThread().getName() + ":" + e))
                .subscribe(e -> Log.d(TAG, "subscribe:" + Thread.currentThread().getName() + ":" + e));



//        All call made on the RxCachedThread using subscribeOn method
        Observable.just("First item", "Second item")
                .subscribeOn(Schedulers.io())
                .doOnNext(e -> Log.d(TAG, "on-next:" +
                        Thread.currentThread().getName() + ":" + e))
                .subscribe(e -> Log.d(TAG, "subscribe:" +
                        Thread.currentThread().getName() + ":" + e));
        
//        The subscribe call will be made on Rx thread and observe will be done on Android Main Thread

        Observable.just("First item", "Second item")
                .subscribeOn(Schedulers.io())
                .doOnNext(e -> Log.d(TAG, "on-next:" +
                        Thread.currentThread().getName() + ":" + e))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> Log.d(TAG, "subscribe:" +
                        Thread.currentThread().getName() + ":" + e));
        
        
        
//        Now Checking the flow of RxJava using log statements

        Observable.just("One", "Two")
                .subscribeOn(Schedulers.io())
                .doOnDispose(() -> log("doOnDispose"))
                .doOnComplete(() -> log("doOnComplete"))
                .doOnNext(e -> log("doOnNext", e))
                .doOnEach(e -> log("doOnEach"))
                .doOnSubscribe((e) -> log("doOnSubscribe"))
                .doOnTerminate(() -> log("doOnTerminate"))
                .doFinally(() -> log("doFinally"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(e -> log("subscribe", e));
    }


    private void log(String stage, String item) {
        Log.d(TAG, stage + ":" + Thread.currentThread().getName() + ":" +
                item);
    }

    private void log(String stage) {
        Log.d(TAG, stage + ":" + Thread.currentThread().getName());
    }
}
